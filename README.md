# Discrete-Event Simulator

A scalable **web platform for discrete-event system simulation**.  
Model anything from intergalactic spaceports to coffee shop queues — complete with **real-time visualization, insightful metrics, and a modular architecture**.

---

## Core Goals
- **Modular OOP-based simulation engine** with REST API (Spring Boot + JPA)
- **Interactive frontend** (React + TypeScript + Chart.js)
- **Persistence** with PostgreSQL (prod) and H2 (dev)
- **Testing**: JUnit (backend), Jest + React Testing Library (frontend)
- **Future**: Dockerized deployment (AWS Fargate + CI/CD via GitHub Actions)
- **Future**: Observability with Prometheus & Grafana

---

## Tech Stack
**Backend**
- Java 21, Spring Boot, Spring Data JPA
- PostgreSQL (prod) / H2 (dev)

**Frontend**
- React, TypeScript, Vite
- Chart.js for dynamic visualizations

**Testing**
- JUnit (backend)
- Jest + React Testing Library (frontend)

**Future**
- Docker, Micrometer, Prometheus, Grafana

---

## Current Features

### Backend (Spring Boot)
- REST API endpoints:
  - `POST /simulate` → Run a new simulation
  - `GET /simulations` → Retrieve past runs
  - `GET /metrics` → Fetch aggregated metrics
- Simulation engine:
  - Randomized interarrival & service times (exponential distribution)
  - Multi-server, self-checkout, and queue capacity support
  - Results persisted with JPA (H2/Postgres)

### Frontend (React + Vite)
- Input simulation parameters:
  - Number of servers, self-checkouts, max queue length, total customers
  - Mean inter-arrival & service times
- Live metrics visualization:
  - Avg. wait time, customers served, customers left
  - Dual-axis charts via Chart.js
- Simulation history table with performance highlighting
- System load factor (ρ) displayed dynamically

---

## Getting Started

### Backend (Spring Boot)
**Prerequisites**
- Java 21+
- `JAVA_HOME` set (or configured in `gradle.properties`)

**Run Locally**
```bash
chmod +x gradlew        # first time only
./gradlew bootRun
App runs at: http://localhost:8080

H2 Console

URL: http://localhost:8080/h2-console

JDBC: jdbc:h2:mem:testdb

User: sa (no password)

### Frontend (React + Vite)

**Prerequisites**

Node.js + npm (or yarn)

Run Locally
