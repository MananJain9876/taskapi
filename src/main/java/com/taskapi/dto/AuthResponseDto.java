package com.taskapi.dto;

public class AuthResponseDto {
    private String token;
    private String userId;
    private String username;

    public AuthResponseDto(String token, String userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
