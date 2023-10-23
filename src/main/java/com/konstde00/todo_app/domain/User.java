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
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity<String> implements Serializable {

  static final long serialVersionUID = 1L;

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

  @Size(max = 256)
  @Column(name = "image_url", length = 256)
  String imageUrl;

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public String getResetKey() {
    return resetKey;
  }

  public void setResetKey(String resetKey) {
    this.resetKey = resetKey;
  }

  public Instant getResetDate() {
    return resetDate;
  }

  public void setResetDate(Instant resetDate) {
    this.resetDate = resetDate;
  }

  public String getLangKey() {
    return langKey;
  }

  public void setLangKey(String langKey) {
    this.langKey = langKey;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  public Set<FeatureFlag> getFeatureFlags() {
    return featureFlags;
  }

  public void setFeatureFlags(Set<FeatureFlag> featureFlags) {
    this.featureFlags = featureFlags;
  }

  public UserRegistrationType getRegistrationType() {
    return registrationType;
  }

  public void setRegistrationType(UserRegistrationType registrationType) {
    this.registrationType = registrationType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    return id != null && id.equals(((User) o).id);
  }

  @Override
  public int hashCode() {
    // see
    // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }
}
