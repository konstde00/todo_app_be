package com.konstde00.todo_app.service.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.IOException;
import java.util.Date;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

  private static final boolean INCLUDE_CLIENT_INFO = true;

  @ExceptionHandler(EntityAlreadyExistsException.class)
  public final ResponseEntity<ErrorDetails> handleEntityAlreadyExistsException(
      EntityAlreadyExistsException ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    log.warn(
        "Entity Already Exists Exception: {}: {}",
        request.getDescription(INCLUDE_CLIENT_INFO),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
  }

  // Generic Exception handler for the whole project
  @ExceptionHandler(Throwable.class)
  public final ResponseEntity<ErrorDetails> handleGenericException(
      Exception ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(new Date(), "An error occurred", request.getDescription(false));
    log.error("Generic Exception Handler: " + request.getDescription(INCLUDE_CLIENT_INFO), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
  }

  @ExceptionHandler(ModelNotFoundException.class)
  public final ResponseEntity<ErrorDetails> handleModelNotFoundException(
      ModelNotFoundException ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    log.warn(
        "Model or Data Not Found: {}: {}",
        request.getDescription(INCLUDE_CLIENT_INFO),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public final ResponseEntity<ErrorDetails> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    log.warn("Access Denied: {}: {}", request.getDescription(INCLUDE_CLIENT_INFO), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
  }

  @ExceptionHandler({
    BadRequestException.class,
    WrongArgumentException.class,
    IllegalStateException.class,
    IllegalArgumentException.class,
  })
  public final ResponseEntity<ErrorDetails> handleBadRequestException(
      Exception ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    log.warn(
        "Bad Request Exception: {}: {}",
        request.getDescription(INCLUDE_CLIENT_INFO),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
  }

  @ExceptionHandler(IOException.class)
  public final ResponseEntity<ErrorDetails> handleIOException(IOException ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(new Date(), "An error occurred", request.getDescription(false));
    log.error("Generic IOException: " + request.getDescription(INCLUDE_CLIENT_INFO), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
  }

  @ExceptionHandler(JsonProcessingException.class)
  public final ResponseEntity<ErrorDetails> handleJsonProcessingException(
      JsonProcessingException ex, WebRequest request) {
    ErrorDetails errorDetails =
        new ErrorDetails(new Date(), "An error occurred", request.getDescription(false));
    log.error("JsonProcessingException: " + request.getDescription(INCLUDE_CLIENT_INFO), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    log.error(
        "Constraint Violation Exception: {}: {}",
        request.getDescription(INCLUDE_CLIENT_INFO),
        ex.getMessage());

    var errorDetails = new ErrorDetails(new Date(), "Validation errors", ex.getMessage());

    return ResponseEntity.badRequest().body(errorDetails);
  }

  @ExceptionHandler(ForbiddenException.class)
  public final ResponseEntity<ErrorDetails> handleForbidden(
      ForbiddenException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(buildErrorDetails(ex.getMessage(), request.getDescription(false)));
  }

  private static ErrorDetails buildErrorDetails(String message, String details) {
    return new ErrorDetails(new Date(), message, details);
  }
}
