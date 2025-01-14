package zuzz.projects.todolist.presentation.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import zuzz.projects.todolist.service.exception.task.TaskNotFoundException;
import zuzz.projects.todolist.service.exception.task.TaskValidationException;
import zuzz.projects.todolist.service.exception.task.UserIsNotOwnerOfTaskException;
import zuzz.projects.todolist.service.exception.user.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaskValidationException.class)
    public ResponseEntity<String> handleTaskValidationException(TaskValidationException e) {
        logger.error("Task validation failed. {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Void> handleTaskNotFoundException(TaskNotFoundException e) {
        logger.error("Task not found: {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFoundException(UserNotFoundException e) {
        logger.error("User not found: {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(UserIsNotOwnerOfTaskException.class)
    public ResponseEntity<String> handleUserIsNotOwnerOfTaskException(UserIsNotOwnerOfTaskException e) {
        logger.error("User is not owner of task: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("You are not authorized to access this task.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred. Please try again later.");
    }
}