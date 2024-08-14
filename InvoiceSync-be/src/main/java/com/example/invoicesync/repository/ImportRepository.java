package com.example.invoicesync.repository;

import com.example.invoicesync.module.Import;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportRepository extends JpaRepository<Import, Integer> {

    List<Import> findByUserId(int id);

    List<Import> findByUserIdAndCompanyId(int userId, int companyId);

}
