# Statistics Service Documentation

## Overview

The Statistics Service manages user listening history and analytics using ClickHouse for high-performance analytical queries. It consumes play events from Kafka and provides history retrieval endpoints.

## Technology Stack

- **Language**: Kotlin
- **Framework**: Spring Boot
- **Database**: ClickHouse (analytical database)
- **Message Queue**: Apache Kafka
- **Build Tool**: Gradle (Kotlin DSL)

## Service Configuration

- **Port**: 8086
- **Service Name**: `statistics-service`
- **Database**: ClickHouse (ports 8123, 9000)
- **Eureka Registration**: Enabled

## Key Components

### Controllers

#### HistoryController
**Path**: [`/history`](../../statistics-service/src/main/kotlin/com/musicapp/statisticsservice/controller/HistoryController.kt)

**Endpoints**:
- `GET /history/for-user/{userId}` - Get user's listening history

### Services

#### HistoryService
**Responsibilities**:
- Store play history entries
- Retrieve user listening history
- Aggregate statistics
- Data retention management

**Key Methods**:
- `addHistoryEntry(HistoryEntryAddRequest)` - Add play history entry
- `getUserHistory(UUID userId, Int limit)` - Get user's recent plays
- `getStatistics(UUID userId)` - Get user statistics (future)

### Repository

#### ClickHouseHistoryRepository
**Purpose**: ClickHouse database interaction
**Responsibilities**:
- Execute ClickHouse queries
- Batch insert optimization
- Connection management
- Query result mapping

**Key Methods**:
- `insert(HistoryEntry)` - Insert single entry
- `batchInsert(List<HistoryEntry>)` - Batch insert for performance
- `findByUserId(UUID, Int)` - Query user history
- `aggregateStats(UUID)` - Aggregate user statistics

### Kafka Integration

#### StatisticsKafkaConsumer
**Purpose**: Consume play events from Kafka
**Responsibilities**:
- Listen to history events topic
- Deserialize events
- Store in ClickHouse
- Error handling and retry

**Topics Consumed**:
- `history-entry-add` - Play history events

### Data Model

#### HistoryEntry Entity
**Table**: `history_entries`

**Fields**:
- `id` (UUID) - Entry ID
- `userId` (UUID) - User who played the track
- `trackId` (UUID) - Track that was played
- `playedAt` (DateTime) - Timestamp of play
- `duration` (Int) - How long the track was played (seconds)
- `completionRate` (Float) - Percentage of track played (0-100)
- `source` (String) - Play source (web, mobile, etc.)

#### SimplifiedHistoryEntryResponse
**Fields**:
- `trackId` (UUID)
- `playedAt` (DateTime)
- `duration` (Int)

#### UserHistoryResponse
**Fields**:
- `userId` (UUID)
- `entries` (List<SimplifiedHistoryEntryResponse>)
- `totalPlays` (Long)

## API Endpoints

### GET /history/for-user/{userId}
**Description**: Get user's listening history

**Path Parameters**:
- `userId` (UUID) - User ID

**Query Parameters**:
- `limit` (Int) - Number of entries to return (default: 10, max: 100)

**Response**:
```json
{
  "userId": "uuid",
  "entries": [
    {
      "trackId": "uuid",
      "playedAt": "2024-01-04T19:30:00Z",
      "duration": 180
    }
  ],
  "totalPlays": 1234
}
```

**Status Codes**:
- `200 OK` - History retrieved successfully
- `404 NOT FOUND` - User not found or no history

**Use Cases**:
- Display recently played tracks
- Generate listening reports
- Recommendation engine input
- User activity tracking

## ClickHouse Integration

### Why ClickHouse?

**Advantages**:
- **High Write Throughput**: Handles millions of play events per second
- **Fast Analytical Queries**: Optimized for aggregations and time-series data
- **Columnar Storage**: Efficient compression and query performance
- **Scalability**: Horizontal scaling for large datasets
- **Real-time Analytics**: Query fresh data immediately

**Use Cases**:
- Play history storage
- User statistics aggregation
- Trending tracks calculation
- Listening pattern analysis

### Database Schema

**Migration**: [`clickhouse-schema.sql`](../../statistics-service/src/main/resources/db/schema/clickhouse-schema.sql)

```sql
CREATE TABLE IF NOT EXISTS history_entries (
    id UUID,
    user_id UUID,
    track_id UUID,
    played_at DateTime,
    duration UInt32,
    completion_rate Float32,
    source String
) ENGINE = MergeTree()
ORDER BY (user_id, played_at)
PARTITION BY toYYYYMM(played_at)
SETTINGS index_granularity = 8192;
```

**Table Engine**: MergeTree
- **Ordering**: By user_id and played_at for efficient user queries
- **Partitioning**: By month for data management and query optimization
- **Index Granularity**: 8192 rows per index mark

### Query Optimization

**Indexes**:
- Primary key on (user_id, played_at)
- Automatic index on partition key

**Query Patterns**:
```sql
-- Get user history (optimized)
SELECT * FROM history_entries
WHERE user_id = ?
ORDER BY played_at DESC
LIMIT ?;

-- Get play count (fast aggregation)
SELECT count(*) FROM history_entries
WHERE user_id = ?;

-- Get most played tracks
SELECT track_id, count(*) as plays
FROM history_entries
WHERE user_id = ?
GROUP BY track_id
ORDER BY plays DESC
LIMIT 10;
```

### Data Retention

**Partitioning Strategy**:
- Monthly partitions for easy data management
- Old partitions can be dropped efficiently
- Configurable retention period (e.g., 2 years)

**Cleanup**:
```sql
-- Drop old partitions
ALTER TABLE history_entries DROP PARTITION '202201';
```

## Kafka Integration

### Events Consumed

#### HistoryEntryAddEvent
**Topic**: `history-entry-add`

**Payload**:
```json
{
  "userId": "uuid",
  "trackId": "uuid",
  "playedAt": "2024-01-04T19:30:00Z",
  "duration": 180,
  "completionRate": 95.5,
  "source": "web"
}
```

**Producer**: Music Service (when user plays a track)

**Consumer**: Statistics Service

**Processing**:
1. Consume event from Kafka
2. Validate event data
3. Insert into ClickHouse
4. Acknowledge message

### Consumer Configuration

**Properties**:
- **Group ID**: `statistics-service-group`
- **Auto Offset Reset**: `earliest`
- **Enable Auto Commit**: `false` (manual commit after successful insert)
- **Concurrency**: Configurable (default: 3)

**Error Handling**:
- Retry on transient failures
- Dead letter queue for permanent failures
- Logging for debugging

### Batch Processing

**Optimization**:
- Batch consume multiple events
- Batch insert to ClickHouse
- Reduces database round trips
- Improves throughput

**Configuration**:
```properties
spring.kafka.consumer.max-poll-records=500
clickhouse.batch-size=1000
clickhouse.batch-timeout=5000
```

## Exception Handling

### KafkaConsumeException
**Thrown when**: Error consuming Kafka messages
**Action**: Log error, retry, send to DLQ if persistent

### ClickHouseException
**Thrown when**: Database operation fails
**Action**: Retry with exponential backoff

### ValidationException
**Thrown when**: Invalid event data
**Action**: Log and skip, send to DLQ

## Configuration Properties

```yaml
# application.yml
server:
  port: 8086

spring:
  application:
    name: statistics-service

  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: statistics-service-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

clickhouse:
  url: ${CLICKHOUSE_URL:jdbc:clickhouse://localhost:8123/statistics_db}
  username: ${CLICKHOUSE_USER:default}
  password: ${CLICKHOUSE_PASSWORD:}
  batch-size: 1000
  batch-timeout: 5000

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
```

## Performance Considerations

### Write Performance
- **Batch Inserts**: Group multiple entries for single insert
- **Async Processing**: Non-blocking Kafka consumption
- **Connection Pooling**: Reuse ClickHouse connections
- **Partitioning**: Monthly partitions for write optimization

### Read Performance
- **Indexed Queries**: Use primary key (user_id, played_at)
- **Partition Pruning**: Query specific time ranges
- **Aggregation Pushdown**: ClickHouse handles aggregations efficiently
- **Result Caching**: Cache frequent queries (future)

### Scalability
- **Horizontal Scaling**: Multiple consumer instances
- **ClickHouse Sharding**: Distribute data across nodes
- **Kafka Partitioning**: Parallel event processing
- **Read Replicas**: Separate read and write workloads

## Testing

### Integration Tests
**Location**: [`src/test/kotlin/com/musicapp/statisticsservice/integration/`](../../statistics-service/src/test/kotlin/com/musicapp/statisticsservice/integration/)

**Test Coverage**:
- Kafka event consumption
- ClickHouse insertion
- History retrieval
- Batch processing
- Error handling

### Test Infrastructure
- Testcontainers for ClickHouse
- Embedded Kafka for event testing
- Mock data generation

## Dependencies

### Key Libraries
- Spring Boot Starter Web
- Spring Kafka
- ClickHouse JDBC Driver
- Kotlin Standard Library
- Kotlin Coroutines
- Spring Cloud Netflix Eureka Client

## Deployment

### Docker
**Dockerfile**: [`statistics-service/Dockerfile`](../../statistics-service/Dockerfile)

**Environment Variables**:
- `CLICKHOUSE_URL` - ClickHouse JDBC URL
- `CLICKHOUSE_USER` - ClickHouse username
- `CLICKHOUSE_PASSWORD` - ClickHouse password
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka brokers
- `EUREKA_URL` - Eureka server URL

### ClickHouse Setup
1. ClickHouse container running
2. Database `statistics_db` created
3. Schema initialized from migration
4. Network connectivity configured

### Kafka Setup
1. Kafka cluster available
2. Topic `history-entry-add` created
3. Consumer group configured
4. Proper permissions set

## Monitoring & Health

### Health Checks
- ClickHouse connectivity
- Kafka consumer status
- Consumer lag monitoring
- Database query performance

### Metrics
- Events consumed per second
- Insert throughput
- Query latency
- Consumer lag
- Error rate
- Batch size statistics

### Logging
- Event consumption
- Database operations
- Errors and retries
- Performance metrics

## Data Analytics

### Available Queries

**User Statistics**:
```sql
-- Total plays
SELECT count(*) FROM history_entries WHERE user_id = ?;

-- Unique tracks played
SELECT count(DISTINCT track_id) FROM history_entries WHERE user_id = ?;

-- Average completion rate
SELECT avg(completion_rate) FROM history_entries WHERE user_id = ?;

-- Listening time
SELECT sum(duration) FROM history_entries WHERE user_id = ?;
```

**Track Statistics**:
```sql
-- Most played tracks
SELECT track_id, count(*) as plays
FROM history_entries
GROUP BY track_id
ORDER BY plays DESC
LIMIT 10;

-- Trending tracks (last 7 days)
SELECT track_id, count(*) as plays
FROM history_entries
WHERE played_at >= now() - INTERVAL 7 DAY
GROUP BY track_id
ORDER BY plays DESC
LIMIT 10;
```

**Time-based Analytics**:
```sql
-- Plays by hour
SELECT toHour(played_at) as hour, count(*) as plays
FROM history_entries
WHERE user_id = ?
GROUP BY hour
ORDER BY hour;

-- Plays by day of week
SELECT toDayOfWeek(played_at) as day, count(*) as plays
FROM history_entries
WHERE user_id = ?
GROUP BY day
ORDER BY day;
```

## Integration with Other Services

### Music Service
**Flow**:
1. User plays track in Music Service
2. Music Service publishes HistoryEntryAddEvent
3. Statistics Service consumes event
4. Statistics Service stores in ClickHouse

### User Service
**Usage**: Statistics Service may query User Service for user details

### Gateway
**Routing**: `/api/history/**` → Statistics Service

## Future Enhancements

- [ ] Real-time listening statistics dashboard
- [ ] Trending tracks calculation
- [ ] User listening patterns analysis
- [ ] Recommendation engine integration
- [ ] Artist analytics (plays, listeners)
- [ ] Geographic listening data
- [ ] Device and platform analytics
- [ ] Listening streaks and achievements
- [ ] Social listening features (friends' activity)
- [ ] Export listening history
- [ ] Privacy controls (anonymous mode)
- [ ] Data aggregation for artists
- [ ] Revenue calculation for artists
- [ ] A/B testing analytics
- [ ] Machine learning features extraction
- [ ] Predictive analytics
- [ ] Anomaly detection (bot detection)
- [ ] Data warehouse integration
- [ ] Business intelligence dashboards
