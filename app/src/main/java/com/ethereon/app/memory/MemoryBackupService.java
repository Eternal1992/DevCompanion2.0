package com.ethereon.app.memory;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MemoryBackupService extends IntentService {

    private static final String TAG = "MemoryBackupService";

    public MemoryBackupService() {
        super("MemoryBackupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            File memoryDir = new File(getFilesDir(), "nova_memory");
            File backupDir = new File(getFilesDir(), "nova_memory/backups");

            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            File memoryFile = new File(memoryDir, "main_memory.json");

            if (memoryFile.exists()) {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                File backupFile = new File(backupDir, "backup_" + timestamp + ".json");

                Files.copy(memoryFile.toPath(), new FileOutputStream(backupFile));
                Log.i(TAG, "Memory backup created: " + backupFile.getAbsolutePath());
            } else {
                Log.w(TAG, "No memory file found to back up.");
            }

        } catch (IOException e) {
            Log.e(TAG, "Backup failed: ", e);
        }
    }
}