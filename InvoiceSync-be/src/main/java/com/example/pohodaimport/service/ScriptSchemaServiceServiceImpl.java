package com.example.pohodaimport.service;

import com.example.pohodaimport.module.ScriptSchema;
import com.example.pohodaimport.repository.ScriptSchemaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public ScriptSchema getScriptByCompanyId(int companyId) {
        return scriptSchemaRepository.findByCompanyId(companyId);
    }

    @Override
    public ScriptSchema getSchemaByCompanyId(int companyId) {
       return scriptSchemaRepository.findByCompanyId(companyId);
    }


}
