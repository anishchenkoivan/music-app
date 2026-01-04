# Music Streaming App 🎵

A production-ready microservice backend for a music streaming and sharing application built with Spring Boot, Spring Cloud, and modern cloud-native technologies.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 🎯 Overview

This is a comprehensive microservice-based backend that provides all the functionality needed for a modern music streaming platform, including user management, music catalog, audio streaming, search, and analytics.

## ✨ Key Features

- **🔐 Authentication & Authorization** - JWT-based auth with role-based access control
- **🎵 Music Catalog** - Tracks, albums, artists, and playlist management
- **📻 Audio Streaming** - Efficient byte-range streaming with HTTP 206 support
- **🖼️ Media Storage** - Album artwork and audio file storage with MinIO/S3
- **🔍 Full-Text Search** - ElasticSearch integration for fast music discovery
- **📊 Analytics** - User listening history with ClickHouse for high-performance queries
- **⚡ Event-Driven** - Kafka for asynchronous communication between services
- **🔄 Service Discovery** - Netflix Eureka for dynamic service registration
- **🚪 API Gateway** - Spring Cloud Gateway as single entry point

## 🏗️ Architecture

The application consists of 8 microservices:

```
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 8080)                      │
└────────────┬────────────────────────────────────────────────────┘
             │
    ┌────────┴───────┬─────────┬──────────┬──────────┬───────────┐
    │                │         │          │          │           │
┌───▼────┐  ┌────────▼───┐  ┌──▼─────┐ ┌──▼──────┐ ┌─▼────────┐ ┌▼──────────┐
│ Auth   │  │   User     │  │ Music  │ │ Image   │ │Streaming │ │Statistics │
│Service │  │  Service   │  │Service │ │ Service │ │ Service  │ │ Service   │
└───┬────┘  └─────┬──────┘  └───┬────┘ └────┬────┘ └────┬─────┘ └─────┬─────┘
    │             │             │           │           │             │
┌───▼──────┐ ┌────▼──────┐  ┌───▼──────┐ ┌──▼──────┐ ┌──▼──────┐  ┌───▼────────┐
│PostgreSQL│ │PostgreSQL │  │PostgreSQL│ │  MinIO  │ │  MinIO  │  │ ClickHouse │
└──────────┘ └───────────┘  └────┬─────┘ └─────────┘ └─────────┘  └────────────┘
                                 │
                            ┌────▼──────────┐
                            │ ElasticSearch │
                            └───────────────┘
```

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose 2.0+
- Java 17+ (for local development)
- 8GB RAM minimum

### Start with Docker

```bash
# Clone repository
git clone https://github.com/yourusername/music-app.git
cd music-app

# Copy environment file
cp .env.example .env

# Start all services
docker-compose up -d

# Check service health
curl http://localhost:8080/actuator/health
```

### Access Services

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin)

## 📚 Documentation

Comprehensive documentation is available in the [`docs/`](docs/) directory:

- **[📖 Documentation Index](docs/README.md)** - Start here for complete documentation
- **[🏛️ Architecture](docs/ARCHITECTURE.md)** - System design and architecture patterns
- **[🔌 API Reference](docs/API-REFERENCE.md)** - Complete API endpoint documentation
- **[🚀 Deployment Guide](docs/DEPLOYMENT.md)** - Production deployment instructions
- **[💻 Development Guide](docs/DEVELOPMENT-GUIDE.md)** - Setup and contribution guidelines

### Service Documentation

- [Auth Service](docs/services/AUTH-SERVICE.md) - Authentication & JWT management
- [User Service](docs/services/USER-SERVICE.md) - User profiles & accounts
- [Music Service](docs/services/MUSIC-SERVICE.md) - Music catalog & playlists
- [Image Service](docs/services/IMAGE-SERVICE.md) - Album artwork storage
- [Streaming Service](docs/services/STREAMING-SERVICE.md) - Audio streaming
- [Statistics Service](docs/services/STATISTICS-SERVICE.md) - Analytics & history

## 🛠️ Technology Stack

### Backend
- **Java 17** & **Kotlin 1.9**
- **Spring Boot 3.x** - Application framework
- **Spring Cloud** - Microservice infrastructure (Gateway, Eureka)
- **Spring Security** - Authentication & authorization

### Databases & Storage
- **PostgreSQL** - Relational data (users, music catalog)
- **ClickHouse** - Analytical database (listening history)
- **ElasticSearch** - Full-text search engine
- **MinIO** - S3-compatible object storage (audio, images)

### Infrastructure
- **Apache Kafka** - Event streaming platform
- **Docker** - Containerization
- **Gradle** - Build automation

## 📊 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Main entry point |
| Eureka | 8761 | Service discovery |
| Auth Service | 8081 | Authentication |
| User Service | 8082 | User management |
| Music Service | 8083 | Music catalog |
| Image Service | 8084 | Image storage |
| Streaming Service | 8085 | Audio streaming |
| Statistics Service | 8086 | Analytics |

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Run with coverage
./gradlew test jacocoTestReport
```

## 🤝 Contributing

Contributions are welcome! Please read our [Development Guide](docs/DEVELOPMENT-GUIDE.md) for details on:

- Setting up your development environment
- Code style guidelines
- Git workflow and commit conventions
- Pull request process

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

See [API Reference](docs/API-REFERENCE.md) for complete documentation.

## 🗺️ Roadmap

### Current Features ✅
- User authentication and management
- Music catalog (tracks, albums, artists, playlists)
- Audio streaming with range support
- Image storage for album artwork
- Full-text search with ElasticSearch
- Listening history and analytics with ClickHouse
- Event-driven architecture with Kafka

### Planned Features 🚧
- Integration with 3rd party music metadata APIs (Spotify, MusicBrainz)
- Recommendation engine based on listening history
- Social features (following, sharing, collaborative playlists)
- Mobile push notifications
- Track lyrics support
- Music charts and trending
- Artist verification system
- Multi-region deployment support

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud teams
- Netflix OSS (Eureka)
- Apache Kafka community
- ClickHouse, ElasticSearch, and MinIO teams

## 📞 Support

- **Documentation**: [docs/](docs/)
- **Issues**: [GitHub Issues](https://github.com/anishchenkoivan/music-app/issues)
- **Discussions**: [GitHub Discussions](https://github.com/anishchenkoivan/music-app/discussions)

---

**Built with ❤️ using Spring Boot and modern cloud-native technologies**
