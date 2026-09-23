# Service Layer Patterns

## Service Tier Hierarchy

**Status:** Single Service is the current reality for every service in InvoiceSync-be — the tiers below are decision
criteria for when to graduate, not a description of today's code.

**Problem:** Service classes grow large when a single class handles reads, mutations, UI workflow logic, and
cross-domain APIs all at once. Responsibilities blur, circular dependencies creep in, and it becomes unclear where a new
method belongs.

**Why this approach:** Splitting by responsibility — not by entity subdivision — keeps each class focused. The base
service owns entity-scoped CRUD and is widely injectable. The management service handles cross-package coordination, UI
workflow, and business logic that spans multiple services. An integration service gives other domains a stable API
surface without exposing internal workflow details.

**Solution:** Up to three service tiers per entity, adopted only as complexity actually demands it:

| Tier                    | Responsibility                                                                      | Naming                                                      | Injected by                            |
|-------------------------|-------------------------------------------------------------------------------------|-------------------------------------------------------------|----------------------------------------|
| **Base Service**        | Entity-scoped CRUD: reads, lookups, and direct mutations on the entity              | `EntityService` / `EntityServiceImpl`                       | Anyone in the owning module            |
| **Management Service**  | Cross-package coordination, UI workflows, DTO assembly, higher-level business logic | `EntityManagementService` / `EntityManagementServiceImpl`   | Controllers, other management services |
| **Integration Service** | Stable API surface for other domains                                                | `EntityIntegrationService` / `EntityIntegrationServiceImpl` | Other domains' management services     |

**Example:** No entity in InvoiceSync-be needs the full three-tier split yet — every service today is the Single Service
Variant (see below). Treat the tiers as the target shape a service graduates into, not a pattern to retrofit
speculatively.

**When to use:**

```
Does this entity need a stable API for other domains?
  YES → Three-tier (Base + Management + Integration)
  NO  → Does it have cross-package coordination, UI workflows, or DTO assembly beyond plain CRUD?
          YES → Two-tier (Base + Management)
          NO  → Single service
```

When in doubt, start with a single service. Add a Management tier when UI workflows or DTO assembly start crowding the
CRUD methods. Add an Integration tier only when a second domain actually starts calling in — that's the signal, not
anticipation of it.

**See also:** [Service Dependency Direction](#service-dependency-direction)

---

## Service Tier Hierarchy: Base Service

**Status:** State of the Art — this is what every current InvoiceSync-be service is.

**Problem:** Which methods belong in the base service vs other tiers?

**Why this approach:** The base service owns direct CRUD for the entity. It's the most widely injected tier and should
be usable within the module without pulling in cross-package concerns or UI workflow logic.

**Solution:** Base service encapsulates entity-related CRUD — reads **and** direct mutations on the entity itself.

| Belongs here                             | Does NOT belong here                              |
|------------------------------------------|---------------------------------------------------|
| `list*`, `get*`, `exists*`, `count*`     | DTO assembly beyond a straight entity→DTO mapping |
| `save`, `update`, `delete` on the entity | Cross-package coordination or orchestration       |
| Lookups used by other tiers              | Result wrappers with validation errors            |

**Example:** `UserServiceImpl`, `AuthServiceImpl`, `PostingAccountServiceImpl`,
`CompanyServiceImpl` — every service in the codebase today.

**When to use:** Every entity with a service has at least this tier.

**See also:** [Service Tier Hierarchy](#service-tier-hierarchy)

---

## Service Tier Hierarchy: Management Service

**Status:** Acceptable — not yet needed anywhere in InvoiceSync-be, but the criteria below tell you when a growing base
service should split.

**Problem:** Which methods belong in the management service?

**Why this approach:** Cross-package coordination, UI workflow, and DTO assembly involve composing multiple services and
domain concepts. Grouping them here keeps controllers thin and keeps the base service focused on entity CRUD.

**Solution:** Management service handles cross-package coordination, UI-facing data preparation, and business logic that
coordinates multiple entity operations.

| Belongs here                                             | Does NOT belong here                                  |
|----------------------------------------------------------|-------------------------------------------------------|
| Higher-level mutations that coordinate multiple services | Simple entity CRUD (→ Base Service)                   |
| `create` workflows spanning more than one entity         | Read-only entity lookups (→ Base Service)             |
| DTO mapping and calculation logic that spans domains     | Methods used by other domains (→ Integration Service) |

**Example:** None in InvoiceSync-be yet. If `UserServiceImpl` ever grows a workflow like "create user + provision
subscription + send welcome email" as one coordinated operation, that's the signal to extract
`UserManagementServiceImpl`.

**When to use:** When a service's UI workflows or cross-package coordination start crowding out its plain CRUD methods.

**Field naming, once this tier exists:** the injected Base Service is named `service` inside the Management Service
(e.g. `UserService service` as a field inside `UserManagementServiceImpl`) — distinguishes it at a glance from every
other, domain-prefixed repository/service field.

**See also:** [Service Tier Hierarchy](#service-tier-hierarchy)

---

## Service Tier Hierarchy: Integration Service

**Status:** Acceptable — not yet needed anywhere in InvoiceSync-be.

**Problem:** Another domain needs to call into this entity's logic, but injecting the Management service would create
tight coupling and risk circular dependencies.

**Why this approach:** The Integration service provides a stable, narrow API surface. It never injects a Management
service, so it can't participate in circular dependency chains.

**Solution:** Integration service exposes methods for other domains. It reads and coordinates but never owns mutation
workflows.

| Belongs here                                            | Does NOT belong here                       |
|---------------------------------------------------------|--------------------------------------------|
| Methods called by other domain services                 | Methods called only by own controllers     |
| Cross-entity lookups providing context to other domains | Full CRUD workflows (→ Base Service)       |
|                                                         | UI form preparation (→ Management Service) |

**Note on existing naming:** `com.invoicesync.integration.google.service.GoogleIntegrationService`
uses "Integration" for a third-party API wrapper (Gmail), which is a different concept from the internal cross-domain
tier described here — don't treat it as a precedent for this pattern.

**When to use:** When a second domain actually starts calling your service methods. That's the signal to extract an
Integration tier.

**See also:** [Service Dependency Direction](#service-dependency-direction)

---

## Service Tier Hierarchy: Two-Tier Variant

**Status:** Acceptable

**Problem:** The entity has cross-package coordination or UI workflows beyond simple entity CRUD, but no other domain
calls into it.

**Why this approach:** Avoids the overhead of an Integration tier when there are no cross-domain consumers. Simpler than
three-tier while still separating entity CRUD from higher-level concerns.

**Solution:** Base Service + Management Service only.

**Example:** None in InvoiceSync-be yet — this is where a service like `UserServiceImpl` would land if it grew UI
workflows spanning multiple entities.

**When to use:** Entities with UI workflows or cross-package coordination but no cross-domain API need.

**See also:** [Service Tier Hierarchy](#service-tier-hierarchy)

---

## Service Tier Hierarchy: Single Service Variant

**Status:** State of the Art for InvoiceSync-be today — every current service is this shape.

**Problem:** The entity is simple enough that splitting into tiers creates unnecessary indirection.

**Why this approach:** A single class with a small surface area is easier to navigate than two or three nearly-empty
classes.

**Solution:** Everything in one service class — reads, mutations, and any cross-service methods.

**Example:** `UserServiceImpl`, `AuthServiceImpl`, `PostingAccountServiceImpl`,
`ReceiptServiceImpl`, `InvoiceServiceImpl`, `CompanyServiceImpl`.

**When to use:** Default choice for a new entity. Revisit only when the class grows large or a second domain starts
depending on it.

**See also:** [Service Tier Hierarchy](#service-tier-hierarchy)

---

## Service Dependency Direction

**Status:** State of the Art

**Problem:** When service tiers can inject each other freely, circular dependencies and tangled call graphs emerge.

**Why this approach:** A strict dependency direction keeps the graph acyclic. Integration services become leaf nodes
that never pull in mutation logic — they only read and coordinate.

**Solution:**

| Dependency                                            | Allowed?                   |
|-------------------------------------------------------|----------------------------|
| ManagementService → own Base Service                  | ✅                         |
| ManagementService → other domain's IntegrationService | ✅                         |
| ManagementService → other domain's ManagementService  | ⚠️ Exception, not the rule |
| IntegrationService → own Base Service                 | ✅                         |
| IntegrationService → own Repository                   | ✅                         |
| IntegrationService → any ManagementService            | ✗ **Never**               |
| Base Service → own Repository                         | ✅                         |
| Base Service → Management or Integration              | ✗ **Never**               |

**When to use:** Always, once a service has more than one tier. No exceptions for the Integration→Management
prohibition. Cross-domain Management→Management is allowed but should be rare and justified.

**See also:** [Service Tier Hierarchy](#service-tier-hierarchy)

---

## Service Implementation Shape

**Status:** State of the Art

**Problem:** Once the tier hierarchy decides *which* service a method belongs in, what does the implementation class
itself look like? Field naming, helper methods, transaction placement, and constructor shape drift across a codebase
when nobody writes the convention down.

**Why this approach:** A predictable internal shape makes services skim-readable, keeps the dependency graph explicit at
the constructor, and eliminates a class of bugs (stateless helpers escaping the test seam as `static` methods,
forgetting `@Transactional` on a write path).

**Solution:** Every `@Service` implementation follows the rules below.

### Class declaration

- `@Service`-annotated, suffixed `Impl`, implements an `EntityService` interface.
- `@Transactional` is `jakarta.transaction.Transactional` (see `AuthServiceImpl`), placed on the class or the specific
  method that needs it — not Spring's
  `org.springframework.transaction.annotation.Transactional`.

### Dependency injection

- Constructor injection only, via Lombok (`@AllArgsConstructor` / `@RequiredArgsConstructor`). No
  `@Autowired` fields.
- Constructor/field order: repositories → domain services → cross-cutting/utility services.

### Fields

| Convention                                                                                                                                        | Status                                                                                                                         |
|---------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| Domain-prefixed repository field (`userRepository`, `postingAccountRepository`, …)                                                                | State of the Art                                                                                                               |
| Bare `repository` field name, even when the service has exactly one tightly-bound repository                                                      | Avoid — every current service already uses the domain-prefixed form, and a mixed style is harder to grep than a consistent one |
| `service` field name for a Base Service injected into a Management Service (see [Management Service](#service-tier-hierarchy-management-service)) | State of the Art, once that tier exists                                                                                        |
| Blank line between fields                                                                                                                         | State of the Art                                                                                                               |

### Methods

- **No `static` methods.** Even stateless private helpers stay as instance methods — keeps them mockable and consistent
  with the rest of the class. (Static *fields* — constants, compiled regex patterns — are fine.)
- Method ordering for CRUD-shaped services, top to bottom:
    1. `list*` and `listBy*` variants — reads returning a collection
    2. `get*`/`getById`/`getBy*` variants — reads returning one entity
    3. Specialized `get*` methods returning a computed/derived value (not the raw entity)
    4. Boolean-returning query methods (`is*`/`has*`)
    5. `save`/`register`/`create`
    6. `update` and variants
    7. `delete`/`deactivate`/`clear` and variants (see [Method naming](../CLAUDE.md#method-naming)
       for choosing between them)

  Rationale for this order: start with the methods that list whole models, then narrow to fetching one, then to derived
  values, then to yes/no checks, then finally the methods that write. Doesn't apply to services whose surface isn't
  CRUD-shaped (e.g. pure calculation/orchestration services).
- `get*` returns `Optional<T>` when the entity may be absent; when a method needs the entity or fails, follow existing
  precedent (`UserServiceImpl.getUserInfo`) —
  `repository.findById(id).orElseThrow(() -> { log.warn(...); return new EntityNotFoundException(...); })`.
- `persist` is the conventional name when a method internally chooses between `save` and `update`.

**Example:** `UserServiceImpl`, `AuthServiceImpl` follow all of the above.

**When to use:** Always for new `@Service` implementations. When touching legacy services that violate any of these
(most commonly the bare `repository` field name), bringing them in line is encouraged but not required.

**See also:** [Service Tier Hierarchy](#service-tier-hierarchy), [Method naming](../CLAUDE.md#method-naming)
