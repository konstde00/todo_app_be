package com.konstde00.todo_app.service.mapper;

import com.konstde00.todo_app.domain.Task;
import com.konstde00.todo_app.domain.opensearch.TaskDocument;
import com.konstde00.todo_app.service.api.dto.*;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {

  public static TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

  public abstract Task toEntity(CreateTaskRequestDto createTaskRequestDto);

  public abstract List<TaskItem> toTaskItems(List<TaskDocument> tasks);

  @Mapping(
      target = "status",
      expression = "java(TaskStatusEnum.valueOf(task.getStatus().toString()))")
  @Mapping(
      target = "priority",
      expression = "java(TaskPriorityEnum.valueOf(task.getPriority().toString()))")
  public abstract TaskItem toTaskItem(Task task);

  @Mapping(
      target = "status",
      expression = "java(TaskStatusEnum.valueOf(task.getStatus().toString()))")
  @Mapping(
      target = "priority",
      expression = "java(TaskPriorityEnum.valueOf(task.getPriority().toString()))")
  public abstract GetTaskResponseDto toGetTaskResponseDto(Task task);

  @Mapping(
      target = "status",
      expression = "java(TaskStatusEnum.valueOf(task.getStatus().toString()))")
  @Mapping(
      target = "priority",
      expression = "java(TaskPriorityEnum.valueOf(task.getPriority().toString()))")
  public abstract CreateTaskResponseDto toCreateTaskResponseDto(Task task);

  @Mapping(
      target = "status",
      expression = "java(TaskStatusEnum.valueOf(task.getStatus().toString()))")
  @Mapping(
      target = "priority",
      expression = "java(TaskPriorityEnum.valueOf(task.getPriority().toString()))")
  public abstract UpdateTaskResponseDto toUpdateTaskResponseDto(Task task);
}
