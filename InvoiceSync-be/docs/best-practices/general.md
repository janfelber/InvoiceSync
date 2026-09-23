# SaaS Backend Best Practices — General Reference

This is a general-purpose reference for what a well-built SaaS backend looks like — not derived from
InvoiceSync-be's code, and not a bug list. Use it to evaluate *any* backend, including ones you build
next. Where a topic has a concrete InvoiceSync-be finding worth knowing, there's a short "**In
InvoiceSync-be:**" note with a pointer into [`docs/best-practices/audit.md`](audit.md)
(the code-grounded audit) — but the point of this document is to stand on its own.

---

## 1. Architecture & layering

The standard shape: **Controller → Service → Repository**, each layer talking only to the one below
it. Controllers translate HTTP ↔ domain calls and do nothing else (no business logic, no direct
repository access). Services own business logic and transaction boundaries. Repositories own data
access only.

**Monolith vs. microservices:** for a solo/small-team SaaS, a **modular monolith** (one deployable,
internally split into clear domain modules with well-defined boundaries) is almost always the right
call. Splitting into microservices before you have the team size and operational maturity to run them
(service discovery, distributed tracing, network failure handling, data consistency across services)
is one of the most common early-SaaS mistakes — it multiplies operational complexity for a
scalability problem you don't have yet. Split a module out only when you have a *concrete* reason
(independent scaling need, a team boundary, a genuinely separate deployment cadence) — not
speculatively.

**Domain model shape:** distinguish three kinds of objects and don't blur them —
- **Entities** — have identity and a lifecycle, persisted, mutable.
- **Value objects** — no identity, defined entirely by their attributes, immutable (money, an address,
  a date range).
- **DTOs** — shape data for a specific boundary (API request/response), immutable, no business logic.

*In InvoiceSync-be:* the codebase already is a modular monolith with the right layering — see
[audit.md §1, §7](audit.md) for gaps in how consistently that shape
is followed (missing interfaces, fat services), and §1/§4 there for the record/class (DTO vs. value
object vs. entity) breakdown.

**Yours today, side by side — partial update, two different answers to the same question:**
```java
// company/service/CompanyServiceImpl.java:87-108 — correct PATCH semantics, but repeats forever
if (request.name() != null) oldCompany.setName(request.name());
if (request.address() != null) oldCompany.setAddress(request.address());
// ...one more null-check to remember per field, on every future field

// receipt/service/ReceiptServiceImpl.java:171-192 — wired as @PatchMapping, but a real bug:
receipt.setDate(request.date());               // no null-check
receipt.setPaymentDate(request.paymentDate());  // silently NULLS any field the client omitted
```

**How it should look — one mechanism, used everywhere, correct by construction:**
```java
@Mapper(componentModel = "spring")
public interface CompanyMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromRequest(CompanyRequest request, @MappingTarget Company entity);

}

@Transactional
public void updateCompany(Long id, CompanyRequest request) {
  Company company = companyRepository.findById(id).orElseThrow(...);
  companyMapper.updateFromRequest(request, company); // null fields in the request are simply skipped
  // no explicit save() needed — Hibernate's dirty checking flushes it at commit
}
```
A new field added to `CompanyRequest` later needs zero new code in the mapper or the service — MapStruct
generates the null-check for every field automatically, so this can't silently regress the way the
Receipt version already did.

---

## 2. API design

- **Resource naming:** plural nouns for collections (`/invoices`, not `/invoice` or `/getInvoices`),
  nesting only one level deep (`/companies/{id}/invoices`, not `/companies/{id}/invoices/{id}/items/{id}/...`).
- **HTTP verbs mean something:** `POST` creates (return `201` + `Location` header), `PUT` replaces
  wholesale, `PATCH` partially updates, `DELETE` removes, `GET` never mutates. Status codes should be
  precise: `409` for a conflict (duplicate resource), `422` for semantically invalid input that passed
  basic validation, `404` for "not found or not yours" (never `403` for a resource that isn't the
  caller's — that confirms the resource *exists*, which is itself information disclosure).
- **Consistent error envelope:** every error response should have the same shape (`{code, message,
  traceId}` or similar) regardless of which endpoint or exception produced it — callers should never
  need endpoint-specific error parsing.
- **Versioning:** decide a strategy (URI path `/v1/...` is the simplest to reason about) *before* you
  need your first breaking change — retrofitting versioning onto an unversioned API is painful.
- **Pagination:** every endpoint returning a collection that can grow unbounded needs pagination
  (offset/limit is fine until a table gets large, then cursor-based pagination avoids the "page 400 is
  slow" problem).
- **OpenAPI as a living contract:** generate it from real annotations, keep it accurate — it's your
  API's documentation and can generate client SDKs/mocks for the frontend team.

*In InvoiceSync-be:* pagination via `Pageable`/`Page<T>` is used consistently; `GlobalExceptionHandler`
gives a mostly-consistent error shape except two controllers that bypass it; OpenAPI is wired but not
filled in. Details in [audit.md §6, §10](audit.md).

**Yours today — two controllers quietly opt out of the shared error shape:**
```java
// receipt/controller/ReceiptController.java:51-71
try {
  return ResponseEntity.ok(receiptService.createReceipt(request));
} catch (LimitExceededException e) {
  return ResponseEntity.status(429).build();   // empty body — GlobalExceptionHandler already has a
} catch (Exception e) {                         // handler for this exact exception WITH a message
  return ResponseEntity.status(500).build();    // catching generic Exception also hides real bugs
}

// receipt/controller/PohodaController.java:30-48
catch (Exception e) {
  return ResponseEntity.status(500).body(e.getMessage()); // leaks internal exception text to the client
}
```

**How it should look — let it propagate, one handler owns the shape:**
```java
@PostMapping
public ReceiptResponse createReceipt(@Valid @RequestBody ReceiptRequest request) {
  return receiptService.createReceipt(request); // no try/catch at all
}

// core/exception/GlobalExceptionHandler.java — this already exists and already does it correctly
@ExceptionHandler(LimitExceededException.class)
public ResponseEntity<String> handleLimitExceeded(LimitExceededException e) {
  return ResponseEntity.status(429).body(e.getMessage());
}
```
Every caller gets the same error shape for the same exception, regardless of which endpoint threw it —
and nobody has to remember to keep two catch blocks in sync with one handler.

---

## 3. Data & persistence

- **Migrations are the only way schema changes happen** — no manual `ALTER TABLE` against prod. Use a
  migration tool (Flyway/Liquibase) with sequential, immutable files — never edit a migration that's
  already shipped to a shared environment; write a new one instead.
- **Backward-compatible ("expand/contract") migrations** for zero-downtime deploys: add a new column
  as nullable first, backfill, deploy code that writes both old and new, then a later migration drops
  the old column — never a single migration that renames/drops a column the currently-running code
  still reads.
- **Explicit fetch strategy** on every JPA/ORM association — know whether each relationship is lazy or
  eager, don't rely on framework defaults you haven't checked.
- **Index every foreign key and every column used in a `WHERE`/`ORDER BY` on a table that will grow.**
  Don't index speculatively on tables that will stay small.
- **Backups + a tested restore procedure.** A backup you've never restored from isn't a backup — it's
  a hope. For a bootstrapped SaaS, this is one of the highest-consequence things to get right early:
  losing customer data is often unrecoverable *for trust*, even if you can restore the bytes.
- **Connection pooling tuned to your actual concurrency**, not framework defaults left unexamined.

*In InvoiceSync-be:* Flyway is used correctly with sequential migrations; several `@ManyToOne`
associations rely on the JPA default (`EAGER`) rather than an explicit choice; indexes were retrofitted
after the fact rather than from the start; no backup strategy exists for the uploaded-documents storage
tier as of the last audit. Details in [audit.md §8](audit.md).

**Yours today — fetch strategy left to the JPA default instead of chosen explicitly:**
```java
// invoice/model/Invoice.java:51 — @ManyToOne with no `fetch` is EAGER by JPA's default
@ManyToOne
private Company company;
```

**How it should look — explicit, so the cost is a decision, not an accident:**
```java
@ManyToOne(fetch = FetchType.LAZY)
private Company company;
// add @EntityGraph(attributePaths = "company") on the specific repository method
// that actually needs company data eagerly for its response shape
```

**LAZY isn't free — know the two failure modes before flipping the switch everywhere:**
1. **`LazyInitializationException`** — accessing a lazy association after the Hibernate session/transaction
   has already closed (e.g. an entity escapes the service layer and something outside a transaction —
   Jackson serializing a response, a view template — touches the association). Fix by loading what a
   specific use case needs *inside* the transaction (map to a DTO in the service method, or use
   `@EntityGraph`/`JOIN FETCH` when you know you need the association) — not by making the whole
   association `EAGER`, which just pays the join cost on every load whether needed or not, and composes
   badly (multiple `EAGER` collections on one entity can produce a cartesian product).
2. **N+1** — iterating a list of parents and touching a lazy child collection per row. `LAZY` doesn't
   cause this any more than `EAGER` does — `EAGER` just always pays a join (or a second query), which
   *hides* the N+1 shape rather than fixing it. The actual fix is a targeted `@EntityGraph`/`JOIN FETCH`
   on the one query that needs it.
3. **Watch `spring.jpa.open-in-view`** — Spring Boot defaults this to `true`, which keeps the Hibernate
   session open for the whole HTTP request specifically to paper over `LazyInitializationException`.
   That hides the real problem (lazy access happening outside the service layer) at the cost of holding
   a DB connection for the entire request and allowing lazy-load queries to fire anywhere in the request
   lifecycle, not just where you expect them. Setting it explicitly to `false` and fixing the resulting
   `LazyInitializationException`s properly (DTO mapping / `@EntityGraph` inside the transaction) is the
   standard recommendation — leaving the default `true` silently is itself a bad habit, arguably a worse
   one than a stray `EAGER` association.

**Yours today — the same external-storage cascade bug, fixed once, not mirrored:**
```java
// invoice/model/Invoice.java:64 — fixed correctly: no cascade, R2 deletion is app-coordinated
@OneToMany(mappedBy = "invoice")
private List<InvoiceDocument> documents;

// receipt/model/Receipt.java:83 — same situation (documents live in R2), NOT fixed the same way
@OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ReceiptDocument> documents;
// a future `receipt.getDocuments().remove(x); save(receipt);` would silently delete the DB row
// and leave the R2 object orphaned — the exact bug class already fixed for Invoice
```
**How it should look:** drop `cascade`/`orphanRemoval` from `Receipt.documents` to match `Invoice.documents`
— document deletion stays an explicit, app-coordinated operation (delete from R2, then delete the row)
regardless of which code path touches the collection.

---

## 4. Multi-tenancy

Three standard models, in increasing order of isolation (and cost):

1. **Shared schema, row-level isolation** — every table has a `tenant_id`/`created_by` column, every
   query filters on it. Cheapest to build and operate; the risk is entirely in *discipline* — one
   missed filter is a cross-tenant data leak (an IDOR). Best mitigated by enforcing the filter at the
   lowest possible layer (a DB-level row-security policy, or a framework mechanism that makes it
   structurally hard to forget) rather than trusting every service method to remember.
2. **Schema-per-tenant** (one Postgres schema per customer, shared DB instance) — stronger isolation,
   migrations must run per-schema, harder to query across tenants for internal analytics.
3. **Database-per-tenant** — strongest isolation, most operational overhead (backup/migration/scaling
   multiplied by tenant count). Usually only justified by a compliance requirement or enterprise
   customer demand for physical data separation.

Most SaaS products start at (1) and stay there for years — it's the right default until a specific
customer or compliance requirement forces a change.

*In InvoiceSync-be:* model (1), enforced via a custom AOP layer (`@RequiresOwnership`/`@Owned*`) rather
than raw per-method checks — a good middle ground (centralizes the *mechanism*, though it still
requires remembering to *apply* the annotation to each new endpoint). Open gaps in current application
of it are tracked in [audit.md §9](audit.md).

**Yours today — the annotation exists in the codebase, just not applied here:**
```java
// datatransfer/service/DataTransferServiceImpl.java:129-135
public DataTransferResponse findById(Long id) {
  return mapper.toResponse(repository.findById(id).orElseThrow(...));
  // no ownership check at all — any authenticated user can read another user's
  // data-transfer import by guessing/incrementing the id
}
```

**How it should look — the same pattern already used correctly elsewhere in this codebase:**
```java
@RequiresOwnership
public DataTransferResponse findById(@OwnedDataTransfer Long id) {
  return mapper.toResponse(repository.findById(id).orElseThrow(...));
  // OwnershipAspect throws EntityNotFoundException (→ 404) if `id` isn't the caller's —
  // same 404-not-403 treatment already applied to Company/Invoice/Receipt
}
```

---

## 5. AuthN/AuthZ

- **Password storage:** bcrypt or argon2, never a fast general-purpose hash (MD5/SHA-*) or reversible
  encryption. Enforce a minimum length (NIST guidance: length over complexity rules — 12+ characters,
  no forced special-character gymnastics).
- **Brute-force protection:** rate limit and/or lock out after repeated failed attempts, on every
  credential-checking endpoint (login, and often-forgotten: registration and password-reset request
  endpoints too, since those are abuse/spam vectors even without a password to guess).
- **JWT access + refresh tokens:** short-lived access token (minutes), longer-lived refresh token that
  is *opaque and hashed at rest* (never a second JWT — a JWT can't be revoked before its expiry), with
  rotation on every use and reuse-detection (if a used-up refresh token is presented again, that's
  evidence of theft — revoke the whole session family, not just that token).
- **MFA and account lockout/recovery flows** become expected once you have paying customers handling
  sensitive data (accounting documents, in this case).
- **RBAC/ABAC:** start with simple roles (`USER`/`ADMIN`); introduce fine-grained permissions only once
  you actually have more than two tiers of access — premature permission systems are their own
  maintenance burden.
- **Third-party OAuth (e.g. "Sign in with Google", or integrating with a customer's Google account):**
  store tokens encrypted at rest, scope requests to the minimum needed, handle token expiry/refresh
  explicitly.

*In InvoiceSync-be:* JWT+refresh with rotation/reuse-detection is implemented properly; brute-force
protection exists on login but not registration/password-reset. Details in
[audit.md §9](audit.md).

---

## 6. Security

Use the **OWASP Top 10** as a standing checklist, not a one-time review. The items most relevant to a
typical CRUD SaaS backend:
- **Broken access control (IDOR)** — the single most common real-world SaaS vulnerability class; see
  §4 above.
- **Injection** — parameterized queries always; never string-concatenate user input into SQL/native
  queries.
- **Security misconfiguration** — don't expose framework management/debug endpoints
  (`/actuator/env`, `/actuator/heapdump`, stack-trace-in-response error pages) in production.
- **Vulnerable dependencies** — automated scanning (Dependabot, Snyk, `mvn versions:display-dependency-updates`)
  on a schedule, not "whenever someone remembers."
- **Secrets management:** environment variables are an acceptable starting point for a solo
  developer; move to a dedicated secrets manager (Vault, AWS/GCP Secrets Manager, Doppler) once you
  have more than one person or more than one deployment environment touching secrets, so rotation and
  access auditing don't depend on everyone trusting a shared `.env` file.
- **CORS:** an explicit origin allowlist, never a wildcard combined with `credentials: true` (that
  combination is invalid per spec and a red flag if seen).
- **Security headers:** CSP, `X-Content-Type-Options: nosniff`, HSTS — either set explicitly or
  confirm the framework's secure defaults are actually active (don't assume).
- **Least privilege:** the DB user your app connects as shouldn't have superuser rights; cloud IAM
  roles should be scoped to exactly the resources a service needs.

*In InvoiceSync-be:* CORS allowlist and headers are done correctly; Actuator is locked down properly;
specific IDOR gaps and secrets-on-disk notes are in
[audit.md §9](audit.md).

**Yours today — an entity annotation that leaks secrets into every log line touching it:**
```java
// user/model/User.java:31
@Data           // generates toString()/equals()/hashCode() over EVERY field
@Entity
public class User {
  private String password;
  private String googleAccessToken;
  private String googleRefreshToken;
  // any log.info("{}", user), a debugger, or an exception message embedding `user`
  // now prints the password hash and OAuth tokens
}
```

**How it should look:**
```java
@Getter
@Setter
@SuperBuilder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // identity = id only, not every mutable field
public class User extends BaseEntity {
  private String password;
  private String googleAccessToken;
  private String googleRefreshToken;
  // no generated toString() — falls back to Object's, which prints nothing sensitive
}
```

---

## 7. Reliability & resilience

- **Idempotency for anything that might be retried** — webhooks (a payment provider *will* redeliver),
  and any client-triggered mutation reachable over a flaky network (consider an `Idempotency-Key`
  header pattern for "create" endpoints your frontend might retry).
- **Timeouts on every external call** — HTTP clients, DB queries — an unbounded call is a resource leak
  waiting to cascade into an outage. Pair with **retries using exponential backoff + jitter** (never a
  tight retry loop) and, once you depend on enough external services, a **circuit breaker** so one slow
  dependency doesn't exhaust your thread pool and take the whole app down with it.
- **Graceful degradation:** if a non-critical external dependency fails (e.g. an AI extraction service),
  the core feature should still work in a degraded form (manual data entry) rather than the whole
  request failing.
- **Transaction boundaries should match actual atomicity needs** — never hold a DB transaction open
  across a network call to another service (see architecture doc for why).

*In InvoiceSync-be:* Stripe webhook idempotency is implemented (DB-level dedupe on event ID); a
network call inside an open DB transaction is flagged in
[audit.md §5](audit.md).

**Yours today — external I/O executing inside an open DB transaction:**
```java
// invoice/service/InvoiceServiceImpl.java — class-level @Transactional covers save() too
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

  public Invoice save(MultipartFile invoicePdf, ExtractionMode mode) {
    var extracted = invoiceExtractor.extract(invoicePdf, mode);   // LLM HTTP call — INSIDE the open transaction
    Invoice invoice = invoiceRepository.save(mapper.toInvoice(extracted));
    fileStorageService.saveInvoiceDocument(invoice.getId(), invoicePdf); // R2 HTTP call — also inside it
    return invoice;
  }
}
```

**How it should look — the DB transaction only wraps the DB write:**
```java
public class InvoiceServiceImpl implements InvoiceService {

  public Invoice save(MultipartFile invoicePdf, ExtractionMode mode) {
    var extracted = invoiceExtractor.extract(invoicePdf, mode);    // no transaction open yet
    Invoice invoice = persistInvoice(extracted);                    // short, focused transaction
    fileStorageService.saveInvoiceDocument(invoice.getId(), invoicePdf); // no transaction open anymore
    return invoice;
  }

  @Transactional
  Invoice persistInvoice(ExtractedInvoice extracted) {
    return invoiceRepository.save(mapper.toInvoice(extracted));
  }
}
```
The DB connection is only held for the actual `INSERT`, not for the round-trip to the LLM or to R2 —
and a failed upload after a successful save is now a plain, visible follow-up problem to handle
explicitly, not a rollback that silently can't undo the already-uploaded file anyway.

---

## 8. Scalability

- **Stateless application instances** — no in-memory session state that would break if a second
  instance is added behind a load balancer; session/auth state belongs in the DB or the JWT itself.
- **Cache what's expensive and rarely changes** — but don't cache speculatively; add caching when you
  have a *measured* hot path, since a stale-cache bug is worse than a slow query.
- **Move slow work off the request thread** — email sending, PDF/report generation, AI extraction,
  webhook processing: these belong in a background job/queue, not blocking an HTTP response. Even a
  simple in-process `@Async` is better than nothing; a real queue (SQS, RabbitMQ) once you need
  retries/visibility into job status.
- **The database is usually your first real bottleneck**, not the application server — invest in query
  review and indexing before reaching for horizontal app scaling or read replicas.

*In InvoiceSync-be:* the app is stateless (JWT-based auth); no caching or async/background job
infrastructure exists yet — reasonable at current scale, worth introducing when email-sending latency
or a specific expensive lookup becomes a measured problem. See
[audit.md §10-11](audit.md).

---

## 9. Observability

The three pillars:
- **Logs** — structured (JSON in production, so they're queryable), with a correlation/request ID that
  ties one user-facing request to every log line and error-tracking event it produced, end-to-end
  (ideally frontend → backend → error tracker, not just within the backend).
- **Metrics** — request rate/latency/error-rate per endpoint at minimum (Micrometer + Prometheus/Datadog
  is the standard Java stack); lets you set SLOs and alert on *trends*, not just individual errors.
- **Traces** — for anything with multiple internal hops (a request that touches several services or a
  slow external call), distributed tracing shows where time actually goes.

Beyond raw collection: **alert on symptoms that matter** (error-rate spike, latency SLO breach, failed
payment webhook) routed somewhere a human actually sees it — collecting data nobody looks at isn't
observability. **Health checks should check real dependencies** (DB connectivity, and ideally critical
external services) — a health check that only confirms "the JVM is running" gives false confidence.

*In InvoiceSync-be:* Sentry + a correlation-ID filter exist; specific gaps (handled exceptions not
reaching Sentry, correlation ID not in console logs, health check not covering external deps) are in
[audit.md §10](audit.md).

---

## 10. Testing

The **testing pyramid**: many fast unit tests (pure logic, no Spring context), a moderate number of
integration tests (real DB via Testcontainers, verifying wiring/queries/security actually work
together), few end-to-end tests (full user flows through the real stack). Mocking away exactly the
thing you need to verify (e.g. mocking a security aspect in a "security test") gives false confidence —
match the test type to what you're actually trying to prove.

**Security-critical paths deserve explicit tests as a general rule**, not just "hope the manual QA
catches it": authorization/tenant-isolation logic, session/token lifecycle, and payment webhook
handling are exactly the kind of thing that regresses silently (no user complains until it's exploited)
and is cheap to regression-test once with a real integration test.

**CI should run the full suite on every PR and block merge on failure** — a test suite nobody's
required to keep green decays into decoration.

*In InvoiceSync-be:* test coverage is currently thin; the two highest-leverage additions (tenant
isolation, refresh-token rotation) are called out in
[audit.md §10](audit.md).

---

## 11. CI/CD & environments

- **At least three environments in spirit** (local/dev, staging, production) — even a solo developer
  benefits from a staging environment that mirrors prod config, to catch environment-specific issues
  (a migration that behaves differently on a populated DB, a missing env var) before they hit
  customers.
- **No manual production deploys or migrations** once you have real customers — a pipeline
  (build → test → migrate → deploy) removes "did I remember to run the migration" as a failure mode.
- **Feature flags decouple deploy from release** — ship code dark, turn it on for a subset of users,
  roll back a bad feature without a redeploy.
- **Infrastructure as code** (Terraform, or even just versioned Docker Compose/deploy scripts) so your
  infrastructure isn't tribal knowledge in one person's head.

*In InvoiceSync-be:* two property profiles exist (prod defaults + local); no dedicated staging profile
yet — reasonable for current stage, worth adding before onboarding a team or before a launch with real
customer data at stake.

---

## 12. Compliance & data privacy

Directly relevant here given the accounting-document domain (Slovak/Czech market, GDPR):
- **Data minimization** — collect only what the feature needs.
- **Right to erasure**, balanced against **legal retention requirements** — accounting documents in
  AT/SK typically need ~10 years retention regardless of a deletion request; know which data must be
  anonymized vs. genuinely deleted vs. retained, and document the distinction rather than leaving it
  implicit.
- **DPAs (Data Processing Agreements) with every subprocessor** that touches customer data — payment
  provider, cloud storage, any AI/LLM API used for document extraction, email provider. This is a
  legal/contractual step, not a code change, but it's a real launch blocker, not optional paperwork.
- **PII never in logs or error trackers** — a stack trace or log line is not covered by the same
  retention/deletion guarantees as your primary database.
- **Audit logging** for who accessed/changed sensitive data, if your compliance posture needs it
  (increasingly expected for accounting/financial SaaS).

*In InvoiceSync-be:* GDPR account deletion/anonymization is implemented; a subprocessor DPA (for the AI
extraction provider specifically) was still an open decision as of the last review, not yet resolved.

---

## 13. Documentation & maintainability

- **Decisions, not just code, should be written down** — an ADR (Architecture Decision Record) or a
  living conventions doc (what `CLAUDE.md` already is for this project) prevents the same debate
  happening twice and lets a new contributor understand *why*, not just *what*.
- **API documentation should be generated from the code** (OpenAPI annotations), not hand-maintained
  separately — hand-maintained docs drift.
- **Onboarding docs** (how to run locally, seed data, required env vars) reduce the cost of the next
  person joining — including future-you after six months away from a module.

*In InvoiceSync-be:* `CLAUDE.md` + this doc + `docs/patterns/` are exactly this pattern, already in
place and worth keeping current as conventions evolve.

---

## 14. The Twelve-Factor App — a quick sanity-check framework

Worth knowing as a reference checklist even if you don't follow it dogmatically: config in environment
variables (not code), explicit dependency declarations, treat backing services (DB, cache, queue) as
attached resources swappable via config, strict separation of build/release/run stages, stateless
processes, port-binding, concurrency via process scaling, fast startup/graceful shutdown
("disposability"), dev/prod parity, logs treated as event streams (not files the app manages), and
admin/one-off tasks run as one-off processes in the same environment as the app.

Most modern Spring Boot apps already satisfy the majority of these by default (env-based config via
`application.properties` + `${VAR}` placeholders, stateless request handling) — it's a useful checklist
to run through occasionally, not a framework to adopt wholesale.

---

## How this fits with the other two docs

This document is the **general reference** — read it to understand what "good" looks like anywhere.
[`audit.md`](audit.md) is the **code-grounded audit** — read it to see
exactly where InvoiceSync-be's current code diverges from these principles, with file:line citations and
a prioritized fix list. [`new-module-template.md`](new-module-template.md) is the **applied version** —
a complete worked example showing all of it used together correctly, to copy the shape from when
building something new instead of re-deriving it each time. Update *this* document rarely (the
principles don't change often); update the audit doc as findings get fixed or new ones surface; update
the template whenever a convention here changes.
