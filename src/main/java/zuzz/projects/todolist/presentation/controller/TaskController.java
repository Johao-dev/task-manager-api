package zuzz.projects.todolist.presentation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import zuzz.projects.todolist.persistence.entity.TaskEntity;
import zuzz.projects.todolist.service.interfaces.TaskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    private Logger logger = LoggerFactory.getLogger(TaskController.class);

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskEntity task) {
        logger.info("POST /api/tasks is called.");
        taskService.createTask(task);
        logger.info("Task created.");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
        @PathVariable Long taskId,
        @RequestBody TaskEntity updatedTask) {
        logger.info("PUT /api/tasks/{taskId} is called.");
        taskService.updateTask(taskId, updatedTask);
        logger.info("Task updated.");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        logger.info("DELETE /api/tasks/{taskId} is called.");
        taskService.deleteTask(taskId);
        logger.info("Task deleted.");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> findById(@PathVariable Long taskId) {
        logger.info("GET /api/tasks/{taskId} is called.");
        TaskEntity task = taskService.fingById(taskId);
        logger.info("Task found.");
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        logger.info("GET /api/tasks is called.");
        List<TaskEntity> tasks = taskService.findAll();
        logger.info("Tasks found.");
        return ResponseEntity.ok(tasks);
    }
}
