// File: MemoryRecallProcessor.java
package com.ethereon.app.memory;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemoryRecallProcessor {

    private static final String TAG = "MemoryRecallProcessor";
    private final Context context;
    private final File memoryFolder;

    public MemoryRecallProcessor(Context context) {
        this.context = context;
        this.memoryFolder = MemoryUtils.getMemoryDirectory(context);
    }

    public List<String> recallByKeyword(String keyword) {
        List<String> results = new ArrayList<>();
        File[] files = memoryFolder.listFiles();

        if (files == null) return results;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".mem")) {
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.toLowerCase().contains(keyword.toLowerCase())) {
                            results.add("[" + file.getName() + "]: " + line);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading memory file: " + file.getName(), e);
                }
            }
        }

        return results;
    }

    public String summarizeRecentMemory() {
        File mainFile = new File(memoryFolder, "main_memory.mem");
        if (!mainFile.exists()) return "Main memory file is empty.";

        StringBuilder summary = new StringBuilder();
        try (Scanner scanner = new Scanner(mainFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                summary.append("• ").append(line).append("\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error summarizing main memory.", e);
            return "Failed to summarize memory.";
        }

        return summary.toString().trim();
    }

    public List<String> listAllMemoryFiles() {
        List<String> list = new ArrayList<>();
        File[] files = memoryFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) list.add(file.getName());
            }
        }

        return list;
    }

    public String getFileContent(String filename) {
        File file = new File(memoryFolder, filename);
        if (!file.exists()) return "Memory file not found.";

        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading memory file: " + filename, e);
            return "Failed to read file.";
        }

        return content.toString().trim();
    }
}