package zuzz.projects.todolist.util.validation;

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
import zuzz.projects.todolist.service.exception.TaskException;

@Component
public class TaskValidation {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Logger logger = LoggerFactory.getLogger(TaskValidation.class);

    public TaskEntity validateTaskCreation(TaskEntity task) throws TaskException {
        logger.info("validating task creation");
        if (task.getTitle() == null || task.getTitle().length() < 2 || task.getTitle().length() > 100) {
            logger.error("title is null or less than 2 or greater than 100.");
            throw new TaskException(
                "The title field shouldn't be null, less than 2 characters, or greater than 100.");
        }
        logger.info("task validated successfully");
        return task;
    }

    public UserEntity getAuthenticatedUser() {
        logger.info("getting authenticated user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        logger.info("User found. User ID: {}", userId);

        return userRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public TaskEntity validateTaskOwnership(Long taskId) {
        logger.info("validating task ownership");
        UserEntity user = getAuthenticatedUser();

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        logger.info("task validated successfully");

        if (!task.getUser().getId().equals(user.getId())) {
            logger.error("unauthorized to access this task");
            throw new RuntimeException("Unauthorized to access this task");
        }

        return task;
    }
}

