package com.invoicesync.admin;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.shared.common.PageResponse;
import com.invoicesync.user.UserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/users")
  public PageResponse<UserDto> findAllUsers(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser
  ) {
    return adminService.getUsers(page, size, connectedUser);
  }

  @GetMapping("/user/{userId}")
  public UserDto getUserInfo(@PathVariable final UUID userId) {
    return adminService.getUserInfo(userId);
  }

}
