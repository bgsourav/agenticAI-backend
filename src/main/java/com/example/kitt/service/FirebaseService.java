package com.example.kitt.service;

import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    /**
     * Save data to Firebase Realtime Database
     */
    public CompletableFuture<Void> saveData(String path, Object data) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        DatabaseReference ref = firebaseDatabase.getReference(path);
        ref.setValue(data, (error, ref1) -> {
            if (error != null) {
                logger.error("Failed to save data to path {}: {}", path, error.getMessage());
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            } else {
                logger.info("Successfully saved data to path: {}", path);
                future.complete(null);
            }
        });
        
        return future;
    }

    /**
     * Read data from Firebase Realtime Database
     */
    public CompletableFuture<Map<String, Object>> readData(String path) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        
        DatabaseReference ref = firebaseDatabase.getReference(path);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    logger.info("Successfully read data from path: {}", path);
                    future.complete(data);
                } catch (Exception e) {
                    logger.error("Error reading data from path {}: {}", path, e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Failed to read data from path {}: {}", path, databaseError.getMessage());
                future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
            }
        });
        
        return future;
    }

    /**
     * Save job data with timestamp
     */
    public CompletableFuture<Void> saveJobData(String jobName, Object data) {
        String path = "jobs/" + jobName + "/" + System.currentTimeMillis();
        return saveData(path, data);
    }

    /**
     * Get latest job data
     */
    public CompletableFuture<Map<String, Object>> getLatestJobData(String jobName) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        
        DatabaseReference ref = firebaseDatabase.getReference("jobs/" + jobName);
        ref.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Map<String, Object> data = (Map<String, Object>) child.getValue();
                            future.complete(data);
                            return;
                        }
                    }
                    future.complete(null);
                } catch (Exception e) {
                    logger.error("Error reading latest job data for {}: {}", jobName, e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Failed to read latest job data for {}: {}", jobName, databaseError.getMessage());
                future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
            }
        });
        
        return future;
    }

    /**
     * Delete data from path
     */
    public CompletableFuture<Void> deleteData(String path) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        DatabaseReference ref = firebaseDatabase.getReference(path);
        ref.removeValue((error, ref1) -> {
            if (error != null) {
                logger.error("Failed to delete data from path {}: {}", path, error.getMessage());
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            } else {
                logger.info("Successfully deleted data from path: {}", path);
                future.complete(null);
            }
        });
        
        return future;
    }
}