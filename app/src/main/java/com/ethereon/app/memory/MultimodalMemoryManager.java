package com.ethereon.app.memory;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores enhanced memory entries with associated image/audio/contextual metadata.
 */
public class MultimodalMemoryManager {

    private static final String TAG = "MultimodalMemoryManager";

    public static class MultimodalMemory {
        public String textContent;
        public List<String> associatedImages;
        public List<String> voiceToneTags;
        public List<String> linkedFiles;

        public MultimodalMemory(String textContent) {
            this.textContent = textContent;
            this.associatedImages = new ArrayList<>();
            this.voiceToneTags = new ArrayList<>();
            this.linkedFiles = new ArrayList<>();
        }
    }

    // Master list of multimodal memory objects
    private static final List<MultimodalMemory> memoryBank = new ArrayList<>();

    /**
     * Adds a new multimodal memory entry.
     */
    public static void addMultimodalMemory(String text, List<String> imagePaths, List<String> tones, List<String> files) {
        MultimodalMemory memory = new MultimodalMemory(text);
        if (imagePaths != null) memory.associatedImages.addAll(imagePaths);
        if (tones != null) memory.voiceToneTags.addAll(tones);
        if (files != null) memory.linkedFiles.addAll(files);
        memoryBank.add(memory);

        Log.d(TAG, "Added multimodal memory: " + text);
    }

    /**
     * Retrieves all memories matching a keyword.
     */
    public static List<MultimodalMemory> searchMemoryByKeyword(String keyword) {
        List<MultimodalMemory> result = new ArrayList<>();
        for (MultimodalMemory m : memoryBank) {
            if (m.textContent.toLowerCase().contains(keyword.toLowerCase())) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Returns all stored memories.
     */
    public static List<MultimodalMemory> getAllMemories() {
        return memoryBank;
    }
}