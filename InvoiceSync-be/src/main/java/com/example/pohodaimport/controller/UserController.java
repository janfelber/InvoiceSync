package com.example.pohodaimport.controller;


import com.example.pohodaimport.module.User;
import com.example.pohodaimport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //get all users
    @GetMapping("/user")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    //get user by id
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUser(id);
    }



}
