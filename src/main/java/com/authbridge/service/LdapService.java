package com.authbridge.service;

import com.unboundid.ldap.sdk.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LdapService {
    
    @Value("${ldap.url}")
    private String ldapUrl;
    
    @Value("${ldap.base.dn}")
    private String baseDn;
    
    @Value("${ldap.bind.dn}")
    private String bindDn;
    
    @Value("${ldap.bind.password}")
    private String bindPassword;
    
    /**
     * Authenticate a user against LDAP using bind operation
     * This is delegated authentication - password is verified by LDAP, not stored by us
     */
    public boolean authenticate(String username, String password) {
        LDAPConnection connection = null;
        try {
            // Extract hostname and port from ldap URL
            String host = ldapUrl.replace("ldap://", "").split(":")[0];
            int port = 389;
            
            // Construct user DN
            String userDn = String.format("uid=%s,ou=users,%s", username, baseDn);
            
            System.out.println("Attempting LDAP authentication for: " + userDn);
            
            // Try to bind with user credentials
            connection = new LDAPConnection(host, port, userDn, password);
            
            System.out.println("✓ Authentication successful for: " + username);
            return true;
            
        } catch (LDAPException e) {
            System.err.println("✗ Authentication failed for: " + username);
            System.err.println("  Reason: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    /**
     * Sync all users from LDAP
     * This retrieves user information from LDAP directory
     */
    public List<Map<String, String>> syncUsersFromLdap() {
        List<Map<String, String>> users = new ArrayList<>();
        LDAPConnection connection = null;
        
        try {
            // Extract hostname from URL
            String host = ldapUrl.replace("ldap://", "").split(":")[0];
            int port = 389;
            
            // Connect as admin
            connection = new LDAPConnection(host, port);
            connection.bind(bindDn, bindPassword);
            
            System.out.println("Connected to LDAP server, syncing users...");
            
            // Search for all users
            SearchResult searchResult = connection.search(
                baseDn,
                SearchScope.SUB,
                "(objectClass=inetOrgPerson)",
                "uid", "cn", "mail", "givenName", "sn"
            );
            
            // Process each user entry
            for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                Map<String, String> user = new HashMap<>();
                user.put("username", entry.getAttributeValue("uid"));
                user.put("email", entry.getAttributeValue("mail"));
                user.put("fullName", entry.getAttributeValue("cn"));
                user.put("firstName", entry.getAttributeValue("givenName"));
                user.put("lastName", entry.getAttributeValue("sn"));
                users.add(user);
                
                System.out.println("  Synced user: " + user.get("username"));
            }
            
            System.out.println("✓ Synced " + users.size() + " users from LDAP");
            
        } catch (LDAPException e) {
            System.err.println("✗ Failed to sync users from LDAP");
            System.err.println("  Reason: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        
        return users;
    }
    
    /**
     * Test LDAP connection
     */
    public boolean testConnection() {
        LDAPConnection connection = null;
        try {
            String host = ldapUrl.replace("ldap://", "").split(":")[0];
            int port = 389;
            
            connection = new LDAPConnection(host, port);
            connection.bind(bindDn, bindPassword);
            
            System.out.println("✓ LDAP connection test successful");
            return true;
            
        } catch (LDAPException e) {
            System.err.println("✗ LDAP connection test failed: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}