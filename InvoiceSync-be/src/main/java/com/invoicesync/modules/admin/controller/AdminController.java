package com.invoicesync.modules.admin.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.admin.service.AdminService;
import com.invoicesync.modules.feature.model.FeatureDto;
import com.invoicesync.modules.feature.model.UpdateUserFeatureRequest;
import com.invoicesync.modules.user.model.UserDto;

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

  @PutMapping("/user/{userId}/features")
  public List<FeatureDto> updateUserFeatures(@PathVariable final UUID userId,
      @RequestBody final UpdateUserFeatureRequest request) {
    return adminService.updateUserFeatures(userId, request);
  }

}
