package com.ethereon.app.build;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;

import java.io.File;

public class NovaProjectGenerator {

    private static final String TAG = "NovaProjectGenerator";

    private final Context context;
    private final CodeModelExecutor executor;

    public NovaProjectGenerator(Context context) {
        this.context = context;
        this.executor = new CodeModelExecutor(context);
    }

    public void startNewProject(AppIdeaModel idea) {
        Log.i(TAG, "Starting new project: " + idea.getAppName());

        executor.generateProject(idea, success -> {
            if (success) {
                Log.i(TAG, "Project generated successfully.");
                // You can trigger Nova's voice/visual feedback here
            } else {
                Log.e(TAG, "Project generation failed.");
                // Provide error handling or retry options
            }
        });
    }
}