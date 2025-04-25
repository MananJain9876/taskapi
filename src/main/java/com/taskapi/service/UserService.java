package com.taskapi.service;

import com.taskapi.model.AuthToken;
import com.taskapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final StorageService storageService;

    @Autowired
    public UserService(StorageService storageService) {
        this.storageService = storageService;
    }

    public User register(String username, String password) {
        // Check if username already exists
        if (storageService.getUserByUsername(username) != null) {
            return null;
        }

        User user = new User(username, password);
        return storageService.createUser(user);
    }

    public AuthToken login(String username, String password) {
        User user = storageService.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return storageService.createToken(user.getId());
        }
        return null;
    }

    public boolean logout(String token) {
        AuthToken authToken = storageService.getToken(token);
        if (authToken != null) {
            storageService.removeToken(token);
            return true;
        }
        return false;
    }

    public User getUserByToken(String token) {
        AuthToken authToken = storageService.getToken(token);
        if (authToken != null && !authToken.isExpired()) {
            return storageService.getUserById(authToken.getUserId());
        }
        return null;
    }
}
