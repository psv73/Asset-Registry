# Asset Registry — Build & Run (Java 21 + Gradle + Postgres)

## Prerequisites
- **Java 21 JDK** (e.g., Dragonwell/Temurin). Check:
  ```bash
  java -version
  ```
- **Gradle Wrapper** already in repo (`./gradlew` / `gradlew.bat`).
- **Docker** (to run PostgreSQL locally).

## 1) Start PostgreSQL in Docker
Minimal one-liner (PowerShell):
```powershell
docker run -d --name asset-postgres ^
  -e POSTGRES_USER=postgres ^
  -e POSTGRES_PASSWORD=postgres ^
  -e POSTGRES_DB=asset_registry ^
  -p 5432:5432 postgres:16
```
> On Bash replace `^` with `\` line breaks.

Grant DB permissions (only once), if needed:
```bash
psql -U postgres -h localhost -p 5432 -c "CREATE ROLE asset LOGIN PASSWORD 'asset';"
psql -U postgres -h localhost -p 5432 -c "ALTER DATABASE asset_registry OWNER TO asset;"
psql -U postgres -h localhost -p 5432 -d asset_registry -c "ALTER SCHEMA public OWNER TO asset;"
psql -U postgres -h localhost -p 5432 -d asset_registry -c "GRANT ALL ON SCHEMA public TO asset;"
psql -U postgres -h localhost -p 5432 -d asset_registry -c "GRANT ALL PRIVILEGES ON DATABASE asset_registry TO asset;"
```

### (Optional) docker-compose.yml
```yaml
services:
  db:
    image: postgres:16
    container_name: asset-postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: asset_registry
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
volumes:
  pgdata: {}
```

## 2) Configure application (already set)
`src/main/resources/application.yml` should contain:
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

## 3) Run the app
Windows PowerShell:
```powershell
.\gradlew clean bootRun
```

Linux/macOS:
```bash
./gradlew clean bootRun
```

Flyway will auto-apply migrations on first start.

## 4) Smoke tests
Health:
```bash
curl http://localhost:8080/api/v1/health
```

List assets:
```bash
curl http://localhost:8080/api/v1/assets
```

Create asset:
```bash
curl -X POST http://localhost:8080/api/v1/assets   -H "Content-Type: application/json"   -d '{ 
        "clientId": 1, "modelId": 1, "statusId": 1, "osId": 1,
        "inventoryCode": "INV-001", "serialNumber": "SN-001",
        "hostname": "pc-001", "ip": "192.168.0.10",
        "dhcp": true, "purchaseDate": "2025-10-10",
        "params": {"ram":"16GB","cpu":"i7"}, "note": "first"
      }'
```

Get by ID:
```bash
curl http://localhost:8080/api/v1/assets/1
```

Filter by inventoryCode:
```bash
curl "http://localhost:8080/api/v1/assets?inventoryCode=INV-001"
```

Update:
```bash
curl -X PUT http://localhost:8080/api/v1/assets/1   -H "Content-Type: application/json"   -d '{ 
        "clientId": 1, "modelId": 1, "statusId": 1, "osId": 1,
        "inventoryCode": "INV-001", "serialNumber": "SN-001",
        "hostname": "pc-001-upd", "ip": "192.168.0.11",
        "dhcp": false, "purchaseDate": "2025-10-10",
        "params": {"ram":"32GB"}, "note": "updated"
      }'
```

Delete:
```bash
curl -X DELETE http://localhost:8080/api/v1/assets/1 -i
```

## 5) Git: commit & push
New branch:
```bash
git checkout -b feature/asset-crud
git add src/main/java/net/psv73/assetregistry README.md
git commit -m "Asset CRUD: endpoints + handlers + docs"
git push -u origin feature/asset-crud
```

Main branch:
```bash
git add src/main/java/net/psv73/assetregistry README.md
git commit -m "Asset CRUD: endpoints + handlers + docs"
git push
```
