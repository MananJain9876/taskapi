package com.taskapi.controller;

import com.taskapi.dto.AuthResponseDto;
import com.taskapi.dto.UserLoginDto;
import com.taskapi.dto.UserRegistrationDto;
import com.taskapi.model.AuthToken;
import com.taskapi.model.User;
import com.taskapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto registrationDto) {
        if (registrationDto.getUsername() == null || registrationDto.getPassword() == null ||
                registrationDto.getUsername().trim().isEmpty() || registrationDto.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        User user = userService.register(registrationDto.getUsername(), registrationDto.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        // Automatically login after registration
        AuthToken token = userService.login(registrationDto.getUsername(), registrationDto.getPassword());
        AuthResponseDto response = new AuthResponseDto(token.getToken(), user.getId(), user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginDto) {
        AuthToken token = userService.login(loginDto.getUsername(), loginDto.getPassword());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userService.getUserByToken(token.getToken());
        AuthResponseDto response = new AuthResponseDto(token.getToken(), user.getId(), user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean success = userService.logout(token);
            if (success) {
                return ResponseEntity.ok().body("Logged out successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}