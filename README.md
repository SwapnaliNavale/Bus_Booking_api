# Bus_Booking_api
Bus Booking System — A Spring Boot REST API for managing bus resources and reservations. Supports authentication with JWT, role-based access (ADMIN/USER), CRUD operations for resources, and seat reservation with availability tracking.

# RESTful Booking System

This project is a RESTful booking system built with Spring Boot, using Java 17, and MySQL. It's designed to manage Resources and Reservations with role-based access control (RBAC) and JWT authentication.

## Table of Contents

  - Features
  - Tech Stack
  - Prerequisites
  - Setup & Run
  - API Documentation
  - Seed Users
  - Assumptions & Trade-offs

## Features

  - **Resource Management:** Admins have full Create, Read, Update, and Delete (CRUD) access to resources. Regular users can only view them.
  - **Reservation Management:**
      - Users can create reservations for a resource and view their own.
      - Admins can manage all reservations.
      - Both roles can filter reservations by status and price range, with pagination and optional sorting.
  - **Authentication:** A secure, stateless authentication system using JWT (JSON Web Tokens).
  - **Authorization:** Role-Based Access Control (RBAC) ensures that only authorized users can access specific endpoints.
  - **Data Persistence:** Uses MySQL as the database with Spring Data JPA for data management.


## Tech Stack

  - **Backend:** Spring Boot 3.x, Java 24
  - **Database:** MySQL
  - **Security:** Spring Security, JWT
  - **ORM:** Hibernate, Spring Data JPA
  - **Dependencies:** Lombok, Springdoc/OpenAPI

-----

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

  - **Java 17 or above:** Check with `java --version`.
  - **MySQL:** You can install it locally or use a Docker container. Ensure the MySQL server is running on `localhost:3306`.
  - **Maven:** Check with `mvn --version`.

-----

## Setup & Run

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/SwapnaliNavale/Bus_Booking_api.git
    cd Bus_Booking_api
    ```

2.  **Configure the database:**
    The application connects to a MySQL database named `bus_reservation_db`. The credentials are pre-configured in `src/main/resources/application.properties`. You don't need to manually create the database; the application will create it for you on startup.

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/bus_reservation_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
    spring.datasource.username=root
    spring.datasource.password=swapnali
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto = update
    ```

    **Note:** If your MySQL username and password are different, please update these lines.

3.  **Run the application:**
    Use Maven to run the application from the command line:

    ```bash
    mvn spring-boot:run
    ```

    The application will start on port `8080` by default.

-----

## API Documentation

This API is fully documented and can be tested using the provided **Postman collection**. The collection includes pre-configured requests for all endpoints, making it easy to get started.

---

### Using the Postman Collection

1.  **Import the Collection:** Download the JSON file for the Postman collection and import it into your Postman application.
2.  **Set Environment Variables:** Configure your environment variables to match your local setup. The base URL should be `http://localhost:8080`.
3.  **Authenticate:** Use the `POST /auth/login` request to get a JWT token. The token is automatically saved to an environment variable for use in subsequent requests.
4.  **Explore Endpoints:** Use the requests in the collection to test the CRUD operations for `Resources` and `Reservations`, with examples for both `ADMIN` and `USER` roles.

For detailed request bodies and query parameters, refer to the examples within each request in the Postman collection.

-----

## Seed Users

For easy testing, the application is pre-configured with two users. Their passwords are BCrypt hashed, but you can use the credentials below to log in:

| Role | Email | Password |
| :--- | :--- | :--- |
| **ADMIN** | `admin1@gmail.com` | `123456` |
| **USER** | `user@gmail.com` | `123456` |

Use the `POST /auth/login` endpoint to get a JWT token. This token must be included in the `Authorization: Bearer <token>` header for all subsequent requests to protected endpoints.

-----

## Assumptions & Trade-offs

  - **Simplicity over Complexity:** The project focuses on core functionality and security, intentionally simplifying some aspects to meet the assignment's timebox. For example, the `Resource` and `User` models are basic and can be extended with more attributes.
  - **No Overlapping Reservations:** While a **nice-to-have** feature, preventing overlapping reservations is not fully implemented to prioritize core requirements. The current system allows for a reservation to be created regardless of whether a resource is already booked for that time slot.
  - **Minimal Error Handling:** Global exception handling is in place for common errors, but specific custom exceptions for every single business logic failure are not fully implemented.
  - **Limited Test Coverage:** The project includes a basic suite of unit and integration tests, but full test coverage is not provided.
  - **Database Schema:** The database schema is managed by `spring.jpa.hibernate.ddl-auto=update`, which is suitable for development but not recommended for production environments. In a production setting, a more robust migration tool like Flyway or Liquibase would be preferred.
