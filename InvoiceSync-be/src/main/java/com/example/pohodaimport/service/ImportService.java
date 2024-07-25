package com.example.pohodaimport.service;

import com.example.pohodaimport.module.Import;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ImportService {

    List<Import> getImports();
    Import getImport(int id);

    //get import by user id
    List<Import> getImportsByUserId(int userId);

    //get import by company id for user
    List<Import> getImportsByCompanyId(int userId, int companyId);

    Import getXmlContentById(int importId);
}
