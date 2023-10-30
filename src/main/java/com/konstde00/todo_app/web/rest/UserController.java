package com.konstde00.todo_app.web.rest;

import com.konstde00.todo_app.config.Constants;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.repository.UserRepository;
import com.konstde00.todo_app.security.AuthoritiesConstants;
import com.konstde00.todo_app.service.UserService;
import com.konstde00.todo_app.service.dto.PaginatedUsersResponseDto;
import com.konstde00.todo_app.service.dto.UploadFileResponseDto;
import com.konstde00.todo_app.service.dto.UserProfileDto;
import com.konstde00.todo_app.service.exception.BadRequestException;
import com.konstde00.todo_app.service.mapper.UserMapper;
import com.konstde00.todo_app.web.rest.errors.BadRequestAlertException;
import com.konstde00.todo_app.web.rest.errors.EmailAlreadyUsedException;
import com.konstde00.todo_app.web.rest.errors.LoginAlreadyUsedException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the {@link com.konstde00.todo_app.domain.User} entity, and needs to fetch
 * its collection of authorities.
 *
 * <p>For a normal use-case, it would be better to have an eager relationship between User and
 * Authority, and send everything to the client side: there would be no View Model and DTO, a lot
 * less code, and an outer-join which would be good for performance.
 *
 * <p>We use a View Model and a DTO for 3 reasons:
 *
 * <ul>
 *   <li>We want to keep a lazy association between the user and the authorities, because people
 *       will quite often do relationships with the user, and we don't want them to get the
 *       authorities all the time for nothing (for performance reasons). This is the #1 goal: we
 *       should not impact our users' application because of this use-case.
 *   <li>Not having an outer join causes n+1 requests to the database. This is not a real issue as
 *       we have by default a second-level cache. This means on the first HTTP call we do the n+1
 *       requests, but then all authorities come from the cache, so in fact it's much better than
 *       doing an outer join (which will get lots of data from the database, for each HTTP call).
 *   <li>As this manages users, for security reasons, we'd rather have a DTO layer.
 * </ul>
 *
 * <p>Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserController {

  private final Logger log = LoggerFactory.getLogger(UserController.class);

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final UserMapper userMapper;
  private final UserService userService;
  private final UserRepository userRepository;

  public UserController(
      UserMapper userMapper, UserService userService, UserRepository userRepository) {
    this.userMapper = userMapper;
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @GetMapping("/admin/v1/users")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<PaginatedUsersResponseDto> getAllUsers(
      @RequestParam(name = "search", required = false, defaultValue = StringUtils.EMPTY)
          String search,
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

    PaginatedUsersResponseDto responseDto =
        userService.getAllManagedUsers(search, pageNumber, pageSize);

    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping("/users/{userId}")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<UserProfileDto> getUserById(@PathVariable String userId) {

    UserProfileDto profile = userService.getProfileById(userId);

    return new ResponseEntity<>(profile, HttpStatus.OK);
  }

  /**
   * {@code POST /admin/users} : Creates a new user.
   *
   * <p>Creates a new user if the login and email are not already used, and sends an mail with an
   * activation link. The user needs to be activated on creation.
   *
   * @param userDTO the user to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   *     user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in
   *     use.
   */
  @PostMapping("/admin/users")
  public ResponseEntity<UserProfileDto> createUser(@Valid @RequestBody UserProfileDto userDTO)
      throws URISyntaxException {
    log.debug("REST request to save User : {}", userDTO);

    if (userDTO.getId() != null) {
      throw new BadRequestAlertException(
          "A new user cannot already have an ID", "userManagement", "idexists");
      // Lowercase the user login before comparing with database
    } else if (userRepository.findOneByEmail(userDTO.getEmail()).isPresent()) {
      throw new BadRequestException("Email has been already used");
    } else {
      User createdUser = userService.createUser(userDTO);

      UserProfileDto responseDto = userMapper.toUserProfileDto(createdUser);
      responseDto.setToken(userDTO.getToken());
      responseDto.setToken(userService.createToken(createdUser));

      return ResponseEntity.created(new URI("/api/admin/users/" + responseDto.getLogin()))
          .body(responseDto);
    }
  }

  /**
   * {@code PUT /admin/users} : Updates an existing User.
   *
   * @param userDTO the user to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
   * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
   * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
   */
  @PutMapping("/admin/users")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<UserProfileDto> updateUser(@Valid @RequestBody UserProfileDto userDTO) {
    log.debug("REST request to update User : {}", userDTO);
    Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
    if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
      throw new EmailAlreadyUsedException();
    }
    existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
    if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
      throw new LoginAlreadyUsedException();
    }
    Optional<UserProfileDto> updatedUser = userService.updateUser(userDTO);

    return ResponseUtil.wrapOrNotFound(
        updatedUser,
        HeaderUtil.createAlert(
            applicationName,
            "A user is updated with identifier " + userDTO.getLogin(),
            userDTO.getLogin()));
  }

  @PatchMapping("/admin/v1/feature-flag")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<?> updateFeatureFlag(
      @RequestParam String userId,
      @RequestParam Integer featureFlagId,
      @RequestParam Boolean isSelected) {

    UserProfileDto profile = userService.updateFeatureFlag(userId, featureFlagId, isSelected);

    return new ResponseEntity<>(profile, HttpStatus.ACCEPTED);
  }

  @PatchMapping("/users/v1")
  @Operation(summary = "Update user")
  public ResponseEntity<?> update(@RequestParam(required = false) String name) {

    userService.update(name);

    return ResponseEntity.ok().build();
  }

  @PostMapping("users/v1/profile/picture")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
  public ResponseEntity<UploadFileResponseDto> uploadAvatar(
      @RequestParam("photo") MultipartFile photo) {

    String imageUrl = userService.uploadAvatar(photo);

    return new ResponseEntity<>(new UploadFileResponseDto(imageUrl), HttpStatus.OK);
  }

  /**
   * {@code DELETE /admin/users/:login} : delete the "login" User.
   *
   * @param login the login of the user to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/admin/users/{login}")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<Void> deleteUser(
      @PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
    log.debug("REST request to delete User: {}", login);
    userService.deleteUser(login);
    return ResponseEntity.noContent()
        .headers(
            HeaderUtil.createAlert(
                applicationName, "A user is deleted with identifier " + login, login))
        .build();
  }
}
