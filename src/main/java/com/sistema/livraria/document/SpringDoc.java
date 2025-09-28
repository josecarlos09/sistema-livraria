package com.sistema.livraria.document;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDoc {

    /**
     * Configura a documentação da API utilizando o SpringDoc e OpenAPI.
     * Define esquemas de segurança para autenticação via JWT e informações gerais da API.
     *
     * @return Instância de OpenAPI configurada.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key", // Nome do esquema de segurança
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP) // Define o tipo como HTTP
                                        .scheme("bearer") // Utiliza o esquema Bearer Token
                                        .bearerFormat("JWT"))) // Formato do token JWT
                // http://localhost:8087/livraria/swagger-ui/swagger-ui/index.html#/
                // http://localhost:8087/livraria/v3/api-docs -> caminhos dos endpoints
                .info(new Info()
                        .title("API de Livraria") // Título da API
                        .description("A API de Livraria oferece funcionalidades para gerenciar livros, autores, editoras e resumos, permitindo operações como cadastro, consulta, atualização e remoção de livros. A API facilita a busca por ISBN e a interação com dados da livraria de forma eficiente e organizada.") // Descrição da API
                        .contact(new Contact()
                                .name("Time de Desenvolvimento de Livraria") // Nome da equipe responsável
                                .email("suporte@livrariasistema.com")) // E-mail de contato
                        .license(new License()
                                .name("Apache 2.0") // Nome da licença
                                .url("http://livrariasistema.com/api/licenca"))); // URL da licença
    }
}