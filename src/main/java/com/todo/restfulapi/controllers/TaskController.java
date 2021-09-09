package com.todo.restfulapi.controllers;

import com.todo.restfulapi.entities.Task;
import com.todo.restfulapi.entities.User;
import com.todo.restfulapi.services.TaskServiceImpl;
import com.todo.restfulapi.services.UserServiceImpl;
import com.todo.restfulapi.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
public class TaskController {

    private TaskServiceImpl taskServiceImpl;
    private UserServiceImpl userService;

    @Autowired
    public TaskController(TaskServiceImpl taskServiceImpl, UserServiceImpl userService) {
        this.taskServiceImpl = taskServiceImpl;
        this.userService = userService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getTasks(@RequestParam(name = "keyword", required = false) String title, Authentication authentication) {
        User user = UserUtils.getUser(authentication,userService);
        if (title != null) {
            return ResponseEntity.ok().body(taskServiceImpl.findByTitle(title.toLowerCase(),user.getId()));
        } else {
            return ResponseEntity.ok().body(taskServiceImpl.findByUserId(user.getId()));
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> saveTask(@RequestBody Task task,Authentication authentication) {
        User user = UserUtils.getUser(authentication,userService);
        task.setUser(user);
        return ResponseEntity.created(null).body(taskServiceImpl.save(task));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok().body(taskServiceImpl.findById(id));
    }


    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task,Authentication authentication) {
        Task editTask = taskServiceImpl.findById(id);
        User user = UserUtils.getUser(authentication,userService);
        task.setUser(user);
        task.setId(editTask.getId());
        return ResponseEntity.ok().body(taskServiceImpl.save(task));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable Long id) {
        Task deleteTask = taskServiceImpl.findById(id);
        taskServiceImpl.delete(id);
        return ResponseEntity.ok().body(deleteTask);
    }

}
