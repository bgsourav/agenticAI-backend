package com.example.kitt.service;

import com.example.kitt.config.properties.ExternalApiProperties;
import com.example.kitt.model.ExternalApiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@Service
public class ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);

    @Autowired
    private WebClient webClient;

    @Autowired
    private ExternalApiProperties externalApiProperties;

    // Add endpoints for Twitter and Reddit (should be set in properties)
    @Value("${external.api.reddit-client-id}")
    private String redditClientId;

    @Value("${external.api.reddit-client-secret}")
    private String redditClientSecret;

    @Value("${external.api.reddit-user-agent:RedditAgent/1.0}")
    private String redditUserAgent;

    @Value("${external.api.reddit-subreddit:all}")
    private String redditSubreddit;

    
    
    /**
     * Fetch Reddit posts using client credentials
     */
    public Mono<Map<String, Object>> fetchRedditData() {
        logger.info("Fetching Reddit data using client credentials");
        // Step 1: Get access token
        return webClient.post()
                .uri("https://www.reddit.com/api/v1/access_token")
                .headers(headers -> {
                    headers.setBasicAuth(redditClientId, redditClientSecret);
                    headers.set("User-Agent", redditUserAgent);
                })
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMap(tokenResponse -> {
                    String accessToken ="54dba411ecc9fd270bca6277dc2a4361940ae697382ddd1318f28a7889cf13ae";
                    // if (accessToken == null) {
                    //     return Mono.error(new RuntimeException("Failed to get Reddit access token"));
                    // }
                    // Step 2: Use access token to fetch subreddit posts
                    String subredditUrl = String.format("https://oauth.reddit.com/r/%s/new?limit=10", redditSubreddit);
                    return webClient.get()
                            .uri(subredditUrl)
                            .header("Authorization", "Bearer " + accessToken)
                            .header("User-Agent", redditUserAgent)
                            .retrieve()
                            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                            .timeout(Duration.ofMillis(externalApiProperties.getTimeout()))
                            .doOnSuccess(data -> logger.info("Successfully fetched Reddit posts"))
                            .doOnError(error -> logger.error("Error fetching Reddit posts: {}", error.getMessage()));
                });
    }



    @Value("${external.api.process-endpoint}")
    private String processEndpoint;




    /**
     * Fetch data from the main data endpoint
     */
    public Mono<ExternalApiData> fetchMainData() {
        logger.info("Fetching data from: {}", externalApiProperties.getFullDataEndpoint());
        
        return webClient.get()
                .uri(externalApiProperties.getDataEndpoint())
                .retrieve()
                .bodyToMono(ExternalApiData.class)
                .timeout(Duration.ofMillis(externalApiProperties.getTimeout()))
                .doOnSuccess(data -> logger.info("Successfully fetched main data"))
                .doOnError(error -> logger.error("Error fetching main data: {}", error.getMessage()));
    }

    /**
     * Fetch data from Job 1 endpoint
     */
    public Mono<Map<String, Object>> fetchJob1Data() {
        logger.info("Fetching Job 1 data from: {}", externalApiProperties.getFullJob1Endpoint());
        
        return webClient.get()
                .uri(externalApiProperties.getJob1Endpoint())
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofMillis(externalApiProperties.getTimeout()))
                .doOnSuccess(data -> logger.info("Successfully fetched Job 1 data"))
                .doOnError(error -> logger.error("Error fetching Job 1 data: {}", error.getMessage()));
    }

    /**
     * Fetch data from Job 2 endpoint
     */
    public Mono<Map<String, Object>> fetchJob2Data() {
        logger.info("Fetching Job 2 data from: {}", externalApiProperties.getFullJob2Endpoint());
        
        return webClient.get()
                .uri(externalApiProperties.getJob2Endpoint())
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofMillis(externalApiProperties.getTimeout()))
                .doOnSuccess(data -> logger.info("Successfully fetched Job 2 data"))
                .doOnError(error -> logger.error("Error fetching Job 2 data: {}", error.getMessage()));
    }

    /**
     * Fetch data from Job 3 endpoint
     */
    public Mono<Map<String, Object>> fetchJob3Data() {
        logger.info("Fetching Job 3 data from: {}", externalApiProperties.getFullJob3Endpoint());
        
        return webClient.get()
                .uri(externalApiProperties.getJob3Endpoint())
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofMillis(externalApiProperties.getTimeout()))
                .doOnSuccess(data -> logger.info("Successfully fetched Job 3 data"))
                .doOnError(error -> logger.error("Error fetching Job 3 data: {}", error.getMessage()));
    }

    /**
     * Generic method to fetch data from any endpoint
     */
    public Mono<Map<String, Object>> fetchDataFromEndpoint(String endpoint) {
        logger.info("Fetching data from endpoint: {}", endpoint);
        
        return webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofMillis(externalApiProperties.getTimeout()))
                .doOnSuccess(data -> logger.info("Successfully fetched data from: {}", endpoint))
                .doOnError(error -> logger.error("Error fetching data from {}: {}", endpoint, error.getMessage()));
    }



    /**
     * Send combined data to external API (POST)
     */
    public Mono<Object> sendDataToExternalApi(Map<String, Object> payload) {
        logger.info("Sending combined data to external API at: {}", processEndpoint);
        return webClient.post()
                .uri(processEndpoint)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofMillis(externalApiProperties.getTimeout()))
                .doOnSuccess(response -> logger.info("Successfully sent data to external API"))
                .doOnError(error -> logger.error("Error sending data to external API: {}", error.getMessage()));
    }
}