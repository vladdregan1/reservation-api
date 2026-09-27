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
The entire ecosystem (MySQL Database + Spring Boot API) can be launched simultaneously with a single command:

*Note: The application uses a Docker healthcheck to automatically hold the API startup sequence until the MySQL database is fully initialized and ready to accept connections.*

## 📖 API Documentation
Once the application is running, the interactive Swagger UI documentation is available at:
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

## 🔌 Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/reservations` | Retrieve a paginated and sorted list of reservations (supports `pageNo`, `pageSize`, `sortBy`, `sortDir`). |
| `POST` | `/api/reservations` | Create a new reservation with automatic validation. |