package com.konstde00.todo_app.web.rest.vm;

import com.konstde00.todo_app.service.dto.AdminUserDTO;
import jakarta.validation.constraints.Size;

public class ManagedUserVM extends AdminUserDTO {

  public static final int PASSWORD_MIN_LENGTH = 4;

  public static final int PASSWORD_MAX_LENGTH = 100;

  @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
  private String password;

  public ManagedUserVM() {
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
