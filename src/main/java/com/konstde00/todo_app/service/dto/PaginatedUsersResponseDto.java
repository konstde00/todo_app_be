package com.konstde00.todo_app.service.dto;

import com.konstde00.todo_app.service.api.dto.CommonIterableResponseMetadata;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PaginatedUsersResponseDto {

  CommonIterableResponseMetadata metadata;

  List<UserProfileDto> items;
}
