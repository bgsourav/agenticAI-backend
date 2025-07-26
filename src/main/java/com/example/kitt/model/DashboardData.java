
package com.example.kitt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;


public class DashboardData {
    private Map<String, Object> job1Data;
    private Map<String, Object> job2Data;
    private Map<String, Object> job3Data;
    private LocalDateTime lastUpdated;

    public DashboardData() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Map<String, Object> getJob1Data() {
        return job1Data;
    }

    public void setJob1Data(Map<String, Object> job1Data) {
        this.job1Data = job1Data;
    }

    public Map<String, Object> getJob2Data() {
        return job2Data;
    }

    public void setJob2Data(Map<String, Object> job2Data) {
        this.job2Data = job2Data;
    }

    public Map<String, Object> getJob3Data() {
        return job3Data;
    }

    public void setJob3Data(Map<String, Object> job3Data) {
        this.job3Data = job3Data;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}