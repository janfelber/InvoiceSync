package com.invoicesync.xmlFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface XmlFileRepository extends JpaRepository<XmlFile, Long>, JpaSpecificationExecutor<XmlFile> {

    // List<XmlFile> findByUserId(Long userId);

}
