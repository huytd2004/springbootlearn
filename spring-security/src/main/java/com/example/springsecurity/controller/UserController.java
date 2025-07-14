package com.example.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    // Endpoint chỉ dành cho người dùng có vai trò ROLE_USER
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user")
    public String userEndpoint() {
        return "Welcome, " + "! You are authorized as ROLE_USER.";
    }
}
