package com.ballastlane.pokemon.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final int POKE_API_MAX_IN_MEMORY_SIZE = 2 * 1024 * 1024;

    @Bean
    WebClient pokeApiWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://pokeapi.co/api/v2")
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(POKE_API_MAX_IN_MEMORY_SIZE))
                .build();
    }
}
