package zuzz.projects.todolist.service.interfaces;

import java.util.List;

import zuzz.projects.todolist.persistence.entity.TaskEntity;
import zuzz.projects.todolist.service.exception.task.TaskNotFoundException;
// import zuzz.projects.todolist.service.exception.task.TaskException;
import zuzz.projects.todolist.service.exception.task.TaskValidationException;
import zuzz.projects.todolist.service.exception.task.UserIsNotOwnerOfTaskException;
import zuzz.projects.todolist.service.exception.user.UserNotFoundException;

public interface TaskService {

    void createTask(TaskEntity task)
        throws TaskValidationException, UserNotFoundException;

    void updateTask(Long taskId, TaskEntity updatedTask)
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException, TaskValidationException;

    void deleteTask(Long taskId)
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException;

    TaskEntity fingById(Long id) 
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException;

    List<TaskEntity> findAll() throws UserNotFoundException;
}
