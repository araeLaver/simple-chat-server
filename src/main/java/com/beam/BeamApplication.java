package com.beam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeamApplication {
    public static void main(String[] args) {
        SpringApplication.run(BeamApplication.class, args);
        System.out.println("⚡ BEAM Server started: http://localhost:8080");
    }
}