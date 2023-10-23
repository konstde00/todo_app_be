package com.konstde00.todo_app.service.exception;

public class BadRequestException extends RuntimeException {

  public BadRequestException(String reason) {
    super(reason);
  }
}
