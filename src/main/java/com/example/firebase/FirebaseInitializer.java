package com.example.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.InputStream;

public class FirebaseInitializer {
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        try {
            InputStream serviceAccount = FirebaseInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("serviceAccountKey.json");

            if (serviceAccount == null) {
                throw new IllegalStateException("❌ serviceAccountKey.json not found!");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            initialized = true;
            System.out.println("✅ Firebase Admin SDK initialized");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to initialize Firebase", e);
        }
    }

    public static Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}
