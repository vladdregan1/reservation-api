# 🍽️ Restaurant Reservation API

A robust, enterprise-grade RESTful API built to handle restaurant reservations. Designed with scalability and clean code principles in mind, this project demonstrates production-ready backend architecture, containerized orchestration, and comprehensive test coverage.

## 🛠️ Tech Stack & Technologies
* **Core:** Java 21, Spring Boot 4.0.3
* **Database:** MySQL 8.0, Spring Data JPA, Hibernate
* **Documentation:** OpenAPI (Swagger 3)
* **Testing:** JUnit 5, Mockito
* **Infrastructure:** Docker, Docker Compose

## 🏗️ Enterprise Architecture & Best Practices
* **N-Tier Architecture:** Strict separation of concerns (Controller, Service, Repository).
* **Data Transfer Objects (DTO):** Decoupling database entities from API payloads to prevent internal structure leakage.
* **Global Exception Handling:** Centralized `@RestControllerAdvice` for standardized and secure API error responses.
* **Pagination & Sorting:** Optimized data retrieval for large datasets using Spring `Pageable`.
* **Isolated Unit Testing:** Business logic validation using Mockito to mock database connections and ensure independent execution.
* **Containerization:** Fully containerized setup allowing seamless deployment without local dependencies.

## 🚀 Getting Started

### Prerequisites
* [Docker Desktop](https://www.docker.com/) installed and running.
* Ports `8080` (API) and `3307` (MySQL host port) available on your machine.

### Run with Docker (Recommended)
1. Copy the example config and fill in your own passwords:
   ```bash
   cp .env.example .env
   ```
2. Build the jar and start everything (MySQL + API):
   ```bash
   ./mvnw clean package -DskipTests
   docker compose up -d --build
   ```

*Note: The application uses a Docker healthcheck to automatically hold the API startup sequence until the MySQL database is fully initialized and ready to accept connections.*

### Run from the IDE
Start only the database with `docker compose up -d mysql-db`, then run `ReservationApplication`. The app reads the same `.env` file automatically.

## 📖 API Documentation
Once the application is running, the interactive Swagger UI documentation is available at:
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Click **Authorize** and log in with `ADMIN_USERNAME` / `ADMIN_PASSWORD` from your `.env` to try the admin endpoints.

## 🔌 Core Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/reservations` | Public | Create a new reservation with automatic validation. |
| `GET`  | `/api/reservations` | Admin | Retrieve a paginated and sorted list of reservations (supports `pageNo`, `pageSize` ≤ 100, `sortBy`, `sortDir`). |
| `PUT`  | `/api/reservations/{id}` | Admin | Update a reservation. |
| `DELETE` | `/api/reservations/{id}` | Admin | Delete a reservation. |

## 🔐 Security

- Anyone can create a reservation (`POST /api/reservations`).
- Listing, editing and deleting reservations requires the **ADMIN** role (HTTP Basic auth, BCrypt-hashed password).
- Secrets live in `.env` (not committed). The app connects to MySQL with its own limited user, not root.
- CORS allows only the configured frontend origin(s) (`CORS_ALLOWED_ORIGINS`).
- Request parameters are validated (`pageSize` ≤ 100, whitelisted `sortBy` fields), so bad input returns 400 instead of 500.
- The frontend renders user data with `textContent`, which prevents stored XSS.

### Possible improvements
- JWT tokens and customer accounts
- Rate limiting on public endpoints
- HTTPS in production