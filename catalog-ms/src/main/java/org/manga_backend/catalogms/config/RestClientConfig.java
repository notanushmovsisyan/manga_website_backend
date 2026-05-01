package org.manga_backend.catalogms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient jikanRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.jikan.moe/v4")
                .build();
    }
}
