package com.taskapi.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuthToken implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private String userId;
    private LocalDateTime expiresAt;

    public AuthToken() {
        this.token = UUID.randomUUID().toString();
        // Token expires after 24 hours
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }

    public AuthToken(String userId) {
        this();
        this.userId = userId;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}