# Music Streaming App - Documentation

Welcome to the comprehensive documentation for the Music Streaming Application - a microservice-based backend for music streaming and sharing.

## 📚 Documentation Index

### Getting Started

- **[Architecture Overview](ARCHITECTURE.md)** - System architecture, components, and design patterns
- **[Development Guide](DEVELOPMENT-GUIDE.md)** - Setup your development environment and start contributing
- **[Deployment Guide](DEPLOYMENT.md)** - Deploy the application locally or to production

### API Documentation

- **[API Reference](API-REFERENCE.md)** - Complete API endpoint documentation with examples

### Service Documentation

Individual service documentation with detailed implementation details:

- **[Auth Service](services/AUTH-SERVICE.md)** - Authentication and JWT token management
- **[User Service](services/USER-SERVICE.md)** - User profile and account management
- **[Music Service](services/MUSIC-SERVICE.md)** - Music catalog, tracks, albums, artists, and playlists
- **[Image Service](services/IMAGE-SERVICE.md)** - Image storage and retrieval (album artwork)
- **[Streaming Service](services/STREAMING-SERVICE.md)** - Audio file streaming with range support
- **[Statistics Service](services/STATISTICS-SERVICE.md)** - User listening history and analytics

## 🏗️ Architecture Overview

The application follows a **microservices architecture** with:

- **8 Microservices**: Auth, User, Music, Image, Streaming, Statistics, Gateway, Eureka
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Event-Driven**: Apache Kafka for async communication
- **Multiple Databases**: PostgreSQL, ClickHouse, ElasticSearch
- **Object Storage**: MinIO (S3-compatible)

```
Client → API Gateway → Microservices → Databases/Storage
                ↓
         Service Discovery (Eureka)
                ↓
         Event Bus (Kafka)
```

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Gradle 7.0+

### Start Development Environment

```bash
# 1. Clone repository
git clone https://github.com/yourusername/music-app.git
cd music-app

# 2. Setup environment
cp .env.example .env

# 3. Start infrastructure
docker-compose -f docker-compose.infra.yml up -d

# 4. Start services
docker-compose -f docker-compose.dev.yml up -d
```

### Verify Installation

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **MinIO Console**: http://localhost:9001

## 📖 Key Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (RBAC)
- Secure password encryption
- Token validation and refresh

### Music Management
- Track, album, and artist management
- Playlist creation and management
- Full-text search with ElasticSearch
- Copyright validation

### Media Storage
- Album artwork storage
- Audio file storage and streaming
- Range-based streaming (HTTP 206)
- MinIO/S3 integration

### Analytics
- User listening history
- Play count tracking
- ClickHouse for high-performance analytics
- Real-time statistics

### Event-Driven Architecture
- Kafka for async communication
- Track upload events
- Play history events
- Search indexing events

## 🛠️ Technology Stack

### Backend
- **Java 17** & **Kotlin**
- **Spring Boot** 3.x
- **Spring Cloud** (Gateway, Eureka)
- **Spring Security** & JWT

### Databases
- **PostgreSQL** - Relational data
- **ClickHouse** - Analytics
- **ElasticSearch** - Full-text search
- **MinIO** - Object storage

### Infrastructure
- **Apache Kafka** - Event streaming
- **Docker** - Containerization
- **Gradle** - Build automation

## 📊 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| Gateway | 8080 | API Gateway (main entry point) |
| Eureka | 8761 | Service Discovery |
| Auth Service | 8081 | Authentication |
| User Service | 8082 | User Management |
| Music Service | 8083 | Music Catalog |
| Image Service | 8084 | Image Storage |
| Streaming Service | 8085 | Audio Streaming |
| Statistics Service | 8086 | Analytics |

### Infrastructure Ports

| Service | Port | Description |
|---------|------|-------------|
| PostgreSQL (auth) | 5434 | Auth database |
| PostgreSQL (user) | 5432 | User database |
| PostgreSQL (music) | 5433 | Music database |
| MinIO API | 9002 | Object storage API |
| MinIO Console | 9001 | MinIO web console |
| Kafka | 9092 | Message broker |
| ClickHouse HTTP | 8123 | Analytics database |
| ClickHouse Native | 9000 | Native protocol |
| ElasticSearch | 9200 | Search engine |

## 🔐 Security

- HTTPS/TLS for all communications (production)
- JWT token-based authentication
- Signed URLs for file uploads
- Password encryption with BCrypt
- Role-based access control
- Rate limiting (recommended)

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Run with coverage
./gradlew test jacocoTestReport
```

## 📈 Monitoring

- Spring Boot Actuator endpoints
- Prometheus metrics export
- Grafana dashboards (recommended)
- Centralized logging with ELK (recommended)
- Distributed tracing with Zipkin (recommended)

## 🤝 Contributing

We welcome contributions! Please see the [Development Guide](DEVELOPMENT-GUIDE.md) for:

- Setting up your development environment
- Code style guidelines
- Git workflow
- Pull request process

### Quick Contribution Steps

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 API Examples

### Create User

```bash
curl -X POST http://localhost:8080/api/user/create-user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "displayName": "John Doe"
  }'
```

### Get Authentication Token

```bash
curl -X POST http://localhost:8080/api/auth/get-token \
  -H "Content-Type: application/json" \
  -d '{"id":"user-uuid","password":"password123"}'
```

### Stream Audio

```bash
curl -H "Range: bytes=0-1023" \
  http://localhost:8080/api/stream/{track-id} \
  -o audio-chunk.mp3
```

See [API Reference](API-REFERENCE.md) for complete documentation.

## 🗺️ Roadmap

### Current Features
- ✅ User authentication and management
- ✅ Music catalog (tracks, albums, artists)
- ✅ Playlist management
- ✅ Audio streaming with range support
- ✅ Image storage for artwork
- ✅ Full-text search
- ✅ Listening history and analytics

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud teams
- Netflix OSS (Eureka)
- Apache Kafka community
- ClickHouse team
- ElasticSearch team
- MinIO team

---

**Happy Coding! 🎵**
