package com.example.pohodaimport.repository;


import com.example.pohodaimport.module.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {


}
