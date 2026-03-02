package com.invoicesync.modules.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.invoicesync.modules.activity.model.UserActivity;

@Repository
public interface UserActivityRepository
    extends JpaRepository<UserActivity, Long>, JpaSpecificationExecutor<UserActivity> {

}
