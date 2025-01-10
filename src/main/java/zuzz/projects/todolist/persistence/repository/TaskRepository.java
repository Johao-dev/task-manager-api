package zuzz.projects.todolist.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import zuzz.projects.todolist.persistence.entity.TaskEntity;

@Repository
public interface TaskRepository extends CrudRepository<TaskEntity, Long> {

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = ?1")
    List<TaskEntity> findAllByUserId(Long userId);
}
