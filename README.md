# Identity Service for the Home Services Platform

A Spring Boot-based identity and access management service for a home services platform developed as part of a final-year master’s degree project.

This service is responsible for user authentication, authorization, profile management, password handling, and role-based access for clients and service providers (prestataires) within the platform.

---

## Project Overview

The Home Services Platform connects customers with trusted service providers for home-related tasks such as cleaning, maintenance, repair, and personalized services. This repository implements the Identity Service, which acts as the security and account management backbone of the platform.

It handles:
- User registration
- Login and JWT-based authentication
- Role management
- Profile update and retrieval
- Password change and reset
- Account deletion
- Service provider onboarding
- Client/provider role switching

This service is designed to integrate with the rest of the platform through secure, stateless communication and role-based access control.

---

## Why This Service Matters

In a distributed platform, identity management is essential to:
- verify who the user is,
- secure platform resources,
- manage user roles and permissions,
- support provider/client workflows,
- ensure trust and accountability across services.

This repository centralizes these concerns in a dedicated microservice, making the platform easier to scale and maintain.

---

## Key Features

- Secure registration and login
- JWT-based authentication and authorization
- User roles:
  - ADMIN
  - CLIENT
  - PRESTATAIRE
- Password update and password reset workflows
- Profile management
- User search and filtering for providers
- Public profile retrieval for service providers
- Provider onboarding flow for clients
- Role switching between client and provider modes
- Database-backed persistence with migrations
- Spring Cloud integration with a configuration server
- Service discovery via Eureka

---

## Architecture

This project follows a typical Spring Boot layered architecture:

- Controllers: expose REST APIs
- Services: implement business logic
- Repositories: manage persistence with JPA
- Entities: represent database models
- DTOs: handle request/response transformation
- Security: JWT generation, validation, and access control
- Filters: handle request authentication processing
- Exceptions: centralize error handling

The application exposes REST endpoints under the base path:

- /api/v1/auth

---

## Technology Stack

- Java 21
- Spring Boot 3.3.4
- Spring Security
- Spring Data JPA
- MySQL
- Flyway
- JWT (jjwt)
- MapStruct
- Lombok
- Spring Cloud Config Client
- Spring Cloud Netflix Eureka Client
- Maven

---

## Project Structure

```text
identity-service-pfe/
├── src/
│   ├── main/
│   │   ├── java/com/dalal/identityservicepfe/
│   │   │   ├── annotations
│   │   │   ├── config
│   │   │   ├── controllers
│   │   │   ├── dtos
│   │   │   ├── entities
│   │   │   ├── enums
│   │   │   ├── exceptions
│   │   │   ├── filters
│   │   │   ├── handler
│   │   │   ├── mappers
│   │   │   ├── repositories
│   │   │   ├── security
│   │   │   ├── services
│   │   │   └── IdentityServicePfeApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/
│   └── test/
├── .gitignore
├── .gitattributes
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## Main API Endpoints

The API is organized under `/api/v1/auth`.

### Authentication
- POST `/api/v1/auth/register`
- POST `/api/v1/auth/login`
- PUT `/api/v1/auth/update-password`
- PUT `/api/v1/auth/reset-password`
- PUT `/api/v1/auth/change-email`
- DELETE `/api/v1/auth/delete-account`

### User and Profile Management
- GET `/api/v1/auth/profile`
- PUT `/api/v1/auth/update-profile`
- GET `/api/v1/auth/users`
- GET `/api/v1/auth/search`
- GET `/api/v1/auth/filter`
- GET `/api/v1/auth/{id}/public-profile`

### Provider / Client Workflow
- POST `/api/v1/auth/become-prestataire`
- POST `/api/v1/auth/switch-to-client`
- POST `/api/v1/auth/switch-to-provider`

### Internal / Cross-Service Access
- GET `/api/v1/auth/profil/{id}`
- POST `/api/v1/auth/public-profils/batch`

> Some endpoints are protected and require the appropriate user roles and JWT token.

---

## Security Model

This service uses:
- Spring Security
- JWT authentication
- Role-based endpoint protection
- Credential validation and secure password update flow

The application is configured to import external configuration from a Spring Cloud Config server:

```properties
spring.config.import=optional:configserver:http://localhost:8888
```

This means the service expects a config server to be available in the local environment.

---

## Configuration

The main configuration file is:

```properties
src/main/resources/application.properties
```

It includes:
- application name
- config server import
- JWT expiration
- private/public key settings

Make sure your environment is properly configured before running the project.

---

## Prerequisites

Before running the project, ensure that you have installed:

- Java 21+
- Maven
- MySQL
- A running Spring Cloud Config Server if required
- Eureka discovery service if used in your platform architecture

---

## Running the Project

Clone the repository:

```bash
git clone https://github.com/DalalYouness/identity-service-pfe.git
cd identity-service-pfe
```

Run the application with Maven:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Or on Windows:

```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

The service will start using the Spring Boot application entry point:

```java
IdentityServicePfeApplication
```

---

## Database

This service uses MySQL as its primary data source and Flyway for database migration management.

The migration scripts are stored in:

```text
src/main/resources/db/
```

This ensures that database schema changes are versioned and reproducible.

---

## Environment and Deployment Notes

For a full platform deployment, this service should typically run alongside:
- API Gateway
- Configuration Server
- Discovery Server (Eureka)
- Other platform microservices

This identity service acts as the authentication and authorization layer for the rest of the system.

---

## Academic Context

This project was developed as part of a final-year master’s degree project in software engineering / distributed systems / software architecture.

The goal is to demonstrate:
- microservice design,
- secure user management,
- modern backend architecture,
- role-based access control,
- JWT-based authentication,
- real-world integration patterns for a platform ecosystem.

---

## Future Improvements

Possible enhancements for this project include:
- Email verification
- OTP-based authentication
- Refresh token support
- Audit logs for sensitive user actions
- Advanced validation and rate limiting
- Swagger/OpenAPI documentation
- CI/CD pipeline setup
- Dockerization and container deployment

---

## License

This project is intended for academic and portfolio purposes.

If you plan to publish it publicly, you may choose to add a LICENSE file depending on your university or project requirements.

---

## Author

Dalal Youness

Master’s Degree Project - Home Services Platform

---

## Contact

If you want, I can also generate:
1. a more modern “startup-style” README,
2. a French version,
3. a README tailored for GitHub portfolio presentation,
4. or a version including badges, architecture diagram, and API documentation style.
