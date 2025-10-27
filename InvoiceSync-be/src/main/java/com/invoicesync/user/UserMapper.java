package com.invoicesync.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {

  public UserDto toUserInfo(final User user) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .role(user.getRole().name())
        .createdOn(user.getCreatedOn())
        .build();
  }

}
