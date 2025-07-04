package com.example.ui;

import com.example.model.ChunkAnalysisResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultsPanel extends JPanel {
    private JTable table;
    private JTextArea detailsArea;
    private List<ChunkAnalysisResult> results;

    public ResultsPanel(List<ChunkAnalysisResult> results) {
        this.results = results;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columns = {"Chunk", "Risk", "Summary"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        for (ChunkAnalysisResult res : results) {
            model.addRow(new Object[]{
                    res.getChunkIndex(),
                    res.getRisk(),
                    shorten(res.getSummary(), 80)
            });
        }

        JScrollPane tableScroll = new JScrollPane(table);

        // Detail text area
        detailsArea = new JTextArea();
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsArea.setEditable(false);
        JScrollPane detailScroll = new JScrollPane(detailsArea);

        // Split pane for table and detail view
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailScroll);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        // Event: when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < results.size()) {
                ChunkAnalysisResult selected = results.get(row);
                detailsArea.setText(
                        "📄 Chunk Text:\n" + selected.getChunkText() + "\n\n" +
                                "⚠️ Risk Level: " + selected.getRisk() + "\n\n" +
                                "📝 Summary:\n" + selected.getSummary() + "\n\n" +
                                "💡 Suggestion:\n" + selected.getSuggestion()
                );
            }
        });
    }

    private String shorten(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
