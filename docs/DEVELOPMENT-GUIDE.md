# Development Guide

## Overview

This guide helps developers set up their local environment and contribute to the Music Streaming Application.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Development Environment](#development-environment)
3. [Project Structure](#project-structure)
4. [Building and Running](#building-and-running)
5. [Testing](#testing)
6. [Code Style and Standards](#code-style-and-standards)
7. [Git Workflow](#git-workflow)
8. [Debugging](#debugging)
9. [Common Tasks](#common-tasks)
10. [Contributing](#contributing)

---

## Getting Started

### Prerequisites

**Required**:
- **Java JDK**: 17 or higher
- **Docker**: 20.10+ with Docker Compose
- **Git**: Latest version
- **IDE**: IntelliJ IDEA (recommended) or VS Code

**Optional**:
- **Postman**: For API testing
- **DBeaver**: For database management
- **Kafka Tool**: For Kafka monitoring

### Quick Start

```bash
# 1. Clone repository
git clone https://github.com/yourusername/music-app.git
cd music-app

# 2. Copy environment file
cp .env.example .env

# 3. Start infrastructure
docker-compose -f docker-compose.infra.yml up -d

# 4. Build all services
./build-all.sh

# 5. Start Eureka (wait for it to start)
cd eureka && ./gradlew bootRun

# 6. Start other services (in separate terminals)
cd gateway && ./gradlew bootRun
cd auth-service && ./gradlew bootRun
# ... etc
```

---

## Development Environment

### IDE Setup

#### IntelliJ IDEA

**Import Project**:
1. File → Open → Select `music-app` directory
2. IntelliJ will auto-detect Gradle projects
3. Wait for Gradle sync to complete

**Recommended Plugins**:
- Lombok
- Spring Boot Assistant
- Docker
- Kubernetes
- Database Navigator

**Code Style**:
1. File → Settings → Editor → Code Style
2. Import scheme from `config/intellij-code-style.xml`

**Run Configurations**:
Create run configurations for each service:
- Main class: `*Application.java`
- Working directory: Service root
- Environment variables: From `.env`

#### VS Code

**Extensions**:
- Java Extension Pack
- Spring Boot Extension Pack
- Docker
- Gradle for Java
- Kotlin Language

**Settings** (`.vscode/settings.json`):
```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.compile.nullAnalysis.mode": "automatic",
  "spring-boot.ls.java.home": "/path/to/jdk-17"
}
```

### Database Tools

**DBeaver Configuration**:

**PostgreSQL Connections**:
```
Auth DB:
  Host: localhost
  Port: 5434
  Database: auth_db
  User: postgres
  Password: password

User DB:
  Host: localhost
  Port: 5432
  Database: user_db

Music DB:
  Host: localhost
  Port: 5433
  Database: music_db
```

**ClickHouse**:
```
Host: localhost
Port: 8123
Database: statistics_db
User: user
Password: password
```

### MinIO Console

Access MinIO console:
```
URL: http://localhost:9001
Username: minioadmin
Password: minioadmin
```

Create buckets:
- `audio`
- `artwork`

### Kafka Tools

**Kafka UI** (optional):
```bash
docker run -p 8090:8080 \
  -e KAFKA_CLUSTERS_0_NAME=local \
  -e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=localhost:9092 \
  provectuslabs/kafka-ui
```

Access at: `http://localhost:8090`

---

## Project Structure

```
music-app/
├── auth-service/           # Authentication service
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── build.gradle.kts
│   └── Dockerfile
├── user-service/           # User management service
├── music-service/          # Music catalog service
├── image-service/          # Image storage service
├── streaming-service/      # Audio streaming service
├── statistics-service/     # Analytics service
├── eureka/                 # Service discovery
├── gateway/                # API Gateway
├── docs/                   # Documentation
│   ├── ARCHITECTURE.md
│   ├── API-REFERENCE.md
│   ├── DEPLOYMENT.md
│   ├── DEVELOPMENT-GUIDE.md
│   └── services/
├── docker-compose.yml      # Main compose file
├── docker-compose.dev.yml  # Development compose
├── docker-compose.infra.yml # Infrastructure only
├── .env.example            # Environment template
└── README.md
```

### Service Structure

Each service follows this structure:

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/com/musicapp/servicename/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # Data access
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── security/        # Security components
│   │   │   ├── exception/       # Custom exceptions
│   │   │   └── ServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/    # Flyway migrations
│   └── test/
│       ├── java/
│       │   ├── unit/            # Unit tests
│       │   └── integration/     # Integration tests
│       └── resources/
├── build.gradle.kts
├── Dockerfile
└── README.md
```

---

## Building and Running

### Build Individual Service

```bash
cd service-name
./gradlew clean build
```

### Build All Services

```bash
# Create a build script
cat > build-all.sh << 'EOF'
#!/bin/bash
services=("eureka" "gateway" "auth-service" "user-service" "music-service" "image-service" "streaming-service" "statistics-service")

for service in "${services[@]}"; do
  echo "Building $service..."
  cd $service
  ./gradlew clean build -x test
  cd ..
done
EOF

chmod +x build-all.sh
./build-all.sh
```

### Run Service

```bash
cd service-name
./gradlew bootRun
```

### Run with Profile

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Run with Debug

```bash
./gradlew bootRun --debug-jvm
```

Then attach debugger to port 5005.

### Hot Reload

**Spring Boot DevTools** (already included):
- Automatic restart on code changes
- LiveReload support
- Property defaults for development

**Gradle Continuous Build**:
```bash
./gradlew build --continuous
```

---

## Testing

### Unit Tests

**Run all unit tests**:
```bash
./gradlew test
```

**Run specific test class**:
```bash
./gradlew test --tests "com.musicapp.service.UserServiceTest"
```

**Run with coverage**:
```bash
./gradlew test jacocoTestReport
```

View coverage report: `build/reports/jacoco/test/html/index.html`

### Integration Tests

**Run integration tests**:
```bash
./gradlew integrationTest
```

**With Testcontainers**:
Integration tests use Testcontainers for:
- PostgreSQL
- Kafka
- ElasticSearch
- ClickHouse

Docker must be running.

### API Testing

**Postman Collection**:
Import `docs/postman/music-app-api.json`

**cURL Examples**:

```bash
# Create user
curl -X POST http://localhost:8080/api/user/create-user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "displayName": "Test User"
  }'

# Get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/get-token \
  -H "Content-Type: application/json" \
  -d '{"id":"user-uuid","password":"password123"}' \
  | jq -r '.')

# Use token
curl http://localhost:8080/api/user/{id} \
  -H "Authorization: Bearer $TOKEN"
```

### Load Testing

**Using Apache Bench**:
```bash
ab -n 1000 -c 10 http://localhost:8080/api/tracks/{id}
```

**Using k6**:
```javascript
import http from 'k6/http';

export default function() {
  http.get('http://localhost:8080/api/tracks/uuid');
}
```

Run:
```bash
k6 run --vus 10 --duration 30s load-test.js
```

---

## Code Style and Standards

### Java Code Style

**Formatting**:
- Indentation: 4 spaces
- Line length: 120 characters
- Braces: K&R style

**Naming Conventions**:
- Classes: PascalCase
- Methods: camelCase
- Constants: UPPER_SNAKE_CASE
- Packages: lowercase

**Example**:
```java
public class UserService {
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    private final UserRepository userRepository;
    
    public UserDto getUserById(UUID id) {
        return userRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

### Kotlin Code Style

**Formatting**:
- Indentation: 4 spaces
- Line length: 120 characters

**Example**:
```kotlin
class ImageService(
    private val imageRepository: ImageRepository,
    private val jwtService: JwtService
) {
    fun save(file: MultipartFile, id: String, type: ImageType) {
        val uploadDto = jwtService.validateTokenAndGetUploadDto(token)
        imageRepository.upload(file.inputStream, id, type)
    }
}
```

### Documentation

**JavaDoc/KDoc**:
```java
/**
 * Creates a new user account with the provided details.
 *
 * @param request the user creation request containing username, email, and password
 * @return the UUID of the newly created user
 * @throws CreateException if user creation fails
 */
public UUID createUser(UserCreateRequest request) {
    // implementation
}
```

### Best Practices

**Controllers**:
- Keep thin, delegate to services
- Use DTOs for request/response
- Handle exceptions with @ExceptionHandler
- Use proper HTTP status codes

**Services**:
- Single responsibility
- Use @Transactional appropriately
- Validate input
- Log important operations

**Repositories**:
- Use Spring Data JPA
- Custom queries with @Query
- Proper indexing

**DTOs**:
- Use records (Java 17+) or data classes (Kotlin)
- Validation annotations
- Immutable when possible

**Testing**:
- Arrange-Act-Assert pattern
- Meaningful test names
- Mock external dependencies
- Test edge cases

---

## Git Workflow

### Branch Strategy

**Main Branches**:
- `main` - Production-ready code
- `develop` - Integration branch

**Feature Branches**:
- `feature/user-authentication`
- `feature/playlist-sharing`

**Bugfix Branches**:
- `bugfix/login-error`
- `bugfix/streaming-timeout`

**Hotfix Branches**:
- `hotfix/security-patch`

### Workflow

```bash
# 1. Create feature branch
git checkout develop
git pull origin develop
git checkout -b feature/my-feature

# 2. Make changes and commit
git add .
git commit -m "feat: add user profile endpoint"

# 3. Push to remote
git push origin feature/my-feature

# 4. Create pull request
# Use GitHub/GitLab UI

# 5. After review and approval, merge to develop
git checkout develop
git merge feature/my-feature
git push origin develop

# 6. Delete feature branch
git branch -d feature/my-feature
git push origin --delete feature/my-feature
```

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance

**Examples**:
```
feat(auth): add JWT token refresh endpoint

Implements token refresh mechanism to allow users to obtain
new access tokens without re-authenticating.

Closes #123
```

```
fix(streaming): resolve audio buffering issue

Fixed race condition in byte-range streaming that caused
audio playback interruptions.

Fixes #456
```

### Pull Request Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No new warnings
- [ ] Tests pass locally
```

---

## Debugging

### Remote Debugging

**Start service with debug**:
```bash
./gradlew bootRun --debug-jvm
```

**IntelliJ IDEA**:
1. Run → Edit Configurations
2. Add → Remote JVM Debug
3. Host: localhost, Port: 5005
4. Start debugging

### Logging

**Enable debug logging**:
```yaml
# application.yml
logging:
  level:
    com.musicapp: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

**Log specific package**:
```yaml
logging:
  level:
    com.musicapp.musicservice.service: TRACE
```

### Database Debugging

**Show SQL queries**:
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
```

**Log query parameters**:
```yaml
logging:
  level:
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Kafka Debugging

**Consumer logs**:
```yaml
logging:
  level:
    org.apache.kafka: DEBUG
    org.springframework.kafka: DEBUG
```

**Check consumer lag**:
```bash
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe --group statistics-service-group
```

---

## Common Tasks

### Add New Endpoint

1. **Create DTO**:
```java
public record TrackCreateRequest(
    String title,
    List<UUID> artistIds,
    Integer duration
) {}
```

2. **Add Service Method**:
```java
@Service
public class TrackService {
    public UUID createTrack(TrackCreateRequest request) {
        // implementation
    }
}
```

3. **Add Controller Endpoint**:
```java
@RestController
@RequestMapping("/tracks")
public class TrackController {
    @PostMapping
    public UUID createTrack(@RequestBody TrackCreateRequest request) {
        return trackService.createTrack(request);
    }
}
```

4. **Add Tests**:
```java
@Test
void shouldCreateTrack() {
    // test implementation
}
```

### Add Database Migration

**Create migration file**:
```sql
-- V2__add_track_genre.sql
ALTER TABLE track_data ADD COLUMN genre VARCHAR(50);
CREATE INDEX idx_track_genre ON track_data(genre);
```

**Run migration**:
```bash
./gradlew flywayMigrate
```

### Add Kafka Event

1. **Define Event**:
```java
public record TrackLikedEvent(
    UUID trackId,
    UUID userId,
    Instant timestamp
) {}
```

2. **Publish Event**:
```java
@Service
public class TrackService {
    private final KafkaTemplate<String, TrackLikedEvent> kafkaTemplate;
    
    public void likeTrack(UUID trackId, UUID userId) {
        // like logic
        kafkaTemplate.send("track-liked", 
            new TrackLikedEvent(trackId, userId, Instant.now()));
    }
}
```

3. **Consume Event**:
```java
@Service
public class StatisticsConsumer {
    @KafkaListener(topics = "track-liked")
    public void handleTrackLiked(TrackLikedEvent event) {
        // process event
    }
}
```

### Add ElasticSearch Index

1. **Define Document**:
```java
@Document(indexName = "tracks")
public class TrackDocument {
    @Id
    private String id;
    private String title;
    private List<String> artistNames;
}
```

2. **Create Repository**:
```java
public interface TrackSearchRepository 
    extends ElasticsearchRepository<TrackDocument, String> {
    
    List<TrackDocument> findByTitleContaining(String title);
}
```

3. **Index Document**:
```java
trackSearchRepository.save(trackDocument);
```

---

## Contributing

### Before Contributing

1. Check existing issues
2. Discuss major changes first
3. Follow code style guidelines
4. Write tests
5. Update documentation

### Contribution Process

1. Fork repository
2. Create feature branch
3. Make changes
4. Run tests
5. Submit pull request
6. Address review comments
7. Merge after approval

### Code Review Guidelines

**As Author**:
- Keep PRs small and focused
- Write clear descriptions
- Respond to feedback promptly
- Update based on comments

**As Reviewer**:
- Be constructive and respectful
- Focus on code quality
- Check for edge cases
- Verify tests are adequate

---

## Resources

### Documentation
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Cloud Docs](https://spring.io/projects/spring-cloud)
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Gradle Docs](https://docs.gradle.org/)

### Tools
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Postman](https://www.postman.com/)
- [DBeaver](https://dbeaver.io/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)

### Community
- GitHub Discussions
- Slack Channel
- Stack Overflow tag: `music-app`

---

## Troubleshooting

### Port Already in Use

```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Gradle Build Fails

```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

### Docker Issues

```bash
# Restart Docker
docker-compose down
docker-compose up -d

# Clean Docker
docker system prune -a
```

### Database Connection Issues

- Check if PostgreSQL container is running
- Verify connection details in application.properties
- Check firewall settings
- Ensure database exists

---

## FAQ

**Q: How do I reset the database?**
```bash
docker-compose down -v
docker-compose up -d
```

**Q: How do I add a new service?**
1. Create service directory
2. Copy build.gradle.kts from existing service
3. Create Application class
4. Add to docker-compose.yml
5. Register with Eureka

**Q: How do I test Kafka locally?**
Use Kafka UI or command-line tools to produce/consume messages.

**Q: How do I debug a specific service?**
Start service with `--debug-jvm` and attach remote debugger.

---

## Next Steps

- Read [Architecture Documentation](ARCHITECTURE.md)
- Review [API Reference](API-REFERENCE.md)
- Check [Deployment Guide](DEPLOYMENT.md)
- Explore service-specific docs in `docs/services/`
