package com.konstde00.todo_app.domain;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import com.konstde00.todo_app.domain.enums.Priority;
import com.konstde00.todo_app.domain.enums.Status;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Entity
@DynamicUpdate
@NoArgsConstructor
@Table(name = "tasks")
@FieldDefaults(level = PRIVATE)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Task {

  @Getter
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String title;

  String description;

  @Enumerated(STRING)
  Status status;

  @Enumerated(STRING)
  Priority priority;

  @Column Integer position;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  Instant createdAt = Instant.now();

  @ManyToOne
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "created_by")
  User createdBy;

  @LastModifiedDate
  @Column(name = "last_modified_at")
  Instant lastModifiedAt = Instant.now();

  @ManyToOne
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "last_modified_by")
  User lastModifiedBy;

  public Long getId() {
    return id;
  }
}
