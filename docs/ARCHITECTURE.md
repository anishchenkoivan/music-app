# Music Streaming App - Architecture Documentation

## Overview

This is a microservice-based backend for a music streaming and sharing application built with Spring Boot and Spring Cloud. The system provides comprehensive music streaming capabilities, user management, authentication, and analytics.

## Architecture Pattern

The application follows a **microservices architecture** with the following key patterns:

- **Service Discovery**: Netflix Eureka for dynamic service registration and discovery
- **API Gateway**: Spring Cloud Gateway for unified entry point and routing
- **Event-Driven Architecture**: Apache Kafka for asynchronous communication between services
- **Database per Service**: Each microservice has its own database for data isolation
- **JWT Authentication**: Token-based authentication with signed URLs for secure streaming

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         API Gateway (8080)                      │
│                    Spring Cloud Gateway                         │
└────────────┬────────────────────────────────────────────────────┘
             │
             ├─────────────────────────────────────────────────┐
             │                                                 │
┌────────────▼──────────┐                    ┌─────────────────▼──────────┐
│   Eureka Server       │                    │   Service Discovery        │
│   (8761)              │◄───────────────────┤   All services register    │
└───────────────────────┘                    └────────────────────────────┘
             │
             │
    ┌────────┴───────┬─────────┬──────────┬──────────┬───────────┐
    │                │         │          │          │           │
┌───▼────┐  ┌────────▼───┐  ┌──▼─────┐ ┌──▼──────┐ ┌─▼────────┐ ┌▼──────────┐
│ Auth   │  │   User     │  │ Music  │ │ Image   │ │Streaming │ │Statistics │
│Service │  │  Service   │  │Service │ │ Service │ │ Service  │ │ Service   │
│(8081)  │  │  (8082)    │  │(8083)  │ │ (8084)  │ │ (8085)   │ │ (8086)    │
└───┬────┘  └─────┬──────┘  └───┬────┘ └────┬────┘ └────┬─────┘ └─────┬─────┘
    │             │             │           │           │             │
┌───▼──────┐ ┌────▼──────┐  ┌───▼──────┐ ┌──▼──────┐ ┌──▼──────┐  ┌───▼────────┐
│PostgreSQL│ │PostgreSQL │  │PostgreSQL│ │  MinIO  │ │  MinIO  │  │ ClickHouse │
│auth_db   │ │ user_db   │  │ music_db │ │ (Images)│ │ (Audio) │  │statistics  │
│(5434)    │ │ (5432)    │  │ (5433)   │ │(9002)   │ │(9002)   │  │(8123/9000) │
└──────────┘ └───────────┘  └────┬─────┘ └─────────┘ └─────────┘  └────────────┘
                                 │
                            ┌────▼──────────┐
                            │ ElasticSearch │
                            │   (9200)      │
                            └───────────────┘

                    ┌──────────────────────┐
                    │   Apache Kafka       │
                    │   (9092)             │
                    │  Event Bus           │
                    └──────────────────────┘
```

## Core Components

### 1. Eureka Service Discovery (Port 8761)
- **Technology**: Spring Cloud Netflix Eureka
- **Purpose**: Service registry for dynamic service discovery
- **Responsibilities**:
  - Service registration
  - Health monitoring
  - Load balancing support
  - Service instance tracking

### 2. API Gateway (Port 8080)
- **Technology**: Spring Cloud Gateway
- **Purpose**: Single entry point for all client requests
- **Responsibilities**:
  - Request routing to appropriate microservices
  - Authentication and authorization
  - Load balancing
  - Rate limiting and security
- **Routes**:
  - `/api/auth/**` → Auth Service
  - `/api/user/**` → User Service
  - `/api/albums/**`, `/api/tracks/**`, `/api/artists/**`, `/api/playlists/**` → Music Service
  - `/api/images/**` → Image Service
  - `/api/search` → Music Service (ElasticSearch)

### 3. Auth Service (Port 8081)
- **Technology**: Java, Spring Boot, Spring Security
- **Database**: PostgreSQL (port 5434)
- **Purpose**: Authentication and authorization management
- **Responsibilities**:
  - JWT token generation and validation
  - User credential management
  - Password encryption
  - Role-based access control (RBAC)
- **Key Features**:
  - Token expiration: 604800 seconds (7 days)
  - Secure password hashing
  - Admin and user role management

### 4. User Service (Port 8082)
- **Technology**: Java, Spring Boot
- **Database**: PostgreSQL (port 5432)
- **Purpose**: User profile and account management
- **Responsibilities**:
  - User profile CRUD operations
  - Public and private user details
  - User lookup by email/username
  - Integration with Auth Service for authentication
- **Key Features**:
  - Public vs private user data separation
  - User validation
  - Profile updates

### 5. Music Service (Port 8083)
- **Technology**: Java, Spring Boot, Kotlin
- **Database**: PostgreSQL (port 5433)
- **Search Engine**: ElasticSearch (port 9200)
- **Purpose**: Core music catalog and library management
- **Responsibilities**:
  - Track, album, and artist management
  - Playlist creation and management
  - User music library (favorites, history)
  - Full-text search with ElasticSearch
  - Copyright validation
- **Key Features**:
  - Album and track creation with JWT upload tokens
  - Artist-track relationship management
  - Search indexing for tracks and artists
  - Event publishing for track/album creation
  - Integration with Kafka for event streaming

### 6. Image Service (Port 8084)
- **Technology**: Kotlin, Spring Boot
- **Storage**: MinIO (ports 9001-9002)
- **Purpose**: Image storage and retrieval for album artwork
- **Responsibilities**:
  - Image upload with JWT authentication
  - Image retrieval and serving
  - MinIO object storage management
- **Key Features**:
  - Artwork upload with signed tokens
  - Image type validation
  - Direct binary streaming

### 7. Streaming Service (Port 8085)
- **Technology**: Java, Spring Boot
- **Storage**: MinIO (ports 9001-9002)
- **Purpose**: Audio file streaming
- **Responsibilities**:
  - MP3 file upload and storage
  - Range-based audio streaming (HTTP 206 Partial Content)
  - Audio file validation
  - Track upload event publishing
- **Key Features**:
  - Efficient byte-range streaming
  - MP3 format validation
  - Duration extraction
  - Kafka event publishing on upload

### 8. Statistics Service (Port 8086)
- **Technology**: Kotlin, Spring Boot
- **Database**: ClickHouse (ports 8123, 9000)
- **Message Queue**: Apache Kafka
- **Purpose**: User listening history and analytics
- **Responsibilities**:
  - Play history tracking
  - User listening statistics
  - Kafka event consumption
  - High-volume data storage
- **Key Features**:
  - ClickHouse for analytical queries
  - Kafka consumer for history events
  - User history retrieval with pagination

## Data Stores

### PostgreSQL Databases
- **auth_db** (port 5434): User credentials and roles
- **user_db** (port 5432): User profiles and details
- **music_db** (port 5433): Music catalog (tracks, albums, artists, playlists)

### MinIO Object Storage (ports 9001-9002)
- **Images**: Album artwork and user avatars
- **Audio**: MP3 files for streaming

### ClickHouse (ports 8123, 9000)
- **statistics_db**: User play history and analytics
- Optimized for high-volume write operations and analytical queries

### ElasticSearch (port 9200)
- **Music Search Index**: Full-text search for tracks and artists
- Real-time indexing of music catalog

## Communication Patterns

### Synchronous Communication
- **REST APIs**: All services expose RESTful endpoints
- **Service-to-Service**: Via Eureka service discovery
- **Client-to-Service**: Through API Gateway

### Asynchronous Communication
- **Apache Kafka** (port 9092):
  - Track upload events (Streaming Service → Music Service)
  - Album creation events (Music Service → Search indexing)
  - Play history events (Music Service → Statistics Service)

## Security Architecture

### Authentication Flow
1. Client sends credentials to Auth Service via Gateway
2. Auth Service validates credentials and generates JWT token
3. Client includes JWT in subsequent requests
4. Gateway validates token with Auth Service
5. Request forwarded to target service with user context

### Upload Security
1. User creates album/track in Music Service
2. Music Service generates signed JWT upload token
3. Client uses upload token to upload files to Image/Streaming Service
4. Services validate upload token before accepting files

### Streaming Security
- Signed URLs for audio streaming
- Range-based requests for efficient bandwidth usage
- Token validation on each stream request

## Scalability Considerations

### Horizontal Scaling
- All services are stateless and can be scaled horizontally
- Eureka handles multiple instances with load balancing
- Database connections pooled per instance

### Data Partitioning
- Each service has isolated database
- ClickHouse for high-volume analytics data
- MinIO for distributed object storage

### Caching Strategy
- Service discovery cache in Eureka clients
- Potential for Redis cache layer (not yet implemented)

## Technology Stack

### Backend Frameworks
- **Spring Boot**: Core framework for all services
- **Spring Cloud**: Microservice infrastructure (Gateway, Eureka)
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database access for PostgreSQL
- **Spring Kafka**: Event streaming

### Languages
- **Java**: Auth, User, Music, Streaming services
- **Kotlin**: Image, Statistics services

### Databases & Storage
- **PostgreSQL**: Relational data storage
- **ClickHouse**: Analytical database for statistics
- **ElasticSearch**: Full-text search engine
- **MinIO**: S3-compatible object storage

### Infrastructure
- **Apache Kafka**: Event streaming platform
- **Docker**: Containerization
- **Docker Compose**: Local development orchestration

### Build Tools
- **Gradle**: Build automation (Kotlin DSL)
- **Flyway**: Database migrations

## Deployment Architecture

### Development Environment
- Docker Compose orchestrates all services and dependencies
- Separate compose files for infrastructure and services
- Environment variables for configuration

### Service Ports
- Gateway: 8080
- Eureka: 8761
- Auth Service: 8081
- User Service: 8082
- Music Service: 8083
- Image Service: 8084
- Streaming Service: 8085
- Statistics Service: 8086

### Infrastructure Ports
- PostgreSQL (auth): 5434
- PostgreSQL (user): 5432
- PostgreSQL (music): 5433
- MinIO API: 9002
- MinIO Console: 9001
- Kafka: 9092
- Zookeeper: 2181
- ClickHouse HTTP: 8123
- ClickHouse Native: 9000
- ElasticSearch: 9200, 9300

## Future Enhancements

### Planned Features
- Integration with 3rd party music metadata APIs (Spotify, MusicBrainz)
- Redis caching layer for improved performance
- Recommendation engine based on listening history
- Social features (following, sharing)
- Mobile push notifications

### Potential Improvements
- Circuit breaker pattern (Resilience4j)
- Distributed tracing (Zipkin/Jaeger)
- Centralized logging (ELK stack)
- API rate limiting
- CDN integration for static assets
- Multi-region deployment support
