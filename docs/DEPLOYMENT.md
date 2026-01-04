# Deployment Guide

## Overview

This guide covers deployment strategies for the Music Streaming Application, including local development, Docker deployment, and production considerations.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Docker Deployment](#docker-deployment)
4. [Production Deployment](#production-deployment)
5. [Environment Configuration](#environment-configuration)
6. [Monitoring and Logging](#monitoring-and-logging)
7. [Backup and Recovery](#backup-and-recovery)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

- **Docker**: 20.10+ and Docker Compose 2.0+
- **Java**: JDK 17+ (for local development)
- **Gradle**: 7.0+ (wrapper included)
- **Git**: For version control

### System Requirements

**Development**:
- CPU: 4+ cores
- RAM: 8GB minimum, 16GB recommended
- Disk: 20GB free space

**Production**:
- CPU: 8+ cores per service cluster
- RAM: 32GB+ per node
- Disk: 100GB+ for databases and storage
- Network: 1Gbps+

---

## Local Development Setup

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/music-app.git
cd music-app
```

### 2. Configure Environment

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin
CLICKHOUSE_USER=user
CLICKHOUSE_PASSWORD=password
```

### 3. Start Infrastructure

Start all infrastructure services (databases, message queues, etc.):

```bash
docker-compose -f docker-compose.infra.yml up -d
```

This starts:
- PostgreSQL (3 instances)
- MinIO
- Kafka + Zookeeper
- ClickHouse
- ElasticSearch

### 4. Build Services

Build all microservices:

```bash
# Auth Service
cd auth-service
./gradlew build
cd ..

# User Service
cd user-service
./gradlew build
cd ..

# Music Service
cd music-service
./gradlew build
cd ..

# Image Service
cd image-service
./gradlew build
cd ..

# Streaming Service
cd streaming-service
./gradlew build
cd ..

# Statistics Service
cd statistics-service
./gradlew build
cd ..

# Eureka
cd eureka
./gradlew build
cd ..

# Gateway
cd gateway
./gradlew build
cd ..
```

### 5. Run Services

Start each service in separate terminals:

```bash
# Terminal 1 - Eureka
cd eureka
./gradlew bootRun

# Terminal 2 - Gateway
cd gateway
./gradlew bootRun

# Terminal 3 - Auth Service
cd auth-service
./gradlew bootRun

# Terminal 4 - User Service
cd user-service
./gradlew bootRun

# Terminal 5 - Music Service
cd music-service
./gradlew bootRun

# Terminal 6 - Image Service
cd image-service
./gradlew bootRun

# Terminal 7 - Streaming Service
cd streaming-service
./gradlew bootRun

# Terminal 8 - Statistics Service
cd statistics-service
./gradlew bootRun
```

### 6. Verify Deployment

Check Eureka Dashboard:
```
http://localhost:8761
```

All services should be registered.

Test API Gateway:
```bash
curl http://localhost:8080/actuator/health
```

---

## Docker Deployment

### Development Mode

Start all services with Docker Compose:

```bash
docker-compose -f docker-compose.dev.yml up -d
```

This starts infrastructure and all microservices.

### View Logs

```bash
# All services
docker-compose -f docker-compose.dev.yml logs -f

# Specific service
docker-compose -f docker-compose.dev.yml logs -f auth-service
```

### Stop Services

```bash
docker-compose -f docker-compose.dev.yml down
```

### Clean Up

Remove all containers, volumes, and networks:

```bash
docker-compose -f docker-compose.dev.yml down -v
```

---

## Production Deployment

### Architecture Overview

```
┌─────────────────────────────────────────────────┐
│              Load Balancer (Nginx)              │
│                  (Port 80/443)                  │
└────────────────────┬────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼────────┐      ┌────────▼────────┐
│  Gateway (x3)  │      │  Gateway (x3)   │
│   Instances    │      │   Instances     │
└───────┬────────┘      └────────┬────────┘
        │                        │
        └────────────┬───────────┘
                     │
        ┌────────────┴────────────┐
        │   Service Discovery     │
        │   (Eureka Cluster)      │
        └────────────┬────────────┘
                     │
    ┌────────────────┼────────────────┐
    │                │                │
┌───▼───┐      ┌────▼────┐     ┌────▼────┐
│Service│      │ Service │     │ Service │
│ x3    │      │   x3    │     │   x3    │
└───────┘      └─────────┘     └─────────┘
```

### 1. Container Orchestration

#### Using Docker Swarm

**Initialize Swarm**:
```bash
docker swarm init
```

**Deploy Stack**:
```bash
docker stack deploy -c docker-compose.deploy.yml music-app
```

**Scale Services**:
```bash
docker service scale music-app_gateway=3
docker service scale music-app_auth-service=3
docker service scale music-app_music-service=5
```

#### Using Kubernetes

**Create Namespace**:
```bash
kubectl create namespace music-app
```

**Deploy Services**:
```bash
kubectl apply -f k8s/
```

**Scale Deployment**:
```bash
kubectl scale deployment music-service --replicas=5 -n music-app
```

### 2. Database Setup

#### PostgreSQL

**High Availability**:
- Use managed PostgreSQL (AWS RDS, Google Cloud SQL)
- Or setup PostgreSQL cluster with replication
- Configure connection pooling (PgBouncer)

**Configuration**:
```yaml
# Production settings
max_connections: 200
shared_buffers: 4GB
effective_cache_size: 12GB
maintenance_work_mem: 1GB
checkpoint_completion_target: 0.9
wal_buffers: 16MB
default_statistics_target: 100
random_page_cost: 1.1
effective_io_concurrency: 200
work_mem: 10MB
min_wal_size: 1GB
max_wal_size: 4GB
```

#### ClickHouse

**Cluster Setup**:
```xml
<remote_servers>
    <cluster_name>
        <shard>
            <replica>
                <host>clickhouse-1</host>
                <port>9000</port>
            </replica>
            <replica>
                <host>clickhouse-2</host>
                <port>9000</port>
            </replica>
        </shard>
    </cluster_name>
</remote_servers>
```

#### ElasticSearch

**Cluster Configuration**:
```yaml
cluster.name: music-search-cluster
node.name: es-node-1
network.host: 0.0.0.0
discovery.seed_hosts: ["es-node-1", "es-node-2", "es-node-3"]
cluster.initial_master_nodes: ["es-node-1", "es-node-2", "es-node-3"]
```

### 3. Object Storage (MinIO)

**Distributed Setup**:
```bash
minio server http://minio-{1...4}/data{1...4}
```

**Or use S3**:
- AWS S3
- Google Cloud Storage
- Azure Blob Storage

Update service configuration to use S3-compatible endpoints.

### 4. Message Queue (Kafka)

**Production Cluster**:
- 3+ broker nodes
- 3+ Zookeeper nodes (or use KRaft mode)
- Replication factor: 3
- Min in-sync replicas: 2

**Configuration**:
```properties
# Broker settings
num.network.threads=8
num.io.threads=16
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
log.retention.hours=168
log.segment.bytes=1073741824
num.partitions=3
default.replication.factor=3
min.insync.replicas=2
```

### 5. Load Balancer

#### Nginx Configuration

```nginx
upstream gateway {
    least_conn;
    server gateway-1:8080 max_fails=3 fail_timeout=30s;
    server gateway-2:8080 max_fails=3 fail_timeout=30s;
    server gateway-3:8080 max_fails=3 fail_timeout=30s;
}

server {
    listen 80;
    server_name api.yourdomain.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.yourdomain.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    
    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=100r/s;
    limit_req zone=api_limit burst=200 nodelay;
    
    location / {
        proxy_pass http://gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # Streaming endpoint (longer timeout)
    location /api/stream {
        proxy_pass http://gateway;
        proxy_buffering off;
        proxy_read_timeout 3600s;
    }
}
```

### 6. SSL/TLS Configuration

**Using Let's Encrypt**:
```bash
certbot --nginx -d api.yourdomain.com
```

**Or use AWS Certificate Manager** for AWS deployments.

---

## Environment Configuration

### Production Environment Variables

**Auth Service**:
```env
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=prod
AUTH_SERVICE_DB_HOST=postgres-auth.internal
AUTH_SERVICE_DB_PORT=5432
POSTGRES_USER=auth_user
POSTGRES_PASSWORD=<secure-password>
JWT_SECRET=<secure-random-secret>
JWT_EXPIRATION=604800
EUREKA_URL=http://eureka-1:8761/eureka,http://eureka-2:8761/eureka
```

**Music Service**:
```env
SERVER_PORT=8083
SPRING_PROFILES_ACTIVE=prod
MUSIC_SERVICE_DB_HOST=postgres-music.internal
ELASTICSEARCH_URL=http://es-cluster:9200
KAFKA_BOOTSTRAP_SERVERS=kafka-1:9092,kafka-2:9092,kafka-3:9092
```

**Image/Streaming Services**:
```env
MINIO_ENDPOINT=https://s3.amazonaws.com
MINIO_ACCESS_KEY=<aws-access-key>
MINIO_SECRET_KEY=<aws-secret-key>
# Or use MinIO cluster
MINIO_ENDPOINT=http://minio-cluster:9000
```

### Secrets Management

**Using Kubernetes Secrets**:
```bash
kubectl create secret generic db-credentials \
  --from-literal=username=postgres \
  --from-literal=password=<secure-password> \
  -n music-app
```

**Using HashiCorp Vault**:
```bash
vault kv put secret/music-app/db \
  username=postgres \
  password=<secure-password>
```

**Using AWS Secrets Manager**:
```bash
aws secretsmanager create-secret \
  --name music-app/db-credentials \
  --secret-string '{"username":"postgres","password":"<secure-password>"}'
```

---

## Monitoring and Logging

### Application Monitoring

**Spring Boot Actuator**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**Prometheus Configuration**:
```yaml
scrape_configs:
  - job_name: 'music-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets:
        - 'gateway:8080'
        - 'auth-service:8081'
        - 'music-service:8083'
```

**Grafana Dashboards**:
- JVM metrics
- HTTP request rates
- Database connections
- Kafka lag
- Custom business metrics

### Centralized Logging

**ELK Stack**:

**Logstash Configuration**:
```ruby
input {
  tcp {
    port => 5000
    codec => json
  }
}

filter {
  if [logger_name] =~ "com.musicapp" {
    mutate {
      add_tag => ["music-app"]
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "music-app-%{+YYYY.MM.dd}"
  }
}
```

**Application Logging**:
```yaml
logging:
  level:
    com.musicapp: INFO
    org.springframework: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: /var/log/music-app/application.log
```

### Distributed Tracing

**Zipkin/Jaeger Integration**:
```yaml
spring:
  zipkin:
    base-url: http://zipkin:9411
  sleuth:
    sampler:
      probability: 0.1
```

---

## Backup and Recovery

### Database Backups

**PostgreSQL**:
```bash
# Automated backup script
#!/bin/bash
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
pg_dump -h postgres-host -U postgres music_db | gzip > backup_${TIMESTAMP}.sql.gz
aws s3 cp backup_${TIMESTAMP}.sql.gz s3://backups/postgres/
```

**ClickHouse**:
```bash
# Backup
clickhouse-client --query "BACKUP TABLE history_entries TO Disk('backups', 'backup.zip')"

# Restore
clickhouse-client --query "RESTORE TABLE history_entries FROM Disk('backups', 'backup.zip')"
```

### Object Storage Backups

**MinIO to S3**:
```bash
mc mirror minio/audio s3/audio-backup
mc mirror minio/artwork s3/artwork-backup
```

### Disaster Recovery Plan

1. **Regular Backups**: Daily automated backups
2. **Backup Testing**: Monthly restore tests
3. **Multi-Region**: Replicate to secondary region
4. **Documentation**: Maintain runbooks
5. **RTO/RPO**: Define recovery objectives

---

## Troubleshooting

### Service Not Starting

**Check logs**:
```bash
docker logs <container-name>
kubectl logs <pod-name> -n music-app
```

**Common issues**:
- Database connection failure
- Eureka registration timeout
- Port conflicts
- Missing environment variables

### High Memory Usage

**Check JVM settings**:
```bash
java -XX:+PrintFlagsFinal -version | grep -i heap
```

**Adjust heap size**:
```env
JAVA_OPTS=-Xms512m -Xmx2g
```

### Database Connection Pool Exhausted

**Increase pool size**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### Kafka Consumer Lag

**Check lag**:
```bash
kafka-consumer-groups --bootstrap-server kafka:9092 \
  --describe --group statistics-service-group
```

**Solutions**:
- Increase consumer instances
- Optimize processing logic
- Increase partition count

### Service Discovery Issues

**Check Eureka**:
```
http://eureka:8761
```

**Force re-registration**:
```bash
curl -X POST http://service:8080/actuator/restart
```

---

## Security Checklist

- [ ] Use HTTPS/TLS for all communications
- [ ] Rotate JWT secrets regularly
- [ ] Use strong database passwords
- [ ] Enable database encryption at rest
- [ ] Configure firewall rules
- [ ] Implement rate limiting
- [ ] Enable CORS properly
- [ ] Use secrets management system
- [ ] Regular security updates
- [ ] Enable audit logging
- [ ] Implement intrusion detection
- [ ] Regular security scans

---

## Performance Tuning

### JVM Tuning

```bash
JAVA_OPTS="
  -Xms2g -Xmx4g
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+HeapDumpOnOutOfMemoryError
  -XX:HeapDumpPath=/var/log/heapdump.hprof
"
```

### Database Tuning

**Connection Pooling**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### Caching Strategy

**Redis Integration** (future):
```yaml
spring:
  cache:
    type: redis
  redis:
    host: redis-cluster
    port: 6379
```

---

## Maintenance

### Rolling Updates

**Zero-downtime deployment**:
```bash
# Update one instance at a time
kubectl set image deployment/music-service \
  music-service=music-service:v2.0 \
  --record
```

### Database Migrations

**Flyway migrations**:
- Test migrations in staging
- Backup before migration
- Use versioned migrations
- Monitor migration progress

### Scaling Guidelines

**Horizontal Scaling**:
- Gateway: Scale based on request rate
- Music Service: Scale for search load
- Statistics Service: Scale for write throughput
- Streaming Service: Scale for concurrent streams

**Vertical Scaling**:
- Database: Increase CPU/RAM for query performance
- ClickHouse: More RAM for better caching
- ElasticSearch: More disk for index storage

---

## Cost Optimization

- Use spot instances for non-critical services
- Implement auto-scaling policies
- Archive old data to cheaper storage
- Use CDN for static assets
- Optimize database queries
- Implement caching layers
- Monitor and eliminate waste

---

## Support and Resources

- **Documentation**: `/docs`
- **Issue Tracker**: GitHub Issues
- **Monitoring**: Grafana dashboards
- **Logs**: Kibana/CloudWatch
- **Alerts**: PagerDuty/Slack integration
