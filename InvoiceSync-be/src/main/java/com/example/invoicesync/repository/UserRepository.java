package com.example.invoicesync.repository;


import com.example.invoicesync.module.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {


}
