package com.example.invoicesync.service;


import com.example.invoicesync.module.Import;
import com.example.invoicesync.repository.ImportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ImportServiceImpl implements ImportService{

    private final ImportRepository importRepository;

    @Autowired
    public ImportServiceImpl(ImportRepository importRepository) {
        this.importRepository = importRepository;
    }
    @Override
    public List<Import> getImports () {
        return importRepository.findAll();
    }

    @Override
    public Import getImport(int id) {
        return importRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User with " + id + " not found"
        ));
    }

    //get import by user id
    @Override
    public List<Import> getImportsByUserId(int id) {
        return importRepository.findByUserId(id);
    }

    //get import by company id for user
    @Override
    public List<Import> getImportsByCompanyId(int userId, int companyId) {
        return importRepository.findByUserIdAndCompanyId(userId, companyId);
    }

    @Override
    public Import getXmlContentById(int importId) {
        return importRepository.findById(importId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Import with " + importId + " not found"
        ));
    }


}
