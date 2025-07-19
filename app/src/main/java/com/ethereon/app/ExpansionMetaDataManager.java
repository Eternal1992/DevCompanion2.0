package com.ethereon.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ExpansionMetaDataManager {

    private static final String TAG = "ExpansionMetaDataManager";
    private static final String META_FILE_NAME = "expansion.meta";

    public static HashMap<String, String> parseMetadata(File expansionDir) {
        HashMap<String, String> metadata = new HashMap<>();
        File metaFile = new File(expansionDir, META_FILE_NAME);

        if (!metaFile.exists()) {
            Log.w(TAG, "No expansion.meta file found in: " + expansionDir.getAbsolutePath());
            return metadata;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(metaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                metadata.put(parts[0].trim(), parts[1].trim());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse metadata: " + e.getMessage());
        }

        return metadata;
    }

    public static String getVersion(HashMap<String, String> metadata) {
        return metadata.getOrDefault("version", "1.0.0");
    }

    public static String getName(HashMap<String, String> metadata) {
        return metadata.getOrDefault("name", "Unnamed Expansion");
    }

    public static boolean isNewerVersion(String currentVersion, String newVersion) {
        try {
            String[] current = currentVersion.split("\\.");
            String[] incoming = newVersion.split("\\.");

            for (int i = 0; i < Math.max(current.length, incoming.length); i++) {
                int curVal = i < current.length ? Integer.parseInt(current[i]) : 0;
                int newVal = i < incoming.length ? Integer.parseInt(incoming[i]) : 0;

                if (newVal > curVal) return true;
                if (newVal < curVal) return false;
            }
        } catch (Exception e) {
            Log.w(TAG, "Version comparison failed: " + e.getMessage());
        }

        return false;
    }
}