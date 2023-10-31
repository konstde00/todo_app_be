package com.konstde00.todo_app.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.service.UserService;
import com.konstde00.todo_app.service.dto.UserProfileDto;
import com.konstde00.todo_app.service.exception.ForbiddenException;
import com.konstde00.todo_app.service.mapper.UserMapper;
import com.konstde00.todo_app.web.rest.vm.LoginByEmailAndPasswordVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/** Controller to authenticate users. */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

  private final Logger log = LoggerFactory.getLogger(AuthenticateController.class);

  private final UserMapper userMapper;
  private final UserService userService;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  public AuthenticateController(
      UserMapper userMapper,
      UserService userService,
      AuthenticationManagerBuilder authenticationManagerBuilder) {
    this.userMapper = userMapper;
    this.userService = userService;
    this.authenticationManagerBuilder = authenticationManagerBuilder;
  }

  @PostMapping("/authenticate/email")
  public ResponseEntity<UserProfileDto> authorizeEmailPassword(
      @Valid @RequestBody LoginByEmailAndPasswordVM loginVM) throws ForbiddenException {

    User user =
        userService
            .getUserWithAuthoritiesByEmail(loginVM.getEmail())
            .orElseThrow(() -> new ForbiddenException("User not found"));

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginVM.getEmail(), loginVM.getPassword());

    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = userService.createToken(authentication, loginVM.isRememberMe());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(jwt);

    UserProfileDto userProfileDto = userMapper.toUserProfileDto(user);
    userProfileDto.setToken(jwt);
    return new ResponseEntity<>(userProfileDto, httpHeaders, HttpStatus.OK);
  }

  /**
   * {@code GET /authenticate} : check if the user is authenticated, and return its login.
   *
   * @param request the HTTP request.
   * @return the login if the user is authenticated.
   */
  @GetMapping("/authenticate")
  public String isAuthenticated(HttpServletRequest request) {
    log.debug("REST request to check if the current user is authenticated");
    return request.getRemoteUser();
  }

  @PatchMapping("/users/sync-with-idp/google")
  public ResponseEntity<?> syncWithIdp(@RequestParam String token) {
    userService.syncWithIdp(token);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PatchMapping("/users/sync-with-idp/msal")
  public ResponseEntity<?> syncWithMsal(@RequestParam String token) {
    userService.syncWithMsal(token);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  /** Object to return as body in JWT Authentication. */
  static class JWTToken {

    private String token;

    JWTToken(String token) {
      this.token = token;
    }

    @JsonProperty("token")
    String getToken() {
      return token;
    }

    void setToken(String token) {
      this.token = token;
    }
  }
}
