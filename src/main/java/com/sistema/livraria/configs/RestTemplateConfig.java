package com.sistema.livraria.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração para a criação de um bean do RestTemplate.
 *
 * O RestTemplate é uma classe fornecida pelo Spring que facilita a comunicação com APIs externas
 * por meio de requisições HTTP, sendo amplamente utilizada em integrações com serviços RESTful.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Criação do bean RestTemplate para ser utilizado em toda a aplicação.
     *
     * O RestTemplate permite realizar chamadas HTTP, tanto GET quanto POST, PUT, DELETE, etc.,
     * facilitando o consumo de serviços externos.
     *
     * @return Uma instância de RestTemplate configurada.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}