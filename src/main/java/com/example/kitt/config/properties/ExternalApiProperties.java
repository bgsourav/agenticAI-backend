package com.example.kitt.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external.api")
public class ExternalApiProperties {
    private String baseUrl;
    private String dataEndpoint;
    private String job1Endpoint;
    private String job2Endpoint;
    private String job3Endpoint;
    private int timeout;

    // Getters and Setters
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDataEndpoint() {
        return dataEndpoint;
    }

    public void setDataEndpoint(String dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
    }

    public String getJob1Endpoint() {
        return job1Endpoint;
    }

    public void setJob1Endpoint(String job1Endpoint) {
        this.job1Endpoint = job1Endpoint;
    }

    public String getJob2Endpoint() {
        return job2Endpoint;
    }

    public void setJob2Endpoint(String job2Endpoint) {
        this.job2Endpoint = job2Endpoint;
    }

    public String getJob3Endpoint() {
        return job3Endpoint;
    }

    public void setJob3Endpoint(String job3Endpoint) {
        this.job3Endpoint = job3Endpoint;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getFullDataEndpoint() {
        return baseUrl + dataEndpoint;
    }

    public String getFullJob1Endpoint() {
        return baseUrl + job1Endpoint;
    }

    public String getFullJob2Endpoint() {
        return baseUrl + job2Endpoint;
    }

    public String getFullJob3Endpoint() {
        return baseUrl + job3Endpoint;
    }
}

