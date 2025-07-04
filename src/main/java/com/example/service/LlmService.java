package com.example.service;

import com.example.model.ChunkAnalysisResult;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.io.OutputStream;

public class LlmService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("GROQ_API_KEY");
    private static final String ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";

    public static ChunkAnalysisResult analyzeChunk(String chunkText, int index) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("model", "llama3-70b-8192");
        payload.put("temperature", 0.2);

        List<JSONObject> messages = List.of(
                new JSONObject().put("role", "system")
                        .put("content", """
                    You are a legal compliance assistant.
                    Respond ONLY in this JSON format:

                    {
                      "risk": "Low/Medium/High",
                      "summary": "Short plain-English summary.",
                      "suggestion": "Improvement suggestion (if any)"
                    }
                    Do not add anything else or markdown.
                    """),
                new JSONObject().put("role", "user")
                        .put("content", "Analyze this contract clause:\n\n" + chunkText)
        );
        payload.put("messages", messages);

        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) response.append(scanner.nextLine());
        scanner.close();

        JSONObject content = new JSONObject(response.toString())
                .getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message");

        String responseText = content.getString("content").trim();

        try {
            JSONObject json = new JSONObject(responseText);

            String risk = json.optString("risk", "Unknown");
            String summary = json.optString("summary", "");
            String suggestion = json.optString("suggestion", "");

            return new ChunkAnalysisResult(index, chunkText, risk, summary, suggestion);
        } catch (JSONException e) {
            // Fallback incase LLM doesn't run
            return new ChunkAnalysisResult(index, chunkText, "Unknown", responseText, "N/A");
        }
    }
}
