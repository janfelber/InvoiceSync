package com.invoicesync.modules.statistics.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.modules.statistics.model.StatisticsResponseDto;

public interface StatisticsService {

  StatisticsResponseDto getStatistics(Authentication connectedUser, Long companyId);
}
