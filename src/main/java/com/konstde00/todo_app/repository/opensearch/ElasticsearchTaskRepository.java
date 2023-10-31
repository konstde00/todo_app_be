package com.konstde00.todo_app.repository.opensearch;

import com.konstde00.todo_app.domain.opensearch.TaskDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;

public interface ElasticsearchTaskRepository
    extends ElasticsearchRepository<TaskDocument, Integer> {

  @Query(
      "{\"bool\": {\"must\": [{ \"match\": {\"title\": \":title_param\"}}, "
          + "{\"term\": {\"status\": \":status_param\"}}, "
          + "{\"term\": {\"userId\": \":user_id_param\"}}"
          + "]}}")
  Page<TaskDocument> getByTitleAndUserId(
      @Param("title_param") String title,
      @Param("user_id_param") String userId,
      @Param("status_param") String status,
      Pageable pageable);
}
