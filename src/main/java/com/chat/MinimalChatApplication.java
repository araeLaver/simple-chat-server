package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MinimalChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinimalChatApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "ChatApp is running! 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}