package com.yaswanth.itsupport.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yaswanth.itsupport.dto.LoginRequest;
import com.yaswanth.itsupport.dto.LoginResponse;
import com.yaswanth.itsupport.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password) {

        return authService.register(
                username,
                password
        );
    }
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return authService.login(
                request.getUsername(),
                request.getPassword()
        );
    }
}