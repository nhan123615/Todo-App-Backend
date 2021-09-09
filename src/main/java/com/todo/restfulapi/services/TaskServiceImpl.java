package com.todo.restfulapi.services;

import com.todo.restfulapi.entities.Task;
import com.todo.restfulapi.entities.User;
import com.todo.restfulapi.repositories.DAO;
import com.todo.restfulapi.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements DAO<Task>,TaskService {
    @Autowired
    private TaskRepository taskRepository;


    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public Task save(Task vo) {
        return taskRepository.save(vo);
    }

    @Override
    public void delete(Long id) {
         taskRepository.deleteById(id);
    }

    @Override
    public List<Task> findByTitle(String title, Long userId) {
        return taskRepository.findByTitleContainingAndUserId(title,userId);
    }

    @Override
    public List<Task> findByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }
}
