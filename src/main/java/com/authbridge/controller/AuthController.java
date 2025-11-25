package com.authbridge.controller;

import com.authbridge.service.JwtService;
import com.authbridge.service.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private LdapService ldapService;
    
    @Autowired
    private JwtService jwtService;
    
    /**
     * Login endpoint - authenticates user and returns JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        System.out.println("Login attempt for user: " + request.getUsername());
        
        // Validate input
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Username is required"));
        }
        
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Password is required"));
        }
        
        // Authenticate against LDAP
        boolean authenticated = ldapService.authenticate(
            request.getUsername(), 
            request.getPassword()
        );
        
        if (!authenticated) {
            System.out.println("✗ Login failed for user: " + request.getUsername());
            return ResponseEntity.status(401).body(createErrorResponse("Invalid credentials"));
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(request.getUsername());
        
        System.out.println("✓ Login successful for user: " + request.getUsername());
        
        // Return token
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("username", request.getUsername());
        response.put("message", "Authentication successful");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validate token endpoint - checks if token is valid
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequest request) {
        
        boolean valid = jwtService.validateToken(request.getToken());
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        
        if (valid) {
            String username = jwtService.getUsernameFromToken(request.getToken());
            response.put("username", username);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current user from token
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        
        // Extract token from "Bearer <token>" format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(createErrorResponse("Missing or invalid Authorization header"));
        }
        
        String token = authHeader.substring(7);
        
        // Validate token
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body(createErrorResponse("Invalid or expired token"));
        }
        
        // Extract username
        String username = jwtService.getUsernameFromToken(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("message", "User authenticated");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
}

/**
 * Request DTOs (Data Transfer Objects)
 */
class LoginRequest {
    private String username;
    private String password;
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class TokenRequest {
    private String token;
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}