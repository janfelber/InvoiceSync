package com.invoicesync.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.invoicesync.module.ScriptSchema;
import com.invoicesync.repository.ScriptSchemaRepository;

@Service
public class ScriptSchemaServiceServiceImpl implements ScriptSchemaService {

    private final ScriptSchemaRepository scriptSchemaRepository;

    public ScriptSchemaServiceServiceImpl(ScriptSchemaRepository scriptSchemaRepository) {
        this.scriptSchemaRepository = scriptSchemaRepository;
    }

    @Override
    public List<ScriptSchema> getScriptSchemasMapping() {
        return scriptSchemaRepository.findAll();
    }

    @Override
    public List<ScriptSchema> getScriptSchemaByUserId(int userId) {
        return scriptSchemaRepository.findByUserId(userId);
    }

    @Override
    public ScriptSchema getScriptSchema(int id) {
        return scriptSchemaRepository.findById(id).orElse(null);
    }


}
