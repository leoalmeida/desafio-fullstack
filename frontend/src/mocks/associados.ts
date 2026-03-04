import { AssociadoType } from "src/app/models/associado-type";

export const associados:AssociadoType[] = [
  {
    "id": 1,
    "email": "jrrtolk@teste.com",
    "nome": "J.R.R. Tolkien",
    "telefone": "99999",
    "username": "jrrtolk",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwic3ViIjoiMzIzMjEyMTIxIiwidXNlcm5hbWUiOiJqcnJ0b2xrIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sInBlcm1pc3Npb25zIjpbIlJFQUQiLCJXUklURSJdLCJleHAiOjF9.8sJuOv7jrls5DJOIR2ReuKoBHpl8ffvD1gTYbJEv30Q",
    "stats": [
      { "title": "Benefícios Ativos", "value": 5 },
      { "title": "Benefícios Cancelados", "value": 2 },
      { "title": "Benefícios Pendentes", "value": 1 }
    ],
    "logs": [
      "Usuário jrrtolk fez login.",
      "Usuário jrrtolk visualizou o benefício 'Plano de Saúde'.", 
    ]
  },
  {
    "id": 2,
    "email": "1984@teste.com",
    "nome": "George Orwell",
    "telefone": "11111",
    "username": "george.orwell",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Miwic3ViIjoiMTIzNDU2Nzg5MCIsInVzZXJuYW1lIjoiZ2VvcmdlLm9yd2VsbCIsImlhdCI6MTUxNjIzOTAyMiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sInBlcm1pc3Npb25zIjpbIlJFQUQiXSwiZXhwIjoxfQ.sRBjEdUjaO0qzMLSH1W4OIJ6XdvccwYhKfTG-ythDAk",
    "stats": [
      { "title": "Benefícios Ativos", "value": 3 },
      { "title": "Benefícios Cancelados", "value": 1 },
      { "title": "Benefícios Pendentes", "value": 2 }
    ],
    "logs": [
      "Usuário georgeorwell fez login.",
      "Usuário georgeorwell visualizou o benefício 'Plano de Saúde'."
    ]
  },
  {
    "id": 3,
    "email": "jane@teste.com",
    "nome": "Jane Austen",
    "telefone": "55555",
    "username": "jane.austen",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkphbmUgQXVzdGVuIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiaWQiOjMsInVzZXJuYW1lIjoiamFuZUB0ZXN0ZS5jb20iLCJyb2xlcyI6WyJVU0VSIl0sInBlcm1pc3Npb25zIjpbIkZVTEwiXX0.TxL-jwXmqMrY9B60-Fe7IggLILC1n1yeH9aiNqpr_SY",
    "stats": [
      { "title": "Benefícios Ativos", "value": 4 },
      { "title": "Benefícios Cancelados", "value": 0 },
      { "title": "Benefícios Pendentes", "value": 1 }
    ],
    "logs": [
      "Usuário jane fez login.",
      "Usuário jane visualizou o benefício 'Plano de Saúde'."
    ]
  },
  {
    "id": 4,
    "email": "willian@teste.com",
    "nome": "William Gibson",
    "telefone": "4444",
    "username": "william.gibson",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IldpbGxpYW0gR2lic29uIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiaWQiOjQsInVzZXJuYW1lIjoid2lsbGlhbkB0ZXN0ZS5jb20iLCJyb2xlcyI6WyJVU0VSIl0sInBlcm1pc3Npb25zIjpbIkZVTEwiXX0.5tPzsrO8KeIljFC64tItjIa1EasQlb5lwtmAnu4esCc", 
    "stats": [
      { "title": "Benefícios Ativos", "value": 2 },
      { "title": "Benefícios Cancelados", "value": 3 },
      { "title": "Benefícios Pendentes", "value": 0 }
    ],
    "logs": [
      "Usuário william fez login.",
      "Usuário william visualizou o benefício 'Plano de Saúde'."
    ]
  },
  {
    "id": 5,
    "email": "teste1@teste.com",
    "nome": "Teste Gibson",
    "telefone": "333333",
    "username": "teste",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IldpbGxpYW0gR2lic29uIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiaWQiOjQsInVzZXJuYW1lIjoid2lsbGlhbkB0ZXN0ZS5jb20iLCJyb2xlcyI6WyJVU0VSIl0sInBlcm1pc3Npb25zIjpbIkZVTEwiXX0.5tPzsrO8KeIljFC64tItjIa1EasQlb5lwtmAnu4esCc", 
    "stats": [
      { "title": "Benefícios Ativos", "value": 2 },
      { "title": "Benefícios Cancelados", "value": 3 },
      { "title": "Benefícios Pendentes", "value": 0 }
    ],
    "logs": [
      "Usuário william fez login.",
      "Usuário william visualizou o benefício 'Plano de Saúde'."
    ]
  }
];
