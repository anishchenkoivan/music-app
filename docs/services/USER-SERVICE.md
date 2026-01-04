# User Service Documentation

## Overview

The User Service manages user profiles and account information in the music streaming application. It handles user registration, profile updates, and provides both public and private user data access.

## Technology Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL (port 5432)
- **Security**: Spring Security, JWT validation
- **Build Tool**: Gradle (Kotlin DSL)
- **Service Communication**: Feign Client

## Service Configuration

- **Port**: 8082
- **Service Name**: `user-service`
- **Database**: `user_db`
- **Eureka Registration**: Enabled

## Key Components

### Controllers

#### UserController
**Path**: [`/user`](../../user-service/src/main/java/com/musicapp/userservice/controller/UserController.java)

**Endpoints**:
- `POST /user/create-user` - Create new user profile
- `PUT /user/{id}/update` - Update user profile
- `GET /user/{id}` - Get user details (public or private based on auth)
- `POST /user/get-id` - Get user ID by email or username

### Services

#### UserService
**Responsibilities**:
- User profile CRUD operations
- User validation
- Public vs private data filtering
- Integration with Auth Service

**Key Methods**:
- `createUser(UserCreateRequest)` - Create new user profile and credentials
- `updateUser(UUID, UserModifyRequest)` - Update user profile information
- `getUser(UUID)` - Get full user details (private)
- `getPublicUserDetails(UUID)` - Get public user information
- `getId(String email, String username)` - Lookup user ID

### Gateway Clients

#### AuthClient
**Purpose**: Feign client for Auth Service communication
**Responsibilities**:
- Create user credentials in Auth Service
- Validate JWT tokens
- Update user security information

**Endpoints Called**:
- `POST /auth/create-user` - Create credentials
- `POST /auth/validate` - Validate token
- `PUT /auth/update-user` - Update password

### Security

#### SecurityConfig
**Features**:
- JWT-based authentication
- Public endpoints: `/user/create-user`, `/user/get-id`
- Protected endpoints: Require valid JWT
- Integration with Auth Service for token validation

### Data Model

#### User Entity
**Table**: `users`

**Fields**:
- `id` (UUID) - Primary key
- `username` (String) - Unique username
- `email` (String) - Unique email address
- `displayName` (String) - Display name
- `bio` (String) - User biography
- `createdAt` (Timestamp) - Account creation date
- `updatedAt` (Timestamp) - Last update date

### DTOs

#### UserDetailsDto
**Purpose**: Full user details (private)
**Fields**: All user fields including email

#### PublicUserDetailsDto
**Purpose**: Public user information
**Fields**: id, username, displayName, bio (no email)

#### UserCreateRequest
**Fields**: username, email, password, displayName, bio

#### UserModifyRequest
**Fields**: displayName, bio

#### GetUserIdRequest
**Fields**: email, username (at least one required)

## API Endpoints

### POST /user/create-user
**Description**: Create new user account with profile and credentials

**Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "displayName": "string",
  "bio": "string"
}
```

**Response**: User UUID

**Status Codes**:
- `200 OK` - User created successfully
- `400 BAD REQUEST` - Validation error or user already exists

**Process**:
1. Validate user data
2. Create user profile in user_db
3. Call Auth Service to create credentials
4. Return user ID

---

### PUT /user/{id}/update
**Description**: Update user profile information

**Path Parameters**:
- `id` (UUID) - User ID

**Request Body**:
```json
{
  "displayName": "string",
  "bio": "string"
}
```

**Response**: `200 OK` (no body)

**Status Codes**:
- `200 OK` - Profile updated
- `400 BAD REQUEST` - Validation error
- `404 NOT FOUND` - User not found

---

### GET /user/{id}
**Description**: Get user details (public or private based on authentication)

**Path Parameters**:
- `id` (UUID) - User ID

**Headers** (optional):
- `Authorization: Bearer <jwt-token>`

**Response** (authenticated, own profile):
```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "displayName": "string",
  "bio": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

**Response** (public or other user):
```json
{
  "id": "uuid",
  "username": "string",
  "displayName": "string",
  "bio": "string"
}
```

**Status Codes**:
- `200 OK` - User details returned
- `404 NOT FOUND` - User not found

**Logic**:
- If not authenticated → return public details
- If authenticated and requesting own profile → return full details
- If authenticated but requesting other user → return public details

---

### POST /user/get-id
**Description**: Get user ID by email or username

**Request Body**:
```json
{
  "email": "string",
  "username": "string"
}
```
*At least one field required*

**Response**: User UUID

**Status Codes**:
- `200 OK` - User ID found
- `404 NOT FOUND` - User not found
- `400 BAD REQUEST` - Neither email nor username provided

## Exception Handling

### ValidateException
**Thrown when**: User data validation fails
**HTTP Status**: 400 BAD REQUEST
**Examples**: Invalid email format, username too short

### CreateException
**Thrown when**: User creation fails
**HTTP Status**: 400 BAD REQUEST
**Examples**: Duplicate username/email, Auth Service error

### NoSuchElementException
**Thrown when**: User not found
**HTTP Status**: 404 NOT FOUND

## Configuration Properties

```properties
server.port=8082
spring.application.name=user-service

# Database
spring.datasource.url=jdbc:postgresql://${USER_SERVICE_DB_HOST:localhost}:${USER_SERVICE_DB_PORT:5432}/user_db
spring.datasource.username=postgres
spring.datasource.password=password

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka}
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

## Validation Rules

### Username
- Required
- Unique
- Minimum length: 3 characters
- Maximum length: 50 characters
- Alphanumeric and underscores only

### Email
- Required
- Unique
- Valid email format
- Maximum length: 255 characters

### Password
- Required (on creation)
- Minimum length: 8 characters
- Handled by Auth Service

### Display Name
- Optional
- Maximum length: 100 characters

### Bio
- Optional
- Maximum length: 500 characters

## Integration with Other Services

### Auth Service
**Communication**: Feign Client
**Operations**:
- Create user credentials during registration
- Validate JWT tokens for authentication
- Update user passwords

**Flow**:
```
User Service → Auth Service
1. Create user profile
2. Call Auth Service to create credentials
3. If Auth fails, rollback user creation
```

### Music Service
**Usage**: Music Service queries User Service for user details
**Endpoints Used**: `GET /user/{id}`

### Gateway
**Role**: Routes requests to User Service
**Path**: `/api/user/**`

## Data Privacy

### Public Information
- Username
- Display name
- Bio
- User ID

### Private Information
- Email address
- Password (stored in Auth Service)
- Account timestamps (only visible to owner)

### Access Control
- Users can only see full details of their own profile
- Other users see public information only
- Unauthenticated requests always get public information

## Testing

### Integration Tests
**Location**: `src/test/java/com/musicapp/userservice/integration/`

**Test Coverage**:
- User creation flow
- Profile updates
- Public vs private data access
- Auth Service integration
- Validation rules

## Dependencies

### Key Libraries
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Spring Cloud Netflix Eureka Client
- Spring Cloud OpenFeign (for Auth Service communication)
- Spring Boot Starter Validation

## Deployment

### Docker
**Dockerfile**: [`user-service/Dockerfile`](../../user-service/Dockerfile)

**Environment Variables**:
- `USER_SERVICE_DB_HOST` - PostgreSQL host
- `USER_SERVICE_DB_PORT` - PostgreSQL port (default: 5432)
- `EUREKA_URL` - Eureka server URL
- `AUTH_SERVICE_URL` - Auth Service URL (via Eureka)

### Database Setup
1. PostgreSQL container must be running
2. Database `user_db` created automatically
3. Schema auto-generated by Hibernate
4. Unique constraints on username and email

## Monitoring & Health

### Health Check
- Spring Boot Actuator endpoints
- Eureka heartbeat monitoring
- Database connection health
- Auth Service connectivity check

### Logging
- User creation events
- Profile updates
- Failed validations
- Auth Service communication errors

## Error Scenarios

### User Creation Failures
1. **Duplicate Username/Email**: Returns 400 with error message
2. **Auth Service Down**: Returns 500, user profile not created
3. **Database Error**: Returns 500, transaction rolled back
4. **Validation Error**: Returns 400 with validation details

### Profile Update Failures
1. **User Not Found**: Returns 404
2. **Validation Error**: Returns 400
3. **Database Error**: Returns 500

## Future Enhancements

- [ ] User avatar support (integration with Image Service)
- [ ] Email verification
- [ ] User preferences and settings
- [ ] Social features (followers, following)
- [ ] User blocking and reporting
- [ ] Account deactivation/deletion
- [ ] User search functionality
- [ ] Profile completion percentage
- [ ] User badges and achievements
- [ ] Privacy settings (profile visibility)
