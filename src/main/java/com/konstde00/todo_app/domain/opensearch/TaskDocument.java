package com.konstde00.todo_app.domain.opensearch;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import com.konstde00.todo_app.domain.enums.Priority;
import com.konstde00.todo_app.domain.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "tasks")
@FieldDefaults(level = PRIVATE)
public class TaskDocument {

  @Id String id;

  String title;

  String description;

  @Enumerated(STRING)
  Status status;

  @Enumerated(STRING)
  Priority priority;

  @Column Integer position;
}
