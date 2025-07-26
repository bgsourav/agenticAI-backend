package com.example.kitt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class JobData {
    private String jobId;
    private String jobName;
    private Object jobResult;
    private LocalDateTime executionTime;
    private String status;

    public JobData() {
        this.executionTime = LocalDateTime.now();
    }

    public JobData(String jobId, String jobName, Object jobResult, String status) {
        this();
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobResult = jobResult;
        this.status = status;
    }

    // Getters and Setters
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Object getJobResult() {
        return jobResult;
    }

    public void setJobResult(Object jobResult) {
        this.jobResult = jobResult;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}