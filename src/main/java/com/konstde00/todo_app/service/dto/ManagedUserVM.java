package com.konstde00.todo_app.service.dto;

import jakarta.validation.constraints.Size;

/** View Model extending the AdminUserDTO, which is meant to be used in the user management UI. */
public class ManagedUserVM extends UserProfileDto {

  public static final int PASSWORD_MIN_LENGTH = 4;

  public static final int PASSWORD_MAX_LENGTH = 100;

  @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
  private String password;

  public ManagedUserVM() {
    super();
    // Empty constructor needed for Jackson.
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "ManagedUserVM{" + super.toString() + "} ";
  }
}
