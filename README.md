# desafio-fullstack

[Português](README.md) | [English](README.en.md)

Aplicacao fullstack com frontend Angular, backend Spring Boot e modulo EJB para regras de transferencia de beneficios.

## Resumo Executivo

Este repositorio concentra a solucao de beneficios com separacao clara entre interface, API e regras transacionais de transferencia.

Objetivos do projeto:

- oferecer APIs REST para operacoes de beneficios
- manter regras de transferencia no modulo EJB
- integrar frontend e backend com contratos estaveis
- garantir qualidade por testes e padroes de build

Escopo principal:

- `frontend`: SPA Angular para operacoes de beneficios
- `backend-module`: API REST Spring Boot
- `ejb-module`: regras de transferencia
- `db`: scripts de schema e seed

Arquitetura resumida:

Frontend
    |
Backend REST
    |
EJB
    |
Persistencia

## Stack

- Java 17
- Spring Boot 3.2.5
- EJB
- Angular 21
- Maven multi-modulo
- Docker Compose

## Status do Projeto

- arquitetura fullstack estabelecida com separacao por modulo
- build multi-modulo Java e frontend definidos
- roadmap detalhado em [plano.md](plano.md)

## Sumario

- [Resumo Executivo](#resumo-executivo)
- [Stack](#stack)
- [Status do Projeto](#status-do-projeto)
- [Requisitos](#requisitos)
- [Estrutura](#estrutura)
- [Configuracao](#configuracao)
- [Como Rodar Rapido](#como-rodar-rapido)
- [API](#api)
- [Docker](#docker)
- [Testes e Qualidade](#testes-e-qualidade)
- [Troubleshooting](#troubleshooting)
- [Referencias](#referencias)

## Requisitos

- Java 17
- Maven 3.8+
- Node.js 20+ e npm
- Docker e Docker Compose (opcional)

## Estrutura

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

## Configuracao

### Backend

`backend-module` usa H2 em memoria por padrao com:

- `server.port=8081`
- `spring.datasource.url=jdbc:h2:mem:beneficiosdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE`

Variaveis opcionais para sobrescrever conexao:

- `DBURL`
- `DBUSER`
- `DBPASSWORD`
- `DBDRIVER`

### Frontend

Ambiente de desenvolvimento aponta para:

- `http://localhost:8081/api/v1/beneficios`

Script local sobe em:

- `http://localhost:4200`

## Como Rodar Rapido

### Subida local essencial

1. build Java na raiz
2. subir backend
3. subir frontend
4. validar endpoints e fluxo de transferencia

### 1. Build Java na raiz

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack"
mvn clean install
```

### 2. Subir backend

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack\backend-module"
mvn spring-boot:run
```

Recursos do backend:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/api-docs`
- H2 Console: `http://localhost:8081/h2-console`

### 3. Subir frontend

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack\frontend"
npm install
npm start
```

Frontend disponivel em `http://localhost:4200`.

### Resultado esperado

- backend disponivel em `http://localhost:8081`
- frontend disponivel em `http://localhost:4200`
- fluxo de beneficios e transferencia executavel ponta a ponta

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

Exemplo de criacao:

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

Existe `docker-compose.yaml` na raiz para subir backend + frontend.

```powershell
Set-Location "c:\Users\leo_a\projetos\desafio-fullstack"
docker compose up --build
```

O compose usa variaveis de ambiente como:

- `SPRING_LOCAL_PORT`, `SPRING_DOCKER_PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`
- `FRONTEND_LOCAL_PORT`, `FRONTEND_DOCKER_PORT`

## Testes e Qualidade

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

No backend estao configurados plugins como Jacoco, Checkstyle, PMD, SpotBugs e Spotless.

## Troubleshooting

- Frontend nao conecta no backend: confirme backend ativo em `http://localhost:8081`.
- Porta ocupada: ajuste porta do Angular em `frontend/package.json` ou `server.port` no backend.
- Erro de variavel no Docker Compose: confira variaveis obrigatorias no shell ou `.env`.
- Falha de build Java: valide `java -version` com Java 17.

## Referencias

- roadmap e planejamento: [plano.md](plano.md)
- backlog executivo do portfolio: [../votacao-backend/docs/portfolio/backlog-executivo.md](../votacao-backend/docs/portfolio/backlog-executivo.md)
- matriz comparativa dos desafios: [../votacao-backend/docs/portfolio/matriz-comparativa.md](../votacao-backend/docs/portfolio/matriz-comparativa.md)
- backlog operacional Jira/GitHub: [../votacao-backend/docs/portfolio/backlog-jira-github-projects.md](../votacao-backend/docs/portfolio/backlog-jira-github-projects.md)
- one-page executiva: [../votacao-backend/docs/portfolio/one-page-executiva.md](../votacao-backend/docs/portfolio/one-page-executiva.md)