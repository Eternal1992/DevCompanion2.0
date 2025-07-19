package com.ethereon.app.build;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;
import com.ethereon.app.utils.Utils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeModelExecutor {

    private static final String TAG = "CodeModelExecutor";

    private final Context context;
    private final ExecutorService executorService;

    public interface BuildCallback {
        void onComplete(boolean success);
    }

    public CodeModelExecutor(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void generateProject(AppIdeaModel idea, BuildCallback callback) {
        executorService.execute(() -> {
            try {
                File projectDir = new File(Utils.getAppStorageDirectory(context), idea.getAppName().replaceAll("\\s+", ""));
                if (!projectDir.exists()) {
                    projectDir.mkdirs();
                }

                AppBuilderEngine builder = new AppBuilderEngine(context);
                boolean built = builder.generateStarterFiles(idea, projectDir);

                // Simulate AI code generation delay (replace with real inference later)
                Thread.sleep(1000);

                new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(built));

            } catch (Exception e) {
                Log.e(TAG, "Code generation error: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false));
            }
        });
    }
}