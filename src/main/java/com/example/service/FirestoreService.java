package com.example.service;

import com.example.auth.AuthSession;
import com.example.firebase.FirebaseInitializer;
import com.example.model.ChunkAnalysisResult;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.*;

public class FirestoreService {
    private static final Firestore db = FirebaseInitializer.getFirestore();

    public static void saveResults(String documentName, List<ChunkAnalysisResult> results) {
        String userId = AuthSession.getUid();
        CollectionReference resultRef = db.collection("analysis_results");

        for (ChunkAnalysisResult result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("documentName", documentName);
            data.put("chunkIndex", result.getChunkIndex());
            data.put("chunkText", result.getChunkText());
            data.put("risk", result.getRisk());
            data.put("summary", result.getSummary());
            data.put("suggestion", result.getSuggestion());
            data.put("timestamp", FieldValue.serverTimestamp());

            ApiFuture<DocumentReference> future = resultRef.add(data);

            future.addListener(() -> {
                try {
                    System.out.println("✅ Saved chunk " + result.getChunkIndex());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, Runnable::run);
        }
    }

    public static Set<String> getAnalyzedDocumentNames(String userId) {
        Set<String> names = new HashSet<>();
        try {
            QuerySnapshot snapshot = db.collection("analysis_results")
                    .whereEqualTo("userId", userId)
                    .get().get();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                names.add(doc.getString("documentName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    public static List<ChunkAnalysisResult> loadResultsForDocument(String documentName) {
        List<ChunkAnalysisResult> results = new ArrayList<>();
        try {
            QuerySnapshot snapshot = db.collection("analysis_results")
                    .whereEqualTo("userId", AuthSession.getUid())
                    .whereEqualTo("documentName", documentName)
                    .orderBy("chunkIndex")
                    .get().get();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                results.add(new ChunkAnalysisResult(
                        doc.getLong("chunkIndex").intValue(),
                        doc.getString("chunkText"),
                        doc.getString("risk"),
                        doc.getString("summary"),
                        doc.getString("suggestion")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

}
