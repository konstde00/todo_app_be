package com.konstde00.todo_app.service.exception;

public class EntityAlreadyExistsException extends Exception {

  public EntityAlreadyExistsException(String message) {
    super(message);
  }
}
