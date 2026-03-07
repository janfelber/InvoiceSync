package com.invoicesync.modules.activity.mapper;

import java.util.List;

import org.springframework.stereotype.Service;

import com.invoicesync.modules.activity.model.UserActivity;
import com.invoicesync.modules.activity.service.UserActivityRecord;

@Service
public class UserActivityMapper {

  public UserActivity toUserActivity(final UserActivityRecord record) {
    return UserActivity.builder()
        .user(record.userId())
        .type(record.activityType())
        .description(record.description())
        .timestamp(record.timestamp())
        .build();
  }

  public UserActivityRecord toRecord(final UserActivity activity) {
    return UserActivityRecord.forType(
        activity.getUser(),
        activity.getType(),
        activity.getDescription()
    );
  }

  public List<UserActivityRecord> toRecordList(final List<UserActivity> activities) {
    return activities.stream()
        .map(this::toRecord)
        .toList();
  }

}
