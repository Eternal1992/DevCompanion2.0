package com.devcompanion;

import android.app.Application;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set global uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            writeCrashLogToFile(throwable);
            
            // Kill the app after logging the crash
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
    }

    private void writeCrashLogToFile(Throwable throwable) {
        try {
            File crashDir = new File(getExternalFilesDir(null), "crash_logs");
            if (!crashDir.exists()) {
                crashDir.mkdirs();
            }

            String filename = "crash_" + System.currentTimeMillis() + ".txt";
            File crashFile = new File(crashDir, filename);

            PrintWriter writer = new PrintWriter(new FileWriter(crashFile));
            writer.println("Crash occurred on: " + new Date());
            throwable.printStackTrace(writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}