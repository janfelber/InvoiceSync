package com.invoicesync.service;

import com.invoicesync.module.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    List<User> getUsers();
}
