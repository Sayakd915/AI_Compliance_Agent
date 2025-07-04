package com.example.model;

public class ChunkAnalysisResult {
    private int chunkIndex;
    private String chunkText;
    private String risk;
    private String summary;
    private String suggestion;

    public ChunkAnalysisResult(int chunkIndex, String chunkText, String risk, String summary, String suggestion) {
        this.chunkIndex = chunkIndex;
        this.chunkText = chunkText;
        this.risk = risk;
        this.summary = summary;
        this.suggestion = suggestion;
    }

    public int getChunkIndex() { return chunkIndex; }
    public String getChunkText() { return chunkText; }
    public String getRisk() { return risk; }
    public String getSummary() { return summary; }
    public String getSuggestion() { return suggestion; }
}
