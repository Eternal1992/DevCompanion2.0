package com.ethereon.app;

public class LlamaNative {
    static {
        try {
            System.loadLibrary("llama"); // Loads libllama.so from jniLibs/arm64-v8a
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public static native String run(String prompt, String modelPath);
}