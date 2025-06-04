Discrete Event Simulator 

Welcome to a rather spiffy Scalable Web Platform for Discrete-Event System Simulation. This project simulates complex systems, visualizes performance, and provides near real-time observability. Whether you're modeling intergalactic spaceports or coffee shop queues, this platform has you covered — complete with snazzy charts and insightful metrics.
 Core Goals (The Grand Plan)

    Modular OOP-Based Event Simulation Engine with a REST API (Spring Boot + PostgreSQL/H2)

    React + TypeScript Single Page App with dynamic metrics visualized via Chart.js

    (Future) Dockerized deployment on AWS Fargate with CI/CD via GitHub Actions

    (Future) Real-time metrics with Prometheus & Grafana

    Comprehensive Testing: JUnit for backend, Jest for frontend — 80%+ coverage target

 Tech Stack

Backend: Java, Spring Boot, Spring Data JPA
Database: PostgreSQL (prod), H2 (dev)
Frontend: React, TypeScript, Vite, Chart.js
Testing: JUnit (backend), Jest + React Testing Library (frontend)
Containerization (Future): Docker
Monitoring (Future): Micrometer, Prometheus, Grafana
 Current State: What's Cooking?
 Spring Boot Backend

    REST API endpoints:

        /simulate – Run a simulation

        /simulations – Retrieve all previous runs

        /metrics – Get simulation metrics

    Simulation features:

        Randomized inter-arrival and service times (exponential distribution)

        Results persisted using JPA (H2 or PostgreSQL)

        Engine supports multiple servers, self-checkouts, and queue capacity logic

 React Frontend

    Input simulation parameters:

        Number of Servers

        Number of Self-Checkouts

        Max Queue Length

        Number of Customers

        Mean Inter-Arrival Time

        Mean Service Time

    Live Metrics Visualization:

        Avg. Wait Time, Customers Served, Customers Left

        Dual-axis Chart.js chart

    Simulation History Table:

        Conditional row highlighting for performance insight

    System Load Factor (ρ): Displayed dynamically based on inputs

 Getting Started
1️⃣ Backend (Spring Boot)

Prerequisites:

    Java 21+

    Ensure JAVA_HOME is set, or configure in gradle.properties

Run Locally:

chmod +x gradlew      # (first time only)
./gradlew bootRun

    Runs at http://localhost:8080

    H2 Console: http://localhost:8080/h2-console

        JDBC: jdbc:h2:mem:testdb

        User: sa (no password)

2️⃣ Frontend (React + Vite)

Prerequisites:

    Node.js & npm (or yarn)

Run Locally:

cd frontend
npm install       # or: yarn install
npm run dev       # or: yarn dev

    Opens at http://localhost:5173

    Frontend proxies API calls to localhost:8080

 Simulate!

    Open the frontend

    Adjust simulation parameters

    View ρ (System Load) instantly

    Click Run Simulation

    Watch charts and history table update dynamically

 Simulation Parameters Explained
Parameter	Description
Servers	Number of human-operated service points
Self-Checkouts	Self-service stations (same logic as servers currently)
Max Queue Length	Total max queue size (combined for all servers/self-checkouts)
Customers	Total number of customers in the simulation
Mean Inter-Arrival	Avg. time between customer arrivals (exponential distribution)
Mean Service Time	Avg. time to serve one customer (exponential distribution)
 Future Adventures

    Randomness Enhancements

        Server rest times, multiple distributions, seedable RNG

    Simulator Upgrades

        Server-specific queues, customer priorities, server breakdowns

    Advanced Visualization

        Real-time queue animations, diverse chart types

    Testing Focus

        Broader backend unit tests

        Complete frontend UI testing

    Deployment & Monitoring

        Full Docker + docker-compose

        GitHub Actions CI/CD

        Prometheus + Grafana dashboards

 Quick API Test (cURL)

curl 'http://localhost:8080/simulate'

     “If you break it, you get to keep both pieces.”
    Contributions, forks, and ideas are always welcome!
