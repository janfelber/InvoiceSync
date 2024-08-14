package com.example.invoicesync.service;

import com.example.invoicesync.module.ScriptSchema;

import java.util.List;

public interface ScriptSchemaService {
    List<ScriptSchema> getScriptSchemasMapping();

    List<ScriptSchema> getScriptSchemaByUserId(int userId);

    ScriptSchema getScriptSchema(int id);

    ScriptSchema getScriptByCompanyId(int companyId);

    ScriptSchema getSchemaByCompanyId(int companyId);
}
