package zuzz.projects.todolist.service.implementation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import zuzz.projects.todolist.persistence.entity.TaskEntity;
import zuzz.projects.todolist.persistence.entity.UserEntity;
import zuzz.projects.todolist.persistence.repository.TaskRepository;
// import zuzz.projects.todolist.service.exception.task.TaskException;
import zuzz.projects.todolist.service.exception.task.TaskNotFoundException;
import zuzz.projects.todolist.service.exception.task.TaskValidationException;
import zuzz.projects.todolist.service.exception.task.UserIsNotOwnerOfTaskException;
import zuzz.projects.todolist.service.exception.user.UserNotFoundException;
import zuzz.projects.todolist.service.interfaces.TaskService;
import zuzz.projects.todolist.util.validation.TaskValidation;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskValidation taskValidator;

    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    public TaskServiceImpl(TaskRepository taskRepository, TaskValidation taskValidator) {
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
    }

    @Override
    public void createTask(TaskEntity task)
        throws TaskValidationException, UserNotFoundException {
            
        logger.info("creating task");
        task = taskValidator.validateTaskCreation(task);

        UserEntity user = taskValidator.getAuthenticatedUser();
        task.setUser(user);
        taskRepository.save(task);
        logger.info("task saved in database");
    }

    @Override
    public void updateTask(Long taskId, TaskEntity updatedTask)
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException, TaskValidationException {

        logger.info("updating task");
        TaskEntity existingTask = taskValidator.validateTaskOwnership(taskId);
        taskValidator.validateTaskCreation(existingTask);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());

        taskRepository.save(existingTask);
        logger.info("task updated in database");
    }

    @Override
    public void deleteTask(Long taskId)
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException {
            
        logger.info("deleting task");
        TaskEntity task = taskValidator.validateTaskOwnership(taskId);
        taskRepository.delete(task);
        logger.info("task deleted from database");
    }

    @Override
    public TaskEntity findById(Long id)
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException {
            
        logger.info("searching task");
        return taskValidator.validateTaskOwnership(id);
    }

    @Override
    public List<TaskEntity> findAll() throws UserNotFoundException {
        logger.info("searching all tasks");
        UserEntity user = taskValidator.getAuthenticatedUser();
        return taskRepository.findAllByUserId(user.getId());
    }
}
