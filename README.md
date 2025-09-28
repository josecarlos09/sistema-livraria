## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.5**
  - Spring MVC
  - Spring Data JPA
  - Spring Security (JWT)
  - Spring Cache (Redis)
- **Banco de Dados:** PostgreSQL
- **Ferramentas de Build:** Maven
- **Documentação:** SpringDoc OpenAPI (Swagger)
- **Testes:** JUnit 5 / Mockito
- **Integrações Externas:** API Open Library (ISBN)
- **Relatórios:** iText (PDF)
- **Infraestrutura:** Docker


## 📂 Estrutura do Projeto

```bash
sistema-livraria/
│── src/main/java/com/livraria
│   ├── config/         # Configurações globais (segurança, paginação, cache, etc.)
│   ├── controllers/    # Controladores REST
│   ├── dto/            # Objetos de transferência de dados (DTOs)
│   ├── exceptions/     # Tratamento global de erros
│   ├── models/         # Entidades JPA
│   ├── repositories/   # Interfaces do Spring Data JPA
│   ├── services/       # Regras de negócio
│   ├── utils/          # Utilitários
│── src/main/resources/
│   ├── application.yml # Configurações da aplicação
│── pom.xml

#Funcionalidades

- ✅ Cadastro, atualização, exclusão e listagem de livros  
- ✅ Controle de autores e editoras  
- ✅ Integração com API **Open Library** para busca de livros via ISBN  
- ✅ Relatórios em PDF agrupados por **valor**, **autor** e **editora**  
- ✅ Sistema de autenticação e autorização com **JWT**  
- ✅ Perfis de usuário (**ADMIN** e **USER**) com permissões diferentes  
- ✅ Paginação e filtros dinâmicos  
- ✅ Cache de consultas frequentes com **Redis**  
- ✅ Tratamento de exceções a nível global  

#👥 Perfis de Usuário

- **ADMIN** → Acesso total ao sistema  
- **USER** → Acesso restrito a consultas  


# 📌 Requisitos

- JDK 21+  
- Maven 3.9+  
- PostgreSQL 15+  
- Docker (opcional)  


# 🛡️ Segurança

- Autenticação via **JWT**  
- Senhas armazenadas com **BCrypt**  
- Controle de permissões por perfil de usuário  


# 📊 Roadmap

- [x] CRUD de livros, autores e editoras  
- [x] Autenticação e autorização com JWT  
- [x] Integração com Open Library API  
- [x] Relatórios em PDF  
- [x] Paginação global  
- [x] Cache com Redis  
- [ ] Deploy em ambiente Cloud  
- [ ] Implementar microsserviços
