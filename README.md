# 🍽️ Restaurant Reservation API

![CI](https://github.com/vladdregan1/reservation-api/actions/workflows/ci.yml/badge.svg)

A REST API for restaurant reservations, built with Spring Boot. Customers can book a table without an account; an admin can list, edit, confirm, and cancel reservations. Secured with Spring Security, runs with Docker Compose.

## 🛠️ Tech Stack & Technologies
* **Core:** Java 21, Spring Boot 4.0.3
* **Database:** MySQL 8.0, Spring Data JPA, Hibernate
* **Documentation:** OpenAPI (Swagger 3)
* **Testing:** JUnit 5, Mockito
* **Infrastructure:** Docker, Docker Compose

## 🏗️ Architecture & Practices
* **Layered Architecture:** Controller, Service, and Repository layers, each with its own job.
* **Data Transfer Objects (DTO):** The API sends and receives DTOs instead of the database entities.
* **Global Exception Handling:** One `@RestControllerAdvice` turns exceptions into clear JSON errors (400, 404, 409).
* **Pagination & Sorting:** The reservation list is paginated and sortable with Spring `Pageable`.
* **Testing:** Unit tests for the service (Mockito) and security tests for the controller (MockMvc).
* **Status Workflow:** Reservations move from `PENDING` to `CONFIRMED` or `CANCELLED`, with the rules enforced in the service.
* **Docker:** The API and MySQL run together with Docker Compose.
* **CI:** GitHub Actions runs all tests on every push and pull request.

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

## 🖥️ Frontend
A small HTML/JavaScript dashboard is served by Spring Boot itself from `src/main/resources/static/`. Once the app is running, open:
**[http://localhost:8080](http://localhost:8080)**

Anyone can book a table there. Log in with `ADMIN_USERNAME` / `ADMIN_PASSWORD` to see, edit, confirm, cancel, and delete reservations.

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
| `PATCH` | `/api/reservations/{id}/confirm` | Admin | Confirm a `PENDING` reservation. |
| `PATCH` | `/api/reservations/{id}/cancel` | Admin | Cancel a `PENDING` or `CONFIRMED` reservation. |
| `DELETE` | `/api/reservations/{id}` | Admin | Delete a reservation. |

## 🔄 Reservation status

```
            confirm
 PENDING ───────────► CONFIRMED
    │                     │
    │ cancel              │ cancel
    ▼                     ▼
         CANCELLED  (final, no way back)
```

- A new reservation is always `PENDING`.
- Only a `PENDING` reservation can be confirmed.
- A `CANCELLED` reservation can't be cancelled again.
- Breaking a rule returns **409 Conflict** with a message explaining why.

## 🔐 Security

- Anyone can create a reservation (`POST /api/reservations`).
- Listing, editing, confirming, cancelling and deleting reservations requires the **ADMIN** role (HTTP Basic auth, BCrypt-hashed password).
- Secrets live in `.env` (not committed). The app connects to MySQL with its own limited user, not root.
- CORS allows only the configured frontend origin(s) (`CORS_ALLOWED_ORIGINS`).
- Request parameters are validated (`pageSize` ≤ 100, whitelisted `sortBy` fields), so bad input returns 400 instead of 500.
- The frontend renders user data with `textContent`, which prevents stored XSS.

### Possible improvements
- JWT tokens and customer accounts
- Rate limiting on public endpoints
- HTTPS in production
- Table capacity (no double-booking of the same table)
- Opening hours
- Email notifications when a reservation is confirmed or cancelled
