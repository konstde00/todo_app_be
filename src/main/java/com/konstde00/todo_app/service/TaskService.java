package com.konstde00.todo_app.service;

import com.konstde00.todo_app.domain.Task;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.domain.enums.Priority;
import com.konstde00.todo_app.domain.enums.Status;
import com.konstde00.todo_app.repository.TaskRepository;
import com.konstde00.todo_app.service.api.dto.*;
import com.konstde00.todo_app.service.exception.ForbiddenException;
import com.konstde00.todo_app.service.mapper.TaskMapper;
import com.mysql.cj.exceptions.WrongArgumentException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {

  UserService userService;
  TaskRepository taskRepository;

  public static final String TASK_CACHE_NAME = "task";
  public static final String TASKS_CACHE_NAME = "tasks";

  @Cacheable(cacheNames = TASK_CACHE_NAME, key = "#id", unless = "#result == null")
  public Task getById(Long id) {

    if (id == null) {
      throw new WrongArgumentException("Task id could not be null!");
    }

    return taskRepository
        .findById(id)
        .orElseThrow(() -> new WrongArgumentException("Task with id " + id + " not found!"));
  }

  public GetTasksResponseDto getTasks(
      User currentUser, Integer pageNumber, Integer pageSize, TaskStatusEnum status) {

    PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

    Page<Task> tasksPage =
        taskRepository.findAllByCreatedBy(
            currentUser.getId(), Status.valueOf(status.toString()), pageRequest);

    List<TaskItem> items = TaskMapper.INSTANCE.toTaskItems(tasksPage.getContent());

    CommonIterableResponseMetadata metadata =
        new CommonIterableResponseMetadata()
            .pagination(
                new PaginationMetadata()
                    .totalCount(tasksPage.getTotalElements())
                    .totalPageCount(tasksPage.getTotalPages())
                    .pageSize(tasksPage.getSize())
                    .currentPageSize(tasksPage.getNumberOfElements())
                    .currentPageNumber(tasksPage.getNumber()));

    return new GetTasksResponseDto().items(items).metadata(metadata);
  }

  public CreateTaskResponseDto createTask(CreateTaskRequestDto createTaskRequestDto) {

    User user = userService.getCurrentUserWithAuthorities();

    validateTask(createTaskRequestDto, user);

    Task inputTask = TaskMapper.INSTANCE.toEntity(createTaskRequestDto);

    inputTask.setCreatedBy(user);
    inputTask.setLastModifiedBy(user);

    Integer currentMaxPosition =
        taskRepository.getMaxPositionByCreatedByAndStatus(
            user.getId(), Status.valueOf(createTaskRequestDto.getStatus().toString()));
    inputTask.setPosition(currentMaxPosition == null ? 0 : currentMaxPosition + 1);

    Task createdTask = taskRepository.saveAndFlush(inputTask);

    return TaskMapper.INSTANCE.toCreateTaskResponseDto(createdTask);
  }

  @Transactional
  public UpdateTaskResponseDto updateTask(Long taskId, UpdateTaskRequestDto updateTaskRequestDto) {

    log.info("updateTask: taskId = {}, updateTaskRequestDto = {}", taskId, updateTaskRequestDto);

    User user = userService.getCurrentUserWithAuthorities();
    Task task = getById(taskId);

    checkAccessToTask(task, user);

    if (updateTaskRequestDto.getTitle() != null) task.setTitle(updateTaskRequestDto.getTitle());
    if (task.getDescription() != null) task.setDescription(updateTaskRequestDto.getDescription());
    if (updateTaskRequestDto.getPosition() != null
        && !updateTaskRequestDto.getPosition().equals(task.getPosition())) {
      reorderTaskAfterPositionUpdate(user, task, updateTaskRequestDto.getPosition());
      task.setPosition(updateTaskRequestDto.getPosition());
    }
    if (updateTaskRequestDto.getPriority() != null) {
      task.setPriority(Priority.valueOf(updateTaskRequestDto.getPriority().toString()));
    }
    if (updateTaskRequestDto.getStatus() != null) {
      task.setStatus(Status.valueOf(updateTaskRequestDto.getStatus().toString()));
      reorderTasksAfterStatusUpdate(user, task, updateTaskRequestDto.getStatus());
    }
    task.setLastModifiedBy(user);

    Task updatedTask = taskRepository.saveAndFlush(task);

    return TaskMapper.INSTANCE.toUpdateTaskResponseDto(updatedTask);
  }

  public void reorderTaskAfterPositionUpdate(User user, Task task, Integer newPosition) {

    log.info(
        "reorderTaskAfterPositionUpdate: oldPosition = {}, newPosition = {}",
        task.getPosition(),
        newPosition);

    Integer oldPosition = task.getPosition();

    if (oldPosition.equals(newPosition)) {
      return;
    }

    if (oldPosition < newPosition) {
      taskRepository.decrementPositionsBetween(
          oldPosition + 1, newPosition, Status.valueOf(task.getStatus().toString()), user.getId());
    } else {
      taskRepository.incrementPositionsBetween(
          newPosition, oldPosition - 1, Status.valueOf(task.getStatus().toString()), user.getId());
    }
  }

  public void reorderTasksAfterStatusUpdate(User user, Task task, TaskStatusEnum newStatus) {

    log.info(
        "reorderTasksAfterStatusUpdate: oldStatus = {}, newStatus = {}",
        task.getStatus(),
        newStatus);

    Status oldStatus = task.getStatus();

    if (oldStatus.equals(Status.valueOf(newStatus.toString()))) {
      return;
    }

    // 1. Need to decrement positions of tasks with oldStatus and position > task.position
    // 2. Need to increment positions of tasks with newStatus and position >= task.position

    if (oldStatus.ordinal() < Status.valueOf(newStatus.toString()).ordinal()) {
      taskRepository.decrementPositionsBetween(
          task.getPosition() + 1,
          taskRepository.getMaxPositionByCreatedByAndStatus(user.getId(), oldStatus),
          oldStatus,
          user.getId());
      taskRepository.incrementPositionsBetween(
          task.getPosition(),
          taskRepository.getMaxPositionByCreatedByAndStatus(
              user.getId(), Status.valueOf(newStatus.toString())),
          Status.valueOf(newStatus.toString()),
          user.getId());
    } else {
      taskRepository.incrementPositionsBetween(
          task.getPosition(),
          taskRepository.getMaxPositionByCreatedByAndStatus(user.getId(), oldStatus),
          oldStatus,
          user.getId());
      taskRepository.decrementPositionsBetween(
          task.getPosition() + 1,
          taskRepository.getMaxPositionByCreatedByAndStatus(
              user.getId(), Status.valueOf(newStatus.toString())),
          Status.valueOf(newStatus.toString()),
          user.getId());
    }
  }

  @Caching(
      evict = {
        @CacheEvict(cacheNames = TASK_CACHE_NAME, key = "#id"),
        @CacheEvict(cacheNames = TASKS_CACHE_NAME, allEntries = true)
      })
  public void deleteTask(Long id) {

    User user = userService.getCurrentUserWithAuthorities();
    Task task = getById(id);

    checkAccessToTask(task, user);

    taskRepository.deleteById(id);
  }

  private void validateTask(CreateTaskRequestDto requestDto, User user) {

    if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
      throw new WrongArgumentException("Title is empty");
    }

    if (taskRepository.existsTaskByTitleAndCreatedBy(requestDto.getTitle(), user)) {
      throw new WrongArgumentException("Task with this title already exists");
    }
  }

  @SneakyThrows
  private void checkAccessToTask(Task task, User user) {

    if (!task.getCreatedBy().equals(user)) {
      throw new ForbiddenException("You have no access to this task");
    }
  }
}
