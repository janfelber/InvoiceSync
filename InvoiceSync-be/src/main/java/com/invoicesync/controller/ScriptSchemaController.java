package com.invoicesync.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.module.ScriptSchema;
import com.invoicesync.service.ScriptSchemaService;

@RestController
@RequestMapping("/v1")
public class ScriptSchemaController {

    private final ScriptSchemaService scriptSchemaService;

    @Autowired
    public ScriptSchemaController(ScriptSchemaService scriptSchemaService) {
        this.scriptSchemaService = scriptSchemaService;
    }

    //get specific script schema mapping
    @GetMapping("/script-schema/{id}")
    public Map<String, String> getScriptSchemaById(@PathVariable int id) {
        ScriptSchema scriptSchema = scriptSchemaService.getScriptSchema(id);
        Map<String, String> result = new HashMap<>();
        result.put("script_name", scriptSchema.getScript());
        result.put("schema_name", scriptSchema.getSchema());
        return result;
    }

}








