# AuthBridge - Identity Provider with LDAP/AD Integration

A lightweight identity provider demonstrating enterprise Single Sign-On (SSO) concepts, built to showcase understanding of authentication systems similar to **Okta's on-premises agents**.

Built as a portfolio project for the **Okta** application.

---

## 🎯 Project Overview

AuthBridge is an identity broker that:
- Integrates with LDAP/Active Directory for user authentication
- Provides JWT-based Single Sign-On across applications
- Uses delegated authentication (passwords verified by LDAP, never stored)
- Demonstrates understanding of enterprise identity management

---

## 🛠️ Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.2
- **LDAP Library:** UnboundID LDAP SDK
- **Authentication:** JWT (JSON Web Tokens)
- **Database:** H2 (in-memory)
- **Directory:** OpenLDAP (Docker)
- **Build Tool:** Maven

---

## 🏗️ Architecture
```
┌─────────────┐
│  OpenLDAP   │ ← Employee Directory (simulates Active Directory)
│   (Docker)  │
└──────┬──────┘
       │ LDAP Bind Authentication
       │
┌──────▼──────────────────┐
│   AuthBridge Service    │
│  ┌──────────────────┐   │
│  │ LDAP Service     │   │ ← Connects to LDAP
│  │ JWT Service      │   │ ← Generates tokens
│  │ Auth Controller  │   │ ← REST API
│  └──────────────────┘   │
└──────┬──────────────────┘
       │ JWT Tokens (SSO)
       │
┌──────▼──────┐  ┌──────────┐  ┌──────────┐
│   App 1     │  │  App 2   │  │  App 3   │
│ (Dashboard) │  │ (Reports)│  │(Settings)│
└─────────────┘  └──────────┘  └──────────┘
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker Desktop

### 1. Clone the Repository
```bash
git clone https://github.com/sehaj-deep/authbridge.git
cd authbridge
```

### 2. Start OpenLDAP (Directory Server)
```bash
docker compose up -d
```

Wait 10 seconds for LDAP to initialize, then add test users:
```bash
docker cp ldap-data/users.ldif authbridge-ldap:/tmp/users.ldif
docker exec authbridge-ldap ldapadd -x -D "cn=admin,dc=authbridge,dc=com" -w admin -f /tmp/users.ldif
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

Application starts at: **http://localhost:8080**

---

## 📡 API Endpoints

### Login (Get JWT Token)
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john",
  "message": "Authentication successful"
}
```

### Validate Token
```bash
POST /api/auth/validate
Content-Type: application/json

{
  "token": "your-jwt-token"
}
```

### Get Current User
```bash
GET /api/auth/me
Authorization: Bearer <your-jwt-token>
```

---

## 👥 Test Users

The LDAP server comes with pre-configured test users:

| Username | Password    | Email                   |
|----------|-------------|-------------------------|
| john     | password123 | john.doe@authbridge.com |
| jane     | password123 | jane.smith@authbridge.com |
| bob      | admin123    | bob.johnson@authbridge.com |

---

## 🧪 Testing with cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
```

**Validate Token:**
```bash
curl -X POST http://localhost:8080/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{"token":"YOUR_TOKEN_HERE"}'
```

**Get User Info:**
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🔐 Key Features Demonstrated

### 1. **LDAP/AD Integration**
- Direct connection to LDAP directory
- LDAP bind authentication (industry standard)
- User synchronization from directory

### 2. **Delegated Authentication**
- Passwords verified by LDAP server
- No password storage in application
- Follows security best practices

### 3. **JWT-Based SSO**
- Stateless authentication
- Signed tokens (HMAC-SHA512)
- Token validation across services

### 4. **REST API Design**
- Clean endpoint structure
- Proper HTTP methods (POST for auth)
- JSON request/response format

### 5. **Security Practices**
- CSRF protection configured
- Token expiration (24 hours)
- Secure password handling
- Audit logging

---

## 🎓 What I Learned

Building AuthBridge taught me:
- How enterprise identity systems work (like Okta!)
- LDAP protocol and directory services
- JWT token generation and validation
- Secure authentication flows
- Spring Boot and Spring Security
- Docker containerization
- RESTful API design

---

## 🔮 Future Enhancements

If I had more time, I would add:
- [ ] Multi-Factor Authentication (MFA)
- [ ] SAML support for enterprise applications
- [ ] OAuth 2.0 flows
- [ ] User provisioning/de-provisioning
- [ ] Demo applications showing SSO in action
- [ ] Admin dashboard for user management
- [ ] Integration with real Active Directory
- [ ] Kubernetes deployment

---

## 📂 Project Structure
```
authbridge/
├── src/main/java/com/authbridge/
│   ├── AuthBridgeApplication.java    # Main entry point
│   ├── config/
│   │   └── SecurityConfig.java       # Security configuration
│   ├── controller/
│   │   ├── AuthController.java       # Authentication API
│   │   └── TestController.java       # Test endpoints
│   └── service/
│       ├── LdapService.java          # LDAP integration
│       └── JwtService.java           # JWT token handling
├── src/main/resources/
│   └── application.properties        # Configuration
├── docker-compose.yml                # OpenLDAP setup
├── ldap-data/
│   └── users.ldif                    # Test users
└── pom.xml                           # Maven dependencies
```

---

## 🎯 Why This Project?

This project was built to demonstrate my understanding of identity and access management for the **Okta's** role.

The job posting specifically mentioned:
- ✅ Experience with Java (used Spring Boot)
- ✅ Experience with AD/ADFS (built LDAP/AD integration)
- ✅ Understanding of authentication systems (demonstrated with JWT SSO)

AuthBridge showcases these skills through a working implementation of core concepts behind products like Okta.

---

## 📞 Contact

**Sehajdeep Singh**  
- GitHub: [@sehaj-deep](https://github.com/sehaj-deep)
- Email: [sehajdeep490@yahoo.com]
- LinkedIn: [https://www.linkedin.com/in/singh-sehaj-deep]

---

## 📄 License

This is a demonstration project for educational and job application purposes.

---

*Built with ❤️ to demonstrate identity management concepts*