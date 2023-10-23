package com.konstde00.todo_app.service.mapper;

import com.konstde00.todo_app.domain.Task;
import com.konstde00.todo_app.domain.enums.Priority;
import com.konstde00.todo_app.domain.enums.Status;
import com.konstde00.todo_app.service.api.dto.CreateTaskRequestDto;
import com.konstde00.todo_app.service.api.dto.CreateTaskResponseDto;
import com.konstde00.todo_app.service.api.dto.GetTaskResponseDto;
import com.konstde00.todo_app.service.api.dto.TaskItem;
import com.konstde00.todo_app.service.api.dto.TaskPriorityEnum;
import com.konstde00.todo_app.service.api.dto.TaskStatusEnum;
import com.konstde00.todo_app.service.api.dto.UpdateTaskResponseDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-14T23:02:55+0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.3.jar, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class TaskMapperImpl extends TaskMapper {

    @Override
    public Task toEntity(CreateTaskRequestDto createTaskRequestDto) {
        if ( createTaskRequestDto == null ) {
            return null;
        }

        Task task = new Task();

        task.setTitle( createTaskRequestDto.getTitle() );
        task.setDescription( createTaskRequestDto.getDescription() );
        task.setStatus( taskStatusEnumToStatus( createTaskRequestDto.getStatus() ) );
        task.setPriority( taskPriorityEnumToPriority( createTaskRequestDto.getPriority() ) );

        return task;
    }

    @Override
    public List<TaskItem> toTaskItems(List<Task> tasks) {
        if ( tasks == null ) {
            return null;
        }

        List<TaskItem> list = new ArrayList<TaskItem>( tasks.size() );
        for ( Task task : tasks ) {
            list.add( toTaskItem( task ) );
        }

        return list;
    }

    @Override
    public TaskItem toTaskItem(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskItem taskItem = new TaskItem();

        taskItem.setTitle( task.getTitle() );
        taskItem.setDescription( task.getDescription() );
        taskItem.setStatus( statusToTaskStatusEnum( task.getStatus() ) );
        taskItem.setPriority( priorityToTaskPriorityEnum( task.getPriority() ) );
        taskItem.setId( task.getId() );

        return taskItem;
    }

    @Override
    public GetTaskResponseDto toGetTaskResponseDto(Task task) {
        if ( task == null ) {
            return null;
        }

        GetTaskResponseDto getTaskResponseDto = new GetTaskResponseDto();

        getTaskResponseDto.setTitle( task.getTitle() );
        getTaskResponseDto.setDescription( task.getDescription() );
        getTaskResponseDto.setStatus( statusToTaskStatusEnum( task.getStatus() ) );
        getTaskResponseDto.setPriority( priorityToTaskPriorityEnum( task.getPriority() ) );
        getTaskResponseDto.setId( task.getId() );

        return getTaskResponseDto;
    }

    @Override
    public CreateTaskResponseDto toCreateTaskResponseDto(Task task) {
        if ( task == null ) {
            return null;
        }

        CreateTaskResponseDto createTaskResponseDto = new CreateTaskResponseDto();

        createTaskResponseDto.setTitle( task.getTitle() );
        createTaskResponseDto.setDescription( task.getDescription() );
        createTaskResponseDto.setStatus( statusToTaskStatusEnum( task.getStatus() ) );
        createTaskResponseDto.setPriority( priorityToTaskPriorityEnum( task.getPriority() ) );
        createTaskResponseDto.setId( task.getId() );

        return createTaskResponseDto;
    }

    @Override
    public UpdateTaskResponseDto toUpdateTaskResponseDto(Task task) {
        if ( task == null ) {
            return null;
        }

        UpdateTaskResponseDto updateTaskResponseDto = new UpdateTaskResponseDto();

        updateTaskResponseDto.setTitle( task.getTitle() );
        updateTaskResponseDto.setDescription( task.getDescription() );
        updateTaskResponseDto.setStatus( statusToTaskStatusEnum( task.getStatus() ) );
        updateTaskResponseDto.setPriority( priorityToTaskPriorityEnum( task.getPriority() ) );
        updateTaskResponseDto.setId( task.getId() );

        return updateTaskResponseDto;
    }

    protected Status taskStatusEnumToStatus(TaskStatusEnum taskStatusEnum) {
        if ( taskStatusEnum == null ) {
            return null;
        }

        Status status;

        switch ( taskStatusEnum ) {
            case NOT_STARTED: status = Status.NOT_STARTED;
            break;
            case IN_PROGRESS: status = Status.IN_PROGRESS;
            break;
            case COMPLETED: status = Status.COMPLETED;
            break;
            case CANCELLED: status = Status.CANCELLED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + taskStatusEnum );
        }

        return status;
    }

    protected Priority taskPriorityEnumToPriority(TaskPriorityEnum taskPriorityEnum) {
        if ( taskPriorityEnum == null ) {
            return null;
        }

        Priority priority;

        switch ( taskPriorityEnum ) {
            case LOW: priority = Priority.LOW;
            break;
            case MEDIUM: priority = Priority.MEDIUM;
            break;
            case HIGH: priority = Priority.HIGH;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + taskPriorityEnum );
        }

        return priority;
    }

    protected TaskStatusEnum statusToTaskStatusEnum(Status status) {
        if ( status == null ) {
            return null;
        }

        TaskStatusEnum taskStatusEnum;

        switch ( status ) {
            case NOT_STARTED: taskStatusEnum = TaskStatusEnum.NOT_STARTED;
            break;
            case IN_PROGRESS: taskStatusEnum = TaskStatusEnum.IN_PROGRESS;
            break;
            case COMPLETED: taskStatusEnum = TaskStatusEnum.COMPLETED;
            break;
            case CANCELLED: taskStatusEnum = TaskStatusEnum.CANCELLED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + status );
        }

        return taskStatusEnum;
    }

    protected TaskPriorityEnum priorityToTaskPriorityEnum(Priority priority) {
        if ( priority == null ) {
            return null;
        }

        TaskPriorityEnum taskPriorityEnum;

        switch ( priority ) {
            case LOW: taskPriorityEnum = TaskPriorityEnum.LOW;
            break;
            case MEDIUM: taskPriorityEnum = TaskPriorityEnum.MEDIUM;
            break;
            case HIGH: taskPriorityEnum = TaskPriorityEnum.HIGH;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + priority );
        }

        return taskPriorityEnum;
    }
}
