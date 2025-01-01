package com.invoicesync.repository;

import com.invoicesync.module.XmlFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XmlFileRepository extends JpaRepository<XmlFile, Long> {
    
    List<XmlFile> findByUserId(Long userId);

}
