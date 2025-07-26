package com.example.kitt.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-key:pulse-bengaluru-backend-firebase-adminsdk-fbsvc-1155be068e.json}")
    private String serviceAccountKeyPath;

    @Value("${firebase.database-url:https://pulse-bengaluru-backend-default-rtdb.firebaseio.com}")
    private String databaseUrl;

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials credentials;
            
            // Try to load from classpath first (resources folder)
            try {
                Resource resource = new ClassPathResource(serviceAccountKeyPath);
                if (resource.exists()) {
                    InputStream serviceAccount = resource.getInputStream();
                    credentials = GoogleCredentials.fromStream(serviceAccount);
                } else {
                    // If not in classpath, try to load from file system
                    FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);
                    credentials = GoogleCredentials.fromStream(serviceAccount);
                }
            } catch (IOException e) {
                // Fallback to file system path
                FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);
                credentials = GoogleCredentials.fromStream(serviceAccount);
            }

            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                .setCredentials(credentials);

            // Set database URL if provided
            if (databaseUrl != null && !databaseUrl.isEmpty()) {
                optionsBuilder.setDatabaseUrl(databaseUrl);
            }

            FirebaseOptions options = optionsBuilder.build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase. Please check your service account key file: " + serviceAccountKeyPath, e);
        }
    }

    @Bean
    public FirebaseDatabase firebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }
}