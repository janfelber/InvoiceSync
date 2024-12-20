package com.invoicesync.service;

import java.util.List;

import com.invoicesync.module.ScriptSchema;

public interface ScriptSchemaService {
    List<ScriptSchema> getScriptSchemasMapping();

    List<ScriptSchema> getScriptSchemaByUserId(int userId);

    ScriptSchema getScriptSchema(int id);
}
