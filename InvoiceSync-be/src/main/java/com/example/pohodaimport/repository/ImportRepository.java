package com.example.pohodaimport.repository;

import com.example.pohodaimport.module.Import;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportRepository extends JpaRepository<Import, Integer> {

    List<Import> findByUserId(int id);

    List<Import> findByUserIdAndCompanyId(int userId, int companyId);

}
