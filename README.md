> Aplicação Angular 21 que consome APIs do módulo Spring Boot (`backend-module`) e integra com biblioteca EJB legada (`ejb-module`).

# 🅰️ Sobre este repositório

Este projeto é uma solução fullstack com três camadas principais:

- `frontend`: SPA Angular para cadastro e gestão de benefícios
- `backend-module`: API REST em Spring Boot (Java 17)
- `ejb-module`: biblioteca EJB com regras de negócio de transferência

Também inclui:

- `db`: scripts SQL (`schema.sql` e `seed.sql`)
- `docs`: materiais complementares do desafio

## Funcionalidades

- Cadastro de novo benefício
- Consulta de benefícios cadastrados
- Alteração de dados de benefício
- Ativação/Cancelamento de benefício
- Remoção de benefício
- Transferência entre benefícios

## Fluxo da aplicação (frontend)

### Tela de Seleção

Exibe a lista de benefícios existentes para seleção do usuário.

Requisição principal:

```http
GET /api/v1/beneficios
```

### Tela de Formulário

Exibe os dados de um benefício existente (ou vazio para novo cadastro) com ações de **Cancelar**, **Salvar** e **Excluir**.

- **Cancelar**: descarta alterações e retorna à tela de seleção
- **Salvar novo**: envia `POST /api/v1/beneficios`
- **Realizar transferencia**: envia `POST /api/v1/beneficios/transferir`
- **Salvar existente**: envia `PUT /api/v1/beneficios/{id}`
- **Excluir**: envia `DELETE /api/v1/beneficios/{id}`

Exemplo de criação:

```http
POST /api/v1/beneficios
Content-Type: application/json

{
    "id": 1,
    "nome": "Benefício",
    "descricao": "Descrição do benefício",
    "valor": 100.0,
    "ativo": true
}
```
Exemplo de transferência:

```http
POST /api/v1/beneficios/transferir
Content-Type: application/json

{
    "fromId": 1,
    "toId": 2,
    "valor": 50.0
}
```

Exemplo de atualização:

```http
PUT /api/v1/beneficios/1
Content-Type: application/json

{
    "id": 1,
    "nome": "Benefício Alterado",
    "descricao": "Descrição do benefício Alterado",
    "valor": 200.0,
    "ativo": true
}
```

Exemplo de exclusão:

```http
DELETE /api/v1/beneficios/1
```

---

# 🚀 Como executar o projeto

## Pré-requisitos

- Java 17
- Maven 3.8+
- Node.js 20+ e npm
- Docker e Docker Compose (opcional)

## 1) Clonar o projeto

```bash
git clone <url-do-repositorio>
cd bip-teste-integrado
```

## 2) Build dos módulos Java (raiz)

Na raiz, execute:

```bash
mvn clean install
```

Esse comando compila e instala `ejb-module` e `backend-module`.

## 3) Rodar o backend (`backend-module`)

```bash
cd backend-module
mvn spring-boot:run
```

Configuração padrão (`application.properties`):

- Porta da API: `8081`
- Banco: H2 em memória
- URL H2 padrão: `jdbc:h2:mem:beneficiosdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE`

Também é possível sobrescrever via variáveis de ambiente:

- `DBURL`
- `DBUSER`
- `DBPASSWORD`
- `DBDRIVER`

Recursos do backend:

- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api-docs
- Console H2: http://localhost:8081/h2-console

> Observação: o backend inicializa automaticamente schema e seed via `classpath:schema.sql` e `classpath:seed.sql`.

## 4) Rodar o frontend (`frontend`)

```bash
cd frontend
npm install
npm start
```

Aplicação disponível em: http://localhost:3001

Ambientes Angular:

- `environment.ts` (dev): API em `http://localhost:8081/api/v1/beneficios`
- `environment-prod.ts` (prod): API em `http://localhost:8080/api/v1/beneficios`

---

# 🧱 Módulos

## `ejb-module`

- Empacotamento: `ejb`
- Responsável pela regra de transferência entre benefícios
- Java 17

Build/teste:

```bash
cd ejb-module
mvn clean test
```

## `backend-module`

- Spring Boot 3.2.5
- Expõe endpoints REST de benefícios
- Integra com `ejb-module`

Build/teste:

```bash
cd backend-module
mvn clean test
```

## `frontend`

- Angular 21
- Angular Material

Comandos úteis:

```bash
npm start
npm run build
npm run test
npm run lint
```

---

# 🔌 Endpoints principais

Base URL: `http://localhost:8081/api/v1`

- `GET /beneficios` → listar benefícios
- `GET /beneficios/{id}` → buscar benefício por ID
- `POST /beneficios` → criar benefício
- `PUT /beneficios/{id}` → atualizar benefício
- `PUT /beneficios/{id}/ativar` → ativar benefício
- `PUT /beneficios/{id}/cancelar` → cancelar benefício
- `DELETE /beneficios/{id}` → remover benefício
- `POST /beneficios/transferir` → transferir entre benefícios
- `GET /home/` → endpoint de verificação simples

---

# 🐳 Execução com Docker (opcional)

Existe `Dockerfile` para subir o backend e o frontend com Maven (`spring-boot:run` e `npm run start`).

Backend:

```bash
docker build -t backend-module .
docker run --rm -p 8081:8081 backend-module
```

Frontend:

```bash
docker build -t frontend .
docker run --rm -p 80:3001 frontend
```

> Observação: o arquivo `docker-compose.yaml` atual contém placeholders de variáveis de ambiente que podem ser configurados criando um arquivo .env nas pastas "./backend-module" e "./frontend" antes do uso local.

---

# ✅ Testes e qualidade

## Backend

```bash
cd backend-module
mvn clean test
```

## EJB

```bash
cd ejb-module
mvn clean test
```

## Frontend

```bash
cd frontend
npm run test
```

No backend também há plugins de qualidade como Checkstyle, PMD, SpotBugs, Jacoco e Spotless configurados no Maven.

---

# 🛠️ Troubleshooting rápido

- **Frontend não conecta no backend**: confirme backend em `http://localhost:8081`
- **Erro de CORS**: valide origem permitida e porta usada no frontend
- **Porta ocupada**: altere `server.port` no backend ou porta do Angular no script `start`
- **Falha no Maven por Java**: valide `java -version` com Java 17