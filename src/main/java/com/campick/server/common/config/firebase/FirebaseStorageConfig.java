package com.campick.server.common.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseStorageConfig {

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @Value("${firebase.storage.bucket:}")
    private String storageBucket;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions.Builder builder = FirebaseOptions.builder();

        GoogleCredentials credentials = resolveGoogleCredentials();
        builder.setCredentials(credentials.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")));

        if (storageBucket != null && !storageBucket.isBlank()) {
            builder.setStorageBucket(storageBucket);
        }

        return FirebaseApp.initializeApp(builder.build());
    }

    private GoogleCredentials resolveGoogleCredentials() throws IOException {
        // application.yml에 정의된 경로(`firebase.credentials.path에 값이 있는지 확인
        if (credentialsPath != null && !credentialsPath.isBlank()) {
            // classpath: 접두사가 있는 경우, classpath 리소스로 파일을 찾는다
            if (credentialsPath.startsWith("classpath")) {
                String path = credentialsPath.substring("classpath".length());
                Resource resource = new ClassPathResource(path);
                try (InputStream is = resource.getInputStream()) {
                    return GoogleCredentials.fromStream(is);
                }
            }
            // 접두사가 없는 경우, 파일 시스템 경로 또는 classpath 경로로 파일을 찾는다
            Resource resource = new ClassPathResource(credentialsPath);
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    return GoogleCredentials.fromStream(is);
                }
            }
        }
        // 지정된 경로에 파일이 없는 경우, Application Default Credentials를 사용
        return GoogleCredentials.getApplicationDefault();
    }
}
