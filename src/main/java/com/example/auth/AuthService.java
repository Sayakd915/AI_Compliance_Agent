package com.example.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AuthService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("FIREBASE_API_KEY");

    public static String signUp(String email, String password) throws Exception {
        String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
        return authenticate(endpoint, email, password);
    }

    public static String signIn(String email, String password) throws Exception {
        String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
        return authenticate(endpoint, email, password);
    }

    private static String authenticate(String endpoint, String email, String password) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("returnSecureToken", true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject json = new JSONObject(response.toString());
        String uid = json.getString("localId");
        String token = json.getString("idToken");

        AuthSession.login(uid, email, token);
        return uid;
    }
}
