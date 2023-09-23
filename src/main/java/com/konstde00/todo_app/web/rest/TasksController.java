package com.konstde00.todo_app.web.rest;

import com.konstde00.todo_app.service.api.dto.Task;
import com.konstde00.todo_app.web.api.TasksApi;
import java.util.List;
import org.springframework.http.ResponseEntity;

public class TasksController implements TasksApi {

  @Override
  public ResponseEntity<List<Task>> getTasks() throws Exception {
    return TasksApi.super.getTasks();
  }

  @Override
  public ResponseEntity<Void> createTask(Task task) throws Exception {
    return TasksApi.super.createTask(task);
  }
}
