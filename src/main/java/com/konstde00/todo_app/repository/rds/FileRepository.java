package com.konstde00.todo_app.repository.rds;

import com.konstde00.todo_app.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {}
