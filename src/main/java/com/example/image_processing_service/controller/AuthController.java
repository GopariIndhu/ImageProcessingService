package com.example.image_processing_service.controller;

import com.example.image_processing_service.dto.AuthResponse;
import com.example.image_processing_service.dto.LoginRequest;
import com.example.image_processing_service.dto.SignupRequest;
import com.example.image_processing_service.dto.UserResponse;
import com.example.image_processing_service.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @RequestBody SignupRequest request) {

        UserResponse user = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(
                new AuthResponse(token)
        );
    }
}