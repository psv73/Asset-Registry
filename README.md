# Asset Registry

Minimal skeleton for an IT asset management service.  
Database schema is managed **only** by Flyway migrations.

## Stack
- Java 21+, Spring Boot
- PostgreSQL + Flyway (`src/main/resources/db/migration`)
- Gradle Wrapper
- Base package: `net.psv73.assetregistry`

## Status
- ✅ Preserved: migrations, configuration, entry point  
- 🔜 To be implemented: domain, repositories, web layer

## Structure
```
src/
 ├─ main/
 │  ├─ java/net/psv73/assetregistry/
 │  │   ├─ AssetRegistryApplication.java
 │  │   ├─ config/        (.gitkeep)
 │  │   ├─ entity/        (.gitkeep)
 │  │   ├─ repository/    (.gitkeep)
 │  │   └─ web/           (.gitkeep + dto/request/response)
 │  └─ resources/
 │      ├─ application.yml
 │      ├─ application.properties
 │      ├─ application-local.yml
 │      └─ db/migration/
 │          ├─ V1__init.sql
 │          ├─ V2__seed_reference_data.sql
 │          └─ V3__demo_data.sql
 └─ test/java/net/psv73/assetregistry/.gitkeep
```

> **Note about configuration:** keep **one** main source of truth. Recommended:
> - `application.yml` — default
> - `application-local.yml` — local profile
> - remove `application.properties` later if not needed

## Requirements
- JDK 21+
- PostgreSQL 14+ (or Docker)

### Quick start PostgreSQL (Docker)
```bash
docker run --name asset-registry-pg -p 5432:5432   -e POSTGRES_USER=asset -e POSTGRES_PASSWORD=asset   -e POSTGRES_DB=asset_registry -d postgres:15
```

## Configuration (example)

`src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/asset_registry
    username: asset
    password: asset
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate.format_sql: true
      hibernate.jdbc.time_zone: UTC
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

server:
  port: 8080

logging:
  level:
    root: INFO
```

`src/main/resources/application-local.yml` (optional for local dev)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/asset_registry
    username: asset
    password: asset

logging:
  level:
    org.hibernate.SQL: INFO
```

## Build & Run

### Linux/macOS
```bash
./gradlew clean build
./gradlew bootRun
```

### Windows
```bat
gradlew.bat clean build
gradlew.bat bootRun
```

Run with local profile:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Database Migrations (Flyway)
- Scripts: `V{number}__{description}.sql`
- Automatically applied on startup
- `ddl-auto: validate` — schema is changed only via migrations

## Verify
Currently no public controllers. The app should start without errors and apply `V1..V3`.  
Check by startup logs or directly in DB (`\dt` / list tables).

## Next Steps
1. Decide on configuration format (YAML preferred), remove `application.properties` if redundant.
2. Add a simple health endpoint (via Spring Actuator or `HealthController`).
3. Start with a reference entity (e.g., `DeviceType`): entity → repository → GET controller.
4. Add tests (integration with Testcontainers, unit with H2/Mockito).
5. Set up CI (Gradle build + tests) and push to GitHub.

## Documentation

- [Build & Run (Java 21, Gradle, Postgres, Docker)](docs/ASSET-REGISTRY-SETUP.md)
- [Assets API (v1): endpoints, examples, errors](docs/ASSET-REGISTRY-API.md)
### Docker Compose (PostgreSQL)

The repository contains a ready-to-use Docker Compose setup for a local PostgreSQL instance.

- Files: `docs/docker-compose.yml`, `docs/db-init/01-init-asset.sql`
- Initialization: creates the `asset` role, grants ownership and privileges on the `asset_registry` database.

Start the DB:
```bash
docker compose -f docs/docker-compose.yml up -d
```

Check status:
```bash
docker compose -f docs/docker-compose.yml ps
```

Stop:
```bash
docker compose -f docs/docker-compose.yml down
```

Troubleshooting:
- If port **5432** is occupied, either stop the process using it or change the mapped port in `docker-compose.yml` (e.g., `15432:5432`), then update your `spring.datasource.url` accordingly.
- To reset the database completely (fresh start), run:
  ```bash
  docker compose -f docs/docker-compose.yml down -v
  docker compose -f docs/docker-compose.yml up -d
  ```
