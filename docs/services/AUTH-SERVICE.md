# Auth Service Documentation

## Overview

The Auth Service is responsible for authentication and authorization in the music streaming application. It manages user credentials, generates and validates JWT tokens, and handles role-based access control.

## Technology Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL (port 5434)
- **Security**: Spring Security, JWT
- **Build Tool**: Gradle (Kotlin DSL)
- **Migration**: Flyway

## Service Configuration

- **Port**: 8081
- **Service Name**: `auth-service`
- **Database**: `auth_db`
- **Eureka Registration**: Enabled

## Key Components

### Controllers

#### AuthController
**Path**: [`/auth`](../../auth-service/src/main/java/com/musicapp/authservice/controller/AuthController.java)

**Endpoints**:
- `POST /auth/get-token` - Issue JWT token for user authentication
- `POST /auth/validate` - Validate JWT token and return user details
- `POST /auth/create-user` - Create new user credentials
- `PUT /auth/update-user` - Update user password (authenticated)

#### AdminController
**Path**: [`/admin`](../../auth-service/src/main/java/com/musicapp/authservice/controller/AdminController.java)

**Endpoints**:
- Admin-specific operations (requires ADMIN role)

### Services

#### AuthService
**Responsibilities**:
- User credential management
- JWT token generation
- Token validation
- Password encryption and verification
- User role management

**Key Methods**:
- `issueToken(UUID id, String password)` - Authenticate user and generate JWT
- `validateToken(String token)` - Validate JWT and return user entity
- `createUser(UUID id, String password)` - Create new user with encrypted password
- `modifyUserPassword(UUID id, String newPassword)` - Update user password

#### JwtService
**Responsibilities**:
- JWT token creation and parsing
- Token signature verification
- Claims extraction
- Token expiration handling

**Configuration**:
- **Expiration**: 604800 seconds (7 days)
- **Algorithm**: HMAC with SHA
- **Secret**: Configured in application properties

### Security

#### SecurityConfig
**Features**:
- HTTP Basic authentication disabled
- CSRF protection disabled (stateless API)
- Custom authentication filter
- Public endpoints: `/auth/get-token`, `/auth/create-user`, `/auth/validate`
- Protected endpoints: Require authentication

#### AuthenticationFilter
**Purpose**: Custom filter for JWT-based authentication
**Responsibilities**:
- Extract JWT from Authorization header
- Validate token
- Set authentication context

### Data Model

#### User Entity
**Table**: `users`

**Fields**:
- `id` (UUID) - Primary key
- `password` (String) - Encrypted password
- `roles` (Set<Role>) - User roles (ENUM)

#### Role Enum
**Values**:
- `USER` - Standard user role
- `ADMIN` - Administrator role

### Database Schema

**Migration**: [`V1__create_table.sql`](../../auth-service/src/main/resources/db/migration/V1__create_table.sql)

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255)[] NOT NULL
);
```

## API Endpoints

### POST /auth/get-token
**Description**: Authenticate user and receive JWT token

**Request Body**:
```json
{
  "id": "uuid",
  "password": "string"
}
```

**Response**: JWT token (String)

**Status Codes**:
- `200 OK` - Token issued successfully
- `401 UNAUTHORIZED` - Invalid credentials

---

### POST /auth/validate
**Description**: Validate JWT token and get user information

**Request Body**:
```json
{
  "token": "jwt-token-string"
}
```

**Response**:
```json
{
  "id": "uuid",
  "roles": ["USER", "ADMIN"]
}
```

**Status Codes**:
- `200 OK` - Token valid
- `401 UNAUTHORIZED` - Token invalid or expired

---

### POST /auth/create-user
**Description**: Create new user account

**Request Body**:
```json
{
  "id": "uuid",
  "password": "string"
}
```

**Response**: `200 OK` (no body)

**Status Codes**:
- `200 OK` - User created successfully
- `400 BAD REQUEST` - Invalid request data

---

### PUT /auth/update-user
**Description**: Update user password (requires authentication)

**Headers**:
- `Authorization: Bearer <jwt-token>`

**Request Body**:
```json
{
  "password": "new-password"
}
```

**Response**: `200 OK` (no body)

**Status Codes**:
- `200 OK` - Password updated
- `401 UNAUTHORIZED` - Not authenticated
- `404 NOT FOUND` - User not found

## Exception Handling

### TokenInvalidException
**Thrown when**: JWT token is invalid or expired
**HTTP Status**: 401 UNAUTHORIZED

### TokenIssueException
**Thrown when**: Error generating JWT token
**HTTP Status**: 401 UNAUTHORIZED

### UserNotFoundException
**Thrown when**: User ID not found in database
**HTTP Status**: 404 NOT FOUND

## Configuration Properties

```properties
server.port=8081
spring.application.name=auth-service

# Database
spring.datasource.url=jdbc:postgresql://${AUTH_SERVICE_DB_HOST:localhost}:${AUTH_SERVICE_DB_PORT:5434}/auth_db
spring.datasource.username=postgres
spring.datasource.password=password

# JWT
jwt.expiration=604800
jwt.secret=9XU6885Woh6W2k52VBhvrCrl1JTxEeub

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka}
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Security Considerations

### Password Storage
- Passwords are encrypted using BCrypt
- Never stored in plain text
- Salt automatically generated per password

### JWT Security
- Tokens signed with HMAC-SHA algorithm
- Secret key stored in configuration (should use environment variable in production)
- Token expiration enforced (7 days)
- Tokens include user ID and roles in claims

### Best Practices
- Rotate JWT secret regularly in production
- Use HTTPS for all communications
- Implement token refresh mechanism
- Consider shorter token expiration for sensitive operations
- Store JWT secret in secure vault (e.g., HashiCorp Vault)

## Testing

### Unit Tests
**Location**: [`src/test/java/com/musicapp/authservice/unit/`](../../auth-service/src/test/java/com/musicapp/authservice/unit/)

**Test Coverage**:
- Security logic tests
- JWT generation and validation
- Password encryption

## Dependencies

### Key Libraries
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Flyway Core
- JJWT (Java JWT library)
- Spring Cloud Netflix Eureka Client

## Integration with Other Services

### User Service
- User Service calls Auth Service to create user credentials
- Validates tokens for authenticated requests

### Gateway
- Gateway validates all incoming requests with Auth Service
- Forwards authentication context to downstream services

### All Services
- Services can validate JWT tokens by calling `/auth/validate`
- User context propagated through request headers

## Deployment

### Docker
**Dockerfile**: [`auth-service/Dockerfile`](../../auth-service/Dockerfile)

**Environment Variables**:
- `AUTH_SERVICE_DB_HOST` - PostgreSQL host
- `AUTH_SERVICE_DB_PORT` - PostgreSQL port (default: 5434)
- `EUREKA_URL` - Eureka server URL
- `JWT_SECRET` - JWT signing secret (production)
- `JWT_EXPIRATION` - Token expiration in seconds

### Database Setup
1. PostgreSQL container must be running
2. Database `auth_db` created automatically
3. Flyway migrations run on startup
4. Initial schema created from V1 migration

## Monitoring & Health

### Health Check
- Spring Boot Actuator endpoints available
- Eureka heartbeat every 30 seconds
- Database connection health monitored

### Logging
- Authentication attempts logged
- Token validation failures logged
- User creation/modification logged

## Future Enhancements

- [ ] Token refresh mechanism
- [ ] Multi-factor authentication (MFA)
- [ ] OAuth2 integration
- [ ] Password reset functionality
- [ ] Account lockout after failed attempts
- [ ] Audit logging for security events
- [ ] Token revocation/blacklist
- [ ] Role hierarchy and permissions
