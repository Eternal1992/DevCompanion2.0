package com.ethereon.app.memory;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This system creates and maintains a graph of interconnected memory nodes.
 */
public class MemoryGraphManager {

    private static final String TAG = "MemoryGraphManager";

    // Each memory string is treated as a node, connected by shared topics/keywords
    private static final HashMap<String, HashSet<String>> graph = new HashMap<>();

    /**
     * Adds a memory node and tries to link it to existing nodes with shared keywords.
     *
     * @param memoryEntry The new memory text.
     */
    public static void addMemoryNode(String memoryEntry) {
        if (!graph.containsKey(memoryEntry)) {
            graph.put(memoryEntry, new HashSet<>());
        }

        for (String other : graph.keySet()) {
            if (!other.equals(memoryEntry) && isRelated(memoryEntry, other)) {
                graph.get(memoryEntry).add(other);
                graph.get(other).add(memoryEntry);
                Log.d(TAG, "Linked: \"" + memoryEntry + "\" <--> \"" + other + "\"");
            }
        }
    }

    /**
     * Determines if two memory entries are contextually related based on shared words.
     */
    private static boolean isRelated(String a, String b) {
        String[] tokensA = a.toLowerCase().split("\\W+");
        String[] tokensB = b.toLowerCase().split("\\W+");

        HashSet<String> setA = new HashSet<>(List.of(tokensA));
        HashSet<String> setB = new HashSet<>(List.of(tokensB));

        for (String word : setA) {
            if (setB.contains(word) && word.length() > 3) { // skip very short words
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all memory nodes directly connected to a given memory.
     */
    public static List<String> getConnectedNodes(String memoryEntry) {
        return new ArrayList<>(graph.getOrDefault(memoryEntry, new HashSet<>()));
    }

    /**
     * Gets the entire graph.
     */
    public static HashMap<String, HashSet<String>> getFullGraph() {
        return graph;
    }
}