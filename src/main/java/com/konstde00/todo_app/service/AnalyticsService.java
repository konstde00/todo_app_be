package com.konstde00.todo_app.service;

import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.repository.rds.TaskRepository;
import com.konstde00.todo_app.service.api.dto.*;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyticsService {

  UserService userService;
  TaskRepository taskRepository;

  public GetPercentageByStatusResponseDto getPercentageByStatus() {

    User user = userService.getCurrentUserWithAuthorities();

    List<PercentageByStatusItem> items =
        taskRepository.getPercentageByStatus(user.getId()).stream()
            .map(
                row ->
                    new PercentageByStatusItem()
                        .percentage((Float) row[1])
                        .status(TaskStatusEnum.valueOf(row[0].toString())))
            .collect(Collectors.toList());

    return new GetPercentageByStatusResponseDto().items(items);
  }

  public GetStatusByPriorityResponseDto getStatusByPriority() {

    User user = userService.getCurrentUserWithAuthorities();

    List<StatusByPriorityItem> items =
        taskRepository.getStatusByPriority(user.getId()).stream()
            .collect(
                Collectors.groupingBy(
                    row -> TaskPriorityEnum.valueOf(row[0].toString()),
                    Collectors.mapping(
                        row ->
                            new StatusItem()
                                .status(TaskStatusEnum.valueOf(row[1].toString()))
                                .count((Long) row[2]),
                        Collectors.toList())))
            .entrySet()
            .stream()
            .map(
                entry ->
                    new StatusByPriorityItem()
                        .priority(entry.getKey())
                        .statusItems(entry.getValue()))
            .collect(Collectors.toList());

    return new GetStatusByPriorityResponseDto().items(items);
  }
}
