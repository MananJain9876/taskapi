package com.taskapi.service;

import com.taskapi.model.AuthToken;
import com.taskapi.model.Task;
import com.taskapi.model.User;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Service
public class StorageService {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.dat";
    private static final String TOKENS_FILE = DATA_DIR + "/tokens.dat";

    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, AuthToken> tokens = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        createDataDirIfNeeded();
        loadUsers();
        loadTokens();
    }

    private void createDataDirIfNeeded() {
        Path path = Paths.get(DATA_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.err.println("Failed to create data directory: " + e.getMessage());
            }
        }
    }

    private void loadUsers() {
        Path path = Paths.get(USERS_FILE);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
                users = (Map<String, User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load users data: " + e.getMessage());
                users = new ConcurrentHashMap<>();
            }
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(new HashMap<>(users));
        } catch (IOException e) {
            System.err.println("Failed to save users data: " + e.getMessage());
        }
    }

    private void loadTokens() {
        Path path = Paths.get(TOKENS_FILE);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TOKENS_FILE))) {
                tokens = (Map<String, AuthToken>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load tokens data: " + e.getMessage());
                tokens = new ConcurrentHashMap<>();
            }
        }
    }

    private void saveTokens() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TOKENS_FILE))) {
            oos.writeObject(new HashMap<>(tokens));
        } catch (IOException e) {
            System.err.println("Failed to save tokens data: " + e.getMessage());
        }
    }

    // User operations
    public User createUser(User user) {
        users.put(user.getId(), user);
        saveUsers();
        return user;
    }

    public User getUserById(String id) {
        return users.get(id);
    }

    public User getUserByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Auth token operations
    public AuthToken createToken(String userId) {
        AuthToken token = new AuthToken(userId);
        tokens.put(token.getToken(), token);
        saveTokens();
        return token;
    }

    public AuthToken getToken(String token) {
        return tokens.get(token);
    }

    public void removeToken(String token) {
        tokens.remove(token);
        saveTokens();
    }

    // Task operations
    public Task addTask(String userId, Task task) {
        User user = users.get(userId);
        if (user != null) {
            user.getTasks().add(task);
            saveUsers();
            return task;
        }
        return null;
    }

    public List<Task> getAllTasks(String userId) {
        User user = users.get(userId);
        return user != null ? new ArrayList<>(user.getTasks()) : new ArrayList<>();
    }

    public Task getTaskById(String userId, String taskId) {
        User user = users.get(userId);
        if (user != null) {
            return user.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public boolean deleteTask(String userId, String taskId) {
        User user = users.get(userId);
        if (user != null) {
            boolean removed = user.getTasks().removeIf(t -> t.getId().equals(taskId));
            if (removed) {
                saveUsers();
                return true;
            }
        }
        return false;
    }

    public Task updateTask(String userId, Task updatedTask) {
        User user = users.get(userId);
        if (user != null) {
            for (int i = 0; i < user.getTasks().size(); i++) {
                if (user.getTasks().get(i).getId().equals(updatedTask.getId())) {
                    user.getTasks().set(i, updatedTask);
                    saveUsers();
                    return updatedTask;
                }
            }
        }
        return null;
    }

    // Clean up expired tokens periodically
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        List<String> expiredTokens = tokens.values().stream()
                .filter(AuthToken::isExpired)
                .map(AuthToken::getToken)
                .collect(Collectors.toList());

        for (String token : expiredTokens) {
            tokens.remove(token);
        }

        if (!expiredTokens.isEmpty()) {
            saveTokens();
        }
    }
}