# Country Info Service

NCBA Channel Developer Case Study — A Spring Boot REST API that integrates with a SOAP web service to fetch, store, and manage country information.

---

## Tech Stack

| Layer       | Technology                  |
|-------------|-----------------------------|
| Framework   | Spring Boot 3.3.5 (Java 21) |
| Database    | MySQL 8.0                   |
| ORM         | Spring Data JPA / Hibernate |
| SOAP Client | Spring Web Services         |
| Build Tool  | Maven                       |
| Container   | Docker + Docker Compose     |

---

## Architecture

```
POST /api/countries { "name": "kenya" }
        │
        ▼
  Sentence Case → "Kenya"
        │
        ▼
  SOAP: CountryISOCode("Kenya") → "KE"
        │
        ▼
  SOAP: FullCountryInfo("KE") → { capital, currency, ... }
        │
        ▼
  MySQL: Save CountryInfo
        │
        ▼
  REST Response with full country data
```

---

## Prerequisites

- Java 21
- Maven 3.8+
- MySQL 8.0 (local) **OR** Docker + Docker Compose

---

## Option A: Run with Docker (Recommended)

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd country-service
```

### 2. Create environment file
```bash
cp .env.example .env
# Edit .env if you want custom passwords
```

### 3. Build and start
```bash
docker-compose up --build
```

The app will start on **http://localhost:8080** once MySQL is healthy.

### 4. Stop
```bash
docker-compose down
# To also remove the database volume:
docker-compose down -v
```

---

## Option B: Run Locally

### 1. Create MySQL database
```sql
CREATE DATABASE country_db;
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON country_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configure application

Create the properties file manually since it is not included in the repository:

```bash
touch src/main/resources/application.properties
```

Then open the file and paste in the following content, replacing the database username and password with your own:

```properties
# Application
spring.application.name=country-service
server.port=8080

# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/country_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# SOAP
# WSDL reference URL (not used in code - for documentation only)
# soap.wsdl.url=http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL
soap.endpoint.url=http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso
soap.namespace=http://www.oorsprong.org/websamples.countryinfo

# Logging
logging.level.com.ncba.countryservice=DEBUG
logging.level.org.springframework.ws=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=logs/country-service.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

> **Note:** `application.properties` is excluded from version control via `.gitignore`. You must create it manually every time you clone the repository.

### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

---

## Postman Collection

Import and test all endpoints using the Postman collection:

Import the `CountryService.postman_collection.json` file found in the root of this repository into Postman:

1. Open Postman
2. Click **Import**
3. Select `CountryService.postman_collection.json`
4. The collection will appear with all endpoints ready to use

---

## API Reference

Base URL: `http://localhost:8080/api/countries`

---

### POST `/api/countries`
Fetches country info from the SOAP API and persists it. Converts the name to sentence case automatically.

**Request:**
```json
{
  "name": "kenya"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Country fetched and saved successfully",
  "data": {
    "id": 1,
    "countryName": "Kenya",
    "isoCode": "KE",
    "capitalCity": "Nairobi",
    "phoneCode": "254",
    "continentCode": "AF",
    "currencyIsoCode": "KES",
    "currencyName": "Kenyan Shilling",
    "countryFlag": "http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Kenya.jpg",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "name": "Country name must not be blank"
  }
}
```

**Response (409 Conflict):**
```json
{
  "success": false,
  "message": "Country with ISO code 'KE' already exists."
}
```

---

### GET `/api/countries`
Fetch all saved countries.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Retrieved 2 countries",
  "data": [
    {
      "id": 1,
      "countryName": "Kenya",
      "isoCode": "KE",
      "capitalCity": "Nairobi",
      "phoneCode": "254",
      "continentCode": "AF",
      "currencyIsoCode": "KES",
      "currencyName": "Kenyan Shilling",
      "countryFlag": "http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Kenya.jpg",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "countryName": "Tanzania",
      "isoCode": "TZ",
      "capitalCity": "Dodoma",
      "phoneCode": "255",
      "continentCode": "AF",
      "currencyIsoCode": "TZS",
      "currencyName": "Tanzanian Shilling",
      "countryFlag": "http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Tanzania.jpg",
      "createdAt": "2024-01-01T10:01:00",
      "updatedAt": "2024-01-01T10:01:00"
    }
  ]
}
```

---

### GET `/api/countries/{id}`
Fetch a specific country by ID.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Country found",
  "data": {
    "id": 1,
    "countryName": "Kenya",
    "isoCode": "KE",
    "capitalCity": "Nairobi",
    "phoneCode": "254",
    "continentCode": "AF",
    "currencyIsoCode": "KES",
    "currencyName": "Kenyan Shilling",
    "countryFlag": "http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Kenya.jpg",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Country not found with id: 999"
}
```

---

### PUT `/api/countries/{id}`
Update a country record. Only provided fields are updated — null fields are ignored.

**Request:**
```json
{
  "capitalCity": "Mombasa",
  "currencyName": "KES Updated"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Country updated successfully",
  "data": {
    "id": 1,
    "countryName": "Kenya",
    "isoCode": "KE",
    "capitalCity": "Mombasa",
    "phoneCode": "254",
    "continentCode": "AF",
    "currencyIsoCode": "KES",
    "currencyName": "KES Updated",
    "countryFlag": "http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Kenya.jpg",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:05:00"
  }
}
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Country not found with id: 999"
}
```

---

### DELETE `/api/countries/{id}`
Delete a country record.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Country deleted successfully",
  "data": null
}
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Country not found with id: 999"
}
```

---

## Testing with cURL

### Save Kenya
```bash
curl -X POST http://localhost:8080/api/countries \
  -H "Content-Type: application/json" \
  -d '{"name": "kenya"}'
```

### Save Tanzania
```bash
curl -X POST http://localhost:8080/api/countries \
  -H "Content-Type: application/json" \
  -d '{"name": "Tanzania"}'
```

### Get all
```bash
curl http://localhost:8080/api/countries
```

### Get by ID
```bash
curl http://localhost:8080/api/countries/1
```

### Update
```bash
curl -X PUT http://localhost:8080/api/countries/1 \
  -H "Content-Type: application/json" \
  -d '{"capitalCity": "Nairobi"}'
```

### Delete
```bash
curl -X DELETE http://localhost:8080/api/countries/1
```

---

## Running Tests

```bash
mvn test
```

---

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

---

## Logs

Application logs are written to:
- Console (stdout)
- `logs/country-service.log`

When running with Docker Compose, logs are mounted to `./logs/` on your host machine.

---

## Project Structure

```
country-service/
├── src/
│   ├── main/java/com/ncba/countryservice/
│   │   ├── controller/        # REST endpoints
│   │   ├── service/           # Business logic + SOAP client interface + impl
│   │   ├── repository/        # Spring Data JPA
│   │   ├── model/             # JPA entity
│   │   ├── dto/               # Request/Response DTOs
│   │   ├── exception/         # Custom exceptions + global handler
│   │   └── config/            # SOAP/marshaller config
│   └── resources/
│       ├── application.properties.template  ← committed to git
│       └── application.properties           ← NOT committed (create manually)
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## SOAP Endpoints Used

WSDL: `http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL`

| Operation          | Input             | Output                 |
|--------------------|-------------------|------------------------|
| `CountryISOCode`   | `sCountryName`    | `CountryISOCodeResult` |
| `FullCountryInfo`  | `sCountryISOCode` | Full country object    |

---

## ⚠️ Important — Future Improvements

> **Note:** The improvements described in this section are critical for production readiness. They were intentionally left out of scope for this implementation but should be addressed before any production deployment.

### Caching with Redis

The current implementation makes 2 live SOAP calls on every new country request — one to fetch the ISO code and one to fetch the full country info. Additionally, when a country already exists in the database, the original implementation queried the database twice for the same record. Both of these issues have been addressed:

- **Double DB call** — fixed by storing the result of `findByCountryNameIgnoreCase` in an `Optional` and reusing it, eliminating the redundant second query.
- **Repeated SOAP calls** — for subsequent requests for a country not yet in the database, the SOAP service is still called every time. In a production environment this introduces latency and a hard dependency on the external SOAP service availability.

The recommended next improvement is to introduce Redis caching using Spring Cache so that the full country response is cached after the first request and returned directly on subsequent requests without touching the database or the SOAP service.

**How it would work:**

```
POST /api/countries { "name": "Kenya" }
        │
        ▼
  Check Redis → cached? → return immediately (no DB, no SOAP)
        │
        ▼ (cache miss only)
  Check DB → already saved? → store in Redis → return response
        │
        ▼ (not in DB)
  SOAP: CountryISOCode("Kenya") → "KE"
        │
        ▼
  SOAP: FullCountryInfo("KE") → { capital, currency, ... }
        │
        ▼
  Save to DB → store in Redis (TTL 24h) → return response
```

**Key annotation that would be added to `CountryServiceImpl`:**

```java
@Cacheable(value = "countries", key = "#rawName.toLowerCase()")
public CountryResponse fetchAndSaveCountry(String rawName) { ... }
```

This means on the first request Spring executes the full method — checks DB, calls SOAP if needed, saves to DB — then stores the result in Redis. On every subsequent request for the same country name, Spring returns the cached response immediately without entering the method at all.

**Why Redis over simple in-memory cache:**

| Option | Issue |
|---|---|
| In-memory cache | Lost on restart, not shared across multiple app instances |
| Redis | Persists across restarts, shared across all instances if scaled horizontally |