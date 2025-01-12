
# Spring Boot Project with Kafka Integration

This project contains two Gradle modules: `rest` and `calculator`. It uses Kafka for messaging between the two modules, and Docker Compose is used to initiate Kafka and Zookeeper. This guide will walk you through how to build, run the application, and run unit tests.

## Project Structure

The project is structured as follows:

```
spring-boot-kafka-project/
├── build.gradle
├── settings.gradle
├── rest/
│   ├── build.gradle
│   ├── src/main/java/com/example/rest/OperatorController.java
│   ├── src/main/java/com/example/rest/OperatorService.java
│   └── src/test/java/com/example/rest/OperatorControllerTest.java
└── calculator/
    ├── build.gradle
    ├── src/main/java/com/example/calculator/CalculatorListener.java
    ├── src/main/java/com/example/calculator/CalculatorService.java
    └── src/test/java/com/example/calculator/CalculatorListenerTest.java
```

### Modules:
- **rest**: Exposes a REST API to handle mathematical operations.
- **calculator**: Listens to Kafka messages and performs the operations.

## Prerequisites

Before running the project, ensure that you have the following installed:
- [Java 17 or later](https://adoptopenjdk.net/)
- [Gradle](https://gradle.org/install/)
- [Docker](https://www.docker.com/get-started) (for running Kafka and Zookeeper)

## Step 1: Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/your-repository/spring-boot-kafka-project.git
cd spring-boot-kafka-project
```

## Step 2: Build the Project

To build the entire project, run the following command in the root of the project:

```bash
./gradlew build
```

This will build both the `rest` and `calculator` modules.

## Step 3: Docker Compose - Kafka and Zookeeper

We are using Docker Compose to set up Kafka and Zookeeper. To start them, run the following command from the root directory of the project:

```bash
docker-compose up -d
```

This will:
- Start Zookeeper on port `2181`
- Start Kafka on port `9092`

To verify the services are running, you can check the status of the containers:

```bash
docker ps
```

## Step 4: Run the Spring Boot Application

After Kafka and Zookeeper are up, you can run the application. From the root of the project, execute:

```bash
./gradlew bootRun
```

This will start the Spring Boot application with both modules (`rest` and `calculator`). The `rest` module will expose the API at `http://localhost:8080/api/{operation}`, where `{operation}` can be one of the operations (`sum`, `subtraction`, `multiplication`, `division`).

## Step 5: Test the API

You can test the `rest` module's API using `curl`, Postman, or any HTTP client.

### Example Request:
To perform a sum operation with parameters `a=5` and `b=3`:

```bash
curl -X GET "http://localhost:8080/api/sum?a=5&b=3"
```

### Expected Response:
```json
{
  "result": "8",
  "X-Request-ID": "generated-request-id"
}
```

## Step 6: Run Unit Tests

### Running Unit Tests with Gradle

You can run the unit tests for the `rest` and `calculator` modules using Gradle. From the root directory of the project, execute:

```bash
./gradlew test
```

This will run all unit tests for both modules (`rest` and `calculator`). The test results will be displayed in the terminal.

### Running Unit Tests for Specific Modules

If you want to run the tests for a specific module (e.g., `rest`), navigate to the module directory and run:

```bash
cd rest
../gradlew test
```

For the `calculator` module:

```bash
cd calculator
../gradlew test
```

## Step 7: Stopping the Services

Once you're done with testing and development, you can stop the Docker containers by running:

```bash
docker-compose down
```

This will stop and remove the Kafka and Zookeeper containers.

## Troubleshooting

- **Kafka/Zookeeper issues**: If the containers are not starting properly, check the logs using `docker logs <container-id>` for more details.
- **Missing dependencies**: Ensure that you have all the necessary dependencies defined in the `build.gradle` files, and try running `./gradlew clean build` to clear any stale files.

## Conclusion

You have now set up a Spring Boot project with two modules (`rest` and `calculator`), integrated Kafka for communication, and used Docker Compose to start the necessary Kafka and Zookeeper services. You can build and run the application, perform API operations, and run unit tests with Gradle.

Feel free to extend the functionality and modify the application as needed!

## References
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
