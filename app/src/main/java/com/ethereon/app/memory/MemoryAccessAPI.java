package com.ethereon.app.memory;

import android.content.Context;

import java.util.List;

public class MemoryAccessAPI {

    private final NovaMemoryManager memoryManager;

    public MemoryAccessAPI(Context context) {
        this.memoryManager = new NovaMemoryManager(context);
    }

    public List<String> getAllMemory() {
        return memoryManager.loadMainMemory();
    }

    public void addToMemory(String fact) {
        memoryManager.saveToMainMemory(fact);
    }

    public boolean forgetMemoryItem(String fact) {
        return memoryManager.deleteFromMainMemory(fact);
    }

    public boolean clearAllMemory() {
        return memoryManager.clearMainMemory();
    }

    public boolean hasMemory(String query) {
        List<String> memory = memoryManager.loadMainMemory();
        for (String item : memory) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}