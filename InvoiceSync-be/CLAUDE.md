# InvoiceSync-be Conventions

## Service layer

Service tier hierarchy (Base/Management/Integration, when to split), service implementation shape (class declaration,
DI, fields, method ordering) — see
[`docs/patterns/java-services.md`](docs/patterns/java-services.md). Short version: every service today is a Single Service
(`EntityServiceImpl implements EntityService`), constructor-injected via Lombok,
`jakarta.transaction.Transactional`, no `static` methods, no bare `repository` field name.

## Repository method ordering

**Status:** State of the Art

**Problem:** `JpaRepository` interfaces accumulate derived-query methods (`findBy...`,
`existsBy...`, `countBy...`, `deleteBy...`) in whatever order they were added, with no discernible grouping — e.g.
today's `UserRepository` mixes `findByUsernameIgnoreCase`,
`existsByUsernameIgnoreCase`, `findById`, `existsByRegistrationNumber`, `existsByEmailIgnoreCase`,
`findByRoleNot`, `findByEmailIgnoreCase` in that literal order. Finding a specific method means reading the whole
interface top to bottom.

**Why this approach:** Repository methods are declarations, not implementations — there's no method body where a reader
could miss something important buried in the middle (unlike a Service, where
`delete*` is deliberately placed last so it doesn't get skimmed past — see
[Service layer](#service-layer)). That removes the reason to group repository methods semantically. Alphabetical order
is the one ordering an IDE can enforce and fix automatically (IntelliJ: Code Style → Java → Arrangement, or *Code →
Rearrange Code*), so it stays correct without manual discipline — a semantic CRUD-shaped order would require
understanding each method's meaning to place it, which no tool can do for you.

**Solution:** Order every `Repository` interface's methods alphabetically by method name. Inherited/overridden methods
from `JpaRepository` (e.g. `findById`) sort in wherever their name would put them, same as any other method.

**Example:**

```java
public interface UserRepository extends JpaRepository<User, UUID> {

  Boolean existsByUsernameIgnoreCase(String username);

  boolean existsByEmailIgnoreCase(String email);

  boolean existsByRegistrationNumber(String registrationNumber);

  Optional<User> findByEmailIgnoreCase(String email);

  Optional<User> findById(UUID id);

  Page<User> findByRoleNot(Role role, Pageable pageable);

  Optional<User> findByUsernameIgnoreCase(String username);

}
```

**When to use:** Every `Repository` interface, new or existing. Reordering an existing one you're already touching is
encouraged, not a required drive-by change.

**See also:** [Service layer](#service-layer) (contrast: why Services use semantic ordering instead).

**Status:** State of the Art

**Problem:** It's tempting to name a method after *how* it's implemented (e.g. `markUserAsDeleted`
because it flips a boolean flag). That leaks an internal detail into the public API — if the implementation changes
(e.g. soft delete becomes a real row delete, or gains a grace period), every caller's mental model of the name breaks
even though nothing about the *behavior* changed.

**Why this approach:** A method name is a contract with its caller. The caller needs to know what the method
accomplishes for them, not which fields it happens to touch. Naming by intent keeps the name stable across refactors and
makes the method self-documenting at the call site.

**Solution:** Name by outcome/intent, not mechanism.

| Situation                                                                                                                                                     | Convention                                                                                                                                               | Example (this repo)                                                                                                                                                     |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Reads a collection                                                                                                                                            | `list*`                                                                                                                                                  | —                                                                                                                                                                       |
| Reads one entity, may be absent                                                                                                                               | `get*`, returns `Optional<T>` (or throws a domain exception at the boundary, see below)                                                                  | `UserServiceImpl.getUserInfo`                                                                                                                                           |
| Boolean question                                                                                                                                              | `is*` / `has*`, no side effects                                                                                                                          | `User.hasFeature`, `ReceiptDocument.isCanDelete`                                                                                                                        |
| Creates a new row                                                                                                                                             | `save`, `register`, `create`                                                                                                                             | `AuthServiceImpl.registerUser`                                                                                                                                          |
| Removes an entity for good (the only removal mechanism for that entity)                                                                                       | `delete<Entity>` — used even when the implementation happens to be a soft delete under the hood, as long as it's the *only* way that entity gets removed | `deleteInvoice`, `deleteReceipt`, `deletePostingAccount`, `deleteCompany`                                                                                               |
| Removal is reversible / the entity can come back                                                                                                              | `deactivate*` / `disable*` / `close*` instead of `delete*` — the reversibility is worth naming explicitly                                                | (none yet — applies to a future "reactivate account" flow)                                                                                                              |
| Two removal mechanisms coexist (e.g. admin hard-delete vs user soft-delete)                                                                                   | Disambiguate explicitly: `softDelete*` vs `delete*`                                                                                                      | (not needed yet — only add this split when a second mechanism actually exists)                                                                                          |
| Nothing is actually removed from the DB — a specific column is nulled out to erase the value while the row itself must stay (e.g. legally required retention) | `clear<Field>` — name the field being nulled, not the row                                                                                                | (none yet — applies to a future PII-scrub-on-`deactivateAccount` flow, since invoices/receipts must be retained ~10 years by law and can't be deleted with the account) |

**Anti-pattern:** `mark<Entity>As<State>` (e.g. `markUserAsDeleted`). It names the field write instead of the action.
Prefer `delete*`/`deactivate*` per the table above — pick based on whether the action is reversible and whether it's the
only removal path for that entity.

**When to use:** Every new public method on a service/controller. When touching an existing method that violates this
(like `UserService.markUserAsDeleted`), renaming it is encouraged when you're already in that code, not required as a
drive-by change.

**See also:** [Service layer](#service-layer) → `docs/patterns/java-services.md` (method *ordering*
within a class — naming and ordering are separate concerns).

## Null-safety (JSpecify)

**Status:** Acceptable today (per-class), migrating to State of the Art (per-package) — see rollout below.

**Problem:** By default, Java gives no compile-time signal about which references can be `null`. Every parameter, return
value, and field is a potential `NullPointerException` unless the reader digs through the implementation (or the
Javadoc, if it exists and is trustworthy). This is exactly the kind of thing a compiler/IDE should catch instead of a
production log.

**Why this approach:** [JSpecify](https://jspecify.dev) (`org.jspecify.annotations`) is the industry-standard,
tool-agnostic nullness annotation set — it's what Spring Framework itself migrated to (Spring 6.2+), it ships
transitively on the classpath via `spring-boot-starter-parent`
in this project already (no extra `../pom.xml` dependency needed — that's why `@NullMarked` already compiles in the
`auth`
module), and it's understood natively by IntelliJ, Error Prone's NullAway, and other static analysis tools. The
alternative — no annotations, or a framework-specific set like `javax.annotation`/`org.springframework.lang` — either
gives no tooling benefit or locks you into one vendor.

**Solution:**

- **Default every reference to non-null**, and only mark the exceptions. This is what `@NullMarked`
  does for everything in its scope: unannotated types are treated as non-null, and only fields, parameters, and return
  types explicitly annotated `@Nullable` may hold `null`.
- **Apply `@NullMarked` per package, via a `package-info.java`**, not per class:
  ```java
  // src/main/java/com/invoicesync/modules/user/package-info.java
  @NullMarked
  package com.invoicesync.modules.user;

  import org.jspecify.annotations.NullMarked;
  ```
  This is the granularity JSpecify's own docs recommend, because it's easy to forget the annotation on a *new* class
  under per-class opt-in (silently falling back to unannotated/unsafe), whereas a package-level declaration covers every
  class added later for free. The `auth` module's current per-class `@NullMarked` (`AuthServiceImpl`,
  `EmailVerificationServiceImpl`, ...) works, but is the more error-prone variant.
- **Mark `@Nullable` only where `null` is a real, intended value** — e.g. an optional field that's genuinely absent
  until some later step (`User.googleAccessToken` before Google is connected). Don't reach for `Optional<T>` and
  `@Nullable` for the same thing: `Optional` is for return types (`get*` methods, see [Method naming](#method-naming)),
  `@Nullable` is for fields/parameters.
- **Lombok gotcha:** Lombok does **not** copy a field's `@Nullable` annotation onto the getter/setter it generates
  unless told to. Without that, a `@Data` entity like `User` under
  `@NullMarked` would generate getters that lie about nullability. Fix once, project-wide, via a
  `lombok.config` at the repo root (doesn't exist yet):
  ```properties
  lombok.copyableAnnotations=+= org.jspecify.annotations.Nullable
  ```
- **Constructor/field injection stays non-null** — that's the common case and needs no annotation under `@NullMarked`;
  only annotate the rare `@Nullable` dependency.

**Rollout:** Don't do this as one big-bang PR across all 319 source files — a codebase-wide null-audit done in a hurry
just produces `@Nullable` sprinkled defensively everywhere (or worse, wrong `@NullMarked` packages that don't actually
hold under scrutiny) instead of an accurate model. Go module by module: add `package-info.java` with `@NullMarked`, then
let the compiler/IDE surface every spot that now needs a real `@Nullable` decision, resolve those, and only then move to
the next module. Drop the now-redundant class-level `@NullMarked` in `auth` once that module's package-info is in place.

**When to use:** Every new package from now on gets a `package-info.java` with `@NullMarked` from day one. Existing
packages migrate opportunistically — when you're already touching a module for other work, add its `package-info.java`
as part of that change, not as a drive-by across the repo.

**See also:** [JSpecify user guide](https://jspecify.dev/docs/user-guide/).

## API endpoint paths

**Status:** Acceptable (existing `Api.java` usages) → Deprecated for new code — new endpoints declare their path inline
in the controller.

**Problem:** `core.Api` is a single flat class holding every route string for the whole application. It has no
cohesion — grouping is only comments — grows without bound, and every new endpoint edits the same file (merge
conflicts). It's also already drifted: inconsistent path-variable casing across modules
(`RECEIPT_GET_BY_ID = "/{receipt-id}"` vs `RECEIPT_UPDATE = "/update/{receiptId}"`), and near-duplicate constants for
unrelated things (`GET_BY_USER = "/user"` vs `USER = "/user"`).

**Why this approach:** Spring's mapping annotations require compile-time constants either way, so declaring the path
directly in the controller that owns it costs nothing and keeps the route and the code using it in the same place — no
jumping between files to understand one endpoint, and a new controller touches no shared file. A hand-maintained "list
all routes in one place" file also duplicates what tooling already gives for free and accurately: springdoc/OpenAPI
generates a full API overview straight from the live `@GetMapping`/`@PostMapping` annotations, so it can't drift the way
a manually kept list can.

**Solution:** Declare the path as a `private static final String` (or inline literal, for a one-off) on the controller
itself. Pull a constant out to a shared location only when something *outside* that controller genuinely needs it (a
test, a security matcher, another controller).

**Example:**

```java

@RestController
@RequestMapping(Api.USER)
public class UserController {

  private static final String DEACTIVATE_ACCOUNT = "/deactivate";

  @PostMapping(DEACTIVATE_ACCOUNT)
  public void deactivateAccount(final Authentication connectedUser) { ...}

}
```

**When to use:** Every new controller/endpoint. `Api.java`'s existing entries stay as-is — no drive-by migration — but
don't add new constants to it; put new ones on the controller instead.

## Backend best practices & audit

Three companion docs, general → specific → applied, in
[`docs/best-practices/`](docs/best-practices/):
[`general.md`](docs/best-practices/general.md) is a general SaaS-backend reference (API design,
multi-tenancy models, CI/CD, compliance, etc.) independent of this codebase.
[`audit.md`](docs/best-practices/audit.md) is a full-codebase audit against those principles (record
vs. class, partial-update pattern, transaction boundaries, entity/Lombok pitfalls, persistence and ownership gaps),
file:line grounded.
[`new-module-template.md`](docs/best-practices/new-module-template.md) is a complete worked example (migration → entity → DTOs →
repository → mapper → ownership wiring → service → controller → test) showing every convention applied correctly at
once, explained — start here when building anything brand new.
[`code-skeleton-template.md`](docs/best-practices/code-skeleton-template.md) is the same shape as pure copy-paste code with placeholder
names (`Entity`/`Parent`), no explanation — use it once you already know the *why* and just want to move fast. Sections
get promoted into this file once they're consistently followed, per this file's own rule of pointing to a pattern doc
rather than carrying the full weight here.
