package com.example.service;

import java.util.ArrayList;
import java.util.List;

public class Chunker {
    public static List<String> splitIntoChunks(String text, int maxTokens) {
        String[] paragraphs = text.split("\\n\\s*\\n"); // split by empty lines
        List<String> chunks = new ArrayList<>();

        StringBuilder currentChunk = new StringBuilder();
        for (String para : paragraphs) {
            if ((currentChunk.length() + para.length()) < maxTokens) {
                currentChunk.append(para).append("\n\n");
            } else {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder(para).append("\n\n");
            }
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }
}
