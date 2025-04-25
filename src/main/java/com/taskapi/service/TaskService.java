package com.taskapi.service;

import com.taskapi.model.Task;
import com.taskapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final StorageService storageService;
    private final UserService userService;

    @Autowired
    public TaskService(StorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    public Task createTask(String token, String title, String description) {
        User user = userService.getUserByToken(token);
        if (user != null) {
            Task task = new Task(title, description);
            return storageService.addTask(user.getId(), task);
        }
        return null;
    }

    public List<Task> getAllTasks(String token) {
        User user = userService.getUserByToken(token);
        if (user != null) {
            return storageService.getAllTasks(user.getId());
        }
        return null;
    }

    public Task getTask(String token, String taskId) {
        User user = userService.getUserByToken(token);
        if (user != null) {
            return storageService.getTaskById(user.getId(), taskId);
        }
        return null;
    }

    public boolean deleteTask(String token, String taskId) {
        User user = userService.getUserByToken(token);
        if (user != null) {
            return storageService.deleteTask(user.getId(), taskId);
        }
        return false;
    }

    public Task updateTask(String token, Task task) {
        User user = userService.getUserByToken(token);
        if (user != null) {
            return storageService.updateTask(user.getId(), task);
        }
        return null;
    }
}