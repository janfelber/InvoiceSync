package com.invoicesync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.module.ScriptSchema;

public interface ScriptSchemaRepository extends JpaRepository<ScriptSchema, Integer> {
    List<ScriptSchema> findByUserId(int id);
}
