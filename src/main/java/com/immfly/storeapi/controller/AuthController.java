package com.immfly.storeapi.controller;

import com.immfly.storeapi.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        if (!this.username.equals(username) || !this.password.equals(password)) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtils.generateToken(username);
        return ResponseEntity.ok(token);
    }
}

