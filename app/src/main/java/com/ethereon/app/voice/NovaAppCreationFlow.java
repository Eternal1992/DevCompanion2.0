package com.ethereon.voice;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.build.AppBuildOrchestrator;
import com.ethereon.chat.ChatSessionManager;
import com.ethereon.memory.NovaMemoryManager;
import com.ethereon.models.AppBlueprint;
import com.ethereon.models.ProjectFileManager;

import java.io.File;

/**
 * Main voice-to-app creation pipeline powered by Nova.
 */
public class NovaAppCreationFlow {

    private static final String TAG = "NovaAppCreationFlow";

    private final Context context;
    private final ChatSessionManager chatSessionManager;
    private final NovaMemoryManager memoryManager;

    public NovaAppCreationFlow(Context context, ChatSessionManager chatSessionManager, NovaMemoryManager memoryManager) {
        this.context = context;
        this.chatSessionManager = chatSessionManager;
        this.memoryManager = memoryManager;
    }

    public void beginNewAppFlow(String voiceInput, NovaAppCreationCallback callback) {
        Log.i(TAG, "Nova initiating app creation from voice input...");
        memoryManager.storeTemporary("intent", voiceInput);

        AppBlueprint blueprint = AppBlueprintGenerator.fromNaturalLanguage(voiceInput);
        ProjectFileManager.generateFromBlueprint(context, blueprint);

        new AppBuildOrchestrator(context).buildApp(new AppBuildOrchestrator.BuildCallback() {
            @Override
            public void onBuildStarted() {
                callback.onProgress("Starting build for: " + blueprint.getAppName());
            }

            @Override
            public void onBuildCompleted(File apkFile) {
                callback.onSuccess(apkFile);
                memoryManager.commitFinalMemory("Built app: " + blueprint.getAppName());
            }

            @Override
            public void onBuildFailed(String errorMessage) {
                callback.onError("Build failed: " + errorMessage);
                memoryManager.discardTemporary("intent");
            }
        });
    }

    public interface NovaAppCreationCallback {
        void onProgress(String status);
        void onSuccess(File apkFile);
        void onError(String message);
    }
}