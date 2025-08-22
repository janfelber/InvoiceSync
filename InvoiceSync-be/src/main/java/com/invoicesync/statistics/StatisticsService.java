package com.invoicesync.statistics;

import org.springframework.security.core.Authentication;

import com.invoicesync.statistics.dto.StatisticsResponseDto;

public interface StatisticsService {

  StatisticsResponseDto getStatistics(Authentication connectedUser, Long companyId);
}
