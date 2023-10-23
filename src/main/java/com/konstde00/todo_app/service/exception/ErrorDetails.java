package com.konstde00.todo_app.service.exception;

import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ErrorDetails {

  private Date timestamp;
  private String message;
  private String details;
  private ValidationReport validationReport;

  public ErrorDetails(Date timestamp, String message, String details) {
    this.timestamp = timestamp;
    this.message = message;
    this.details = details;
  }
}
