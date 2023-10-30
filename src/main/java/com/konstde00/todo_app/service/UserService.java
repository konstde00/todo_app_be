package com.konstde00.todo_app.service;

import static com.konstde00.todo_app.security.SecurityUtils.AUTHORITIES_KEY;
import static com.konstde00.todo_app.security.SecurityUtils.JWT_ALGORITHM;

import com.konstde00.todo_app.config.Constants;
import com.konstde00.todo_app.domain.Authority;
import com.konstde00.todo_app.domain.File;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.domain.enums.FeatureFlag;
import com.konstde00.todo_app.domain.enums.UserRegistrationType;
import com.konstde00.todo_app.repository.AuthorityRepository;
import com.konstde00.todo_app.repository.UserRepository;
import com.konstde00.todo_app.security.AuthoritiesConstants;
import com.konstde00.todo_app.security.SecurityUtils;
import com.konstde00.todo_app.service.api.dto.CommonIterableResponseMetadata;
import com.konstde00.todo_app.service.api.dto.PaginationMetadata;
import com.konstde00.todo_app.service.dto.PaginatedUsersResponseDto;
import com.konstde00.todo_app.service.dto.UserDTO;
import com.konstde00.todo_app.service.dto.UserProfileDto;
import com.konstde00.todo_app.service.exception.BadRequestException;
import com.konstde00.todo_app.service.exception.EmailAlreadyUsedException;
import com.konstde00.todo_app.service.exception.InvalidPasswordException;
import com.konstde00.todo_app.service.exception.UsernameAlreadyUsedException;
import com.konstde00.todo_app.service.mapper.UserMapper;
import com.konstde00.todo_app.service.password_reset.PasswordResetProducer;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.security.RandomUtil;

/** Service class for managing users. */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final AuthorityRepository authorityRepository;
  private final PasswordResetProducer passwordResetProducer;

  private final CacheManager cacheManager;
  private final FileService fileService;
  private final JwtDecoder jwtDecoder;
  private final JwtEncoder jwtEncoder;
  private final UserMapper userMapper;

  @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
  private long tokenValidityInSeconds;

  @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
  private long tokenValidityInSecondsForRememberMe;

  public static final String USER_PROFILE_BY_ID_CACHE_NAME = "userProfileById";

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthorityRepository authorityRepository,
      PasswordResetProducer passwordResetProducer,
      CacheManager cacheManager,
      FileService fileService,
      JwtDecoder jwtDecoder,
      JwtEncoder jwtEncoder,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authorityRepository = authorityRepository;
    this.passwordResetProducer = passwordResetProducer;
    this.cacheManager = cacheManager;
    this.fileService = fileService;
    this.jwtDecoder = jwtDecoder;
    this.jwtEncoder = jwtEncoder;
    this.userMapper = userMapper;
  }

  @Cacheable(cacheNames = USER_PROFILE_BY_ID_CACHE_NAME, key = "#id", unless = "#result == null")
  public UserProfileDto getProfileById(String id) {

    return userRepository
        .findById(id)
        .map(userMapper::toUserProfileDto)
        .orElseThrow(() -> new BadRequestException("Not found a user with id " + id));
  }

  public Optional<User> activateRegistration(String key) {
    log.debug("Activating user for activation key {}", key);
    return userRepository
        .findOneByActivationKey(key)
        .map(
            user -> {
              // activate given user for the registration key.
              user.setRegistrationType(UserRegistrationType.EMAIL_AND_PASSWORD);
              user.setActivated(true);
              user.setActivationKey(null);
              this.clearUserCaches(user);
              log.debug("Activated user: {}", user);
              return user;
            });
  }

  public void update(String name) {

    var user = getCurrentUserWithAuthorities();
    if (name != null) {
      user.setFirstName(name);
    }

    userRepository.saveAndFlush(user);
  }

  public Optional<User> completePasswordReset(String newPassword, String key) {
    log.debug("Reset user password for reset key {}", key);
    return userRepository
        .findOneByResetKey(key)
        .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
        .map(
            user -> {
              user.setPassword(passwordEncoder.encode(newPassword));
              user.setResetKey(null);
              user.setResetDate(null);
              this.clearUserCaches(user);
              return user;
            });
  }

  public Optional<User> requestPasswordReset(String mail) {
    return userRepository
        .findOneByEmailIgnoreCase(mail)
        .filter(User::isActivated)
        .map(
            user -> {
              user.setResetKey(RandomUtil.generateResetKey());
              user.setResetDate(Instant.now());
              passwordResetProducer.sendPasswordResetRequest(
                  new com.konstde00.todo_app.service.dto.PasswordResetMessage(
                      user.getEmail(), user.getResetKey()));
              this.clearUserCaches(user);
              return user;
            });
  }

  public User registerUser(UserProfileDto userDTO, String password) {
    userRepository
        .findOneByLogin(userDTO.getLogin().toLowerCase())
        .ifPresent(
            existingUser -> {
              boolean removed = removeNonActivatedUser(existingUser);
              if (!removed) {
                throw new UsernameAlreadyUsedException();
              }
            });
    userRepository
        .findOneByEmailIgnoreCase(userDTO.getEmail())
        .ifPresent(
            existingUser -> {
              boolean removed = removeNonActivatedUser(existingUser);
              if (!removed) {
                throw new EmailAlreadyUsedException();
              }
            });
    User newUser = new User();
    newUser.setRegistrationType(UserRegistrationType.EMAIL_AND_PASSWORD);
    String encryptedPassword = passwordEncoder.encode(password);
    newUser.setLogin(userDTO.getLogin().toLowerCase());
    // new user gets initially a generated password
    newUser.setPassword(encryptedPassword);
    newUser.setFirstName(userDTO.getFirstName());
    newUser.setLastName(userDTO.getLastName());
    if (userDTO.getEmail() != null) {
      newUser.setEmail(userDTO.getEmail().toLowerCase());
    }
    newUser.setLangKey(userDTO.getLangKey());
    // new user is not active
    newUser.setActivated(false);
    // new user gets registration key
    newUser.setActivationKey(RandomUtil.generateActivationKey());
    Set<Authority> authorities = new HashSet<>();
    authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
    newUser.setAuthorities(authorities);
    userRepository.save(newUser);
    this.clearUserCaches(newUser);
    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  private boolean removeNonActivatedUser(User existingUser) {
    if (existingUser.isActivated()) {
      return false;
    }
    userRepository.delete(existingUser);
    userRepository.flush();
    this.clearUserCaches(existingUser);
    return true;
  }

  public User createUser(UserProfileDto userDTO) {
    User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setLogin(
        userDTO.getLogin() == null ? userDTO.getEmail() : userDTO.getLogin().toLowerCase());
    user.setFirstName(userDTO.getFirstName());
    user.setLastName(userDTO.getLastName());
    if (userDTO.getEmail() != null) {
      user.setEmail(userDTO.getEmail().toLowerCase());
    }
    if (userDTO.getLangKey() == null) {
      user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
    } else {
      user.setLangKey(userDTO.getLangKey());
    }
    String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
    user.setPassword(encryptedPassword);
    user.setResetKey(RandomUtil.generateResetKey());
    user.setResetDate(Instant.now());
    user.setActivated(true);
    if (userDTO.getAuthorities() != null) {
      Set<Authority> authorities =
          userDTO.getAuthorities().stream()
              .map(authorityRepository::findById)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toSet());
      user.setAuthorities(authorities);
    }
    User savedUser = userRepository.save(user);
    this.clearUserCaches(user);
    log.debug("Created Information for User: {}", user);
    return savedUser;
  }

  /**
   * Update all information for a specific user, and return the modified user.
   *
   * @param userDTO user to update.
   * @return updated user.
   */
  public Optional<UserProfileDto> updateUser(UserProfileDto userDTO) {
    return Optional.of(userRepository.findById(userDTO.getId()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(
            user -> {
              this.clearUserCaches(user);
              user.setLogin(userDTO.getLogin().toLowerCase());
              user.setFirstName(userDTO.getFirstName());
              user.setLastName(userDTO.getLastName());
              if (userDTO.getEmail() != null) {
                user.setEmail(userDTO.getEmail().toLowerCase());
              }
              user.setActivated(userDTO.isActivated());
              user.setLangKey(userDTO.getLangKey());
              Set<Authority> managedAuthorities = user.getAuthorities();
              managedAuthorities.clear();
              userDTO.getAuthorities().stream()
                  .map(authorityRepository::findById)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .forEach(managedAuthorities::add);
              userRepository.save(user);
              this.clearUserCaches(user);
              log.debug("Changed Information for User: {}", user);
              return user;
            })
        .map(userMapper::toUserProfileDto);
  }

  public void deleteUser(String login) {
    userRepository
        .findOneByLogin(login)
        .ifPresent(
            user -> {
              userRepository.delete(user);
              this.clearUserCaches(user);
              log.debug("Deleted User: {}", user);
            });
  }

  /**
   * Update basic information (first name, last name, email, language) for the current user.
   *
   * @param firstName first name of user.
   * @param lastName last name of user.
   * @param email email id of user.
   * @param langKey language key.
   */
  public void updateUser(String firstName, String lastName, String email, String langKey) {
    SecurityUtils.getCurrentUserLogin()
        .flatMap(userRepository::findOneByLogin)
        .ifPresent(
            user -> {
              user.setFirstName(firstName);
              user.setLastName(lastName);
              if (email != null) {
                user.setEmail(email.toLowerCase());
              }
              user.setLangKey(langKey);
              userRepository.save(user);
              this.clearUserCaches(user);
              log.debug("Changed Information for User: {}", user);
            });
  }

  @Transactional
  public void changePassword(String currentClearTextPassword, String newPassword) {
    SecurityUtils.getCurrentUserLogin()
        .flatMap(userRepository::findOneByLogin)
        .ifPresent(
            user -> {
              String currentEncryptedPassword = user.getPassword();
              if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                throw new InvalidPasswordException();
              }
              String encryptedPassword = passwordEncoder.encode(newPassword);
              user.setPassword(encryptedPassword);
              this.clearUserCaches(user);
              log.debug("Changed password for User: {}", user);
            });
  }

  @Transactional(readOnly = true)
  public PaginatedUsersResponseDto getAllManagedUsers(
      String search, Integer pageNumber, Integer pageSize) {

    PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

    search = search == null ? "" : "%" + search + "%";
    Page<User> page = userRepository.findAll(search, pageRequest);

    List<UserProfileDto> items =
        page.getContent().stream().map(userMapper::toUserProfileDto).collect(Collectors.toList());

    CommonIterableResponseMetadata metadata =
        new CommonIterableResponseMetadata()
            .pagination(
                new PaginationMetadata()
                    .totalCount(page.getTotalElements())
                    .totalPageCount(page.getTotalPages())
                    .pageSize(page.getSize())
                    .currentPageSize(page.getNumberOfElements())
                    .currentPageNumber(page.getNumber()));

    return PaginatedUsersResponseDto.builder().items(items).metadata(metadata).build();
  }

  @Transactional(readOnly = true)
  public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
    return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserWithAuthoritiesByEmail(String email) {
    return userRepository.findOneWithAuthoritiesByEmail(email);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserWithAuthoritiesByLogin(String login) {
    return userRepository.findOneWithAuthoritiesByLogin(login);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserWithAuthorities() {
    return SecurityUtils.getCurrentUserLogin()
        .flatMap(userRepository::findOneWithAuthoritiesByLogin);
  }

  @Transactional(readOnly = true)
  public User getCurrentUserWithAuthorities() {
    return getUserWithAuthoritiesByLogin(
            (String)
                ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getClaims()
                    .get("sub"))
        .orElseThrow(() -> new WrongArgumentException("User not found"));
  }

  /**
   * Not activated users should be automatically deleted after 3 days.
   *
   * <p>This is scheduled to get fired everyday, at 01:00 (am).
   */
  @Scheduled(cron = "0 0 1 * * ?")
  public void removeNotActivatedUsers() {
    userRepository
        .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
            Instant.now().minus(3, ChronoUnit.DAYS))
        .forEach(
            user -> {
              log.debug("Deleting not activated user {}", user.getLogin());
              userRepository.delete(user);
              this.clearUserCaches(user);
            });
  }

  public String uploadAvatar(MultipartFile photo) {

    User currentUser = getCurrentUserWithAuthorities();

    return fileService.updateUsersAvatar(currentUser, photo);
  }

  public UserProfileDto updateFeatureFlag(
      String userId, Integer featureFlagId, Boolean isSelected) {

    User user = userRepository.findById(userId).orElseThrow();

    FeatureFlag featureFlag = FeatureFlag.fromId(featureFlagId);

    if (isSelected && !user.getFeatureFlags().contains(featureFlag)) {

      user.getFeatureFlags().add(featureFlag);

    } else if (!isSelected) {

      user.getFeatureFlags().remove(featureFlag);
    }

    return userMapper.toUserProfileDto(userRepository.saveAndFlush(user));
  }

  /**
   * Gets a list of all the authorities.
   *
   * @return a list of all the authorities.
   */
  @Transactional(readOnly = true)
  public List<String> getAuthorities() {
    return authorityRepository.findAll().stream().map(Authority::getName).toList();
  }

  @Transactional
  public void syncWithIdp(String token) {
    Map<String, Object> attributes = jwtDecoder.decode(token).getClaims();

    User user = getUser(attributes);
    user.setFeatureFlags(Objects.requireNonNullElse(user.getFeatureFlags(), Set.of()));
    if (user.getAuthorities() == null) {
      user.setAuthorities(Set.of(new Authority(AuthoritiesConstants.USER)));
    }

    saveAndFlush(user);
  }

  private User getUser(Map<String, Object> details) {

    String id =
        Optional.ofNullable(details.get("uid"))
            .map(String::valueOf)
            .orElse(String.valueOf(details.get("sub")));

    User user = userRepository.findById(id).orElseGet(() -> new User(id));

    File file = fileService.saveAvatarFromIdp((String) details.get("picture"));
    user.setImage(file);

    Boolean activated = Boolean.TRUE;
    String sub = String.valueOf(details.get("sub"));
    String username = null;
    if (details.get("preferred_username") != null) {
      username = ((String) details.get("preferred_username")).toLowerCase();
    }
    // handle resource server JWT, where sub claim is email and uid is ID
    if (details.get("uid") != null) {
      user.setId((String) details.get("uid"));
      user.setLogin(sub);
    } else {
      user.setId(sub);
    }
    if (username != null) {
      user.setLogin(username);
    } else if (user.getLogin() == null) {
      user.setLogin(user.getId());
    }
    if (details.get("given_name") != null) {
      user.setFirstName((String) details.get("given_name"));
    } else if (details.get("name") != null) {
      user.setFirstName((String) details.get("name"));
    }
    if (details.get("family_name") != null) {
      user.setLastName((String) details.get("family_name"));
    }
    if (details.get("email_verified") != null) {
      activated = (Boolean) details.get("email_verified");
    }
    if (details.get("email") != null) {
      user.setEmail(((String) details.get("email")).toLowerCase());
    } else if (sub.contains("|") && (username != null && username.contains("@"))) {
      // special handling for Auth0
      user.setEmail(username);
    } else {
      user.setEmail(sub);
    }
    if (details.get("langKey") != null) {
      user.setLangKey((String) details.get("langKey"));
    } else if (details.get("locale") != null) {
      // trim off country code if it exists
      String locale = (String) details.get("locale");
      if (locale.contains("_")) {
        locale = locale.substring(0, locale.indexOf('_'));
      } else if (locale.contains("-")) {
        locale = locale.substring(0, locale.indexOf('-'));
      }
      user.setLangKey(locale.toLowerCase());
    } else {
      // set langKey to default if not specified by IdP
      user.setLangKey(Constants.DEFAULT_LANGUAGE);
    }
    user.setActivated(activated);
    return user;
  }

  public String createToken(User user) {
    String authorities =
        user.getAuthorities().stream().map(Authority::getName).collect(Collectors.joining(" "));

    Instant now = Instant.now();
    Instant validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);

    // @formatter:off
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(user.getLogin())
            .claim(AUTHORITIES_KEY, authorities)
            .build();

    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  public String createToken(Authentication authentication, boolean rememberMe) {
    String authorities =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

    Instant now = Instant.now();
    Instant validity;
    if (rememberMe) {
      validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
    } else {
      validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
    }

    // @formatter:off
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .build();

    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  public User saveAndFlush(User user) {
    return userRepository.saveAndFlush(user);
  }

  private void clearUserCaches(User user) {
    Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE))
        .evict(user.getLogin());
    if (user.getEmail() != null) {
      Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE))
          .evict(user.getEmail());
    }
  }
}
