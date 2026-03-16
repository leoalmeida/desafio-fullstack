# desafio-fullstack

[Português](README.md) | [English](README.en.md)

Fullstack application with Angular frontend, Spring Boot backend, and an EJB module for benefit transfer business rules.

## Executive Summary

This repository delivers a fullstack benefits solution with clear separation between UI, REST API, and transfer business rules.

Project goals:

- provide REST APIs for benefits lifecycle operations
- keep transfer rules centralized in the EJB module
- integrate frontend and backend through stable contracts
- maintain quality through tests and build standards

Main scope:

- `frontend`: Angular SPA for benefits operations
- `backend-module`: Spring Boot REST API
- `ejb-module`: transfer business rules
- `db`: schema and seed scripts

Architecture at a glance:

Frontend
    |
Backend REST
    |
EJB
    |
Persistence

## Stack

- Java 17
- Spring Boot 3.2.5
- EJB
- Angular 21
- Maven multi-module
- Docker Compose

## Project Status

- fullstack modular architecture established
- multi-module Java build and frontend flow available
- detailed roadmap available in [plano.md](plano.md)

## Table of Contents

- [Executive Summary](#executive-summary)
- [Stack](#stack)
- [Project Status](#project-status)
- [Requirements](#requirements)
- [Structure](#structure)
- [Configuration](#configuration)
- [Quick Start](#quick-start)
- [API](#api)
- [Docker](#docker)
- [Tests and Quality](#tests-and-quality)
- [Troubleshooting](#troubleshooting)
- [References](#references)

## Requirements

- Java 17
- Maven 3.8+
- Node.js 20+ and npm
- Docker and Docker Compose (optional)

## Structure

```text
desafio-fullstack/
    backend-module/
    ejb-module/
    frontend/
    db/
    deployment/
    docker-compose.yaml
    docker-compose-ci.yaml
    pom.xml
```

## Configuration

### Backend

`backend-module` uses in-memory H2 by default:

- `server.port=8081`
- `spring.datasource.url=jdbc:h2:mem:beneficiosdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE`

Optional environment variables to override DB connection:

- `DBURL`
- `DBUSER`
- `DBPASSWORD`
- `DBDRIVER`

### Frontend

Development environment points to:

- `http://localhost:8081/api/v1/beneficios`

Local script serves on:

- `http://localhost:4200`

## Quick Start

### Essential local startup

1. run Java build from repository root
2. start backend
3. start frontend
4. validate API and transfer flow

### 1. Build Java modules from root

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack"
mvn clean install
```

### 2. Start backend

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack\backend-module"
mvn spring-boot:run
```

Backend resources:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/api-docs`
- H2 Console: `http://localhost:8081/h2-console`

### 3. Start frontend

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack\frontend"
npm install
npm start
```

Frontend available at `http://localhost:4200`.

### Expected result

- backend available at `http://localhost:8081`
- frontend available at `http://localhost:4200`
- benefits and transfer flow executable end-to-end

## API

Base URL: `http://localhost:8081/api/v1`

- `GET /beneficios`
- `GET /beneficios/{id}`
- `POST /beneficios`
- `PUT /beneficios/{id}`
- `PUT /beneficios/{id}/ativar`
- `PUT /beneficios/{id}/cancelar`
- `DELETE /beneficios/{id}`
- `POST /beneficios/transferir`

Create payload example:

```json
{
    "id": 1,
    "nome": "Beneficio",
    "descricao": "Descricao do beneficio",
    "valor": 100.0,
    "ativo": true
}
```

## Docker

A root `docker-compose.yaml` is available to run backend + frontend.

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack"
docker compose up --build
```

Compose uses environment variables such as:

- `SPRING_LOCAL_PORT`, `SPRING_DOCKER_PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`
- `FRONTEND_LOCAL_PORT`, `FRONTEND_DOCKER_PORT`

## Tests and Quality

Backend:

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack\backend-module"
mvn clean test
```

EJB:

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack\ejb-module"
mvn clean test
```

Frontend:

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack\frontend"
npm run test
npm run lint
```

Backend includes quality plugins such as Jacoco, Checkstyle, PMD, SpotBugs, and Spotless.

## Troubleshooting

- Frontend cannot connect to backend: confirm backend running at `http://localhost:8081`.
- Port conflict: change Angular port in `frontend/package.json` or backend `server.port`.
- Docker Compose variable errors: verify required variables in shell or `.env`.
- Java build failures: verify `java -version` with Java 17.

## References

- roadmap and planning: [plano.md](plano.md)
- executive portfolio backlog: [../votacao-backend/docs/portfolio/executive-backlog.md](../votacao-backend/docs/portfolio/executive-backlog.md)
- challenge comparison matrix: [../votacao-backend/docs/portfolio/challenge-comparison.md](../votacao-backend/docs/portfolio/challenge-comparison.md)
- operational backlog for Jira/GitHub Projects: [../votacao-backend/docs/portfolio/backlog-jira-github-projects.md](../votacao-backend/docs/portfolio/backlog-jira-github-projects.md)
- executive one-page: [../votacao-backend/docs/portfolio/one-page-executiva.md](../votacao-backend/docs/portfolio/one-page-executiva.md)
