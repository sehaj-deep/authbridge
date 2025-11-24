package com.authbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthBridgeApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  AuthBridge Started Successfully!");
        System.out.println("========================================\n");
    }
}