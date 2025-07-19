package com.ethereon.app.build;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ethereon.app.build.logs.BuildOrchestrationLog;
import com.ethereon.app.build.models.BuildSession;

import java.io.File;
import java.io.IOException;

public class AppBuilderOrchestrator {

    private static final String TAG = "AppBuilderOrchestrator";
    private final Context context;
    private final File scriptFile;
    private BuildSession buildSession;
    private BuildOrchestrationLog orchestrationLog;

    public interface BuildCallback {
        void onBuildStarted();
        void onBuildCompleted(File apkFile);
        void onBuildFailed(String errorMessage);
    }

    public AppBuilderOrchestrator(Context context) {
        this.context = context;
        this.scriptFile = new File(context.getFilesDir(), "scripts/compile.sh");
    }

    public void buildApp(BuildCallback callback) {
        if (!scriptFile.exists()) {
            callback.onBuildFailed("Build script not found: " + scriptFile.getAbsolutePath());
            return;
        }

        buildSession = new BuildSession(System.currentTimeMillis());
        orchestrationLog = new BuildOrchestrationLog();
        orchestrationLog.logInfo("Starting build process...");
        new BuildTask(callback).execute();
    }

    private class BuildTask extends AsyncTask<Void, Void, Boolean> {
        private final BuildCallback callback;
        private File outputApk;
        private String error;

        BuildTask(BuildCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            callback.onBuildStarted();
            orchestrationLog.logInfo("Build task initialized.");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                orchestrationLog.logInfo("Launching compile script: " + scriptFile.getAbsolutePath());

                ProcessBuilder pb = new ProcessBuilder("sh", scriptFile.getAbsolutePath());
                pb.directory(context.getFilesDir());
                pb.redirectErrorStream(true);
                Process process = pb.start();
                int result = process.waitFor();

                File output = new File(context.getFilesDir(), "scripts/output/fake-app.apk");
                if (output.exists()) {
                    outputApk = output;
                    buildSession.setSuccessful(true);
                    orchestrationLog.logInfo("Build completed successfully.");
                    return true;
                } else {
                    error = "Build ran, but output APK not found.";
                    buildSession.setSuccessful(false);
                    orchestrationLog.logError(error);
                    return false;
                }
            } catch (IOException | InterruptedException e) {
                error = "Exception during build: " + e.getMessage();
                buildSession.setSuccessful(false);
                orchestrationLog.logException("Build failed", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            buildSession.setEndTime(System.currentTimeMillis());
            orchestrationLog.logInfo("Build session duration: " + buildSession.getDurationMillis() + "ms");

            if (success) {
                callback.onBuildCompleted(outputApk);
            } else {
                callback.onBuildFailed(error);
            }
        }
    }
}