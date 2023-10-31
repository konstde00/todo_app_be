package com.konstde00.todo_app.web.rest;

import com.konstde00.todo_app.domain.Task;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.service.GoogleDriveService;
import com.konstde00.todo_app.service.TaskService;
import com.konstde00.todo_app.service.UserService;
import com.konstde00.todo_app.service.api.dto.*;
import com.konstde00.todo_app.service.mapper.TaskMapper;
import com.konstde00.todo_app.web.api.TasksApi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TasksController implements TasksApi {

  TaskService taskService;
  UserService userService;
  GoogleDriveService googleDriveService;

  @Override
  public ResponseEntity<GetTasksResponseDto> getTasks(
      TaskStatusEnum status, Integer pageNumber, Integer pageSize) {

    User currentUser = userService.getCurrentUserWithAuthorities();

    GetTasksResponseDto tasks = taskService.getTasks(currentUser, pageNumber, pageSize, status);

    return ResponseEntity.ok(tasks);
  }

  @Override
  public ResponseEntity<GetTaskResponseDto> getTask(Long taskId) {

    Task task = taskService.getById(taskId);

    GetTaskResponseDto responseDto = TaskMapper.INSTANCE.toGetTaskResponseDto(task);

    return ResponseEntity.ok(responseDto);
  }

  @Override
  public ResponseEntity<CreateTaskResponseDto> createTask(
      CreateTaskRequestDto createTaskRequestDto) {

    CreateTaskResponseDto createdTask = taskService.createTask(createTaskRequestDto);

    return ResponseEntity.ok(createdTask);
  }

  @Override
  public ResponseEntity<UpdateTaskResponseDto> updateTask(
      Long taskId, UpdateTaskRequestDto updateTaskRequestDto) {

    UpdateTaskResponseDto updatedTask = taskService.updateTask(taskId, updateTaskRequestDto);

    return ResponseEntity.ok(updatedTask);
  }

  @PostMapping("/api/tasks/v1/upload")
  public ResponseEntity<String> uploadFileToGoogleDrive(@RequestParam Long taskId,
                                                        @RequestParam MultipartFile file) {

    return ResponseEntity.ok(googleDriveService.uploadFile(taskId, file));
  }

  @Override
  public ResponseEntity<Void> deleteTask(Long taskId) {

    taskService.deleteTask(taskId);

    return ResponseEntity.ok().build();
  }
}
