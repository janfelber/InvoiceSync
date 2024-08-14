package com.example.invoicesync.service;

import com.example.invoicesync.module.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    List<User> getUsers();
}
