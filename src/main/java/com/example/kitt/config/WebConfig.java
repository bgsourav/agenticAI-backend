package com.example.kitt.config;

import com.example.kitt.config.properties.ExternalApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class WebConfig {

    @Autowired
    private ExternalApiProperties externalApiProperties;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(externalApiProperties.getBaseUrl())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }
}