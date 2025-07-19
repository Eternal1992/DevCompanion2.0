package com.ethereon.app.memory;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NovaMemoryManager {

    private static final String TAG = "NovaMemoryManager";
    private static final String MEMORY_FOLDER = "nova_memory";
    private static final String MAIN_MEMORY_FILE = "main_memory.txt";

    private final Context context;

    public NovaMemoryManager(Context context) {
        this.context = context;
    }

    public File getMemoryDirectory() {
        File dir = new File(context.getFilesDir(), MEMORY_FOLDER);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public File getMainMemoryFile() {
        return new File(getMemoryDirectory(), MAIN_MEMORY_FILE);
    }

    public void saveToMainMemory(String data) {
        File file = getMainMemoryFile();
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(data + "\n");
            Log.d(TAG, "Memory saved: " + data);
        } catch (IOException e) {
            Log.e(TAG, "Failed to write to main memory file", e);
        }
    }

    public void replaceMainMemory(List<String> lines) {
        File file = getMainMemoryFile();
        try (FileWriter writer = new FileWriter(file, false)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            Log.d(TAG, "Main memory file replaced with new lines.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to replace memory file", e);
        }
    }

    public void clearMemory() {
        File file = getMainMemoryFile();
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.i(TAG, "Main memory file cleared.");
            } else {
                Log.e(TAG, "Failed to delete main memory file.");
            }
        }
    }
}