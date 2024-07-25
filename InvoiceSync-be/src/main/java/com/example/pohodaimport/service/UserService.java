package com.example.pohodaimport.service;

import com.example.pohodaimport.module.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    List<User> getUsers();
}
