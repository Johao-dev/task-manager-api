package zuzz.projects.todolist.service.implementation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zuzz.projects.todolist.persistence.entity.TaskEntity;
import zuzz.projects.todolist.persistence.entity.UserEntity;
import zuzz.projects.todolist.persistence.repository.TaskRepository;
import zuzz.projects.todolist.service.exception.TaskException;
import zuzz.projects.todolist.service.interfaces.TaskService;
import zuzz.projects.todolist.util.validation.TaskValidation;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskValidation taskValidator;

    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Override
    public void createTask(TaskEntity task) {
        logger.info("creating task");
        try {
            task = taskValidator.validateTaskCreation(task);
        } catch (TaskException e) {
            logger.error("validation failed. {}", e);
            throw new RuntimeException("Validation failed" + e.getMessage());
        }

        UserEntity user = taskValidator.getAuthenticatedUser();
        task.setUser(user);
        taskRepository.save(task);
        logger.info("task saved in database");
    }

    @Override
    public void updateTask(Long taskId, TaskEntity updatedTask) {
        logger.info("updating task");
        TaskEntity existingTask = taskValidator.validateTaskOwnership(taskId);

        try {
            taskValidator.validateTaskCreation(existingTask);
        } catch (TaskException e) {
            logger.error("validation failed. {}", e);
            throw new RuntimeException("Validation failed" + e.getMessage());
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());

        taskRepository.save(existingTask);
        logger.info("task updated in database");
    }

    @Override
    public void deleteTask(Long taskId) {
        logger.info("deleting task");
        TaskEntity task = taskValidator.validateTaskOwnership(taskId);
        taskRepository.delete(task);
        logger.info("task deleted from database");
    }

    @Override
    public TaskEntity fingById(Long id) {
        logger.info("searching task");
        return taskValidator.validateTaskOwnership(id);
    }

    @Override
    public List<TaskEntity> findAll() {
        logger.info("searching all tasks");
        UserEntity user = taskValidator.getAuthenticatedUser();
        return taskRepository.findAllByUserId(user.getId());
    }
}
