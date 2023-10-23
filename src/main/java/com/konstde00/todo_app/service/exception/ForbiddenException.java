package com.konstde00.todo_app.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception {

  public ForbiddenException(String message) {
    super(message);
  }
}
