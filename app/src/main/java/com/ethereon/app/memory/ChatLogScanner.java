package com.ethereon.app.memory;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatLogScanner {

    private static final String TAG = "ChatLogScanner";
    private static final String CHAT_LOG_FOLDER = "chat_logs";

    // Entry method to start the scan and extraction
    public static void scanAndExtractImportant(Context context) {
        List<String> importantInfo = new ArrayList<>();

        File logDir = new File(context.getFilesDir(), CHAT_LOG_FOLDER);
        if (!logDir.exists() || !logDir.isDirectory()) {
            Log.w(TAG, "No chat logs found to scan.");
            return;
        }

        File[] logs = logDir.listFiles((dir, name) -> name.endsWith(".log"));
        if (logs == null || logs.length == 0) {
            Log.w(TAG, "No .log files in chat_logs.");
            return;
        }

        for (File logFile : logs) {
            try (Scanner scanner = new Scanner(logFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();

                    // Simple importance detection (can be upgraded)
                    if (isLineImportant(line)) {
                        importantInfo.add(line);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to scan file: " + logFile.getName(), e);
            }
        }

        if (!importantInfo.isEmpty()) {
            String joined = String.join("\n", importantInfo);
            MemoryUtils.appendToMemory(context, joined);
            Log.i(TAG, "Added important lines to memory.");
        } else {
            Log.i(TAG, "No important lines found.");
        }
    }

    // Simple heuristic for what counts as important
    private static boolean isLineImportant(String line) {
        String lower = line.toLowerCase();
        return lower.contains("remember") ||
               lower.contains("important") ||
               lower.contains("note:") ||
               lower.contains("you said") ||
               lower.contains("i like") ||
               lower.contains("i don't like") ||
               lower.contains("call me") ||
               lower.contains("my name is");
    }
}