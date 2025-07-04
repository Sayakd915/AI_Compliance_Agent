package com.example.ui;

import com.example.auth.AuthSession;
import com.example.model.ChunkAnalysisResult;
import com.example.service.Chunker;
import com.example.service.FirestoreService;
import com.example.service.LlmService;
import com.example.service.PdfTextExtractor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainPage extends JFrame {
    private JLabel uploadedLabel;
    private JButton analysisButton;
    private File selectedFile;
    private JTabbedPane tabbedPane;

    public MainPage() {
        setTitle("AI Compliance Agent");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tabbedPane.addTab("Agent", createAgentPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        tabbedPane.addTab("History", new HistoryPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createAgentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JButton uploadBtn = new JButton("Upload PDF Document");
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        uploadBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        uploadBtn.setBackground(new Color(70, 130, 180));
        uploadBtn.setForeground(Color.WHITE);

        uploadedLabel = new JLabel("No document uploaded.");
        uploadedLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        uploadedLabel.setForeground(Color.GRAY);
        uploadedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        analysisButton = new JButton("Start Analysis");
        analysisButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        analysisButton.setBackground(new Color(34, 139, 34));
        analysisButton.setForeground(Color.WHITE);
        analysisButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        analysisButton.setEnabled(false); // Enabled after upload

        uploadBtn.addActionListener(e -> selectAndUploadDocument());
        analysisButton.addActionListener(e -> startAnalysis());

        panel.add(uploadBtn);
        panel.add(Box.createVerticalStrut(20));
        panel.add(analysisButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(uploadedLabel);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel welcomeLabel = new JLabel("Logged in as: " + AuthSession.getEmail());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            AuthSession.logout();
            dispose();
            new AuthPage();
        });

        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(logoutBtn);
        return panel;
    }

    private void selectAndUploadDocument() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                JOptionPane.showMessageDialog(this,
                        "Please upload a valid PDF file.",
                        "Invalid File",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            selectedFile = file;
            uploadedLabel.setText("📄 \"" + file.getName() + "\" has been uploaded.");
            uploadedLabel.setForeground(new Color(34, 139, 34));
            analysisButton.setEnabled(true);
        }
    }

    private void startAnalysis() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please upload a document first.",
                    "No Document",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String rawText = PdfTextExtractor.extractText(selectedFile);
            List<String> chunks = Chunker.splitIntoChunks(rawText, 1500);
            List<ChunkAnalysisResult> results = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                ChunkAnalysisResult result = LlmService.analyzeChunk(chunk, i);
                results.add(result);
            }

            FirestoreService.saveResults(selectedFile.getName(), results);

            JOptionPane.showMessageDialog(this,
                    "✅ Analysis complete for " + results.size() + " chunks!",
                    "Analysis Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            tabbedPane.addTab("Results", new ResultsPanel(results));
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "❌ Error during analysis: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
