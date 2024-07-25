package com.example.pohodaimport.repository;

import com.example.pohodaimport.module.Import;
import com.example.pohodaimport.module.ScriptSchema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptSchemaRepository extends JpaRepository<ScriptSchema, Integer> {
    List<ScriptSchema> findByUserId(int id);

    ScriptSchema findByCompanyId(int id);
}
