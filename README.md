# Sistema de Gerenciamento Acadêmico

## 📚 Descrição do Projeto

Este projeto é um ecossistema completo para o gerenciamento de alunos, cursos e matrículas de uma instituição de ensino.

A aplicação foi desenvolvida focando em alta performance e experiência de usuário reativa, dividindo-se em duas camadas independentes:

* **Backend API**: API construída sobre a JVM, responsável pelas regras de negócio, autenticação via tokens e persistência dos dados acadêmicos.
* **Frontend SPA**: Portal web moderno, responsivo e baseado em componentes reativos para administração da plataforma.

Todo o projeto foi conteinerizado, permitindo que o ecossistema completo seja iniciado com um único comando Docker.

---

# 🔧 Visão Geral da Arquitetura

## Backend Service (API) — Porta 8081

Responsabilidades:

* Gerenciamento completo de usuários, estudantes, cursos e matrículas.
* Autenticação e autorização utilizando BCrypt e JWT.
* Barramento reativo de alta performance com Vert.x Router.
* Controle de CORS.
* Persistência de dados através de ORM reativo.
* Banco de dados relacional em memória.

### Tecnologias

* Kotlin
* Quarkus 3.36.x
* RESTEasy Reactive
* Jackson
* Hibernate ORM com Panache Kotlin
* H2 Database
* SmallRye JWT
* Eclipse MicroProfile JWT
* Vert.x Web

---

## Frontend Service (Portal Web) — Porta 4200

Responsabilidades:

* Interface SPA moderna e responsiva.
* Consumo assíncrono dos endpoints da API.
* Gerenciamento reativo de estado.
* Distribuição dos arquivos compilados via Nginx.

### Tecnologias

* Angular 17+
* Nginx Alpine

---

# ⚙️ Funcionalidades Atuais

## Gerenciamento Acadêmico

### Painel de Cursos

* Cadastro de cursos.
* Consulta de cursos.
* Atualização de cursos.
* Remoção lógica (Soft Delete).
* Controle de carga horária.

### Painel de Alunos

* Cadastro de alunos.
* Consulta de alunos.
* Atualização de dados.
* Remoção lógica (Soft Delete).
* Validações de integridade.

### Módulo de Matrículas

* Matrícula de alunos em cursos.
* Desmatrícula de alunos.
* Consulta de alunos vinculados.
* Atualização imediata dos relacionamentos.

---

## Segurança e Infraestrutura

### Autenticação

* Login baseado em JWT.
* Senhas protegidas com BCrypt.

### CORS

* Filtro customizado utilizando Vert.x.

### Inicialização de Dados

* Banco populado automaticamente através do arquivo `import.sql`.

---

# 🛠️ Tecnologias Utilizadas

## Backend

| Tecnologia            | Finalidade             |
| --------------------- | ---------------------- |
| Kotlin                | Linguagem principal    |
| Quarkus               | Framework Backend      |
| RESTEasy Reactive     | API REST               |
| Jackson               | Serialização JSON      |
| Hibernate ORM Panache | Persistência           |
| H2 Database           | Banco de Dados         |
| JWT                   | Autenticação           |
| BCrypt                | Criptografia de Senhas |
| Vert.x Web            | Camada Reativa         |

---

## Frontend

| Tecnologia     | Finalidade          |
| -------------- | ------------------- |
| Angular 17+    | SPA                 |
| TypeScript     | Linguagem Frontend  |
| RxJS           | Programação Reativa |
| Angular Router | Navegação           |
| Angular Forms  | Formulários         |

---

## Infraestrutura

| Tecnologia     | Finalidade         |
| -------------- | ------------------ |
| Docker         | Conteinerização    |
| Docker Compose | Orquestração       |
| Nginx Alpine   | Servidor Web       |
| Git            | Controle de Versão |

---

# 🔧 Pré-requisitos

Para executar, debugar e evoluir o projeto localmente, é recomendado possuir os seguintes componentes instalados.

## Containers e Orquestração

* Docker Desktop 24+
* Docker Engine
* Docker Compose v2

## Ambiente Backend

### Linguagens e SDKs

* Java Development Kit (JDK) 21
* Kotlin SDK 1.9+

### Build e Dependências

* Apache Maven 3.9+

### IDEs Recomendadas

* IntelliJ IDEA Ultimate ou Community
* Visual Studio Code

## Ambiente Frontend

### Runtime

* Node.js 20.x (LTS)
* npm 10.x

### Ferramentas

* Angular CLI 17+

Instalação:

```bash
npm install -g @angular/cli
```

## Ferramentas de Desenvolvimento

* Git
* Postman
* Insomnia

---

# 🚀 Inicialização do Projeto

## 1. Clonar o Repositório

```bash
git clone https://github.com/felipemachadovidal/Desafio-Unifor-Desenvolvedor-Junior

cd desafio-unifor
```

---

## 2. Verificar Configurações

O projeto já possui:

* Variáveis de ambiente configuradas.
* Chaves JWT configuradas.
* Docker Compose preparado para execução local.

---

## 3. Construir e Iniciar os Containers

Na raiz do projeto execute:

```bash
docker compose up --build
```

O processo realizará automaticamente:

* Download das imagens.
* Build do Backend Kotlin.
* Build do Frontend Angular.
* Criação das redes Docker.
* Inicialização dos serviços.

---

## 4. Verificar Containers

Abra outro terminal:

```bash
docker ps
```

Containers esperados:

| Container               | Porta |
| ----------------------- | ----- |
| desafio-unifor-backend  | 8081  |
| desafio-unifor-frontend | 4200  |

---

## 5. Acessar a Aplicação

Frontend:

```text
http://localhost:4200
```

---

## Credenciais Padrão

```text
Usuário: admin@unifor.br
Senha: admin123
```

---

# 🌐 API REST

Todas as rotas estão disponíveis sob o prefixo:

```text
/api
```

---

# 🔐 Autenticação

| Método | Endpoint        | Descrição                   |
| ------ | --------------- | --------------------------- |
| POST   | /api/auth/login | Realiza login e retorna JWT |

---

# 📘 Cursos

| Método | Endpoint                                     | Descrição           |
| ------ | -------------------------------------------- | ------------------- |
| GET    | /api/courses                                 | Lista cursos ativos |
| GET    | /api/courses/{id}                            | Busca curso por ID  |
| POST   | /api/courses                                 | Cria curso          |
| PUT    | /api/courses/{id}                            | Atualiza curso      |
| DELETE | /api/courses/{id}                            | Soft Delete         |
| POST   | /api/courses/{courseId}/enroll/{studentId}   | Matricular aluno    |
| GET    | /api/courses/{courseId}/students             | Listar matriculados |
| DELETE | /api/courses/{courseId}/unenroll/{studentId} | Remover matrícula   |

---

# 👨‍🎓 Alunos

| Método | Endpoint                     | Descrição           |
| ------ | ---------------------------- | ------------------- |
| GET    | /api/students                | Lista alunos        |
| GET    | /api/students/{id}           | Busca aluno         |
| POST   | /api/students                | Cadastra aluno      |
| PUT    | /api/students/{id}           | Atualiza aluno      |
| DELETE | /api/students/{id}           | Soft Delete         |
| DELETE | /api/students/{id}/permanent | Exclusão permanente |

---

# 🚀 Roadmap de Evolução

## Cadastro de Usuários

* Endpoint público de cadastro.
* Fluxo de criação de conta.
* Geração automática de credenciais.

---

## Migração para MariaDB

Substituição do H2 por:

* MariaDB
* Persistência real dos dados
* Volume Docker

---

## Validação de CPF

Implementar:

* Validação de formato.
* Validação dos dígitos verificadores.
* Rejeição de CPFs inválidos.

---

## Perfil Professor (ROLE_TEACHER)

Novo modelo de permissões:

### ADMIN

* Gerencia cursos.
* Gerencia usuários.
* Gerencia matrículas.

### PROFESSOR

* Gerencia turmas.
* Lança notas.
* Controla frequência.

### ALUNO

* Consulta dados acadêmicos.

---

## Integração com ViaCEP

Preenchimento automático de:

* Logradouro
* Bairro
* Cidade
* Estado

---

## Auditoria com Hibernate Envers

Registrar:

* Inclusões
* Alterações
* Exclusões

Mantendo histórico completo das operações.

---

## OpenAPI e Swagger

Documentação automática da API:

* Swagger UI
* Contratos JSON
* Testes interativos

---

# 📖 Documentação e Referências

## Quarkus

https://quarkus.io/guides/

## Angular

https://angular.dev

## Hibernate ORM Panache Kotlin

https://quarkus.io/guides/hibernate-orm-panache-kotlin

## Nginx para SPA

https://www.nginx.com/resources/wiki/

---

# 👥 Desafios técnicos

O maior desafio foi o tempo, não tive tempo de implementar tudo que eu queria então decidir simplificar a criação de usuários e banco de dados para ser o mais ágil sem comprometer os testes.

---

# 📄 Licença

Este projeto possui finalidade educacional e avaliativa.
