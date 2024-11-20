package com.hyejin.ruti.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        Dotenv dotenv = Dotenv.configure().load();

        String firebaseConfigPath = dotenv.get("FIREBASE_CONFIG_PATH");

        if (firebaseConfigPath == null) {
            throw new IllegalStateException("환경 변수 'FIREBASE_CONFIG_PATH'가 설정되지 않았습니다.");
        }

        try (FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath)) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new IllegalStateException("Firebase 설정 파일을 읽는 중 오류가 발생했습니다: " + firebaseConfigPath, e);
        }
    }
}