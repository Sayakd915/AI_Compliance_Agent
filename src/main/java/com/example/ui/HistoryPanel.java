package com.example.ui;

import com.example.auth.AuthSession;
import com.example.model.ChunkAnalysisResult;
import com.example.service.FirestoreService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class HistoryPanel extends JPanel {
    private JList<String> docList;
    private DefaultListModel<String> listModel;
    private JPanel detailPanel;

    public HistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        listModel = new DefaultListModel<>();
        docList = new JList<>(listModel);
        docList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane listScroll = new JScrollPane(docList);

        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Analysis Results"));
        detailPanel.add(new JLabel("Select a document to view results..."), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, detailPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        loadDocumentNames();

        docList.addListSelectionListener(e -> {
            String selectedDoc = docList.getSelectedValue();
            if (selectedDoc != null) {
                List<ChunkAnalysisResult> results = FirestoreService.loadResultsForDocument(selectedDoc);
                detailPanel.removeAll();
                detailPanel.add(new ResultsPanel(results), BorderLayout.CENTER);
                detailPanel.revalidate();
                detailPanel.repaint();
            }
        });
    }

    private void loadDocumentNames() {
        Set<String> docNames = FirestoreService.getAnalyzedDocumentNames(AuthSession.getUid());
        listModel.clear();
        for (String name : docNames) {
            listModel.addElement(name);
        }
    }
}
