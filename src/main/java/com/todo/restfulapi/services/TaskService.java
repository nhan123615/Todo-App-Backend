package com.todo.restfulapi.services;

import com.todo.restfulapi.entities.Task;

import java.util.List;

public interface TaskService {
    List<Task> findByTitle(String title,Long userId);
    List<Task> findByUserId(Long userId);
}
