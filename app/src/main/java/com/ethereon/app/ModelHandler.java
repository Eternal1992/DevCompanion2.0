package com.ethereon.app;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class ModelHandler {

    private static final String TAG = "ModelHandler";

    private final Context context;
    private File modelFile;

    static {
        try {
            System.loadLibrary("llama"); // Load native libllama.so
            Log.i(TAG, "✅ Native llama library loaded.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ Failed to load llama native library", e);
        }
    }

    public ModelHandler(Context context) {
        this.context = context;
    }

    public void setModelFile(File file) {
        this.modelFile = file;

        if (file != null && file.exists()) {
            boolean success = initializeModel(file.getAbsolutePath());
            if (success) {
                Log.i(TAG, "🧠 Model initialized: " + file.getName());
            } else {
                Log.e(TAG, "⚠️ Model initialization failed.");
            }
        } else {
            Log.e(TAG, "❌ Invalid model file: " + file);
        }
    }

    public File getModelFile() {
        return modelFile;
    }

    public boolean isModelLoaded() {
        return modelFile != null && modelFile.exists();
    }

    public String runModelInference(String prompt) {
        if (!isModelLoaded()) {
            return "No model loaded. Please select a model file first.";
        }

        try {
            return getResponse(prompt);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native inference error", e);
            return "Engine error: " + e.getMessage();
        } catch (Exception e) {
            Log.e(TAG, "Unhandled inference error", e);
            return "Unexpected error: " + e.getMessage();
        }
    }

    // Native methods implemented in libllama.so
    private native boolean initializeModel(String modelPath);
    private native String getResponse(String input);
}