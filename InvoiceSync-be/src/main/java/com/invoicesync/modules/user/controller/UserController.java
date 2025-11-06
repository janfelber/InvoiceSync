package com.invoicesync.modules.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.modules.user.model.UserDto;
import com.invoicesync.modules.user.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public UserDto getCurrentUserInfo(final Authentication connectedUser) {
    return userService.getCurrentUserInfo(connectedUser);
  }


}
