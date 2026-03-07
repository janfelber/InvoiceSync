package com.invoicesync.modules.activity.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.invoicesync.modules.activity.mapper.UserActivityMapper;
import com.invoicesync.modules.activity.repository.UserActivityRepository;

@Service
public class UserActivityServiceImpl implements UserActivityService {

  private final UserActivityRepository repository;

  private final UserActivityMapper mapper;

  public UserActivityServiceImpl(final UserActivityRepository repository, final UserActivityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public void save(final UserActivityRecord record) {
    repository.save(mapper.toUserActivity(record));
  }

  @Override
  public List<UserActivityRecord> getAllActivities() {
    return mapper.toRecordList(repository.findAll());
  }

}
