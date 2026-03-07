package com.invoicesync.modules.activity.service;

import java.util.List;

public interface UserActivityService {

  void save(UserActivityRecord record);

  List<UserActivityRecord> getAllActivities();

}
