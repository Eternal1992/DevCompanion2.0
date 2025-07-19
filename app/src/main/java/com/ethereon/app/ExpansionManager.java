package com.ethereon.app;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExpansionManager {

    private static final String TAG = "ExpansionManager";
    private static final String EXPANSION_FOLDER = "expansions";
    private static final String INSTALL_FOLDER = "installed_expansions";
    private static final String VERSION_FILE = "installed_versions.txt";

    private final Context context;

    public ExpansionManager(Context context) {
        this.context = context;
    }

    public File getExpansionDirectory() {
        File dir = new File(context.getFilesDir(), EXPANSION_FOLDER);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public File getInstallDirectory() {
        File dir = new File(context.getFilesDir(), INSTALL_FOLDER);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public List<File> scanForExpansions() {
        File[] files = getExpansionDirectory().listFiles();
        List<File> validExpansions = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".exp") || name.endsWith(".zip") || name.endsWith(".patch")) {
                    validExpansions.add(file);
                    Log.d(TAG, "Found expansion: " + name);
                }
            }
        }
        return validExpansions;
    }

    public boolean installExpansion(File expansionFile) {
        Log.i(TAG, "Installing expansion: " + expansionFile.getName());

        try {
            File installDir = getInstallDirectory();
            unzipFile(expansionFile, installDir);

            // Parse metadata AFTER unzipping
            File expansionRoot = installDir;  // Assumes flat structure after unzip
            HashMap<String, String> metadata = ExpansionMetaDataManager.parseMetadata(expansionRoot);
            String name = ExpansionMetaDataManager.getName(metadata);
            String newVersion = ExpansionMetaDataManager.getVersion(metadata);

            String currentVersion = readInstalledVersions().getOrDefault(name, "0.0.0");

            if (!ExpansionMetaDataManager.isNewerVersion(currentVersion, newVersion)) {
                Log.i(TAG, "Expansion is not newer than current: " + name + " (installed: " + currentVersion + ", new: " + newVersion + ")");
                return false;
            }

            Map<String, String> updatedVersions = readInstalledVersions();
            updatedVersions.put(name, newVersion);
            saveInstalledVersions(updatedVersions);

            Log.i(TAG, "Expansion installed successfully: " + name + " v" + newVersion);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Failed to install expansion: " + e.getMessage());
            return false;
        }
    }

    private Map<String, String> readInstalledVersions() {
        Map<String, String> map = new HashMap<>();
        File versionFile = new File(getInstallDirectory(), VERSION_FILE);
        if (!versionFile.exists()) return map;

        try (BufferedReader reader = new BufferedReader(new FileReader(versionFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    map.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read installed versions.");
        }
        return map;
    }

    private void saveInstalledVersions(Map<String, String> versions) {
        File versionFile = new File(getInstallDirectory(), VERSION_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(versionFile))) {
            for (Map.Entry<String, String> entry : versions.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to save installed versions.");
        }
    }

    private void unzipFile(File zipFile, File targetDirectory) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(targetDirectory, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    File parent = outFile.getParentFile();
                    if (!parent.exists()) parent.mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}