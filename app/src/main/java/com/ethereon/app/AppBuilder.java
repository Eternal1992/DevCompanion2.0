package com.ethereon.app;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppBuilder {

    private static final String TAG = "AppBuilder";
    private final Context context;
    private final File buildDir;

    public AppBuilder(Context context) {
        this.context = context;
        this.buildDir = new File(context.getFilesDir(), "NovaProjects");
        if (!buildDir.exists()) buildDir.mkdirs();
    }

    public File createNewProject(String projectName) {
        String safeName = projectName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        File projectFolder = new File(buildDir, safeName + "_" + UUID.randomUUID());
        if (projectFolder.mkdirs()) {
            Log.d(TAG, "✅ New project created: " + projectFolder.getAbsolutePath());
        } else {
            Log.w(TAG, "⚠️ Failed to create project directory: " + projectFolder.getAbsolutePath());
        }
        return projectFolder;
    }

    public boolean writeToProject(File projectFolder, String fileName, String content) {
        try {
            File outFile = new File(projectFolder, fileName);
            try (FileWriter writer = new FileWriter(outFile)) {
                writer.write(content);
                Log.d(TAG, "✍️ File written: " + outFile.getAbsolutePath());
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "❌ Error writing to file: " + fileName, e);
            return false;
        }
    }

    public boolean compressProject(File projectFolder) {
        File zipFile = new File(projectFolder.getParent(), projectFolder.getName() + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zipDirectory(projectFolder, projectFolder.getName(), zos);
            Log.d(TAG, "📦 Project compressed to: " + zipFile.getAbsolutePath());
            return true;

        } catch (IOException e) {
            Log.e(TAG, "❌ Compression failed", e);
            return false;
        }
    }

    private void zipDirectory(File folder, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            String entryName = basePath + "/" + file.getName();
            if (file.isDirectory()) {
                zipDirectory(file, entryName, zos);
            } else {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                    ZipEntry zipEntry = new ZipEntry(entryName);
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = bis.read(buffer)) != -1) {
                        zos.write(buffer, 0, count);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    public File getBuildDirectory() {
        return buildDir;
    }
}