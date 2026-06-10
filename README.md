# Task Manager API

![Java](https://img.shields.io/badge/Java-25-%23ED8B00?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-%236DB33F?style=flat-square)
![H2](https://img.shields.io/badge/Database-H2-%23007396?style=flat-square)
![License](https://img.shields.io/badge/license-Unlicense-blue?style=flat-square)

A lightweight REST API for task management with a built-in web UI. Built with Spring Boot and designed for simplicity —  external database integration in progress.

- **RESTful API** — full CRUD operations with JSON
- **Web UI** — vanilla HTML/CSS/JS interface at `/`
- **H2 Database** — in-memory, zero configuration
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
- [License](#license)

---

## Getting Started

### Prerequisites

| Tool | Version | Installation |
|------|---------|-------------|
| Java | 25+ | [jdk.java.net/25/](https://jdk.java.net/25/) |
| Maven | 3.9+ | Bundled as `./mvnw` |

### Quick Start

```bash
git clone https://github.com/<your-user>/task-manager.git
cd task-manager
./mvnw spring-boot:run
```

Open **http://localhost:8080** in your browser.

> **Network access:** The server binds to `0.0.0.0`. From another device on the same LAN use `http://<YOUR_LOCAL_IP>:8080`.

---

## Architecture

### Layer Diagram

```mermaid
flowchart LR
    Browser["🌐 Browser"] -->|"HTTP :8080"| SB["Spring Boot"]
    Client["💻 curl / Postman"] -->|"REST API"| SB

    subgraph SB [Spring Boot Application]
        Web["static/index.html<br/>Web UI"]
        Ctrl["TaskController<br/>REST Endpoints"]
        Repo["TaskRepository<br/>JPA"]
        Advice["TaskNotFoundAdvice<br/>@ExceptionHandler"]
    end

    Web --> Browser
    Repo --> DB[("H2<br/>In-Memory Database")]
```

### Request Flow

```mermaid
sequenceDiagram
    participant Client
    participant Controller as TaskController
    participant Repository as TaskRepository
    participant DB as H2 Database

    Client->>Controller: GET /task
    Controller->>Repository: findAll()
    Repository->>DB: SELECT * FROM task
    DB-->>Repository: [Task, ...]
    Repository-->>Controller: Task list
    Controller-->>Client: 200 OK — JSON array

    Client->>Controller: POST /task {title, description}
    Controller->>Repository: save(newTask)
    Repository->>DB: INSERT INTO task
    DB-->>Repository: Task (with generated id)
    Repository-->>Controller: Task
    Controller-->>Client: 200 OK — JSON object
```

### Entity-Relationship

```mermaid
erDiagram
    TASK {
        Long id PK "AUTO_INCREMENT"
        String title "NOT NULL"
        String description "NULLABLE"
    }
```

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `Long` | PK, Auto-generated | Unique identifier |
| `title` | `String` | `NOT NULL` | Task name |
| `description` | `String` | Nullable | Optional details |

---

## API Reference

Base URL: `http://localhost:8080`

### `GET /task`

Returns all tasks.

| | |
|---|---|
| **Response** | `200 OK` — `List&lt;Task&gt;` |

```bash
curl http://localhost:8080/task
```

```json
[
  {
    "id": 1,
    "title": "Buy groceries",
    "description": "Milk, eggs, bread"
  }
]
```

---

### `POST /task`

Creates a new task.

| | |
|---|---|
| **Request body** | `{ "title": "string (required)", "description": "string (optional)" }` |
| **Response** | `200 OK` — `Task` with assigned `id` |

```bash
curl -X POST http://localhost:8080/task \
  -H "Content-Type: application/json" \
  -d '{"title": "Write report", "description": "Q3 financial summary"}'
```

```json
{
  "id": 2,
  "title": "Write report",
  "description": "Q3 financial summary"
}
```

---

### `GET /task/{id}`

Returns a single task by ID.

| | |
|---|---|
| **Path parameter** | `id` — `Long` |
| **Response 200** | `Task` |
| **Response 404** | `{ "error": "Could not find task {id}" }` |

```bash
curl http://localhost:8080/task/1
```

```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, eggs, bread"
}
```

```bash
curl http://localhost:8080/task/99
```

```json
{
  "error": "Could not find task 99"
}
```

---

### `PUT /task/{id}`

Replaces an existing task (full update). If the task does not exist, it is created.

| | |
|---|---|
| **Path parameter** | `id` — `Long` |
| **Request body** | `{ "id": Long, "title": "string", "description": "string" }` |
| **Response** | `200 OK` — Updated `Task` |

```bash
curl -X PUT http://localhost:8080/task/2 \
  -H "Content-Type: application/json" \
  -d '{"id": 2, "title": "Write final report", "description": "Q3 financial summary – updated"}'
```

```json
{
  "id": 2,
  "title": "Write final report",
  "description": "Q3 financial summary – updated"
}
```

---

### `DELETE /task/{id}`

Deletes a task by ID.

| | |
|---|---|
| **Path parameter** | `id` — `Long` |
| **Response** | `200 OK` — no body |

```bash
curl -X DELETE http://localhost:8080/task/1
```

---

## Web Interface

Open **http://localhost:8080** in your browser.

- **Add** a task using the form at the top
- **Edit** inline by clicking the *Edit* button
- **Delete** with confirmation via the *Delete* button
- **Keyboard shortcuts:** `Enter` to submit, `Escape` to cancel editing

---

## Configuration

File: `src/main/resources/application.properties`

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Web server port |
| `server.address` | `0.0.0.0` | Bind address |

> **Note:** Persistent database integration (PostgreSQL/MySQL) is in progress. Currently using H2 in-memory — data is lost on restart.

**Change port:**

```properties
server.port=9090
```

**Persist data to disk (default is in-memory):**

```properties
spring.datasource.url=jdbc:h2:file:./data/taskmanager
```

---

## Project Structure

```
src/main/java/com/example/task_manager/
├── Task.java                     # JPA entity
├── TaskController.java           # REST controller
├── TaskRepository.java           # Spring Data repository
├── TaskNotFoundException.java    # Custom 404 exception
├── TaskNotFoundAdvice.java       # Global error handler
├── TaskManagerApplication.java   # Application entry point
└── LoadDatabase.java             # Demo data seeder

src/main/resources/
├── static/
│   ├── index.html                # Web UI
│   └── styles.css                # Stylesheet
└── application.properties        # Configuration
```

---

## Building

Generate a production JAR:

```bash
./mvnw package
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```

---

## License

This is free and unencumbered software released into the public domain. See [LICENSE](LICENSE) for details.
