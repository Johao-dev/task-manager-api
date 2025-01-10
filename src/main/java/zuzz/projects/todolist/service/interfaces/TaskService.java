package zuzz.projects.todolist.service.interfaces;

import java.util.List;

import zuzz.projects.todolist.persistence.entity.TaskEntity;

public interface TaskService {

    void createTask(TaskEntity task);

    void updateTask(Long taskId, TaskEntity updatedTask);

    void deleteTask(Long taskId);

    TaskEntity fingById(Long id);

    List<TaskEntity> findAll();
}
