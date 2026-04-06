package com.college.controller;

import com.college.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users-count")
    public ResponseEntity<Map<String, Object>> getUsersCount() {
        Map<String, Object> response = new HashMap<>();
        long count = userRepository.count();
        response.put("userCount", count);
        response.put("users", userRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public endpoint accessible");
        return ResponseEntity.ok(response);
    }
}
