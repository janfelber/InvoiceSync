# Backend Best Practices & Audit

**Audit date:** 2026-09-23. This document is a snapshot — re-verify file:line references against current code before
treating them as fact, code moves on.

**This is the code-grounded middle of a set of three.** For general SaaS-backend best practices that don't depend on
InvoiceSync-be's current code at all (API design, multi-tenancy models, CI/CD, compliance, the twelve-factor app, etc.),
see
[`general.md`](general.md). This document exists to show exactly where the
current code diverges from those principles, with file:line citations. For a complete worked example of building
something brand new with every convention applied correctly from the start, see [
`new-module-template.md`](new-module-template.md).

**Relation to [`CLAUDE.md`](../../CLAUDE.md):** `CLAUDE.md` is the source of truth for conventions the team has explicitly
*adopted*. This document is broader: general SaaS-backend best practices, findings from a full-codebase audit, and
recommendations that haven't been promoted into `CLAUDE.md`
yet. When a section here reaches consensus and gets followed consistently, pull a one-line pointer into `CLAUDE.md` the
same way it already points at
[`docs/patterns/java-services.md`](../patterns/java-services.md) — don't duplicate the full text there.

Each section uses the same `Problem` / `Why this approach` / `Solution` / `Example` / `When to use`
shape as `CLAUDE.md`, because this audit *is* current InvoiceSync-be code, not a foreign catalog.

---

## 1. Record vs. class — decision guide

**Problem:** The codebase has no explicit rule for when a type is a `record` vs. a Lombok class. Current count in
`modules/`: ~19 records vs. ~15 classes for request/response DTOs, split roughly (but not deliberately) by age — newer
`*Request` types (invoice/receipt/company/posting-account) are records, `auth` module and almost all `*Response` types
are mutable Lombok classes. Some request classes even stack `@Getter @Setter @Data @Builder` together
(`RegisterRequest`) — redundant, since
`@Data` already implies `@Getter`/`@Setter`.

**Why this approach:** A `record` is Java's built-in immutable-data-carrier: final fields, a canonical constructor,
generated `equals`/`hashCode`/`toString` based on *all* components. That's exactly what a DTO or value object should
be — nothing about a `CompanyRequest` should change after it's built from an incoming JSON body. A `class` is for
anything with mutable state across its lifecycle, or that JPA needs to manage.

**Solution — decide by what the type *is*, not by when it was written:**

| Type of object                                                                        | Use                                | Why                                                                                                             |
|---------------------------------------------------------------------------------------|------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| Request/response DTO, no behavior beyond holding data                                 | `record`                           | Immutable by construction, `equals`/`hashCode`/`toString` for free, no accidental setter calls after validation |
| Value object (money, an ID wrapper, IČO/DIČ)                                          | `record`                           | Same reasoning — `MonetaryAmount` and `OrganizationDto` in `shared/` already do this correctly                  |
| `@Entity` (JPA-managed)                                                               | **Always a class, never a record** | See below                                                                                                       |
| Anything mutated in place after construction (an accumulator, a builder-style helper) | `class`                            | Records are final by design; fighting that is a smell                                                           |

**Never make a `@Entity` a record.** Two concrete reasons, not just style: (1) Hibernate needs a no-arg constructor and
the ability to set fields after instantiation to hydrate an entity from a row — a record's canonical constructor and
final fields fight that. (2) A record's generated `equals`/
`hashCode` compares *all* fields, which is wrong for entity identity (two entities with the same ID but a stale
in-memory field would compare unequal) and breaks under lazy-loaded proxies. Keep entities as
`@Getter @Setter @SuperBuilder` classes (see §2 for why not `@Data`).

**Example:**

```java
// Good — value object, immutable, used today in shared/MonetaryAmount.java
public record MonetaryAmount(BigDecimal priceWithoutVAT, BigDecimal priceWithVAT) {

}

// Bad — RegisterRequest.java stacks redundant annotations on what should just be a record
@Getter
@Setter
@Data
@Builder
public class RegisterRequest { ...
}

// should be:
public record RegisterRequest(@NotBlank String email, @NotBlank @Size(min = 12) String password) {

}
```

**When to use:** Every new DTO/value object from now on. Existing `*Response` classes migrate opportunistically when
you're already touching that module — not a drive-by rewrite.

**See also:** §4 (value objects currently exist only on the response side, not the entity side).

---

## 2. Entities & Lombok — never `@Data` on an `@Entity`

**Problem:** `modules/user/model/User.java:31` has `@Data` on the `User` entity. `@Data` generates
`equals`/`hashCode`/`toString` over **every** field, including `password`, `googleAccessToken`, and
`googleRefreshToken`. That means `user.toString()` — called implicitly by any `log.info("{}", user)`, a debugger, or an
exception message — leaks the password hash and OAuth tokens into logs. It also means `equals`/`hashCode` change every
time any field changes, which is wrong for an entity whose identity is its database row, not its current field values
(breaks `Set`/`Map` usage across a save-then-modify cycle, and is unsafe with Hibernate's lazy proxies).

**Why this approach:** An entity's identity is its primary key, established once, for its entire lifecycle — including
before it has an ID (transient) and after it's detached. Lombok's `@Data` was designed for plain value-holder POJOs, not
for anything JPA manages; using it on an `@Entity` is a well-known footgun the JPA/Lombok community explicitly warns
about.

**Solution:** Use `@Getter @Setter` (already the pattern on `Invoice`, `Receipt`, `Company`, `BaseEntity`)
plus an explicit, ID-only equals/hashCode:

```java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

}
```

Subclasses inherit this and never need their own `@EqualsAndHashCode`. For `toString()`, either omit it (default
`Object.toString()` is fine for entities — you don't want a full field dump in logs anyway) or use
`@ToString(onlyExplicitlyIncluded = true)` naming just the id/a business key.

**Example:** `User.java:31` (`@Data`, leaks `password`/OAuth tokens via `toString`) is the concrete bug to fix here.
`InternalDocumentAccount`/`CashDocumentAccount` also use `@Data` on empty entity subclasses — lower risk (no own fields)
but still the wrong annotation by convention.

**When to use:** Every `@Entity`, new or existing. Fixing `User` is worth doing opportunistically soon given the
log-leak angle, even though it's not a required drive-by.

---

## 3. Partial updates — pick one mechanism (this is the answer to "how do I do PATCH properly")

**Problem:** Two different, inconsistent approaches exist today, and one of them is an active bug:

- **Correct but verbose** — `CompanyServiceImpl.updateCompany` (`company/service/CompanyServiceImpl.java:87-108`):
  loads the entity, then `if (request.name() != null) oldCompany.setName(request.name());` repeated per field. Correct
  PATCH semantics, but every new field needs a new hand-written null-check, and it's easy to forget one.
- **Actively buggy** — `ReceiptServiceImpl.updateReceiptById` (`receipt/service/ReceiptServiceImpl.java:171-192`):
  loads the entity, then unconditionally calls `receipt.setDate(request.date())`,
  `setPaymentDate(request.paymentDate())`, etc. for every field, no null-checks. The endpoint is wired as
  `@PatchMapping` (`ReceiptController.java:151`), but any field the client omits from the JSON body gets **nulled out**
  on save — it behaves like a `PUT`, not a `PATCH`, while advertising `PATCH`
  semantics.
- No `MapStruct` dependency exists in the project at all, and no `.toBuilder()` usage exists anywhere despite 44
  `@Builder`/12 `@SuperBuilder` usages — so a copy-with-changes builder pattern isn't even available today.

**Why this approach:** There are two genuinely different "partial update" problems, and they want two different tools —
conflating them is exactly how the codebase ended up with two inconsistent patterns:

1. **Updating a mutable, Hibernate-managed `@Entity` from a request DTO** (Company, Receipt, Invoice, PostingAccount
   updates). You want: only the fields present in the request get written, and JPA's dirty-checking handles the actual
   `UPDATE` statement. Hand-written null-checks work but don't scale and are exactly what a mapping generator is for.
2. **Deriving a modified copy of an immutable value** (a record/DTO you don't own the persistence lifecycle of — e.g. a
   filter object, a config snapshot, a test fixture). Here mutation isn't possible or desirable — you want a *new*
   instance with one field changed. This is the actual
   `with...`/builder-copy pattern.

**Solution:**

**For case 1 (entity from request DTO), add MapStruct** (`mapstruct` + `mapstruct-processor` +
`lombok-mapstruct-binding` in `pom.xml` — not present today) and use `@MappingTarget` with null-ignoring semantics:

```java

@Mapper(componentModel = "spring")
public interface CompanyMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromRequest(CompanyRequest request, @MappingTarget Company entity);

}
```

```java

@Transactional
public void updateCompany(Long id, CompanyRequest request) {
  Company company = companyRepository.findById(id).orElseThrow(...)
  companyMapper.updateFromRequest(request, company);
  // no explicit save() call needed — dirty checking flushes it at commit
}
```

This is a strict upgrade over both existing approaches: it removes Company's repetitive null-checks *and* fixes
Receipt's bug, with the same one-line annotation, for every field, forever — a new field added to the record is
automatically handled correctly with no code change to the mapper.

**A caveat worth knowing before you rely on `IGNORE`-strategy PATCH everywhere:** `null` in the request DTO is being
used to mean two genuinely different things, and this pattern conflates them — *"the client didn't send this field,
leave it alone"* vs. *"the client explicitly wants this field cleared to null."* A plain Java field can't distinguish
"absent from the JSON body" from "present and
`null`" once Jackson has deserialized it — both arrive as `null` on the record/DTO. `IGNORE` picks the first
interpretation always, which is the right call for the overwhelming majority of PATCH endpoints (you almost never want
to clear a company's name to null, say) — but if a field genuinely needs to be clearable via PATCH (e.g. removing an
optional `dueDate`), `IGNORE` will silently refuse to do it. If that case actually comes up, the standard fixes are:
wrap that one field in a tri-state wrapper type (`JsonNullable<T>` from `org.openapitools:jackson-databind-nullable`,
which Jackson deserializes into
"absent"/"null"/"present with value") instead of a raw type, or model the update as a full JSON Merge Patch (RFC 7396) /
JSON Patch (RFC 6902) body instead of a regular DTO. Don't reach for either until a concrete field actually needs "clear
this" semantics — for everything else, `IGNORE` is the right, simpler default.

**For case 2 (immutable copy-with-change), use `@Builder(toBuilder = true)`** — Lombok supports this on both classes and
records:

```java

@Builder(toBuilder = true)
public record InvoiceFilter(String status, LocalDate from, LocalDate to) {

}

InvoiceFilter paidOnly = baseFilter.toBuilder().status("PAID").build();
```

This *is* the `with...`-style pattern you were picturing — it just belongs on immutable value types, not on JPA entities
(an entity should be mutated in place and left to dirty-checking, not replaced).

**When to use:** Introduce MapStruct the next time you touch `ReceiptServiceImpl.updateReceiptById`
(it's an actual bug, worth prioritizing) or `CompanyServiceImpl.updateCompany`. Reach for
`toBuilder = true` when you need a modified copy of a record/value object, not for entity updates.

---

## 4. Value objects — extend `MonetaryAmount`/`OrganizationDto` to the entity side

**Problem:** `shared/MonetaryAmount.java` (immutable record pairing `priceWithoutVAT`/`priceWithVAT`)
and `OrganizationDto` (IČO/DIČ/IČ DPH) already exist as proper value objects — but they're only used in
`InvoiceResponse` (the outbound read model). The actual `Invoice` entity and `InvoiceRequest`/
`ReceiptRequest` still carry raw `BigDecimal totalPriceWithoutVat`/`totalPriceWithVat` fields with no associated
currency field at all (36 raw `BigDecimal` occurrences across invoice/receipt models vs. 4 files using
`MonetaryAmount`).

**Why this approach:** A value object earns its keep by making an invalid state unrepresentable and by being reusable
behavior (e.g. `MonetaryAmount.add()`, currency-mismatch checks) instead of scattering the same "two amounts + implicit
currency" convention across every entity/DTO that touches money. Using it only on the response side means the
entity/request layer — where the actual business rules should live — gets none of that benefit.

**Solution:** When next touching `Invoice`/`Receipt`'s persistence model, consider an `@Embeddable`
version of `MonetaryAmount` for the entity side (JPA embeddables are the standard way to give a value object first-class
column mapping without a separate table), and use the same record on the request DTOs. Not urgent — flagging the
inconsistency, not proposing an immediate migration.

**When to use:** Next schema change that touches invoice/receipt monetary fields, not as a standalone drive-by migration
(touches persisted columns, needs a migration + data backfill plan).

---

## 5. Transaction boundaries — no external I/O inside `@Transactional`, use `readOnly` on reads

**Problem:** `Transactional(readOnly = ...)` is used **zero times** anywhere in the codebase, despite class-level
`@Transactional` covering pure read methods too (e.g. `InvoiceServiceImpl.findById`). More seriously:
`InvoiceServiceImpl` is class-level `@Transactional`, and its `save()` method calls
`invoiceExtractor.extract(...)` (an external LLM HTTP call, `InvoiceServiceImpl.java:198`) and
`fileStorageService.saveInvoiceDocument`/`deleteBatchDocuments` (R2/S3 HTTP calls) — all **inside** the open
transaction. This holds a DB connection open for the duration of a network call to a third-party service, and a DB
rollback can't undo the already-executed external side effect (the file is already uploaded to R2, or the LLM call
already happened, even if the surrounding DB write fails afterward). Only 4 of ~27 services use class-level
`@Transactional` at all, with no documented rule for when.

**Why this approach:** `@Transactional` should bound exactly the unit of work that must be atomic at the database
level — nothing more. Widening it to cover an HTTP call couples DB connection lifetime to network latency (connection
pool exhaustion risk under load) and creates a false sense of atomicity:
the external call isn't part of the DB transaction and can't be rolled back with it.

**Solution:**

- Mark pure-read service methods `@Transactional(readOnly = true)` — lets Hibernate skip dirty-checking and lets the
  connection pool/driver optimize (some drivers route read-only transactions to replicas).
- Move external I/O (HTTP calls, R2/S3, LLM extraction) **outside** the transactional boundary: do the external call
  first, then open a short `@Transactional` method just for the DB write (s) that depend on its result. If the external
  call fails, nothing was written; if the DB write fails after a successful external call, handle that as its own
  compensating-action concern (e.g. delete the already-uploaded R2 object) rather than relying on transaction rollback
  to do it for you.
- Put `@Transactional` at the method level, not the class level, so it's obvious at each call site whether a DB
  transaction is actually open — a class-level annotation makes every public method transactional by default, including
  ones that shouldn't be.

**Example:** `InvoiceServiceImpl.save()` should look roughly like: extract via `invoiceExtractor` (no transaction
open) → open a `@Transactional` method that persists the `Invoice` + line items → upload the document to R2 (no
transaction open) → if the upload fails, delete the just-created DB row (or mark it as needing a re-upload) rather than
hoping a surrounding transaction protects you.

**When to use:** Every new service method. Retrofitting `InvoiceServiceImpl` is worth prioritizing — it's the one place
doing this today, and it's on the hot path (invoice creation with AI extraction).

---

## 6. Exception handling — always route through `GlobalExceptionHandler`

**Problem:** `GlobalExceptionHandler` (`core/exception/GlobalExceptionHandler.java`) exists with ~18
`@ExceptionHandler` methods and is generally used correctly — but two controllers bypass it:

- `ReceiptController.createReceipt` (`ReceiptController.java:51-71`) catches `LimitExceededException`
  and generic `Exception` locally, hand-building `ResponseEntity.status(429).build()` /
  `.status(500).build()` with an **empty body**, while `GlobalExceptionHandler` already has a handler for the same
  exception that returns a message body — same error, different response shape depending on which endpoint threw it.
- `PohodaController.java:30-48` catches generic `Exception` and puts `e.getMessage()` directly into the HTTP response
  body — an information-disclosure smell (internal exception messages, stack details, or library-specific text reaching
  the client) — and contains ~14 lines of dead, commented-out endpoint code that looks like an abandoned duplicate of
  `ReceiptController`'s own `/export/pohoda` endpoint.

**Why this approach:** A single global handler is only useful if every path actually goes through it — the moment
individual controllers start catching-and-formatting locally, callers can no longer rely on one consistent error shape,
and it's easy to accidentally leak internals (as `PohodaController` does)
in the one-off path that a shared handler would have redacted.

**Solution:** Let exceptions propagate; don't catch `Exception` in a controller. If a controller genuinely needs to
react to a specific exception before it reaches the client (rare), do it in the service layer or add/extend a handler in
`GlobalExceptionHandler` instead of a local `try/catch`. Never put `e.getMessage()` into a response body for an
unexpected exception — return a generic message and log the real one server-side.

**When to use:** Fix `ReceiptController.createReceipt` and `PohodaController` next time either is touched;
`PohodaController` in particular looks worth deleting outright if it's dead code duplicating
`ReceiptController`'s Pohoda export.

---

## 7. Service layer — gaps beyond what `CLAUDE.md` already covers

`CLAUDE.md`/`docs/patterns/java-services.md` already document the intended shape
(`EntityServiceImpl implements EntityService`, constructor injection, no `static`, method naming by intent). Beyond that
documented shape, the audit found:

- **Missing interfaces:** `InvoiceXmlServiceImpl` (`invoice/pohoda/service/`) and
  `AccountingDocumentServiceImpl` have no matching interface — class-only, injected by concrete type elsewhere, breaking
  the "code to an interface" convention the rest of the codebase follows (25/27 services do have one).
- **Naming drift:** `EmailSubscribeImpl` implements `EmailSubscribeService` but isn't named
  `EmailSubscribeServiceImpl` — breaks the pattern by one word, easy to fix opportunistically.
- **Fat services:** `InvoiceServiceImpl` has 14 injected dependencies (repositories, mappers, the extractor, two other
  services, a registry, `ObjectMapper`) — a strong signal it's doing more than one job (persistence + AI extraction +
  document handling + PDF generation). Worth splitting extraction concerns into their own service the next time this
  class needs a non-trivial change, not as a standalone refactor.
- **Duplicate injection bug:** `ReceiptServiceImpl` has both `documentRepository` and
  `receiptDocumentRepository` fields — same `ReceiptDocumentRepository` type injected twice under two names (lines 83,
  85). Dead weight, easy one-line cleanup.
- **Leftover field/constructor `@Autowired`:** a handful of classes (`PohodaInvoiceMapper`,
  `InvoiceController`, `StripeController`) still use explicit `@Autowired` instead of Lombok's
  `@RequiredArgsConstructor` — redundant since Spring 4.3 auto-wires a single constructor, and inconsistent with the
  rest of the codebase.

**When to use:** All of the above are opportunistic fixes — address them when you're already touching that class, per
the same rollout philosophy as `CLAUDE.md`'s other sections.

---

## 8. Persistence — fetch strategy, cascades, indexes, bulk operations

**Problem — fetch strategy defaults to EAGER where it shouldn't:** `@ManyToOne` fields are largely left unannotated
(`Invoice.company`, `Receipt.company`), which means Hibernate's *default* for
`@ManyToOne` — `FetchType.EAGER` — applies. Only a few associations explicitly set `LAZY`
(`InvoiceVatBreakdown.invoice`, `ReceiptDocument.receipt`, `InvoiceDocument.invoice`). Separately,
`User.featureIds` is `@ElementCollection(fetch = FetchType.EAGER)` — an extra query fires on *every*
`User` load whether or not features are needed. No `@EntityGraph`/`JOIN FETCH`/DTO projections exist anywhere to fetch
exactly what's needed for a given use case.

**Why this approach:** JPA's default `EAGER` for `@ManyToOne`/`@OneToOne` is widely considered a mistake in the spec
itself — it means every load of an `Invoice` also loads its `Company` whether or not the caller needs it, and it
composes badly (eager associations chain into more eager associations). Explicit `LAZY` everywhere, with `@EntityGraph`
or a `JOIN FETCH` query added *only* where a specific use case genuinely needs the association eagerly, keeps each
query's cost visible and intentional.

**Solution:** Add `fetch = FetchType.LAZY` explicitly to every `@ManyToOne`/`@OneToOne`. Where a list endpoint needs the
parent's data too (e.g. showing `company.name` in an invoice table row), add a targeted
`@EntityGraph(attributePaths = "company")` on that specific repository method rather than leaving the association
globally eager. (Today's list endpoints happen to map to scalar-only response DTOs and don't touch other lazy
associations in a loop, so there's no classic per-row N+1 today — this is about controlling cost going forward, not an
active bug.)

**Problem — cascade inconsistency:** `Invoice.documents` correctly has no cascade (R2 deletion is app-coordinated:
`InvoiceServiceImpl.deleteInvoice` deletes the R2 object before removing the row).
`Receipt.documents` still has `cascade = CascadeType.ALL, orphanRemoval = true` — structurally the same external-storage
situation as `Invoice.documents`, just not yet fixed. Today's only delete path (`ReceiptServiceImpl.deleteReceiptById`)
happens to delete the R2 object manually first, so it's not currently broken — but the entity mapping itself would
silently orphan an R2 object the moment any future code does `receipt.getDocuments().remove(x); save(receipt)` instead
of going through that service method.

**Solution:** Remove `cascade`/`orphanRemoval` from `Receipt.documents`, mirroring the `Invoice.documents`
fix, so the entity mapping itself can't produce an orphaned R2 object regardless of call path.

**Problem — missing indexes on filter columns:** `V25__IS_235.sql` retrofitted indexes on FK columns (`created_by`,
`company`) after the fact, but no index exists on `invoice_status`/`receipt_status`
despite being used as Specification filter predicates.

**Solution:** Add a migration indexing the status columns once list-filtering by status is a real, measured hot path
(don't index speculatively on tables that are still small).

**Problem — inconsistent `@Modifying` cache invalidation:** `RefreshSessionRepository.consumeByTokenHash`
correctly uses `@Modifying(clearAutomatically = true)` to avoid stale first-level-cache reads after a bulk JPQL update,
but `revokeFamilyByFamilyId`/`revokeAllByUserId` in the same repository use bare
`@Modifying` — reintroducing the same stale-read risk if a caller reads a `RefreshSession` in the same transaction right
after calling either.

**Solution:** Add `clearAutomatically = true` to every `@Modifying` query in that repository for consistency, unless a
specific one is proven never to be followed by a same-transaction read.

**When to use:** Fetch-type explicitness — every new/touched association. Cascade fix — worth doing soon given it's the
same bug class already fixed once for Invoice. Indexes/`clearAutomatically` — opportunistic.

---

## 9. Multi-tenancy / ownership — remaining gaps and a generalizable rule

The custom `@RequiresOwnership`/`@Owned*` AOP system (`modules/auth/security/`) is the right architecture and is
correctly applied to most of Company/Invoice/Receipt/PostingAccount/XML-import. As of this audit:

**Still open (confirmed against current code):**

- `FeatureController.getAllFeatures` (`feature/controller/FeatureController.java:26-28`) — raw
  `@PathVariable UUID userId`, no ownership check at all; any authenticated user can read another user's feature flags
  by UUID.
- `PostingAccountServiceImpl.savePostingAccount` (`postingaccount/service/PostingAccountServiceImpl.java:208-210`)
  — `request.companyId()` used unchecked, `//TODO check ownership` comment still present.
- **New finding:** `DataTransferServiceImpl.findById`/`saveMapping`/`downloadConvert`
  (`datatransfer/service/DataTransferServiceImpl.java:129-161`) — no `@Owned*` annotation and no manual `createdBy`
  check, despite `Authentication connectedUser` being available and unused in two of the three methods. Same exploitable
  shape as the two known gaps above: view/edit/download another user's data-transfer import by ID.

**Already fixed since the last audit:** `XmlFileServiceImpl.processAndGenerateZip` now has
`@RequiresOwnership`/`@OwnedXML`.

**A generalizable rule going forward, not just a punch list:** every service method that takes an entity ID as a
parameter and is reachable by an authenticated (non-admin) user needs either an
`@Owned*` annotation or an explicit `createdBy` check — no exceptions, because the failure mode (IDOR)
is silent and only shows up when someone actually tries IDs that aren't theirs. When adding a new endpoint that takes an
ID, treat "which `@Owned*` annotation does this need" as a mandatory question, the same way you'd ask "does this need
`@Valid`."

**Self-invocation AOP anti-pattern — one latent instance found:** `ReceiptServiceImpl.saveReceipt`
calls `uploadReceiptDocument(...)` internally (`this.` call bypasses Spring's proxy-based AOP, so that method's own
`@OwnedReceipt` check never fires for this call path). Harmless today only because the
`receiptId` passed was just created in the same transaction — but it means `uploadReceiptDocument`'s annotation gives
false confidence if the method is ever called from a different, untrusted path later. **Rule:** when a method is
annotated `@RequiresOwnership`, never rely on it when calling that method via
`this.` from inside the same class — either inline the check, or move the annotated method to a different bean and
inject it.

**Rate limiting gap:** login has brute-force protection (`BruteForceProtectionServiceImpl`, 5 attempts / 15 min,
username-based) but `/auth/register` and `/auth/forgot-password` have none — worth adding before those become an abuse
vector (registration spam, password-reset email flooding).

**When to use:** The three open IDOR gaps are worth fixing before wider launch — they're the same class of bug already
fixed for Company/Invoice/Receipt, just not yet applied to these three call paths.

---

## 10. Observability & testing — where the risk actually is

**Problem — Sentry silently misses handled exceptions:** Sentry is wired via the Spring/logback integration and captures
uncaught exceptions and explicit `log.error(...)` calls, but
`GlobalExceptionHandler`'s ~18 `@ExceptionHandler` methods all catch-and-return without logging or calling Sentry —
meaning validation errors, not-found errors, invalid-token errors, and account-locked errors (i.e. almost everything
client-facing) are invisible to Sentry. Only genuinely uncaught exceptions and explicit `log.error` calls show up, which
under-represents how often things are actually going wrong in production.

**Solution:** Decide deliberately which handled-exception categories are worth Sentry visibility (e.g.
`InvalidTokenException` reuse-detection events, `AccountLockedException` spikes are useful signals; a routine 404
usually isn't) and add `log.warn`/`Sentry.captureException` calls for those specific handlers rather than all 18.

**Problem — correlation ID doesn't reach console logs:** `RequestIdFilter` correctly puts a request ID into MDC and it
reaches Sentry via `sentry.context-tags=requestId`, but no `logback-spring.xml` or
`logging.pattern.*` override exists, so Spring Boot's default console pattern never interpolates
`%X{requestId}` — the correlation ID is invisible in local/console logs, only in Sentry.

**Solution:** Add a `logback-spring.xml` (or `logging.pattern.console` property) that includes
`%X{requestId}` in the console appender pattern.

**Problem — testing is at smoke-test level:** 3 test files for 319 main source files. No test exists for the
`OwnershipAspect`/`@RequiresOwnership` behavior (the exact mechanism protecting tenant isolation) or for
`RefreshSessionServiceImpl`'s rotation/reuse-detection logic (the exact mechanism protecting session security) — both
are pure-logic-plus-security-critical and currently regress silently if broken.

**Solution — priority order, not "write more tests" generically:** a Testcontainers-backed
`@SpringBootTest` that registers two real users via `/auth/register`, logs both in, and asserts cross-user 404s on
Company/Invoice/Receipt/PostingAccount endpoints would catch any future regression of the ownership system in one
place — this is higher leverage than unit-testing individual services, because it tests the actual AOP wiring, not a
mock that would bypass exactly the behavior being verified. A second test exercising refresh-token reuse detection
(rotate twice with the same stale token, assert the whole family gets revoked) covers the other security-critical,
currently-untested mechanism.

**Lower-priority, worth noting:**

- `springdoc-openapi` is wired but `OpenApiConfig` has placeholder values (`"Licence name"`,
  `https://google.com` as the prod server URL, a personal Gmail contact) and zero `@Operation`/`@Tag`
  annotations anywhere — fine to leave until the API is actually shared externally, but don't forget it's there when
  that day comes.
- Config style is inconsistent: only 2 `@ConfigurationProperties` classes (`StripeConfig`,
  `RefreshCookieProperties`) vs. 21 scattered `@Value` injections elsewhere. `@ConfigurationProperties`
  groups related settings and gets validation/type-safety for free — worth preferring for any new config that's more
  than one or two values.
- 9 leftover `System.out.println` calls in production code paths (not test/debug-only) — replace with
  `@Slf4j` logging or delete.

**When to use:** The two test additions (ownership AOP, refresh-token rotation) are the highest-value next testing
investment in this codebase specifically because they guard security-critical behavior that's currently invisible to any
automated check.

---

## 11. Cross-cutting SaaS fundamentals not yet in place

Not urgent bugs, but worth knowing they're absent so it's a deliberate choice, not an oversight:

- **No caching** anywhere (`@Cacheable`, Redis) — every lookup, including things that rarely change (feature flag
  catalog, the `partners.xml`-backed companies registry), hits the DB/file fresh every call. Fine at current scale;
  revisit if either becomes a measured hot path.
- **No async/scheduled jobs** — zero `@Async`/`@Scheduled` usage anywhere. Concretely, email sending
  (`EmailVerificationServiceImpl`) runs synchronously inside the registration request — a slow mail provider directly
  slows down every registration. Worth making fire-and-forget (`@Async`) once it matters for latency.
- **No optimistic locking (`@Version`)** anywhere — in particular, Stripe subscription state has no guard against two
  concurrent webhook deliveries racing on the same row. The one place that does need concurrency safety today
  (refresh-token consumption) already solved it with a conditional atomic
  `UPDATE ... WHERE revoked = false AND expires_at > now()`, which is a reasonable pattern to reuse for Stripe state if
  a concrete race is ever observed.

**When to use:** Introduce each of these when a concrete need shows up (a real latency complaint, a real caching win, a
real observed race condition) — none of these are worth building speculatively.

---

## Where to start — top 5, ranked by risk/effort

1. **Fix `ReceiptServiceImpl.updateReceiptById`** (§3) — it's a live data-loss bug behind a `PATCH`
   endpoint today, not just a style issue, and the fix is small even before introducing MapStruct.
2. **Close the 3 remaining IDOR gaps** (§9: `FeatureController`, `PostingAccountServiceImpl.savePostingAccount`,
   `DataTransferServiceImpl`) — same fix shape already applied elsewhere in the codebase, just not yet here.
3. **Fix `Receipt.documents` cascade** (§8) — one annotation change, closes the same latent-orphan bug class already
   fixed once for `Invoice.documents`.
4. **Move R2/LLM calls outside `@Transactional` in `InvoiceServiceImpl`** (§5) — the highest-traffic write path in the
   app is holding a DB connection open across third-party network calls today.
5. **Add the two security-critical tests** (§10: ownership AOP cross-user test, refresh-token reuse test) — the two
   mechanisms protecting tenant isolation and session security have zero automated coverage right now.
