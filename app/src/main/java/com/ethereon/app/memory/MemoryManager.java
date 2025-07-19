package com.ethereon.app.memory;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MemoryManager {

    private static final String TAG = "MemoryManager";
    private static final String MEMORY_FILE = "MainMemory.json";

    private final Context context;

    public MemoryManager(Context context) {
        this.context = context;
    }

    public File getMemoryFile() {
        return new File(context.getFilesDir(), MEMORY_FILE);
    }

    public JSONArray loadMemory() {
        File memoryFile = getMemoryFile();
        if (!memoryFile.exists()) {
            return new JSONArray(); // Return empty memory
        }

        try (Scanner scanner = new Scanner(memoryFile, StandardCharsets.UTF_8.name())) {
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            return new JSONArray(builder.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Failed to load memory: ", e);
            return new JSONArray();
        }
    }

    public void saveMemory(JSONArray memoryArray) {
        File memoryFile = getMemoryFile();
        try (FileWriter writer = new FileWriter(memoryFile, false)) {
            writer.write(memoryArray.toString(2));
            Log.d(TAG, "Memory saved successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to save memory: ", e);
        }
    }

    public void addMemoryEntry(JSONObject memoryEntry) {
        JSONArray currentMemory = loadMemory();
        currentMemory.put(memoryEntry);
        saveMemory(currentMemory);
    }

    public void clearMemory() {
        saveMemory(new JSONArray());
    }
}