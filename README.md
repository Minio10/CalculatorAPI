# Calculator RESTful API

## Table of Contents
- [Description](#description)
- [Requirements](#requirements)
- [Build Instructions](#build-instructions)
- [Run Instructions](#run-instructions)
    - [Prerequisites](#prerequisites)
    - [Steps](#steps)
- [API Examples](#api-examples)
- [Error Handling](#error-handling)
- [Kafka Integration](#kafka-integration)
- [Docker Configuration](#docker-configuration)
- [Logs](#logs)
- [Unit Tests](#unit-tests)

---

## Description
This project provides a RESTful API that performs basic calculator functionalities including addition, subtraction, multiplication, and division. The API supports two operands (`a` and `b`) and allows arbitrary precision signed decimal numbers.

The project is implemented in Java using Spring Boot as the framework and Gradle for managing dependencies and building both modules.

---

## Requirements

This project implements the following summarized requirements:

- **Operations**: Provides endpoints for addition, subtraction, multiplication, and division.
- **Operands**: Supports two signed decimal operands with arbitrary precision.
- **Framework**: Built using Java, Spring Boot, and Gradle.
- **Modules**: Organized into two modules:
    - `rest`: Handles API requests and responses.
    - `calculator`: Implements the core calculation logic.
- **Communication**: Utilizes Apache Kafka for inter-module communication.
- **Configuration**: Managed through `application.properties` with no XML except for potential logging.
- **Containerization**: Includes Dockerfiles and a Docker Compose setup.
- **Testing**: Includes unit tests to ensure correctness and reliability.
- **Logging**: Supports SLF4J with Logback, including unique request identifiers and MDC propagation.

---

## Build Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Minio10/CalculatorAPI
   ```

2. **Build the Project**:
    - Using Gradle:
      ```bash
      ./gradlew build
      ```
3. **Run Unit Tests**:
    - Using Gradle:
      ```bash
      ./gradlew test
      ```
---

## Run Instructions

### Prerequisites
- **Docker** and **Docker Compose** must be installed on your system.

### Steps
1. **Build Docker Images**:
   ```bash
   docker-compose build
   ```

2. **Run the Application**:
   ```bash
   docker-compose up
   ```

3. **Access the REST API**:
    - Base URL: `http://localhost:8080`
    - Available Endpoints:
        - `GET /api/sum?a=<operand_a>&b=<operand_b>` - Perform addition.
        - `GET /api/subtraction?a=<operand_a>&b=<operand_b>` - Perform subtraction.
        - `GET /api/multiplication?a=<operand_a>&b=<operand_b>` - Perform multiplication.
        - `GET /api/division?a=<operand_a>&b=<operand_b>` - Perform division.

4. **Kafka Setup**: Kafka is pre-configured via `docker-compose.yml`. Ensure Kafka starts correctly with the application.

---

## API Examples
### Sum
```bash
GET /api/sum?a=5.2&b=3.8
Response:
{
  "result": 9.0
}
```

### Subtraction
```bash
GET /api/subtraction?a=10.5&b=4.5
Response:
{
  "result": 6.0
}
```

### Multiplication
```bash
GET /api/multiplication?a=7&b=3
Response:
{
  "result": 21
}
```

### Division
```bash
GET /api/division?a=20&b=4
Response:
{
  "result": 5.0
}
```

---

## Error Handling
- **Invalid Input**:
  ```bash
  GET /api/division?a=5&b=0
  Response:
  {
    "error": "Division by zero is not allowed."
  }
  ```
    - Status Code: `400 Bad Request`

- **Missing Parameters**:
  ```bash
  GET /api/sum?a=10
  Response:
  {
    "error": "Missing parameters"
  }
  ```
    - Status Code: `400 Bad Request`

---

## Kafka Integration
- **Purpose**: Kafka handles communication between the `rest` and `calculator` modules.
- **Topics**:
    - `calculator-topic`: Sends calculation requests.
    - `result-topic`: Receives calculation results.
- **Message Format**:
    - Request Example:
      ```json
      {
        "request_id": "123e4567-e89b-12d3-a456-426614174000",
        "operation": "sum",
        "a": "10.5",
        "b": "4.5"
      }
      ```
    - Response Example:
      ```json
      {
        "result": "15.0",
        "request_id": "123e4567-e89b-12d3-a456-426614174000"
      }
      ```
- **Error Handling**:
    - If a module fails to process a message, the error is logged, and retries are attempted based on Kafka's configuration.

---

## Docker Configuration
- **Dockerfile**: Configures the individual modules (`rest` and `calculator`).
- **Docker Compose**: Manages the multi-container setup, including Kafka.

---

## Logs
- Log files are stored in `logs/` directory of each module.
- Logs include input/output events and errors with unique request identifiers for easy tracing.

---

## Unit Tests
- Unit tests ensure:
    - Correct calculation logic.
    - Proper inter-module communication via Kafka.
    - Correct API responses to the client.

Run tests for each module with:
```bash
./gradlew :rest:test
```
```bash
./gradlew :calculator:test
```
Test reports are available inside each module on:
```
rest/build/reports/tests/test/index.html
calculator/build/reports/tests/test/index.html
```

