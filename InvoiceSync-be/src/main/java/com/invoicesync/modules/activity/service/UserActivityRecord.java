package com.invoicesync.modules.activity.service;

import java.time.LocalDateTime;

import com.invoicesync.modules.activity.model.UserActivityType;

public record UserActivityRecord(
    String userId,
    UserActivityType activityType,
    String description,
    LocalDateTime timestamp
) {

  public static UserActivityRecord forType(final String userId, final UserActivityType type, final String description) {
    return new Builder()
        .withUserId(userId)
        .withType(type)
        .withDescription(description)
        .withTimestampNow()
        .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String userId;

    private UserActivityType activityType;

    private String description;

    private LocalDateTime timestamp;

    public Builder withUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder withType(UserActivityType type) {
      this.activityType = type;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder withTimestampNow() {
      this.timestamp = LocalDateTime.now();
      return this;
    }

    public UserActivityRecord build() {
      return new UserActivityRecord(userId, activityType, description, timestamp);
    }

  }

}
