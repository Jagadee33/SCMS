package com.college.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test2")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class TestController2 {
    
    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok(Map.of(
            "message", "Hello from AI/ML Backend!",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
