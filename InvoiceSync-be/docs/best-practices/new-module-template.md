# New Module From Scratch — Worked Example

This is the "start here" reference for any brand-new piece of backend functionality (a new entity, a new endpoint, a new
service). It exists because every convention in
[`CLAUDE.md`](../../CLAUDE.md), [`general.md`](general.md) and
[`audit.md`](audit.md) is easy to *know* and easy to *accidentally skip* under
deadline pressure — this doc removes the "which pattern did we agree on again" lookup by showing one complete, correct
example top to bottom. Copy the shape, not necessarily the domain.

The example domain (`CompanyNote` — a free-text internal note a user can attach to their company) is **illustrative
only**, chosen because it's simple enough to show every layer without the example itself becoming the thing you have to
understand. It is not a real feature request.

Want the same shape as pure copy-paste code with placeholder names instead of a worked example? See
[`code-skeleton-template.md`](code-skeleton-template.md).

---

## The checklist (skim this first, read the worked example for the "why")

For any new owned resource, in this order:

1. **Migration** — new table, FK to its owner, indexes on the FK and any filter column.
2. **`package-info.java`** with `@NullMarked` if this is a new package (per `CLAUDE.md`'s JSpecify rollout — every *new*
   package gets this from day one, no exceptions).
3. **Entity** — `@Getter @Setter @SuperBuilder`, never `@Data`, explicit `fetch = FetchType.LAZY` on every association,
   `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` via `BaseEntity`.
4. **Request/response DTOs as records.**
5. **Repository** — methods ordered alphabetically.
6. **Mapper** — MapStruct, one method for full create-mapping, one `@MappingTarget` method for partial update.
7. **Ownership wiring** — a new `@OwnedX` annotation + a registration in `OwnershipLoaderRegistry`, if this resource
   belongs to a user/company (almost everything does).
8. **Service interface + Impl** — method-level `@Transactional`/`@Transactional(readOnly = true)`, constructor
   injection, method naming by intent (`get*`/`list*`/`save`/`deleteX`).
9. **Controller** — thin, path as a `private static final String` on the controller itself, `@Valid`
   on the request body, no try/catch (let `GlobalExceptionHandler` handle errors).
10. **Test** — at minimum, one test proving cross-user access is rejected (mirrors the ownership test gap called out in
    `audit.md` §10 — don't repeat that gap on new code).

---

## 1. Migration

```sql
-- V99__IS_XXX_add_company_note.sql
CREATE TABLE invoice_sync.company_note
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT    NOT NULL REFERENCES invoice_sync.company (id),
    text       TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_company_note_company_id ON invoice_sync.company_note (company_id);
```

Index the FK from the start — don't wait for a later "retrofit indexes" migration like `V25__IS_235.sql`
had to.

## 2. `package-info.java`

```java
// modules/company/note/package-info.java
@NullMarked
package com.invoicesync.modules.company.note;

import org.jspecify.annotations.NullMarked;
```

New package → `@NullMarked` from the start, per `CLAUDE.md`'s rollout plan. You get compiler-enforced null-safety for
every class you add here, for free, forever — no separate step needed later.

## 3. Entity

```java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company_note")
public class CompanyNote extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @Column(nullable = false)
  private String text;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

}
```

No `@Data`. No `equals`/`hashCode`/`toString` overrides needed here — `BaseEntity` already supplies
`@EqualsAndHashCode(onlyExplicitlyIncluded = true)` scoped to `id`. `fetch = FetchType.LAZY` is explicit, not left to
JPA's `@ManyToOne` default.

## 4. Request/response DTOs

```java
public record CompanyNoteRequest(@NotBlank @Size(max = 2000) String text) {

}

public record CompanyNoteResponse(Long id, String text, LocalDateTime createdAt) {

}
```

Both are immutable records — nothing about a note's request/response payload needs to be mutated after construction.
Validation lives directly on the record component.

## 5. Repository

```java
public interface CompanyNoteRepository extends JpaRepository<CompanyNote, Long> {

  boolean existsByIdAndCompanyId(Long id, Long companyId);

  List<CompanyNote> findByCompanyId(Long companyId);

  Optional<CompanyNote> findById(Long id);

}
```

Alphabetical by method name (`CLAUDE.md`'s "Repository method ordering") — `existsBy*` before `findBy*`, and the
inherited `findById` sorts in wherever its name puts it, same as any other method. When a new method is added later, it
goes into its alphabetical slot, not appended at the bottom — IntelliJ's *Code → Rearrange Code* can enforce this
automatically instead of relying on remembering to do it by hand.

## 6. Mapper

```java

@Mapper(componentModel = "spring")
public interface CompanyNoteMapper {

  CompanyNoteResponse toResponse(CompanyNote entity);

  @Mapping(target = "company", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  CompanyNote toEntity(CompanyNoteRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromRequest(CompanyNoteRequest request, @MappingTarget CompanyNote entity);

}
```

One mapper, three jobs: full response mapping, full create mapping, and partial update — the same
`IGNORE`-strategy pattern recommended in `audit.md` §3, applied from day one instead of retrofitted
after a bug like `ReceiptServiceImpl`'s.

## 7. Ownership wiring

```java
// modules/auth/security/OwnedCompanyNote.java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnedCompanyNote {

}
```

```java
// registered alongside the other entity types in OwnershipLoaderRegistry
registry.register(OwnedCompanyNote .class, id ->companyNoteRepository.

findById((Long) id));
```

```java

@RequiresOwnership
public CompanyNoteResponse getNote(@OwnedCompanyNote Long noteId) { ...}
```

This is the step that's easiest to forget on a brand-new entity — it's not part of Spring Security by default, it's this
project's own mechanism, so it has to be wired by hand for every new owned entity type. Treat "does this need `@Owned*`"
as a mandatory question for every new endpoint taking an ID, per
`general.md` §4 — never assume it's someone else's job to add later.

## 8. Service — interface + Impl

```java
public interface CompanyNoteService {

  CompanyNoteResponse getNote(Long noteId);

  List<CompanyNoteResponse> listNotesForCompany(Long companyId);

  CompanyNoteResponse saveNote(Long companyId, CompanyNoteRequest request);

  CompanyNoteResponse updateNote(Long noteId, CompanyNoteRequest request);

  void deleteNote(Long noteId);

}
```

```java

@Service
@RequiredArgsConstructor
public class CompanyNoteServiceImpl implements CompanyNoteService {

  private final CompanyNoteRepository companyNoteRepository;

  private final CompanyRepository companyRepository;

  private final CompanyNoteMapper companyNoteMapper;

  @Override
  @Transactional(readOnly = true)
  @RequiresOwnership
  public CompanyNoteResponse getNote(@OwnedCompanyNote Long noteId) {
    return companyNoteMapper.toResponse(findEntityOrThrow(noteId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CompanyNoteResponse> listNotesForCompany(Long companyId) {
    return companyNoteRepository.findByCompanyId(companyId).stream()
        .map(companyNoteMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public CompanyNoteResponse saveNote(Long companyId, CompanyNoteRequest request) {
    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new EntityNotFoundException(Company.class, companyId));
    CompanyNote note = companyNoteMapper.toEntity(request);
    note.setCompany(company);
    return companyNoteMapper.toResponse(companyNoteRepository.save(note));
  }

  @Override
  @Transactional
  @RequiresOwnership
  public CompanyNoteResponse updateNote(@OwnedCompanyNote Long noteId, CompanyNoteRequest request) {
    CompanyNote note = findEntityOrThrow(noteId);
    companyNoteMapper.updateFromRequest(request, note);
    return companyNoteMapper.toResponse(note); // dirty checking flushes it — no explicit save()
  }

  @Override
  @Transactional
  @RequiresOwnership
  public void deleteNote(@OwnedCompanyNote Long noteId) {
    companyNoteRepository.deleteById(noteId);
  }

  private CompanyNote findEntityOrThrow(Long id) {
    return companyNoteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(CompanyNote.class, id));
  }

}
```

Notice: `@Transactional(readOnly = true)` on both read paths (never done anywhere in the current codebase, per
`audit.md` §5); method-level `@Transactional`, not class-level, so it's visible per-method whether a
transaction is open; no external I/O in this example, but if there were (e.g. a notification on note creation), it would
sit *outside* the `@Transactional` method, called from a thin wrapper — see `general.md` §7 for that
shape.

## 9. Controller

```java
@RestController
@RequestMapping(CompanyNoteController.BASE_PATH)
@RequiredArgsConstructor
public class CompanyNoteController {

  static final String BASE_PATH = "/company-notes";
  private static final String BY_COMPANY = "/company/{companyId}";
  private static final String BY_ID = "/{noteId}";

  private final CompanyNoteService companyNoteService;

  @GetMapping(BY_COMPANY)
  public List<CompanyNoteResponse> listNotesForCompany(@PathVariable Long companyId) {
    return companyNoteService.listNotesForCompany(companyId);
  }

  @PostMapping(BY_COMPANY)
  public CompanyNoteResponse saveNote(@PathVariable Long companyId,
                                       @Valid @RequestBody CompanyNoteRequest request) {
    return companyNoteService.saveNote(companyId, request);
  }

  @PatchMapping(BY_ID)
  public CompanyNoteResponse updateNote(@PathVariable Long noteId,
                                         @Valid @RequestBody CompanyNoteRequest request) {
    return companyNoteService.updateNote(noteId, request);
  }

  @DeleteMapping(BY_ID)
  public void deleteNote(@PathVariable Long noteId) {
    companyNoteService.deleteNote(noteId);
  }

}
```

No try/catch anywhere — a missing note throws `EntityNotFoundException`, a foreign note throws the same exception via
`OwnershipAspect`, both land on `GlobalExceptionHandler`'s existing handler and come back as a `404` with a consistent
body. Path is declared on the controller itself (per `CLAUDE.md`'s deprecation of `Api.java` for new code), not added to
the shared constants file.

## 10. Test

```java

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CompanyNoteOwnershipTest {

  @Test
  void userCannotAccessAnotherUsersCompanyNote() {
    // register + log in userA and userB via the real /auth endpoints
    // userA creates a note under their company
    // userB requests it by id → expect 404, not the note's content
  }

}
```

This is the same Testcontainers-based, real-HTTP-flow shape recommended in `audit.md`
§10 for closing the existing ownership-test gap — writing it for every *new* owned entity as it's built is far cheaper
than retrofitting coverage for dozens of entities later.

---

## Why this is worth following even under time pressure

Every shortcut this template avoids is a shortcut the audit already found taken somewhere in the existing codebase —
`@Data` on an entity, a forgotten ownership check, a class-level `@Transactional`
wrapping external I/O, a partial update that silently nulls fields, a missing test on a security- critical path. None of
those happened because anyone didn't know better in the moment — they happened because the "obviously correct" version
wasn't written down anywhere to copy from. That's what this file is for.
