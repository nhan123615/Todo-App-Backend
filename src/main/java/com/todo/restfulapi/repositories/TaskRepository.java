package com.todo.restfulapi.repositories;

import com.todo.restfulapi.entities.Task;
import com.todo.restfulapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTitleContainingAndUserId(String title,Long userId);
    List<Task> findByUserId(Long userId);
}
