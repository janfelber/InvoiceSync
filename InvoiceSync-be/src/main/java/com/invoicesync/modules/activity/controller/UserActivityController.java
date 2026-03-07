package com.invoicesync.modules.activity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.activity.service.UserActivityRecord;
import com.invoicesync.modules.activity.service.UserActivityService;

@RestController
@RequestMapping(Api.ADMIN_USER_ACTIVITY)
public class UserActivityController {

  private final UserActivityService userActivityService;

  public UserActivityController(final UserActivityService userActivityService) {
    this.userActivityService = userActivityService;
  }

  @GetMapping(Api.ADMIN_USER_ACTIVITY_TABLE)
  public List<UserActivityRecord> getTable() {
    return userActivityService.getAllActivities();
  }

}
