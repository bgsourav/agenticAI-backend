



package com.example.kitt.service;

import com.example.kitt.model.DashboardData;
import com.example.kitt.model.ExternalApiData;
import com.example.kitt.model.JobData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;

@Service
public class DataService {

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private FirebaseService firebaseService;

    // In-memory cache for latest data (can be replaced with Redis later)
    private Map<String, Object> job1Cache = new HashMap<>();
    private Map<String, Object> job2Cache = new HashMap<>();
    private Map<String, Object> job3Cache = new HashMap<>();

    // For demonstration, assuming this is the key for Reddit
    private static final String REDDIT_KEY = "reddit_data";

    // Frequency for scheduled job (can be set in application.properties)
    @Value("${jobs.job1.interval:60000}")
    private long job1Interval;

    /**
     * Get main data from external API
     */
    public Mono<ExternalApiData> getMainData() {
        logger.info("Fetching main data");
        return externalApiService.fetchMainData()
                .doOnSuccess(data -> {
                    // Save to Firebase
                    firebaseService.saveData("main_data", data);
                })
                .onErrorResume(error -> {
                    logger.error("Error fetching main data, trying to get from Firebase: {}", error.getMessage());
                    // Fallback to Firebase data
                    return getMainDataFromFirebase();
                });
    }

    /**
     * Scheduled job to continuously fetch Reddit data and store in Firebase
     */
    @Scheduled(fixedDelayString = "${jobs.job1.interval:60000}")
    public void continuousJob1Fetch() {
        logger.info("[Job1] Scheduled fetch for Reddit data");
        // Fetch Reddit data
        externalApiService.fetchRedditData()
                .subscribe(
                        redditData -> {
                            job1Cache.put(REDDIT_KEY, redditData);
                            firebaseService.saveJobData(REDDIT_KEY, redditData);
                            logger.info("[Job1] Reddit data saved to Firebase");
                        },
                        error -> logger.error("[Job1] Error fetching Reddit data: {}", error.getMessage())
                );
    }

    // Placeholder for Job 2 and Job 3 continuous fetch
    // @Scheduled(fixedDelay = ...)
    // public void continuousJob2Fetch() { }
    // public void continuousJob3Fetch() { }

    /**
     * Fetch latest Reddit data from Firebase, send to external API, and return response
     */
    public Mono<Object> processAndSendDataToExternalApi() {
        logger.info("Processing Reddit data from Firebase and sending to external API");
        return Mono.fromFuture(firebaseService.getLatestJobData(REDDIT_KEY))
                .flatMap(redditData -> {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("reddit", redditData);
                    // Call external API with Reddit data only
                    return externalApiService.sendDataToExternalApi(payload)
                            .doOnSuccess(response -> logger.info("External API responded successfully"))
                            .doOnError(error -> logger.error("Error calling external API: {}", error.getMessage()));
                });
    }

    /**
     * Get dashboard data for frontend
     */
    public CompletableFuture<DashboardData> getDashboardData() {
        logger.info("Preparing dashboard data");
        
        CompletableFuture<DashboardData> future = new CompletableFuture<>();
        
        try {
            DashboardData dashboardData = new DashboardData();
            dashboardData.setJob1Data(job1Cache);
            dashboardData.setJob2Data(job2Cache);
            dashboardData.setJob3Data(job3Cache);
            dashboardData.setLastUpdated(LocalDateTime.now());
            
            future.complete(dashboardData);
        } catch (Exception e) {
            logger.error("Error preparing dashboard data: {}", e.getMessage());
            future.completeExceptionally(e);
        }
        
        return future;
    }

    /**
     * Process Job 1 data
     */
    public void processJob1Data() {
        logger.info("Processing Job 1 data");
        
        externalApiService.fetchJob1Data()
                .subscribe(
                    data -> {
                        job1Cache = data;
                        JobData jobData = new JobData("job1", "Data Processing Job 1", data, "SUCCESS");
                        firebaseService.saveJobData("job1", jobData);
                        logger.info("Job 1 data processed successfully");
                    },
                    error -> {
                        logger.error("Error processing Job 1 data: {}", error.getMessage());
                        JobData jobData = new JobData("job1", "Data Processing Job 1", null, "FAILED");
                        firebaseService.saveJobData("job1", jobData);
                    }
                );
    }

    /**
     * Process Job 2 data
     */
    public void processJob2Data() {
        logger.info("Processing Job 2 data");
        
        externalApiService.fetchJob2Data()
                .subscribe(
                    data -> {
                        job2Cache = data;
                        JobData jobData = new JobData("job2", "Data Processing Job 2", data, "SUCCESS");
                        firebaseService.saveJobData("job2", jobData);
                        logger.info("Job 2 data processed successfully");
                    },
                    error -> {
                        logger.error("Error processing Job 2 data: {}", error.getMessage());
                        JobData jobData = new JobData("job2", "Data Processing Job 2", null, "FAILED");
                        firebaseService.saveJobData("job2", jobData);
                    }
                );
    }

    /**
     * Process Job 3 data
     */
    public void processJob3Data() {
        logger.info("Processing Job 3 data");
        
        externalApiService.fetchJob3Data()
                .subscribe(
                    data -> {
                        job3Cache = data;
                        JobData jobData = new JobData("job3", "Data Processing Job 3", data, "SUCCESS");
                        firebaseService.saveJobData("job3", jobData);
                        logger.info("Job 3 data processed successfully");
                    },
                    error -> {
                        logger.error("Error processing Job 3 data: {}", error.getMessage());
                        JobData jobData = new JobData("job3", "Data Processing Job 3", null, "FAILED");
                        firebaseService.saveJobData("job3", jobData);
                    }
                );
    }

    /**
     * Get specific job data
     */
    public CompletableFuture<Map<String, Object>> getJobData(String jobName) {
        logger.info("Getting data for job: {}", jobName);
        
        switch (jobName.toLowerCase()) {
            case "job1":
                return CompletableFuture.completedFuture(job1Cache);
            case "job2":
                return CompletableFuture.completedFuture(job2Cache);
            case "job3":
                return CompletableFuture.completedFuture(job3Cache);
            default:
                return firebaseService.getLatestJobData(jobName);
        }
    }

    /**
     * Initialize data on startup
     */
    public void initializeData() {
        logger.info("Initializing data on startup");
        
        // Load cached data from Firebase
        firebaseService.getLatestJobData("job1")
                .thenAccept(data -> {
                    if (data != null) {
                        job1Cache = data;
                    }
                });
                
        firebaseService.getLatestJobData("job2")
                .thenAccept(data -> {
                    if (data != null) {
                        job2Cache = data;
                    }
                });
                
        firebaseService.getLatestJobData("job3")
                .thenAccept(data -> {
                    if (data != null) {
                        job3Cache = data;
                    }
                });
    }

    /**
     * Fallback method to get main data from Firebase
     */
    private Mono<ExternalApiData> getMainDataFromFirebase() {
        return Mono.fromFuture(firebaseService.readData("main_data"))
                .map(data -> {
                    ExternalApiData apiData = new ExternalApiData();
                    // Map the data from Firebase to ExternalApiData object
                    if (data != null) {
                        apiData.setId((String) data.get("id"));
                        apiData.setName((String) data.get("name"));
                        apiData.setValue(data.get("value"));
                        apiData.setTimestamp((String) data.get("timestamp"));
                        apiData.setMetadata((Map<String, Object>) data.get("metadata"));
                    }
                    return apiData;
                })
                .onErrorReturn(new ExternalApiData("fallback", "Fallback Data", "No data available", LocalDateTime.now().toString()));
    }

    // ...existing code...
}