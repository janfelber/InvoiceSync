package com.invoicesync.modules.statistics.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.statistics.model.StatisticsResponseDto;
import com.invoicesync.modules.statistics.service.StatisticsService;

@RestController
@RequestMapping(Api.STATS)
public class StatisticsController {

  private final StatisticsService statisticsService;

  public StatisticsController(final StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping(Api.STATS_BASIC)
  public StatisticsResponseDto getStatistics(@PathVariable final Long companyId, final Authentication connectedUser) {
    return statisticsService.getStatistics(connectedUser, companyId);
  }

}
