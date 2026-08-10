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