// File: com/ethereon/app/memory/NovaBrain.java
package com.ethereon.app.memory;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.AppBuilder;
import com.ethereon.app.ModelHandler;
import com.ethereon.app.memory.engine.MemoryRetentionEngine;
import com.ethereon.app.memory.io.MemoryAccessAPI;
import com.ethereon.app.memory.scheduler.MemoryBackupScheduler;

import java.io.File;

public class NovaBrain {

    private static final String TAG = "NovaBrain";

    private final Context context;
    private final ModelHandler modelHandler;
    private final AppBuilder appBuilder;

    private final MemoryAccessAPI memoryAccessAPI;
    private final MemoryRetentionEngine memoryRetentionEngine;
    private final MemoryBackupScheduler memoryBackupScheduler;

    public NovaBrain(Context context) {
        this.context = context;
        this.modelHandler = new ModelHandler(context);
        this.appBuilder = new AppBuilder(context);

        this.memoryAccessAPI = new MemoryAccessAPI(context);
        this.memoryRetentionEngine = new MemoryRetentionEngine(context);
        this.memoryBackupScheduler = new MemoryBackupScheduler(context);

        memoryBackupScheduler.scheduleDailyBackup(); // Default: once per day at midnight
    }

    // -------------------- Project + Model Handling -------------------- //

    public void dispatchPrompt(String userRequest, NovaCallback callback) {
        Log.d(TAG, "Dispatching task: " + userRequest);
        String result = modelHandler.runModelInference(userRequest);
        if (callback != null) {
            callback.onResult(result);
        }
    }

    public void startNewProject(String projectName, NovaCallback callback) {
        File folder = appBuilder.createNewProject(projectName);
        if (callback != null) {
            callback.onResult("Project created at: " + folder.getAbsolutePath());
        }
    }

    public void writeCodeToProject(File folder, String fileName, String code, NovaCallback callback) {
        boolean success = appBuilder.writeToProject(folder, fileName, code);
        if (callback != null) {
            callback.onResult(success
                    ? "Code written to " + fileName
                    : "Failed to write code to " + fileName);
        }
    }

    public void compressProject(File folder, NovaCallback callback) {
        boolean success = appBuilder.compressProject(folder);
        if (callback != null) {
            callback.onResult(success
                    ? "Project compressed: " + folder.getName()
                    : "Compression failed for " + folder.getName());
        }
    }

    // -------------------- Memory System Integration -------------------- //

    public void absorbMemoryChunk(String content) {
        memoryAccessAPI.saveMemoryChunk(content);
    }

    public String recallFromMemory(String query) {
        return memoryAccessAPI.searchMemory(query);
    }

    public void processMemoryRetention(String chatLog) {
        String importantInfo = memoryRetentionEngine.extractKeyDetails(chatLog);
        memoryAccessAPI.saveMemoryChunk(importantInfo);
    }

    public void triggerMemoryBackup() {
        memoryBackupScheduler.performBackupNow();
    }

    // -------------------- Callback Interface -------------------- //
    public interface NovaCallback {
        void onResult(String result);
    }
}