# desafio-fullstack

[Português](README.md) | [English](README.en.md)

![Angular](https://img.shields.io/badge/angular-21-red)
![Spring Boot](https://img.shields.io/badge/spring--boot-3.2.5-6DB33F)
![Java](https://img.shields.io/badge/java-17-orange)
![Status](https://img.shields.io/badge/status-active-brightgreen)

Fullstack application with Angular frontend, Spring Boot backend, and an EJB module for benefit transfer business rules.

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Structure](#structure)
- [Configuration](#configuration)
- [How to Run](#how-to-run)
- [API](#api)
- [Docker](#docker)
- [Tests and Quality](#tests-and-quality)
- [Troubleshooting](#troubleshooting)

## Overview

Solution layers:

- `frontend`: Angular 21 SPA for benefit operations
- `backend-module`: Spring Boot REST API (port `8081`)
- `ejb-module`: EJB module with transfer rules

Supporting folders:

- `db`: database scripts (`schema.sql` and `seed.sql`)
- `deployment`: deployment support files

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

## How to Run

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
