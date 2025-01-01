package com.invoicesync.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserCredentialServiceImpl implements UserCredentialService {

  @Value("${user.upload.dir}")
  private String uploadDir;

  @Override
  public void createUserDirectory(final Long userId) {
    final String userDirectoryPath = uploadDir + "/users/" + userId;

    final File userDirectory = new File(userDirectoryPath);

    if (!userDirectory.exists()) {
      final boolean isCreated = !userDirectory.mkdirs();
      if (isCreated) {
        throw new RuntimeException("Unable to create user directory for userId: " + userId);
      }
    }
  }

}
