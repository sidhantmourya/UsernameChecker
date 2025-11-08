
# UsernameChecker

## Project Overview

This project provides a username availability checker.  It's built with Java and packaged with Docker for easy deployment.  Further description is currently not provided.

## Key Features & Benefits

*   **Username Availability Check:** The core functionality is to verify the availability of a given username.
*   **Dockerized Deployment:** Easy deployment using Docker containers.
*   **Simple Architecture:** Java-based application with a straightforward structure.

## Prerequisites & Dependencies

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK):** Version 21 or higher.
*   **Maven:**  For building the project.
*   **Docker:** For containerization and deployment.

## Installation & Setup Instructions

1.  **Clone the Repository:**

    ```bash
    git clone https://github.com/sidhantmourya/UsernameChecker.git
    cd UsernameChecker
    ```

2.  **Build the Project using Maven:**

    ```bash
    ./mvnw clean install
    ```
    (Or `mvn clean install` if you have Maven installed globally.)

3.  **Docker Build:**

    Navigate to the project root directory and build the Docker image:

    ```bash
    docker build -t username-checker .
    ```

4.  **Docker Compose (Optional):**

    If you choose to use Docker Compose (via `deploy/docker-compose.yml`), navigate to the `deploy` directory and run:

    ```bash
    docker-compose up -d
    ```

## Usage Examples & API Documentation

The application exposes an endpoint to check username availability. To access the application, ensure it's running (either directly from Java or via Docker).

1.  **Accessing the Web Interface:**

    The application serves a basic HTML interface at `http://localhost:8081` (or the port exposed by your Docker container).

2. **Checking Username via Web Interface**
    The `src/main/resources/static/index.html` provides a user interface to check the availability of a username.

API details and specific usage examples are currently unavailable.  Future development should include API specifications.

## Configuration Options

*   **Port Configuration:**  The application runs on port `8081` by default. This is configured in the `Dockerfile`. To change this, modify the `EXPOSE` instruction in the `Dockerfile` and re-build the image.
*   **Environment Variables:**  No environment variables are currently configured or used.

## Contributing Guidelines

Contributions are welcome! To contribute:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes and commit them with clear, descriptive messages.
4.  Submit a pull request.

Please ensure your code adheres to the project's coding standards and includes relevant tests.

## License Information

License information not currently specified. All rights reserved unless explicitly stated otherwise.

