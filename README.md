# AuthSpring

JWT-based authentication API built with Spring Boot 4.

## What is included

- User registration (`POST /api/auth/register`)
- User login (`POST /api/auth/login`)
- Authenticated profile endpoint (`GET /api/users/me`)
- H2 in-memory database for local development
- Scalar API reference page at `/scalar.html`
- Ready-to-run HTTP requests in `requests.http`

## Tech stack

- Java 25
- Spring Boot 4.0.3
- Spring Security (JWT)
- Spring Data JPA + H2
- Spring Validation
- Lombok
- Springdoc OpenAPI + Scalar (static page)
- Maven Wrapper (`./mvnw`)

## Quick start

### 1) Run tests

```bash
./mvnw -q test
```

### 2) Start the app

```bash
./mvnw spring-boot:run
```

App default URL:

- `http://localhost:8080`

## API docs

Once running:

- OpenAPI JSON: `http://localhost:8080/api-docs`
- Scalar UI: `http://localhost:8080/scalar.html`
- H2 Console: `http://localhost:8080/h2-console`

## Authentication flow

1. Register a user at `/api/auth/register`
2. Login at `/api/auth/login`
3. Use returned token as:
   - `Authorization: Bearer <token>`
4. Call `/api/users/me`

## Try with HTTP file

Use `requests.http` in your IDE HTTP client.

It already includes:

- Register request
- Login request
- Profile request with automatic token capture

## Configuration

Main config is in `src/main/resources/application.yaml`.

Current custom properties:

- `security.jwt.secret`
- `security.jwt.expiration`

> Note: replace the JWT secret before production usage.

## Project structure

- `src/main/java/com/laurosantos/authspring/auth` - auth controller/service/DTOs
- `src/main/java/com/laurosantos/authspring/security` - JWT and security config
- `src/main/java/com/laurosantos/authspring/user` - user entity/repository/controller/service
- `src/main/java/com/laurosantos/authspring/exception` - API exception handling
- `src/test/java/com/laurosantos/authspring` - unit and integration tests

## Useful commands

```bash
./mvnw clean package
./mvnw spring-boot:build-image
```

