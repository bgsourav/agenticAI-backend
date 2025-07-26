package com.example.kitt.controller;

import com.example.kitt.model.ApiResponse;
import com.example.kitt.model.DashboardData;
import com.example.kitt.model.ExternalApiData;
import com.example.kitt.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataService dataService;

    /**
     * Get main data endpoint for UI
     */
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<ExternalApiData>> getMainData(Authentication authentication) {
        logger.info("API call to get main data by user: {}", authentication.getName());
        
        try {
            ExternalApiData data = dataService.getMainData().block();
            ApiResponse<ExternalApiData> response = new ApiResponse<>(true, "Data retrieved successfully", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting main data: {}", e.getMessage());
            ApiResponse<ExternalApiData> response = new ApiResponse<>(false, "Error retrieving data: " + e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint to process data from Firebase, send to external API, and return the response
     */
    @PostMapping("/process-data")
    public Mono<ResponseEntity<Object>> processDataAndSendToExternalApi() {
        return dataService.processAndSendDataToExternalApi()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.internalServerError().body("No data or error occurred"));
    }

    /**
     * Get dashboard data for frontend
     */
    @GetMapping("/dashboard")
    public CompletableFuture<ResponseEntity<ApiResponse<DashboardData>>> getDashboardData(Authentication authentication) {
        logger.info("API call to get dashboard data by user: {}", authentication.getName());
        
        return dataService.getDashboardData()
                .thenApply(data -> {
                    ApiResponse<DashboardData> response = new ApiResponse<>(true, "Dashboard data retrieved successfully", data);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(error -> {
                    logger.error("Error getting dashboard data: {}", error.getMessage());
                    ApiResponse<DashboardData> response = new ApiResponse<>(false, "Error retrieving dashboard data: " + error.getMessage(), null);
                    return ResponseEntity.internalServerError().body(response);
                });
    }

    /**
     * Get specific job data
     */
    @GetMapping("/job/{jobName}")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> getJobData(
            @PathVariable String jobName, 
            Authentication authentication) {
        
        logger.info("API call to get job data for {} by user: {}", jobName, authentication.getName());
        
        return dataService.getJobData(jobName)
                .thenApply(data -> {
                    ApiResponse<Map<String, Object>> response = new ApiResponse<>(true, "Job data retrieved successfully", data);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(error -> {
                    logger.error("Error getting job data for {}: {}", jobName, error.getMessage());
                    ApiResponse<Map<String, Object>> response = new ApiResponse<>(false, "Error retrieving job data: " + error.getMessage(), null);
                    return ResponseEntity.internalServerError().body(response);
                });
    }

    /**
     * Get Job 1 specific data endpoint
     */
    @GetMapping("/job1/data")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> getJob1Data(Authentication authentication) {
        return getJobData("job1", authentication);
    }

    /**
     * Get Job 2 specific data endpoint
     */
    @GetMapping("/job2/data")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> getJob2Data(Authentication authentication) {
        return getJobData("job2", authentication);
    }

    /**
     * Get Job 3 specific data endpoint
     */
    @GetMapping("/job3/data")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> getJob3Data(Authentication authentication) {
        return getJobData("job3", authentication);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.debug("Health check endpoint called");
        ApiResponse<String> response = new ApiResponse<>(true, "Service is healthy", "OK");
        return ResponseEntity.ok(response);
    }

    /**
     * Get user info endpoint
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(Authentication authentication) {
        logger.info("API call to get user info by: {}", authentication.getName());
        
        Map<String, Object> userInfo = Map.of(
            "name", authentication.getName(),
            "authorities", authentication.getAuthorities(),
            "authenticated", authentication.isAuthenticated()
        );
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(true, "User info retrieved successfully", userInfo);
        return ResponseEntity.ok(response);
    }
}