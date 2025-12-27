# Username Checker

This is a Spring Boot application that demonstrates how to build a scalable and resilient username checking service.

## Tech Stack

*   **Java 21**
*   **Spring Boot 3**
*   **Maven**
*   **Cassandra**: NoSQL database for storing user data.
*   **Redis**: In-memory data store for caching and quick lookups.
*   **Docker & Docker Compose**: For containerizing and running the application and its dependencies.
*   **Guava**: For the Bloom Filter implementation.

## Setup and Run

1.  **Prerequisites**:
    *   Docker and Docker Compose must be installed on your machine.
    *   Java 21 or higher.
    *   Maven.

2.  **Build the application**:
    Navigate to the root of the project and run the following command to build the application:
    ```bash
    mvn clean install
    ```

3.  **Run the application**:
    Navigate to the `deploy` directory and run the following command to start the application and its dependencies:
    ```bash
    docker-compose up -d
    ```

The application will be available at `http://localhost:8081`.