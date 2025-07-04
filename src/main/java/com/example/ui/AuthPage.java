package com.example.ui;

import com.example.auth.AuthService;

import javax.swing.*;
import java.awt.*;

public class AuthPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public AuthPage() {
        setTitle("AI Compliance Agent - Login");

        // Responsive sizing (80% width/height)
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screen.width * 0.35);   // ~670px on 1920 screen
        int height = (int) (screen.height * 0.45); // ~480px
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Fonts
        Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);

        Color accentColor = new Color(40, 130, 255);
        Color successColor = new Color(60, 179, 113);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Welcome to AI Compliance Agent", SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton signInBtn = createStyledButton("Sign In", accentColor, buttonFont);
        JButton signUpBtn = createStyledButton("Sign Up", successColor, buttonFont);

        signInBtn.addActionListener(e -> authenticate("signIn"));
        signUpBtn.addActionListener(e -> authenticate("signUp"));

        // Add components to panel
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(30));
        panel.add(signInBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(signUpBtn);

        add(panel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Font font) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void authenticate(String mode) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.");
            return;
        }

        try {
            if (mode.equals("signIn")) {
                AuthService.signIn(email, password);
            } else {
                AuthService.signUp(email, password);
            }
            dispose();
            new MainPage();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Authentication failed: " + ex.getMessage());
        }
    }
}
