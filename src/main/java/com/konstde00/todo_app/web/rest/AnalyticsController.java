package com.konstde00.todo_app.web.rest;

import com.konstde00.todo_app.service.AnalyticsService;
import com.konstde00.todo_app.service.api.dto.GetPercentageByStatusResponseDto;
import com.konstde00.todo_app.service.api.dto.GetStatusByPriorityResponseDto;
import com.konstde00.todo_app.web.api.AnalyticsApi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyticsController implements AnalyticsApi {

  AnalyticsService analyticsService;

  @RequestMapping(
      method = RequestMethod.GET,
      value = "/api/tasks/analytics/percentage-by-status",
      produces = {"application/json"})
  public ResponseEntity<GetPercentageByStatusResponseDto> getPercentageByStatus() {

    GetPercentageByStatusResponseDto percentageByStatus = analyticsService.getPercentageByStatus();

    return ResponseEntity.ok(percentageByStatus);
  }

  @Override
  public ResponseEntity<GetStatusByPriorityResponseDto> getStatusByPriority() {

    GetStatusByPriorityResponseDto statusByPriority = analyticsService.getStatusByPriority();

    return ResponseEntity.ok(statusByPriority);
  }
}
