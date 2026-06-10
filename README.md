# Task Manager API

![Java](https://img.shields.io/badge/Java-25-%23ED8B00?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-%236DB33F?style=flat-square)
![MySQL](https://img.shields.io/badge/Database-MySQL-%234479A1?style=flat-square)
![JWT](https://img.shields.io/badge/Auth-JWT-%23000000?style=flat-square)
![License](https://img.shields.io/badge/license-Unlicense-blue?style=flat-square)
![CI](https://github.com/FranciscoGoal/Task-Manager/actions/workflows/maven.yml/badge.svg)

A REST API for task management with user authentication, persistent MySQL storage, and a built-in web UI. Built with Spring Boot.

- **RESTful API** — full CRUD operations with JSON
- **Authentication** — JWT-based login and registration
- **Web UI** — vanilla HTML/CSS/JS login and task management
- **MySQL Database** — persistent storage with user-task relationships
- **Network-ready** — accessible from any device on your LAN

---

## Table of Contents

- [Getting Started](#getting-started)
- [Architecture](#architecture)
- [API Reference](#api-reference)
- [Web Interface](#web-interface)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Building](#building)
- [Docker](#docker)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Getting Started

### Prerequisites

| Tool | Version | Installation |
|------|---------|-------------|
| Java | 25+ | [jdk.java.net/25/](https://jdk.java.net/25/) |
| Maven | 3.9+ | Bundled as `./mvnw` |
| MySQL | 8.0+ | [mysql.com](https://dev.mysql.com/downloads/) |

### Database Setup

```sql
CREATE DATABASE task;
USE task;

CREATE TABLE `user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_id`)
);

CREATE TABLE `task` (
  `task_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(20) NOT NULL,
  `content` VARCHAR(100) NOT NULL,
  `start_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `user_id` INT DEFAULT NULL,
  PRIMARY KEY (`task_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

INSERT INTO `user` (`username`, `password`) VALUES ('test', 'test'));
```

> **Note:** Passwords are automatically hashed with bcrypt on first application startup.

### Quick Start

```bash
git clone https://github.com/FranciscoGoal/Task-Manager.git
cd task-manager
./mvnw spring-boot:run
```

Open **http://localhost:8080** in your browser and sign in with `Fran` / `fran123`.

> **Network access:** The server binds to `0.0.0.0`. From another device on the same LAN use `http://<YOUR_LOCAL_IP>:8080`.

---

## Architecture

### Layer Diagram

![Layer Diagram](assets/diagrams/layer.svg)

### Request Flow

![Request Flow](assets/diagrams/sequence.svg)

### Application Flow

![Application Flow](assets/diagrams/flow.svg)

### Entity-Relationship

![ER Diagram](assets/diagrams/er.svg)

#### `User`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `userId` | `Integer` | PK, Auto-generated | Unique identifier |
| `username` | `String` | `NOT NULL, UNIQUE` | Login name |
| `password` | `String` | `NOT NULL` | bcrypt-hashed password |

#### `Task`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `taskId` | `Integer` | PK, Auto-generated | Unique identifier |
| `title` | `String` | `NOT NULL` | Task name |
| `content` | `String` | `NOT NULL` | Task details |
| `startDate` | `LocalDateTime` | — | Auto-assigned on creation |
| `user` | `User` | FK → `user.user_id` | Owner |

---

## API Reference

Base URL: `http://localhost:8080`

### Authentication

#### `POST /login`

Authenticates a user and returns a JWT token.

| | |
|---|---|
| **Request body** | `{ "username": "string", "password": "string" }` |
| **Response 200** | `{ "token": "jwt...", "userId": 1, "username": "Fran" }` |
| **Response 401** | `{ "error": "..." }` |

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username": "Fran", "password": "fran123"}'
```

```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "userId": 1,
  "username": "Fran"
}
```

#### `POST /register`

Creates a new user and returns a JWT token (auto-login).

| | |
|---|---|
| **Request body** | `{ "username": "string", "password": "string (min 4 chars)" }` |
| **Response 200** | `{ "token": "jwt...", "userId": 3, "username": "..." }` |
| **Response 400** | `{ "error": "..." }` — validation error |
| **Response 409** | `{ "error": "El usuario ya existe" }` |

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username": "NewUser", "password": "mypassword"}'
```

---

### Tasks

All task endpoints require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

#### `GET /task/my`

Returns tasks owned by the authenticated user.

| | |
|---|---|
| **Response** | `200 OK` — `List&lt;Task&gt;` |

```bash
curl http://localhost:8080/task/my \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

```json
[
  {
    "taskId": 3,
    "title": "Huevos",
    "content": "comprar huevos XL",
    "startDate": "2026-06-10T21:30:00",
    "user": { "userId": 1, "username": "Fran" }
  }
]
```

#### `GET /task`

Returns all tasks (admin).

| | |
|---|---|
| **Response** | `200 OK` — `List&lt;Task&gt;` |

```bash
curl http://localhost:8080/task \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

---

#### `POST /task`

Creates a new task for the authenticated user.

| | |
|---|---|
| **Request body** | `{ "title": "string (required)", "content": "string (required)" }` — `startDate` is auto-assigned |
| **Response** | `200 OK` — `Task` with assigned `taskId` and `startDate` |

```bash
curl -X POST http://localhost:8080/task \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -d '{"title": "Write report", "content": "Q3 financial summary"}'
```

```json
{
  "taskId": 5,
  "title": "Write report",
  "content": "Q3 financial summary",
  "startDate": "2026-06-10T21:30:00",
  "user": { "userId": 1, "username": "Fran" }
}
```

---

#### `GET /task/{id}`

Returns a single task by ID.

| | |
|---|---|
| **Path parameter** | `id` — `Integer` |
| **Response 200** | `Task` |
| **Response 404** | `"Could not find task {id}"` |

```bash
curl http://localhost:8080/task/3 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

```json
{
  "taskId": 3,
  "title": "Huevos",
  "content": "comprar huevos XL",
  "startDate": "2026-06-10T21:30:00",
  "user": { "userId": 1, "username": "Fran" }
}
```

---

#### `PUT /task/{id}`

Replaces an existing task (full update). If the task does not exist, it is created.

| | |
|---|---|
| **Path parameter** | `id` — `Integer` |
| **Request body** | `{ "taskId": Integer, "title": "string", "content": "string" }` — `startDate` is preserved |
| **Response** | `200 OK` — Updated `Task` |

```bash
curl -X PUT http://localhost:8080/task/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -d '{"taskId": 3, "title": "Huevos XL", "content": "comprar huevos extra grandes"}'
```

---

#### `DELETE /task/{id}`

Deletes a task by ID.

| | |
|---|---|
| **Path parameter** | `id` — `Integer` |
| **Response** | `200 OK` — no body |

```bash
curl -X DELETE http://localhost:8080/task/3 \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

---

## Web Interface

Open **http://localhost:8080** in your browser.

1. **Sign In** — login with username/password, or toggle to **Sign Up** to create an account
2. **Task list** — shows only tasks belonging to the logged-in user
3. **Add** — create a task using the form at the top
4. **Edit** — inline by clicking the *Edit* button
5. **Delete** — with confirmation via the *Delete* button
6. **Logout** — top-right button clears session and returns to login
7. **Keyboard shortcuts:** `Enter` to submit, `Escape` to cancel editing

---

## Configuration

File: `src/main/resources/application.properties`

### Database

| Property | Description |
|----------|-------------|
| `spring.datasource.url` | JDBC URL — `jdbc:mysql://127.0.0.1:3306/task` |
| `spring.datasource.username` | MySQL user |
| `spring.datasource.password` | MySQL password |
| `spring.jpa.hibernate.ddl-auto` | `update` — auto-syncs schema with entities |

### JWT

| Property | Description |
|----------|-------------|
| `jwt.secret` | HMAC key (min 256 bits) |
| `jwt.expiration` | Token expiry in ms (default: 86400000 = 24h) |

### Server

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Web server port |
| `server.address` | `0.0.0.0` | Bind address |

**Change port:**

```properties
server.port=9090
```

---

## Project Structure

```
src/main/java/com/example/task_manager/
├── AuthController.java          # POST /login, POST /register
├── AuthService.java             # Authentication + registration logic
├── JwtAuthenticationFilter.java  # JWT validation filter
├── JwtService.java              # JWT token generation / parsing
├── SecurityConfig.java          # Spring Security configuration
├── User.java                    # User JPA entity
├── UserRepository.java          # User data access
├── Task.java                    # Task JPA entity
├── TaskController.java          # REST controller (CRUD + /task/my)
├── TaskRepository.java          # Task data access
├── TaskNotFoundException.java   # Custom 404 exception
├── TaskNotFoundAdvice.java      # Global error handler
├── TaskManagerApplication.java  # Application entry point
├── LoadDatabase.java            # Password hasher on startup
└── WebConfig.java               # Root redirect to login.html

src/main/resources/
├── static/
│   ├── login.html               # Login / Register page
│   ├── tasks.html               # Task management UI
│   └── styles.css               # Stylesheet
└── application.properties       # Configuration
```

---

## Building

Generate a production JAR:

```bash
./mvnw package
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```

---

## Docker

Build and run the application using Docker:

```bash
# Build the image
docker build -t task-manager .

# Run the container
docker run -p 8080:8080 task-manager
```

Open **http://localhost:8080** in your browser.

> **Note:** The Docker container connects to MySQL on the host. Use `--network="host"` or configure `spring.datasource.url` accordingly.

> The `Dockerfile` uses a multi-stage build with `eclipse-temurin:25-jdk-alpine` for compilation and `eclipse-temurin:25-jre-alpine` for runtime. The final image is ~150MB.

---

## Roadmap

- [x] **Persistent database** — MySQL support (replaced in-memory H2)
- [x] **Authentication** — JWT-based user login and registration
- [ ] **Task categories** — add labels and filters
- [x] **Auto timestamp** — creation date tracked automatically
- [ ] **Due dates** — deadline tracking for tasks
- [ ] **Docker Compose** — add MySQL service for local development
- [ ] **Deploy** — one-click deploy to Railway / Render / Fly.io

---

## Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## License

This is free and unencumbered software released into the public domain. See [LICENSE](LICENSE) for details.
