package com.invoicesync.modules.admin.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.feature.model.FeatureDto;
import com.invoicesync.modules.feature.model.UpdateUserFeatureRequest;
import com.invoicesync.modules.user.model.UserDto;

public interface AdminService {

  PageResponse<UserDto> getUsers(int page, int size, Authentication connectedUser);

  UserDto getUserInfo(UUID userId);

  List<FeatureDto> updateUserFeatures(UUID userId, UpdateUserFeatureRequest request);
}
