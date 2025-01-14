package zuzz.projects.todolist.util.validation;

import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import zuzz.projects.todolist.persistence.entity.TaskEntity;
import zuzz.projects.todolist.persistence.entity.UserEntity;
import zuzz.projects.todolist.persistence.repository.TaskRepository;
import zuzz.projects.todolist.persistence.repository.UserRepository;
// import zuzz.projects.todolist.service.exception.task.TaskException;
import zuzz.projects.todolist.service.exception.task.TaskNotFoundException;
import zuzz.projects.todolist.service.exception.task.TaskValidationException;
import zuzz.projects.todolist.service.exception.task.UserIsNotOwnerOfTaskException;
import zuzz.projects.todolist.service.exception.user.UserNotFoundException;

@Component
public class TaskValidation {

    private UserRepository userRepository;
    private TaskRepository taskRepository;

    private Logger logger = LoggerFactory.getLogger(TaskValidation.class);

    public TaskValidation() {}

    @Autowired
    public TaskValidation(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public TaskEntity validateTaskCreation(TaskEntity task) throws TaskValidationException {
        logger.info("validating task creation");

        Set<TaskEntity.Status> VALID_STATUSES = EnumSet.of(
            TaskEntity.Status.PENDING,
            TaskEntity.Status.IN_PROGRESS,
            TaskEntity.Status.COMPLETED
        );

        if (task.getTitle() == null || task.getTitle().length() < 2 || task.getTitle().length() > 100) {
            logger.error("title is null or less than 2 or greater than 100.");
            throw new TaskValidationException(
                "The title field shouldn't be null, less than 2 characters, or greater than 100.");
        }

        if (task.getStatus() == null || !VALID_STATUSES.contains(task.getStatus())) {
            logger.error("status is null or not valid.");
            throw new TaskValidationException(
                "The status field is not valid.");
        }

        logger.info("task validated successfully");
        return task;
    }

    public UserEntity getAuthenticatedUser() throws UserNotFoundException {
        logger.info("getting authenticated user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        logger.info("User found. User ID: {}", userId);

        return userRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public TaskEntity validateTaskOwnership(Long taskId)
        throws TaskNotFoundException, UserNotFoundException, UserIsNotOwnerOfTaskException {

        logger.info("validating task ownership");
        UserEntity user = getAuthenticatedUser();

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Task with id: " + taskId + " not found"));
        logger.info("task validated successfully");

        if (!task.getUser().getId().equals(user.getId())) {
            logger.error("unauthorized to access this task");
            throw new UserIsNotOwnerOfTaskException("Unauthorized to access this task");
        }

        return task;
    }
}
