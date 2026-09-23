# Code Skeleton — Copy-Paste Template

Pure code, minimal prose. Replace `Entity`/`entity`/`Parent`/`parent` with your real names. For the
*why* behind each piece, see [`new-module-template.md`](new-module-template.md) (same shape, explained)
and [`audit.md`](audit.md)/[`general.md`](general.md).

`Entity` = the thing you're adding. `Parent` = whatever it belongs to (drop everything `Parent`-related
if your entity has no owner, e.g. a pure lookup table).

---

## 1. Migration

```sql
-- V<n>__IS_<ticket>_<description>.sql
CREATE TABLE invoice_sync.entity (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parent_id   BIGINT NOT NULL REFERENCES invoice_sync.parent(id),
    field_a     VARCHAR(255) NOT NULL,
    field_b     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_entity_parent_id ON invoice_sync.entity(parent_id);
```

## 2. `package-info.java`

```java
// modules/<module>/<submodule>/package-info.java
@NullMarked
package com.invoicesync.modules.<module>.<submodule>;

import org.jspecify.annotations.NullMarked;
```

## 3. Entity

```java
package com.invoicesync.modules.<module>.model;

import com.invoicesync.core.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entity", schema = "invoice_sync")
public class Entity extends BaseEntity {

  // same-aggregate parent (owned/cascaded together) — real object reference:
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", nullable = false)
  private Parent parent;

  // cross-aggregate reference (independent lifecycle, e.g. Company) — ID only instead:
  // @Column(name = "other_id", nullable = false)
  // private Long otherId;

  @Column(name = "field_a", nullable = false)
  private String fieldA;

  @Column(name = "field_b")
  private String fieldB;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

}
```
No `@Data`. `BaseEntity` already supplies `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` on `id`.

## 4. Request/response DTOs

```java
package com.invoicesync.modules.<module>.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntityRequest(
    @NotBlank @Size(max = 255) String fieldA,
    String fieldB
) {}

public record EntityResponse(
    Long id,
    String fieldA,
    String fieldB,
    LocalDateTime createdAt
) {}
```

## 5. Repository

```java
package com.invoicesync.modules.<module>.repository;

import com.invoicesync.modules.<module>.model.Entity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<Entity, Long> {

  // alphabetical by method name — CLAUDE.md "Repository method ordering":
  // existsBy* / findBy* / findById are all just sorted by name, including inherited ones like findById

  boolean existsByFieldA(String fieldA);

  List<Entity> findByParentId(Long parentId);

  Optional<Entity> findById(Long id); // inherited from JpaRepository, sorts in wherever its name puts it

}
```
Adding a new method later? Find its alphabetical slot, don't append to the bottom — IntelliJ's
*Code → Rearrange Code* (Arrangement rules configured per `CLAUDE.md`) can do this for you automatically.

## 6. Mapper

```java
package com.invoicesync.modules.<module>.mapper;

import com.invoicesync.modules.<module>.model.Entity;
import com.invoicesync.modules.<module>.model.EntityRequest;
import com.invoicesync.modules.<module>.model.EntityResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EntityMapper {

  EntityResponse toResponse(Entity entity);

  @Mapping(target = "parent", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  Entity toEntity(EntityRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromRequest(EntityRequest request, @MappingTarget Entity entity);

}
```

## 7. Ownership annotation + registration

```java
// modules/auth/security/OwnedEntity.java
package com.invoicesync.modules.auth.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnedEntity {}
```
```java
// OwnershipLoaderRegistry.java — add alongside the other registrations
registry.register(
    OwnedEntity.class,
    id -> entityRepository.findById((Long) id)
        .orElseThrow(() -> new EntityNotFoundException("Entity with id " + id + " does not exist"))
);
```

## 8. Service interface

```java
package com.invoicesync.modules.<module>.service;

import com.invoicesync.modules.<module>.model.EntityRequest;
import com.invoicesync.modules.<module>.model.EntityResponse;
import java.util.List;

public interface EntityService {

  EntityResponse getEntity(Long entityId);

  List<EntityResponse> listEntitiesForParent(Long parentId);

  EntityResponse saveEntity(Long parentId, EntityRequest request);

  EntityResponse updateEntity(Long entityId, EntityRequest request);

  void deleteEntity(Long entityId);

}
```

## 9. Service impl

```java
package com.invoicesync.modules.<module>.service;

import com.invoicesync.core.exception.EntityNotFoundException;
import com.invoicesync.modules.<module>.mapper.EntityMapper;
import com.invoicesync.modules.<module>.model.Entity;
import com.invoicesync.modules.<module>.model.EntityRequest;
import com.invoicesync.modules.<module>.model.EntityResponse;
import com.invoicesync.modules.<module>.repository.EntityRepository;
import com.invoicesync.modules.auth.security.OwnedEntity;
import com.invoicesync.modules.auth.security.RequiresOwnership;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EntityServiceImpl implements EntityService {

  private final EntityRepository entityRepository;
  private final ParentRepository parentRepository;
  private final EntityMapper entityMapper;

  @Override
  @Transactional(readOnly = true)
  @RequiresOwnership
  public EntityResponse getEntity(@OwnedEntity Long entityId) {
    return entityMapper.toResponse(findEntityOrThrow(entityId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<EntityResponse> listEntitiesForParent(Long parentId) {
    return entityRepository.findByParentId(parentId).stream()
        .map(entityMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public EntityResponse saveEntity(Long parentId, EntityRequest request) {
    Parent parent = parentRepository.findById(parentId)
        .orElseThrow(() -> new EntityNotFoundException(Parent.class, parentId));
    Entity entity = entityMapper.toEntity(request);
    entity.setParent(parent);
    return entityMapper.toResponse(entityRepository.save(entity));
  }

  @Override
  @Transactional
  @RequiresOwnership
  public EntityResponse updateEntity(@OwnedEntity Long entityId, EntityRequest request) {
    Entity entity = findEntityOrThrow(entityId);
    entityMapper.updateFromRequest(request, entity);
    return entityMapper.toResponse(entity); // dirty checking flushes it — no explicit save()
  }

  @Override
  @Transactional
  @RequiresOwnership
  public void deleteEntity(@OwnedEntity Long entityId) {
    entityRepository.deleteById(entityId);
  }

  private Entity findEntityOrThrow(Long id) {
    return entityRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(Entity.class, id));
  }

}
```

## 10. Controller

```java
package com.invoicesync.modules.<module>.controller;

import com.invoicesync.modules.<module>.model.EntityRequest;
import com.invoicesync.modules.<module>.model.EntityResponse;
import com.invoicesync.modules.<module>.service.EntityService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EntityController.BASE_PATH)
@RequiredArgsConstructor
public class EntityController {

  static final String BASE_PATH = "/entities";
  private static final String BY_PARENT = "/parent/{parentId}";
  private static final String BY_ID = "/{entityId}";

  private final EntityService entityService;

  @GetMapping(BY_ID)
  public EntityResponse getEntity(@PathVariable Long entityId) {
    return entityService.getEntity(entityId);
  }

  @GetMapping(BY_PARENT)
  public List<EntityResponse> listEntitiesForParent(@PathVariable Long parentId) {
    return entityService.listEntitiesForParent(parentId);
  }

  @PostMapping(BY_PARENT)
  public EntityResponse saveEntity(@PathVariable Long parentId,
                                    @Valid @RequestBody EntityRequest request) {
    return entityService.saveEntity(parentId, request);
  }

  @PatchMapping(BY_ID)
  public EntityResponse updateEntity(@PathVariable Long entityId,
                                      @Valid @RequestBody EntityRequest request) {
    return entityService.updateEntity(entityId, request);
  }

  @DeleteMapping(BY_ID)
  public void deleteEntity(@PathVariable Long entityId) {
    entityService.deleteEntity(entityId);
  }

}
```
No try/catch — `GlobalExceptionHandler` already handles `EntityNotFoundException` → `404`.

## 11. Test

```java
package com.invoicesync.modules.<module>;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class EntityOwnershipTest {

  @Test
  void userCannotAccessAnotherUsersEntity() {
    // 1. register + log in userA and userB via /auth/register + /auth/login
    // 2. userA creates a Parent, then an Entity under it
    // 3. userB requests that Entity by id → assert 404
  }

}
```
