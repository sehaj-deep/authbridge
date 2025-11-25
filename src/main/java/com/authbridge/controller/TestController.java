package com.authbridge.controller;

import com.authbridge.service.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private LdapService ldapService;
    
    @GetMapping("/ldap-connection")
    public Map<String, Object> testLdapConnection() {
        Map<String, Object> response = new HashMap<>();
        boolean connected = ldapService.testConnection();
        response.put("connected", connected);
        response.put("message", connected ? "LDAP connection successful" : "LDAP connection failed");
        return response;
    }
    
    @GetMapping("/sync-users")
    public Map<String, Object> syncUsers() {
        Map<String, Object> response = new HashMap<>();
        var users = ldapService.syncUsersFromLdap();
        response.put("count", users.size());
        response.put("users", users);
        return response;
    }
    
    @PostMapping("/authenticate")
    public Map<String, Object> testAuth(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        boolean authenticated = ldapService.authenticate(username, password);
        response.put("authenticated", authenticated);
        response.put("username", username);
        return response;
    }
}