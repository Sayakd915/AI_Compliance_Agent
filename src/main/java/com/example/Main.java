package com.example;

import com.example.firebase.FirebaseInitializer;
import com.example.ui.AuthPage;

public class Main {
    public static void main(String[] args) {
        FirebaseInitializer.init();
        javax.swing.SwingUtilities.invokeLater(AuthPage::new);
    }
}
