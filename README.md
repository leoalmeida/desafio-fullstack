> **Aplicação Angular 21 que utiliza APIs criadas pela aplicação Spring Boot backend-module e utiliza biblioteca ejb-module.**

# 🅰️ Sobre este repositório

Essa solução possui as seguintes funcionalidades implementadas:

- Aplicação web SPA de cadastro de beneficios
- Micro serviço de cadastro de beneficios
- Biblioteca EJB Legada

A solução disponibiliza ao usuário as seguintes operações:

- Cadastro de novo beneficio
- Consulta de beneficios cadastrados
- Desabilitar/Habilitar um beneficio
- Remover um beneficio
- Alterar dados de um beneficio

### Descrição das funcionalidades

A seguir serão detalhadas as tela implementadas pela aplicação, assim como os endpoints utilizados e os tipos de informações disponíveis para a interação do usuário.

### Tipo de tela – SELECAO

A tela de Seleção exibe uma lista dos benefícios existentes para seleção do usuário. 

O aplicativo envia uma requisição GET para o endpoint de beneficios e espera o retorno de uma lista dos benefícios já cadastrados, criando um card para benefício da lista.

```
GET /api/v1/beneficios
```

### Tipo de tela – FORMULARIO

A tela de Formulário exibe as informações do benefício (ou vazio no caso de um benefício novo) que estão disponíveis para modificação pelo usuário e possui os botões de ação para Cancelar, Salvar ou Excluir na parte inferior.

OBS: A ação de excluir ficará disponível apenas quando um benefício existente for selecionado.

Ao cancelar, o aplicativo fechar a tela de formulário, descarta as informações preenchidas e redireciona o usuário para a tela de Seleção.

Ao salvar um novo benefício, o aplicativo envia uma requisição POST para o endpoint de beneficios com o body definido pelo objeto criado a partir do formulário preenchido pelo usuário. Os valores informados são adicionados ao corpo da requisição. Segue exemplo de requisição que o aplicativo irá submeter fazer quando o botão “Salvar” for acionado:

```
POST /api/v1/beneficios
{
    "id":1,
    "nome":"Benefício",
    "descricao":"Descrição do benefício",
    "valor":100.00,
    "ativo":true
}
```

Ao salvar um benefício existente, o aplicativo envia uma requisição PUT para o endpoint de beneficios com o body definido pelo objeto atualizado a partir do formulário modificado pelo usuário. Os valores informados são adicionados ao corpo da requisição e o identificador anexado a URI. Segue exemplo de requisição que o aplicativo irá submeter quando o botão “Salvar” for acionado:

```
PUT /api/v1/beneficios/1
{
    "id":1,
    "nome":"Benefício Alterado",
    "descricao":"Descrição do benefício Alterado",
    "valor":200.00,
    "ativo":true
}
```

Ao excluir um benefício existente, o aplicativo envia uma requisição DELETE para o endpoint de beneficios passando o identificador do benefício na URI. Segue exemplo de requisição que o aplicativo irá submeter quando o botão “Excluir” for acionado:

```
DELETE /api/v1/beneficios/1
```

# Direcionamentos para rodar a aplicação

Inicialmente realizar o clone do projeto.

## Procedimentos Bibliotece EJB

## Procedimentos MicroServiço de beneficio
Antes de realizar o deploy acessar o arquivo `application.properties` e alterar o endereco do banco de dados.

O banco de dados utilizado foi o H2.
Incluir as configurações abaixo com as informações de conexão com o banco de dados:

 `spring.datasource.url=jdbc:h2:mem:testdb`

 `spring.datasource.username=sa`

 `spring.datasource.password=sa`

Com isso feito, basta executar o comando abaixo para subir o microserviço.

 `mvn spring-boot:run`

Com a aplicação rodando, a documentação da API pode ser acessada utilizando o endereço http://localhost:8080/swagger-ui.html para visualização e teste dos endpoints.

## Procedimentos Aplicação Angular