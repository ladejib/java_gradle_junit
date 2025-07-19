# User Registration API

A Spring Boot REST API for user registration with comprehensive validation, testing, and documentation.

## Features

- User registration with validation
- Duplicate username/email checking
- RESTful API endpoints
- Comprehensive error handling
- Unit and integration testing
- H2 in-memory database
- Logging and monitoring

## Tech Stack

- Java 17
- Spring Boot 3.1.0
- Spring Data JPA
- H2 Database
- JUnit 5
- Spock Framework (Groovy)
- Gradle

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/check-username/{username}` | Check username availability |
| GET | `/api/users/check-email?email={email}` | Check email availability |

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 8.x

### Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run the application:

```bash
./gradlew bootRun
