package com.invoicesync.modules.admin.service;

import static com.invoicesync.core.enums.Role.ROLE_ADMIN;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.feature.model.FeatureDto;
import com.invoicesync.modules.feature.model.UpdateUserFeatureRequest;
import com.invoicesync.modules.feature.service.FeatureService;
import com.invoicesync.modules.user.mapper.UserMapper;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.model.UserDto;
import com.invoicesync.modules.user.repository.UserRepository;
import com.invoicesync.modules.user.service.UserServiceImpl;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final UserServiceImpl userService;

  private final FeatureService featureService;

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  //TODO try not to use string as parameter it should be in some one file where it can be called
  @Override
  public PageResponse<UserDto> getUsers(final int page, final int size, final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size);
    final Page<User> users = userRepository.findByRoleNot(ROLE_ADMIN, pageable);

    return PageResponse.from(users, userMapper::toUserInfo);
  }

  @Override
  public UserDto getUserInfo(final UUID userId) {
    return userService.getUserInfo(userId);
  }

  @Override
  public List<FeatureDto> updateUserFeatures(final UUID userId, final UpdateUserFeatureRequest request) {
    return featureService.updateUserFeatures(userId, request);
  }

}
