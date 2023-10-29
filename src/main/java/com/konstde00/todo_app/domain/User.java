package com.konstde00.todo_app.domain;

import static jakarta.persistence.EnumType.STRING;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.konstde00.todo_app.config.Constants;
import com.konstde00.todo_app.domain.enums.FeatureFlag;
import com.konstde00.todo_app.domain.enums.UserRegistrationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Entity
@Cacheable
@NoArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity<String> implements Serializable {

  @Id String id;

  @NotNull
  @Pattern(regexp = Constants.LOGIN_REGEX)
  @Size(min = 1, max = 50)
  @Column(length = 50, unique = true, nullable = false)
  String login;

  @JsonIgnore
  @Column(name = "password_hash")
  String password;

  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  String firstName;

  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  String lastName;

  @Email
  @Size(min = 5, max = 254)
  @Column(length = 254, unique = true)
  String email;

  @NotNull
  @Column(nullable = false)
  boolean activated = false;

  @Size(min = 2, max = 10)
  @Column(name = "lang_key", length = 10)
  String langKey;

  @OneToOne
  @JoinColumn(name = "file_id")
  File image;

  @Size(max = 20)
  @Column(name = "activation_key", length = 20)
  @JsonIgnore
  String activationKey;

  @Size(max = 20)
  @Column(name = "reset_key", length = 20)
  @JsonIgnore
  String resetKey;

  @Column(name = "reset_date")
  Instant resetDate = null;

  @Enumerated(STRING)
  @Column(name = "registration_type")
  UserRegistrationType registrationType;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_authorities",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
  @BatchSize(size = 20)
  Set<Authority> authorities = new HashSet<>();

  @ElementCollection(targetClass = FeatureFlag.class, fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @Column(table = "users", name = "feature_flags")
  Set<FeatureFlag> featureFlags = new HashSet<>();

  public User(String id) {
    this.id = id;
  }
}
