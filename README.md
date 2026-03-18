# Country Info Service

NCBA Channel Developer Case Study — A Spring Boot REST API that integrates with a SOAP web service to fetch, store, and manage country information.

---

## Tech Stack

| Layer       | Technology             |
|-------------|------------------------|
| Framework   | Spring Boot 3.2 (Java 17) |
| Database    | MySQL 8.0              |
| ORM         | Spring Data JPA / Hibernate |
| SOAP Client | Spring Web Services    |
| Build Tool  | Maven                  |
| Container   | Docker + Docker Compose |

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

- Java 17+
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
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/country_db
spring.datasource.username=appuser
spring.datasource.password=apppassword
```

Or pass as environment variables:
```bash
export DB_USERNAME=appuser
export DB_PASSWORD=apppassword
```

### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

---

## API Reference

Base URL: `http://localhost:8080/api/countries`

### POST `/api/countries`
Fetch country info from SOAP and persist it.

**Request:**
```json
{ "name": "Tanzania" }
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Country fetched and saved successfully",
  "data": {
    "id": 1,
    "countryName": "Tanzania",
    "isoCode": "TZ",
    "capitalCity": "Dodoma",
    "phoneCode": "255",
    "continentCode": "AF",
    "currencyIsoCode": "TZS",
    "currencyName": "Tanzanian Shilling",
    "countryFlag": "...",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

### GET `/api/countries`
Fetch all saved countries.

```bash
curl http://localhost:8080/api/countries
```

---

### GET `/api/countries/{id}`
Fetch a specific country by ID.

```bash
curl http://localhost:8080/api/countries/1
```

---

### PUT `/api/countries/{id}`
Update a country record.

```bash
curl -X PUT http://localhost:8080/api/countries/1 \
  -H "Content-Type: application/json" \
  -d '{ "capitalCity": "Dodoma", "currencyName": "TZS" }'
```

---

### DELETE `/api/countries/{id}`
Delete a country record.

```bash
curl -X DELETE http://localhost:8080/api/countries/1
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
│   │   ├── service/           # Business logic + SOAP client
│   │   ├── repository/        # Spring Data JPA
│   │   ├── model/             # JPA entity
│   │   ├── dto/               # Request/Response DTOs
│   │   ├── exception/         # Custom exceptions + global handler
│   │   └── config/            # SOAP/marshaller config
│   └── resources/
│       └── application.properties
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## SOAP Endpoints Used

WSDL: `http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL`

| Operation          | Input          | Output                  |
|--------------------|----------------|-------------------------|
| `CountryISOCode`   | `sCountryName` | `CountryISOCodeResult`  |
| `FullCountryInfo`  | `sCountryISOCode` | Full country object  |
