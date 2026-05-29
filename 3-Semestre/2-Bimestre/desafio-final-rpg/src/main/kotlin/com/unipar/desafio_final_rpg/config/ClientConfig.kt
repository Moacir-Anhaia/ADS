package com.unipar.desafio_final_rpg.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

// @Configuration indica ao Spring que essa classe é uma fonte de configurações
@Configuration
class ClientConfig {

    @Bean
    fun restClient(): RestClient {
        // RestClient.builder() oferece uma API fluente
        return RestClient.builder()
            // defaultHeader define um header enviado automaticamente em TODAS as requisições
            .defaultHeader("Accept", MediaType.TEXT_PLAIN_VALUE)
            // build() finaliza a configuração e cria a instância do RestClient
            .build()
    }
}