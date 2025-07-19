package com.ethereon.app.memory;

import android.util.Log;

import com.ethereon.app.voice.EmotionToneAnalyzer;

import java.util.HashMap;
import java.util.Map;

public class EmotionTaggedMemoryEnhancer {

    private static final String TAG = "EmotionMemoryEnhancer";

    // Simple data structure to hold memory entries with emotion tags
    private static final Map<String, String> taggedMemory = new HashMap<>();

    /**
     * Attaches an emotion tag to a memory entry.
     *
     * @param content The memory content to analyze and tag.
     */
    public static void tagMemoryWithEmotion(String content) {
        String emotion = EmotionToneAnalyzer.analyze(content);
        taggedMemory.put(content, emotion);
        Log.d(TAG, "Tagged memory: \"" + content + "\" as [" + emotion + "]");
    }

    /**
     * Retrieves all memory entries with their associated emotion tags.
     */
    public static Map<String, String> getAllEmotionTaggedMemory() {
        return taggedMemory;
    }

    /**
     * Retrieves only entries that match a specific emotion.
     *
     * @param emotionFilter The emotion to filter by (e.g., "happy").
     */
    public static Map<String, String> getMemoryByEmotion(String emotionFilter) {
        Map<String, String> filtered = new HashMap<>();
        for (Map.Entry<String, String> entry : taggedMemory.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(emotionFilter)) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }
}