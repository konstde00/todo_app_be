package com.konstde00.todo_app.repository.rds;

import com.konstde00.todo_app.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the {@link Authority} entity. */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
