package com.konstde00.todo_app.repository;

import com.konstde00.todo_app.domain.Task;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.domain.enums.Status;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  Boolean existsTaskByTitleAndCreatedBy(String title, User createdBy);

  @Query(
      """
          SELECT t
          FROM Task t
          WHERE t.createdBy.id = :userId and t.status = :status
          ORDER BY t.position
      """)
  Page<Task> findAllByCreatedBy(
      @Param("userId") String userId, @Param("status") Status status, Pageable pageable);

  @Query(
      """
          SELECT t.status,
                 (COUNT(t) * 100.0) / total.total_count AS percentage
          FROM Task t,
          (SELECT COUNT(t2) AS total_count FROM Task t2 WHERE t2.createdBy.id = :userId) AS total
          WHERE t.createdBy.id = :userId
          GROUP BY t.status, total.total_count
          ORDER BY t.status
  """)
  List<Object[]> getPercentageByStatus(@Param("userId") String userId);

  @Query(
      """
              SELECT t.priority,
                     t.status,
                     COUNT(t) AS count
              FROM Task t
              WHERE t.createdBy.id = :userId
              GROUP BY t.priority, t.status
              ORDER BY t.priority, t.status
      """)
  List<Object[]> getStatusByPriority(@Param("userId") String userId);

  @Query(
      """
            SELECT MAX(t.position)
            FROM Task t
            WHERE t.createdBy.id = :userId and t.status = :status
        """)
  Integer getMaxPositionByCreatedByAndStatus(
      @Param("userId") String userId, @Param("status") Status status);

  @Modifying
  @Query(
      """
                UPDATE Task t
                SET t.position = t.position - 1
                WHERE t.position >= :from AND t.position <= :to AND t.status =:status AND t.createdBy.id = :userId
            """)
  void decrementPositionsBetween(
      @Param("from") Integer from,
      @Param("to") Integer to,
      @Param("status") Status status,
      @Param("userId") String userId);

  @Modifying
  @Query(
      """
                UPDATE Task t
                SET t.position = t.position + 1
                WHERE t.position >= :from AND t.position <= :to AND t.status =:status AND t.createdBy.id = :userId
            """)
  void incrementPositionsBetween(
      @Param("from") Integer from,
      @Param("to") Integer to,
      @Param("status") Status status,
      @Param("userId") String userId);
}
