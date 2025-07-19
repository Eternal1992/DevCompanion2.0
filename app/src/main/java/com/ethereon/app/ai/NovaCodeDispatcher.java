package com.ethereon.app.ai;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;
import com.ethereon.app.build.CodeModelExecutor;
import com.ethereon.app.memory.NovaMemoryManager;

public class NovaCodeDispatcher {

    private static final String TAG = "NovaCodeDispatcher";

    private final Context context;
    private final CodeModelExecutor codeExecutor;
    private final NovaMemoryManager memoryManager;

    public NovaCodeDispatcher(Context context) {
        this.context = context;
        this.codeExecutor = new CodeModelExecutor(context);
        this.memoryManager = new NovaMemoryManager(context);
    }

    public void dispatchCodeBuild(AppIdeaModel idea) {
        Log.i(TAG, "Dispatching code generation task for: " + idea.getAppName());

        memoryManager.rememberEvent("Nova is starting code generation for " + idea.getAppName());

        // Send to executor
        codeExecutor.generateProject(idea, success -> {
            if (success) {
                Log.i(TAG, "Code generation complete.");
                memoryManager.rememberEvent("Code generation completed successfully for " + idea.getAppName());
            } else {
                Log.e(TAG, "Code generation failed.");
                memoryManager.rememberEvent("Code generation failed for " + idea.getAppName());
            }
        });
    }
}