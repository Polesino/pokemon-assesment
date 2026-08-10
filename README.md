# ⚡ Pokémon Technical Assessment — Monorepo

A full-stack, enterprise-grade web application built with **Java 17 & Spring Boot 3** (Backend) and **React, TypeScript & Vite** (Frontend). The project leverages **Clean Architecture** (Hexagonal/Ports and Adapters) and **Test-Driven Development (TDD)** to communicate with the external [PokéAPI](https://pokeapi.co/), cache responses for fair-use compliance, replicate entities locally with proprietary fields, and perform secure CRUD operations.

---

## 🏛️ System Architecture

The application adheres strictly to Clean Architecture to maintain high testability, maintainability, and strict decoupling between core domain logic and external delivery mechanisms.

### Backend Package Breakdown (`/backend`)
```text
com.ballastlane.pokemon
├── domain                  # Pure Java Core (ZERO Framework/Spring Dependencies)
│   ├── model               # Core Business Models (Pokemon, User, EvolutionNode)
│   ├── exception           # Business Domain Exceptions (e.g., PokemonNotFoundException)
│   └── port                # Pure Interfaces
│       ├── in              # Inbound Use Case Contracts
│       └── out             # Outbound Repository & HTTP Client Contracts
│
├── application             # Business Logic Layer
│   └── usecase             # Interactors implementing Inbound Ports
│
└── infrastructure          # Framework Adapters & External Wiring
    ├── web                 # REST Controllers, Request/Response DTOs, Exception Handler
    ├── persistence         # JPA Entities, Spring Data Repositories, DB Adapters
    ├── external            # Non-blocking PokéAPI WebClient Adapter & Caffeine Cache Config
    └── security            # Spring Security 6, JWT Utilities, Password Encoders

Key Technical Highlights
Reactive & Non-Blocking Outbound Client: PokéAPI integrations use Spring's WebClient combined with reactive operators (Flux, flatMap) to execute concurrent queries in parallel, drastically lowering payload assembly latencies.

Fair-Use Compliant Caching Layer: Integrated Caffeine Cache via Spring's @Cacheable annotation on external API adapters to honor PokéAPI's fair-use policy and accelerate API responses.

Stateless JWT Security: Fine-grained authorization where public routes (GET /api/v1/pokemon/**) remain accessible, while mutation/sync operations (POST, PUT, DELETE) require Bearer token validation.

🚀 Getting Started & Environment Setup
You can run the entire solution locally either using Docker Compose (recommended) or manually running backend and frontend services.

Prerequisites
Docker and Docker Compose (for Docker setup)

JDK 17 LTS or higher and Apache Maven 3.8+ (for manual backend setup)

Node.js 18+ and npm (for manual frontend setup)

Option 1: Quickstart with Docker Compose (Recommended)
Clone the repository and navigate to the project root:

Bash
git clone <your-public-github-repo-url>
cd pokemon-assessment
Spin up all services (PostgreSQL, Spring Boot Backend, and React Frontend):

Bash
docker-compose up --build -d
Access the applications in your browser:

Frontend UI: http://localhost:3000

Backend API Base: http://localhost:8080/api/v1

Database (PostgreSQL): localhost:5432 (User: postgres, Password: postgres, Database: pokemondb)

Option 2: Manual Local Execution
1. Backend Service (/backend)
Bash
cd backend

# Execute unit and integration test suite
mvn clean test

# Run Spring Boot application locally (Uses embedded H2 or active DB profile)
mvn spring-boot:run
Backend runs at http://localhost:8080

2. Frontend Service (/frontend)
Bash
cd frontend

# Install dependencies
npm install

# Verify zero build errors or TypeScript warnings
npm run build

# Start Vite development server
npm run dev
Frontend runs at http://localhost:5173