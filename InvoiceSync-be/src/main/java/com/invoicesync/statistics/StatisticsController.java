package com.invoicesync.statistics;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.statistics.dto.StatisticsResponseDto;

@RestController
@RequestMapping("/stats")
public class StatisticsController {

  private final StatisticsService statisticsService;

  public StatisticsController(final StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/basic-stats/{companyId}")
  public StatisticsResponseDto getStatistics(@PathVariable final Long companyId, final Authentication connectedUser) {
    return statisticsService.getStatistics(connectedUser, companyId);
  }
}
