# Plano de Trabalho - desafio-fullstack

## 1. Objetivo

Entregar e evoluir a solucao fullstack de beneficios, garantindo:

- fluxo funcional completo entre frontend Angular e backend Spring Boot
- regras de transferencia encapsuladas no modulo EJB
- API REST documentada e testavel
- qualidade continua por testes automatizados e pipeline

## 2. Escopo Atual do Repositorio

Modulos e diretorios principais:

- `frontend`
- `backend-module`
- `ejb-module`
- `db`
- `deployment`

Tecnologias base:

- Java 17
- Spring Boot 3.2.5
- EJB
- Angular 21
- H2 (padrao local) e suporte a banco externo por variaveis
- Docker Compose
- Maven multi-modulo

## 3. Arquitetura e Decisoes

Fluxo macro:

Frontend
   |
Backend REST (`backend-module`)
   |
Servico EJB (`ejb-module`)
   |
Persistencia

Decisoes obrigatorias para novas features:

- contrato OpenAPI primeiro para endpoints publicos
- regras de transferencia centralizadas no modulo EJB
- tratamento de erro padronizado entre backend e frontend
- scripts de banco mantidos em `db/` para reprodutibilidade

## 4. Estrategia de Desenvolvimento

Modelo de execucao: vertical slices por capacidade de negocio.

Cada slice deve incluir:

1. modelagem de dominio
2. persistencia (`repository`)
3. regra de negocio (`service`/EJB)
4. API (`controller` + DTOs + validacoes)
5. integracao frontend
6. testes unitarios
7. testes de integracao

## 5. Fases de Entrega

### Fase 1 - Foundation

Objetivo: estabilizar ambiente local e convencoes.

Entregaveis:

- build completo dos modulos Java na raiz
- configuracao local de backend e frontend revisada
- compose validado para execucao integrada
- documentacao de setup atualizada

Criterios de aceite:

- `mvn clean install` concluido na raiz
- frontend e backend iniciam localmente com configuracao documentada

### Fase 2 - Dominio de Beneficios

Objetivo: consolidar casos de uso core da API.

Entregaveis minimos:

- CRUD de beneficios funcional
- ativacao e cancelamento por endpoints dedicados
- transferencia de beneficios via regra EJB

Criterios de aceite:

- fluxos core respondem conforme contrato
- regras de negocio criticas cobertas por testes

### Fase 3 - Integracao Fullstack e Contratos

Objetivo: estabilizar integracao entre camadas.

Entregaveis:

- frontend integrado aos endpoints principais
- contrato OpenAPI atualizado
- tratamento consistente de erros e estados de tela
- testes de integracao backend + EJB

Criterios de aceite:

- fluxo ponta a ponta de beneficio e transferencia funcionando
- regressao de contrato evitada por testes e validacao

### Fase 4 - Qualidade e Release Readiness

Objetivo: elevar robustez para demonstracao e avaliacao.

Entregaveis:

- cobertura e qualidade estatica consolidadas
- troubleshooting operacional revisado
- validacao de imagem/compose para subida reproduzivel

Criterios de aceite:

- pipeline verde
- documentacao suficiente para avaliacao tecnica

## 6. Estrategia de Testes

### 6.1 Unitarios

Ferramentas:

- JUnit 5
- Mockito

Escopo:

- regras de negocio do backend
- regras de transferencia do EJB
- validacoes de entrada

### 6.2 Integracao

Ferramentas:

- Spring Boot Test
- testes de integracao entre `backend-module` e `ejb-module`

Escopo:

- endpoints principais
- persistencia e transacoes de transferencia
- tratamento de erro em fluxos criticos

### 6.3 Frontend

Ferramentas:

- testes e lint do projeto Angular

Escopo:

- componentes e servicos de consulta/manutencao
- estados de carregamento, erro e sucesso

## 7. CI/CD e Releases

Pipeline minimo:

1. build Java
2. testes backend
3. testes frontend
4. cobertura e relatorios de qualidade
5. build de imagem Docker

Ambientes-alvo:

- local
- dev
- homologacao
- prod

## 8. Roadmap Sugerido (4 Sprints)

Premissa de conversao para planejamento inicial:

- 2 pontos = 1 dia-pessoa

### Sprint 1

Estimativa: 16 a 24 pontos

Equivalente: 8 a 12 dias-pessoa

- setup local, build multi-modulo e configuracoes
- revisao de contratos base e estrutura frontend/backend

### Sprint 2

Estimativa: 20 a 30 pontos

Equivalente: 10 a 15 dias-pessoa

- consolidacao do CRUD e operacoes de estado
- implementacao/ajuste da transferencia via EJB

### Sprint 3

Estimativa: 16 a 24 pontos

Equivalente: 8 a 12 dias-pessoa

- integracao frontend com fluxos core
- hardening de contratos e erros

### Sprint 4

Estimativa: 12 a 20 pontos

Equivalente: 6 a 10 dias-pessoa

- observabilidade, qualidade e readiness de entrega

## 9. Backlog por Servico

Padrao de identificacao das historias:

- `PROJETO-SERVICO-NNN`
- `PROJETO`: `VTB`, `MAG`, `BTG` ou `FST`
- `SERVICO`: codigo curto do modulo ou contexto
- `NNN`: sequencial de tres digitos

### `backend-module`

Historia `FST-BE-001`

- como operador, quero manter beneficios por API para operar o ciclo de vida completo do cadastro

Criterios de aceite:

- endpoints de criar, consultar, atualizar e remover funcionam conforme contrato
- validacoes de entrada bloqueiam dados inconsistentes

Historia `FST-BE-002`

- como operador, quero ativar e cancelar beneficios para refletir seu estado operacional

Criterios de aceite:

- endpoints de ativacao e cancelamento atualizam estado corretamente
- erros de transicao invalida retornam resposta consistente

### `ejb-module`

Historia `FST-EJB-001`

- como negocio, quero executar transferencia de beneficios com regras centralizadas para preservar consistencia

Criterios de aceite:

- regra de transferencia e aplicada no modulo EJB
- cenarios invalidos retornam erro previsivel e testado

### `frontend`

Historia `FST-FE-001`

- como usuario, quero consultar e manter beneficios na interface web

Criterios de aceite:

- telas cobrem operacoes core da API
- estados de carregamento, erro e sucesso sao exibidos corretamente

Historia `FST-FE-002`

- como usuario, quero acionar transferencia pela interface para concluir o fluxo de negocio ponta a ponta

Criterios de aceite:

- acao de transferencia consome endpoint correto e exibe resultado
- erros de integracao nao quebram o fluxo principal

## 10. RACI Simplificada

| Atividade | Backend | Frontend | QA | DevOps |
| --- | --- | --- | --- | --- |
| API, dominio e regras EJB | A/R | I | C | I |
| Integracao de interface com APIs | C | A/R | C | I |
| Estrategia e execucao de testes | R | R | A | C |
| Build, compose e pipeline | C | I | C | A/R |
| Readiness de release e troubleshooting | R | C | C | A/R |

## 11. Priorizacao MoSCoW

### Must Have

- CRUD de beneficios
- ativar/cancelar beneficio
- transferencia via EJB
- integracao frontend com fluxos principais
- testes essenciais e pipeline verde

### Should Have

- padronizacao robusta de erros
- melhor cobertura de cenarios de transferencia
- documentação operacional detalhada

### Could Have

- melhorias de UX e filtros avancados
- suites adicionais de regressao automatica

### Won't Have (nesta fase)

- IAM corporativo completo
- orquestracao Kubernetes

## 12. Estimativa por Frente

- foundation e setup: 10 a 16 pontos = 5 a 8 dias-pessoa
- backend e dominio: 16 a 24 pontos = 8 a 12 dias-pessoa
- regras EJB: 8 a 14 pontos = 4 a 7 dias-pessoa
- frontend e integracao: 10 a 18 pontos = 5 a 9 dias-pessoa
- qualidade e readiness: 8 a 12 pontos = 4 a 6 dias-pessoa

## 13. Definition of Done

- testes passando
- contrato OpenAPI atualizado
- cobertura dentro do minimo acordado
- build/compose reproduzivel
- documentacao atualizada

## 14. Riscos e Mitigacoes

Riscos principais:

- divergencia entre regras no backend e no EJB
- regressao na integracao frontend/backend
- ambiente local inconsistente em Docker

Mitigacoes:

- testes de integracao cobrindo transferencia
- validacao de contrato e cenarios criticos em CI
- padronizacao de variaveis e guias de execucao local