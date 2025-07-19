package com.ethereon.app.build;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AppCompiler {

    private static final String TAG = "AppCompiler";
    private final Context context;
    private final BuildSession session;

    public AppCompiler(Context context, BuildSession session) {
        this.context = context;
        this.session = session;
    }

    public boolean compileApp(File projectDir) {
        session.addBuildStep("Starting app compilation...");
        session.getLog().add("AppCompiler", "Running compile.sh inside: " + projectDir.getAbsolutePath());

        try {
            ProcessBuilder pb = new ProcessBuilder("sh", "compile.sh");
            pb.directory(projectDir);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                Log.i(TAG, "App compiled successfully.");
                session.addBuildStep("Compilation finished successfully.");
                return true;
            } else {
                Log.e(TAG, "App compilation failed with exit code: " + exitCode);
                session.getLog().add("AppCompiler", "Compilation failed with exit code: " + exitCode);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Compilation error: " + e.getMessage());
            session.getLog().add("AppCompiler", "Error: " + e.getMessage());
            return false;
        }
    }
}