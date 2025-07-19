// File: java/com/ethereon/app/utils/Utils.java
package com.ethereon.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Utils {

    private static final String TAG = "Utils";

    // Check if external storage is available
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    // Get the app's external storage directory
    public static File getAppStorageDirectory(Context context) {
        File dir = new File(context.getExternalFilesDir(null), "DevProjects");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    // Show a toast message
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Open a file with a default viewer
    public static void openFile(Context context, File file, String mimeType) {
        try {
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            showToast(context, "No app found to open this file.");
        }
    }

    // Request MANAGE_EXTERNAL_STORAGE permission for Android 11+
    public static void requestAllFilesAccess(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                showToast(context, "Unable to open settings. Please enable file permissions manually.");
            }
        }
    }

    // Merge classes.dex into unsigned APK
    public static void mergeDexIntoApk(File dexFile, File apkFile) throws IOException {
        File tempApk = new File(apkFile.getParent(), "temp-merged.apk");

        try (
            ZipInputStream zin = new ZipInputStream(new FileInputStream(apkFile));
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tempApk))
        ) {
            ZipEntry entry;

            // Copy existing entries except existing classes.dex
            while ((entry = zin.getNextEntry()) != null) {
                if (!entry.getName().equals("classes.dex")) {
                    zout.putNextEntry(new ZipEntry(entry.getName()));
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zin.read(buffer)) > 0) {
                        zout.write(buffer, 0, len);
                    }
                    zout.closeEntry();
                    zin.closeEntry();
                }
            }

            // Add the new classes.dex
            ZipEntry dexEntry = new ZipEntry("classes.dex");
            zout.putNextEntry(dexEntry);
            try (BufferedInputStream dexIn = new BufferedInputStream(new FileInputStream(dexFile))) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = dexIn.read(buffer)) > 0) {
                    zout.write(buffer, 0, len);
                }
            }
            zout.closeEntry();
        }

        // Replace original APK with merged one
        if (!apkFile.delete() || !tempApk.renameTo(apkFile)) {
            throw new IOException("Failed to finalize APK with classes.dex");
        }

        Log.i(TAG, "classes.dex merged into APK");
    }

    // ✨ Generate app name from voice idea string
    public static String generateAppNameFromIdea(String ideaText) {
        if (ideaText == null || ideaText.trim().isEmpty()) {
            return "MyApp";
        }

        // Grab relevant keywords
        ideaText = ideaText.toLowerCase(Locale.ROOT);
        String[] keywords = ideaText.split("\\s+");
        StringBuilder name = new StringBuilder();

        for (String word : keywords) {
            // Skip common filler words
            if (word.matches("a|an|the|app|create|make|build|for|of|my|me|to|please|nova|generate")) continue;
            if (name.length() < 3) { // Limit to 3-word names
                name.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
            }
        }

        return name.length() > 0 ? name.toString() : "MyApp";
    }
}