package com.invoicesync.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.feature.FeatureService;
import com.invoicesync.shared.common.PageResponse;
import com.invoicesync.subscription.UserServiceImpl;
import com.invoicesync.user.User;
import com.invoicesync.user.UserDto;
import com.invoicesync.user.UserMapper;
import com.invoicesync.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final UserServiceImpl userService;

  private final FeatureService featureService;

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  @Override
  public PageResponse<UserDto> getUsers(final int page, final int size, final Authentication connectedUser) {
    final Pageable pageable = PageRequest.of(page, size);
    final Page<User> users = userRepository.findAll(pageable);

    final List<UserDto> usersResponse = users.stream()
        .map(userMapper::toUserInfo)
        .filter(u -> !"ROLE_ADMIN".equals(u.getRole()))
        .toList();
    return new PageResponse<>(
        usersResponse,
        users.getNumber(),
        users.getSize(),
        users.getTotalElements(),
        users.getTotalPages(),
        users.isFirst(),
        users.isLast()
    );
  }

  @Override
  public UserDto getUserInfo(final UUID userId) {
    return userService.getUserInfo(userId);
  }

}
