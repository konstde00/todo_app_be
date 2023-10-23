package com.konstde00.todo_app.service.exception;

public class InvalidPasswordException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InvalidPasswordException() {
    super("Incorrect password");
  }
}
