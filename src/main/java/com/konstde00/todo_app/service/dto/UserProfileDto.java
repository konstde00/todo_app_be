package com.konstde00.todo_app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto implements Serializable {

  private static final long serialVersionUID = 1L;

  String id;

  String token;

  String login;

  String password;

  @Size(max = 50)
  String firstName;

  @Size(max = 50)
  String lastName;

  @Email
  @Size(min = 5, max = 254)
  String email;

  @Size(max = 256)
  String imageUrl;

  boolean activated;

  @Size(min = 2, max = 10)
  String langKey;

  String createdBy;

  Instant createdDate;

  String lastModifiedBy;

  Instant lastModifiedDate;

  Set<String> authorities;
  Set<Integer> featureFlags;
}
