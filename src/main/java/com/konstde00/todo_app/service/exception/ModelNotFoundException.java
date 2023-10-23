package com.konstde00.todo_app.service.exception;

public class ModelNotFoundException extends RuntimeException {

  public ModelNotFoundException(String message) {
    super(message);
  }

  public ModelNotFoundException(Integer modelId) {
    super("Model with id: " + modelId + " not found");
  }

  public ModelNotFoundException(Long modelId) {
    super("Model with id: " + modelId + " not found");
  }
}
