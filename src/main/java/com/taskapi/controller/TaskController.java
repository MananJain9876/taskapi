package com.taskapi.controller;

import com.taskapi.dto.TaskDto;
import com.taskapi.dto.TaskResponseDto;
import com.taskapi.model.Task;
import com.taskapi.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskDto taskDto) {
        String token = authHeader.substring(7);
        Task task = taskService.createTask(token, taskDto.getTitle(), taskDto.getDescription());
        if (task == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        TaskResponseDto responseDto = mapToResponseDto(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        List<Task> tasks = taskService.getAllTasks(token);
        if (tasks == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        List<TaskResponseDto> responseDtos = tasks.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String taskId) {
        String token = authHeader.substring(7);
        Task task = taskService.getTask(token, taskId);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        TaskResponseDto responseDto = mapToResponseDto(task);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String taskId,
            @RequestBody TaskDto taskDto) {
        String token = authHeader.substring(7);
        Task existingTask = taskService.getTask(token, taskId);
        if (existingTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setCompleted(taskDto.isCompleted());

        Task updatedTask = taskService.updateTask(token, existingTask);
        TaskResponseDto responseDto = mapToResponseDto(updatedTask);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String taskId) {
        String token = authHeader.substring(7);
        boolean deleted = taskService.deleteTask(token, taskId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
        return ResponseEntity.ok().body("Task deleted successfully");
    }

    private TaskResponseDto mapToResponseDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }
}