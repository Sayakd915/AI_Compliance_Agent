
package com.example.auth;

public class AuthSession {
    private static String uid;
    private static String email;
    private static String idToken;

    public static void login(String u, String e, String token) {
        uid = u;
        email = e;
        idToken = token;
    }

    public static void logout() {
        uid = null;
        email = null;
        idToken = null;
    }

    public static String getUid() {
        return uid;
    }

    public static String getEmail() {
        return email;
    }

    public static boolean isAuthenticated() {
        return uid != null && idToken != null;
    }
}
