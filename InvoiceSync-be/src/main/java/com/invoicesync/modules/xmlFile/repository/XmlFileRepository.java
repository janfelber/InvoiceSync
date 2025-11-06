package com.invoicesync.modules.xmlFile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.invoicesync.modules.xmlFile.model.XmlFile;

@Repository
public interface XmlFileRepository extends JpaRepository<XmlFile, Long>, JpaSpecificationExecutor<XmlFile> {

    // List<XmlFile> findByUserId(Long userId);

}
