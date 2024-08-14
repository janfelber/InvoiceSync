package com.example.invoicesync.repository;

import com.example.invoicesync.module.ScriptSchema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptSchemaRepository extends JpaRepository<ScriptSchema, Integer> {
    List<ScriptSchema> findByUserId(int id);

    ScriptSchema findByCompanyId(int id);
}
