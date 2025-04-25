package com.taskapi.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public Task() {
        this.id = UUID.randomUUID().toString();
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public Task(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}