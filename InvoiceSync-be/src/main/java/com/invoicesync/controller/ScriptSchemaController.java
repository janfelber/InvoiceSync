package com.invoicesync.controller;


import com.invoicesync.module.ScriptSchema;
import com.invoicesync.service.ScriptSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
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

    //get script by company_id
    @GetMapping("/script-schema/script/{company_id}")
    public String getScriptByCompanyId (@PathVariable int company_id) {
        ScriptSchema script = scriptSchemaService.getScriptByCompanyId(company_id);
        return script.getScript();
    }

    //get schema by company_id
    @GetMapping("/script-schema/schema/{company_id}")
    public String getSchemaByCompanyId (@PathVariable int company_id) {
        ScriptSchema schema = scriptSchemaService.getSchemaByCompanyId(company_id);
        return schema.getSchema();
    }

}








