package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimpleChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleChatApplication.class, args);
        System.out.println("채팅 서버가 시작되었습니다. http://localhost:8080");
    }
}