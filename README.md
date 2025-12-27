# Username Checker

This is a Spring Boot application that demonstrates how to build a scalable and resilient username checking service.

## Features

*   **Scalable Architecture**: Uses Bloom Filters (Redis or In-Memory) and Redis caching for ultra-fast username existence checks.
*   **Async Persistence**: Implements an asynchronous write-behind pattern with retry logic (exponential backoff) to buffer and save users to Cassandra, ensuring high write throughput.
*   **Smart Suggestions**: Utilizes a Trie data structure to provide intelligent username suggestions when a conflict occurs.
*   **Configurable Strategies**: flexible configuration to switch between Redis-backed (distributed) and In-Memory (local) Bloom Filter implementations.

## Tech Stack

*   **Java 21**
*   **Spring Boot 3**
*   **Maven**
*   **Cassandra**: NoSQL database for storing user data.
*   **Redis**: In-memory data store for caching and quick lookups.
*   **Docker & Docker Compose**: For containerizing and running the application and its dependencies.
*   **Guava**: For the In-Memory Bloom Filter implementation.

## Configuration

The application supports different strategies for the Bloom Filter. You can configure this in `application.yaml`:

```yaml
bloom:
  strategy: redis # Options: redis, in-memory
  redis:
    enabled: true
  in-memory:
    enabled: true
```

*   **redis**: Uses Redis-based Bloom Filter (Scalable, distributed).
*   **in-memory**: Uses Guava-based in-memory Bloom Filter (Fast, local).

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