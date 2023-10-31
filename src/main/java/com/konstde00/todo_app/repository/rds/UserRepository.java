package com.konstde00.todo_app.repository.rds;

import com.konstde00.todo_app.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the {@link User} entity. */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
  String USERS_BY_LOGIN_CACHE = "usersByLogin";

  String USERS_BY_EMAIL_CACHE = "usersByEmail";

  @Query(
      "select u from User u where (lower(u.login) like lower(?1) "
          + "or lower(u.email) like lower(?1) or lower(u.firstName) like lower(?1)"
          + " or lower(u.firstName) like lower(?1) or lower(u.lastName) like lower(?1))")
  Page<User> findAll(String search, Pageable pageable);

  Optional<User> findOneByActivationKey(String activationKey);

  List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
      Instant dateTime);

  Optional<User> findOneByResetKey(String resetKey);

  Optional<User> findOneByEmailIgnoreCase(String email);

  Optional<User> findOneByLogin(String login);

  Optional<User> findOneByEmail(String email);

  @Query("select u from User u where u.email = ?1")
  Optional<User> findOneWithAuthoritiesByEmail(String email);

  @Query("select u from User u where u.login = ?1")
  Optional<User> findOneWithAuthoritiesByLogin(String login);

  @EntityGraph(attributePaths = "authorities")
  @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
  Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

  Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
