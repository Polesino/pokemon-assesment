# PROMPT USED

Act as a Senior Backend Engineer. I need to scaffold a RESTful API for a Task Management System in Java 17 and Spring Boot 3 using Clean Architecture principles.


Core Requirements:

1. Support full CRUD operations for Tasks (`/api/v1/tasks`).

2. Task Attributes: `id` (Long), `title` (String), `description` (String), `status` (Enum: PENDING, IN_PROGRESS, COMPLETED), `due_date` (LocalDate), and `userId` (Long).

3. Assume a basic User entity exists.

4. Include DTOs for request/response payloads, standard validation, repository layer, service interactor, and a `@RestController`. 

# Task Management System API — Clean Architecture Scaffold

A production-ready RESTful API scaffold for a **Task Management System** built with **Java 17**, **Spring Boot 3**, and **Clean Architecture (Hexagonal Architecture)** principles.

---

## 📐 Architectural Overview

In accordance with Clean Architecture principles, dependencies strictly flow **inward**. The **Domain Core** holds business logic and enterprise rules without any compile-time dependency on Spring, JPA, or HTTP layers.

```
                  ┌─────────────────────────────────────────────────────────┐
                  │                    PRESENTATION LAYER                   │
                  │   TaskController  │  CreateTaskRequest  │  RestMapper   │
                  └────────────────────────────┬────────────────────────────┘
                                               │
                                               ▼
                  ┌─────────────────────────────────────────────────────────┐
                  │                    APPLICATION LAYER                    │
                  │        TaskUseCase  │  CreateTaskCommand  │  DTOs       │
                  └────────────────────────────┬────────────────────────────┘
                                               │
                                               ▼
                  ┌─────────────────────────────────────────────────────────┐
                  │                      DOMAIN LAYER                       │
                  │       Task (Entity) │ TaskStatus │ TaskRepositoryPort   │
                  └────────────────────────────▲────────────────────────────┘
                                               │
                  ┌────────────────────────────┴────────────────────────────┐
                  │                   INFRASTRUCTURE LAYER                  │
                  │   TaskRepositoryAdapter │ TaskJpaEntity │ SpringData    │
                  └─────────────────────────────────────────────────────────┘
```

### Layer Separation & Responsibilities

1. **Domain Layer (`domain`)**: Core business entities and rules (`Task`, `TaskStatus`) and outbound port interfaces (`TaskRepositoryPort`). Zero framework dependencies.
2. **Application Layer (`application`)**: Business use cases and service interactors (`TaskUseCase`). Coordinates workflows between domain entities and ports using application DTOs (`CreateTaskCommand`, `UpdateTaskCommand`, `TaskResponse`).
3. **Infrastructure Layer (`infrastructure`)**: Adapters for secondary/driven concerns like DB persistence (`TaskJpaEntity`, `SpringDataTaskRepository`, `TaskRepositoryAdapter`).
4. **Presentation Layer (`presentation`)**: Controllers (`TaskController`) and HTTP request validation DTOs (`CreateTaskRequest`, `UpdateTaskRequest`). Maps HTTP requests into application commands and calls use cases.

---

## 📁 Project Directory Structure

```text
com.example.taskmanager/
├── domain/                         <-- Core Business Logic & Enterprise Rules
│   ├── model/
│   │   ├── Task.java
│   │   └── TaskStatus.java
│   └── repository/                 <-- Driven Ports (Interfaces)
│       └── TaskRepositoryPort.java
├── application/                    <-- Use Cases / Interactors
│   ├── dto/
│   │   ├── CreateTaskCommand.java
│   │   ├── UpdateTaskCommand.java
│   │   └── TaskResponse.java
│   └── usecase/
│       └── TaskUseCase.java
├── infrastructure/                 <-- Frameworks, DB, External Adapters
│   └── persistence/
│       ├── entity/
│       │   └── TaskJpaEntity.java
│       ├── repository/
│       │   ├── SpringDataTaskRepository.java
│       │   └── TaskRepositoryAdapter.java
│       └── mapper/
│           └── TaskPersistenceMapper.java
└── presentation/                   <-- Driving Adapters (REST Controllers)
    ├── controller/
    │   └── TaskController.java
    ├── dto/
    │   ├── CreateTaskRequest.java
    │   ├── UpdateTaskRequest.java
    │   └── TaskResponseDto.java
    └── exception/
        ├── GlobalExceptionHandler.java
        └── ErrorResponse.java
```

---

## 🔌 API Specification (`/api/v1/tasks`)

| Method   | Endpoint           | Description                      | Request Body           | Response Code |
| :------- | :────────────────- | :─────────────────────────────── | :───────────────────── | :------------ |
| `POST`   | `/api/v1/tasks`     | Create a new task                | `CreateTaskRequest`    | `201 Created` |
| `GET`    | `/api/v1/tasks/{id}`| Fetch task by ID                 | N/A                    | `200 OK`      |
| `GET`    | `/api/v1/tasks`     | List all tasks (or filter user) | Query Param: `userId`  | `200 OK`      |
| `PUT`    | `/api/v1/tasks/{id}`| Update task details or status    | `UpdateTaskRequest`    | `200 OK`      |
| `DELETE` | `/api/v1/tasks/{id}`| Delete a task by ID              | N/A                    | `204 No Content` |

---

## 💻 Source Code Implementation

### 1. Domain Layer

#### `TaskStatus.java`
```java
package com.example.taskmanager.domain.model;

public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
```

#### `Task.java`
```java
package com.example.taskmanager.domain.model;

import java.time.LocalDate;

public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private Long userId;

    public Task(Long id, String title, String description, TaskStatus status, LocalDate dueDate, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.dueDate = dueDate;
        this.userId = userId;
    }

    // Domain Business Logic
    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
    }

    public void updateDetails(String title, String description, LocalDate dueDate, TaskStatus status) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        this.description = description;
        this.dueDate = dueDate;
        if (status != null) {
            this.status = status;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public Long getUserId() { return userId; }
}
```

#### `TaskRepositoryPort.java`
```java
package com.example.taskmanager.domain.repository;

import com.example.taskmanager.domain.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findAll();
    List<Task> findByUserId(Long userId);
    void deleteById(Long id);
    boolean existsById(Long id);
}
```

---

### 2. Application Layer

#### Commands and DTO Records
```java
package com.example.taskmanager.application.dto;

import com.example.taskmanager.domain.model.TaskStatus;
import java.time.LocalDate;

public record CreateTaskCommand(
    String title,
    String description,
    LocalDate dueDate,
    Long userId
) {}

public record UpdateTaskCommand(
    String title,
    String description,
    TaskStatus status,
    LocalDate dueDate
) {}

public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    LocalDate dueDate,
    Long userId
) {}
```

#### `TaskUseCase.java` (Interactor)
```java
package com.example.taskmanager.application.usecase;

import com.example.taskmanager.application.dto.CreateTaskCommand;
import com.example.taskmanager.application.dto.TaskResponse;
import com.example.taskmanager.application.dto.UpdateTaskCommand;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import com.example.taskmanager.domain.repository.TaskRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;

    public TaskUseCase(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    public TaskResponse createTask(CreateTaskCommand command) {
        Task task = new Task(
            null,
            command.title(),
            command.description(),
            TaskStatus.PENDING,
            command.dueDate(),
            command.userId()
        );
        Task savedTask = taskRepositoryPort.save(task);
        return toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepositoryPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id));
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepositoryPort.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByUserId(Long userId) {
        return taskRepositoryPort.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    public TaskResponse updateTask(Long id, UpdateTaskCommand command) {
        Task task = taskRepositoryPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id));

        task.updateDetails(command.title(), command.description(), command.dueDate(), command.status());
        Task updatedTask = taskRepositoryPort.save(task);
        return toResponse(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepositoryPort.existsById(id)) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }
        taskRepositoryPort.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getDueDate(),
            task.getUserId()
        );
    }
}
```

---

### 3. Infrastructure Layer

#### `TaskJpaEntity.java`
```java
package com.example.taskmanager.infrastructure.persistence.entity;

import com.example.taskmanager.domain.model.TaskStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class TaskJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public TaskJpaEntity() {}

    public TaskJpaEntity(Long id, String title, String description, TaskStatus status, LocalDate dueDate, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.userId = userId;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public Long getUserId() { return userId; }
}
```

#### `SpringDataTaskRepository.java`
```java
package com.example.taskmanager.infrastructure.persistence.repository;

import com.example.taskmanager.infrastructure.persistence.entity.TaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataTaskRepository extends JpaRepository<TaskJpaEntity, Long> {
    List<TaskJpaEntity> findByUserId(Long userId);
}
```

#### `TaskRepositoryAdapter.java`
```java
package com.example.taskmanager.infrastructure.persistence.repository;

import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.repository.TaskRepositoryPort;
import com.example.taskmanager.infrastructure.persistence.entity.TaskJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TaskRepositoryAdapter implements TaskRepositoryPort {

    private final SpringDataTaskRepository repository;

    public TaskRepositoryAdapter(SpringDataTaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = toEntity(task);
        TaskJpaEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Task> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    private TaskJpaEntity toEntity(Task domain) {
        return new TaskJpaEntity(
            domain.getId(),
            domain.getTitle(),
            domain.getDescription(),
            domain.getStatus(),
            domain.getDueDate(),
            domain.getUserId()
        );
    }

    private Task toDomain(TaskJpaEntity entity) {
        return new Task(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getDueDate(),
            entity.getUserId()
        );
    }
}
```

---

### 4. Presentation Layer

#### Presentation DTOs & Validation
```java
package com.example.taskmanager.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateTaskRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @FutureOrPresent(message = "Due date must be today or in the future")
    @JsonProperty("due_date")
    LocalDate dueDate,

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    Long userId
) {}

public record UpdateTaskRequest(
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    String status,

    @JsonProperty("due_date")
    LocalDate dueDate
) {}
```

#### `TaskController.java`
```java
package com.example.taskmanager.presentation.controller;

import com.example.taskmanager.application.dto.CreateTaskCommand;
import com.example.taskmanager.application.dto.TaskResponse;
import com.example.taskmanager.application.dto.UpdateTaskCommand;
import com.example.taskmanager.application.usecase.TaskUseCase;
import com.example.taskmanager.domain.model.TaskStatus;
import com.example.taskmanager.presentation.dto.CreateTaskRequest;
import com.example.taskmanager.presentation.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskUseCase taskUseCase;

    public TaskController(TaskUseCase taskUseCase) {
        this.taskUseCase = taskUseCase;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        CreateTaskCommand command = new CreateTaskCommand(
            request.title(),
            request.description(),
            request.dueDate(),
            request.userId()
        );
        TaskResponse response = taskUseCase.createTask(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskUseCase.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(taskUseCase.getTasksByUserId(userId));
        }
        return ResponseEntity.ok(taskUseCase.getAllTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {

        TaskStatus statusEnum = null;
        if (request.status() != null) {
            statusEnum = TaskStatus.valueOf(request.status().toUpperCase());
        }

        UpdateTaskCommand command = new UpdateTaskCommand(
            request.title(),
            request.description(),
            statusEnum,
            request.dueDate()
        );

        return ResponseEntity.ok(taskUseCase.updateTask(id, command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### `GlobalExceptionHandler.java`
```java
package com.example.taskmanager.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("validationErrors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
```

---

## 🚀 Getting Started & Requirements

- **Java Version**: Java 17+
- **Spring Boot**: 3.x
- **Build Tool**: Maven or Gradle
- **Database**: H2 (In-Memory for dev/test) or PostgreSQL

```bash
# Build and run with Maven
./mvnw clean spring-boot:run
```

## Validations and Corrections

I would verify if the AI does not expose entities to REST controllers inputs or outputs, DTO's instead.
Also verify some validations annotations to avoid empty titles, or non valid values
Work con Exception handlings
Also refactor or work Security with authentiation and authorization.