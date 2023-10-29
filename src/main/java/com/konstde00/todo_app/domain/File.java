package com.konstde00.todo_app.domain;

import static lombok.AccessLevel.PRIVATE;

import com.konstde00.todo_app.domain.enums.ProfileImageOrigin;
import com.konstde00.todo_app.domain.util.HashMapConverter;
import jakarta.persistence.*;
import java.util.Map;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
@FieldDefaults(level = PRIVATE)
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "origin")
  @Enumerated(EnumType.STRING)
  ProfileImageOrigin origin;

  String url;

  @Column(name = "bucket_name")
  String bucketName;

  @Column(name = "file_key")
  String key;

  @Column(name = "content_type")
  String contentType;

  @Transient
  @Convert(converter = HashMapConverter.class)
  Map<String, String> metadata;
}
