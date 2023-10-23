package com.konstde00.todo_app.service.exception;

public class UsernameAlreadyUsedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UsernameAlreadyUsedException() {
    super("Login name already used!");
  }
}
