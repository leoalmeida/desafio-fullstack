package com.example.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin (origins = {"http://localhost:80", "http://localhost:4200"}, maxAge = HomeController.MAX_AGE)
@RequestMapping("/api/v1/home")
public class HomeController {
    public static final long MAX_AGE = 3600L;

    @GetMapping("/")
    public String greeting() {
        return "Hello, World";
    }
}
