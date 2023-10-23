package com.konstde00.todo_app.web.rest.vm;

import jakarta.validation.constraints.NotNull;

/** View Model object for storing a user's credentials. */
public class LoginByGoogleVM {

  @NotNull private String idToken;

  public String getIdToken() {
    return idToken;
  }

  public void setIdToken(String idToken) {
    this.idToken = idToken;
  }
}
