package com.ethereon.app.memory;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemoryRetentionEngine {

    private static final String TAG = "MemoryRetentionEngine";
    private final NovaMemoryManager memoryManager;
    private final Set<String> knownFacts = new HashSet<>();

    public MemoryRetentionEngine(Context context) {
        this.memoryManager = new NovaMemoryManager(context);
    }

    public void retainImportantFactsFromChat(List<String> chatLines) {
        List<String> important = extractImportantFacts(chatLines);
        for (String fact : important) {
            if (!knownFacts.contains(fact)) {
                knownFacts.add(fact);
                memoryManager.saveToMainMemory(fact);
                Log.d(TAG, "Retained fact: " + fact);
            }
        }
    }

    private List<String> extractImportantFacts(List<String> chatLines) {
        List<String> importantFacts = new ArrayList<>();
        for (String line : chatLines) {
            String lower = line.toLowerCase();
            if (lower.contains("i like") || lower.contains("i don’t like")
                    || lower.contains("remember that") || lower.contains("from now on")
                    || lower.contains("call me") || lower.contains("my name is")
                    || lower.contains("i need you to remember") || lower.contains("i want you to remember")) {
                importantFacts.add(line.trim());
            }
        }
        return importantFacts;
    }

    public void clearKnownFacts() {
        knownFacts.clear();
        Log.i(TAG, "Known facts cleared from memory engine.");
    }
}