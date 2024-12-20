package com.invoicesync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.module.Import;

public interface ImportRepository extends JpaRepository<Import, Integer> {

    List<Import> findByUserId(int id);

}
