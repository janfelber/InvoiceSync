package com.invoicesync.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.invoicesync.feature.FeatureDto;
import com.invoicesync.feature.UpdateUserFeatureRequest;
import com.invoicesync.shared.common.PageResponse;
import com.invoicesync.user.UserDto;

public interface AdminService {

  PageResponse<UserDto> getUsers(int page, int size, Authentication connectedUser);

  UserDto getUserInfo(UUID userId);

  List<FeatureDto> updateUserFeatures(UUID userId, UpdateUserFeatureRequest request);
}
