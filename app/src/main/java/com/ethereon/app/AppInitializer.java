package com.ethereon.app;

import android.app.Application;
import android.util.Log;

public class AppInitializer extends Application {

    private static final String TAG = "AppInitializer";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            System.loadLibrary("llama"); // Load native libllama.so
            Log.i(TAG, "✅ Native library libllama.so loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ Failed to load native libllama.so", e);
        }

        // Future: Initialize Nova engine, memory, and background model runner
        // NovaEngineBootstrap.init(getApplicationContext());
    }
}