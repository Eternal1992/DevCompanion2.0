package com.ethereon.app.voice;

import android.content.Context;
import android.util.Log;

import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineManager;
import ai.picovoice.porcupine.PorcupineManagerCallback;

public class NovaVoiceController {

    private static final String TAG = "NovaVoiceController";
    private static final String ACCESS_KEY = "TARl7Q1X8e9J7/nQqRYmDfiwXXeTunDhagu1s9d2Ykg55QGRsYoN7g==";
    private static final String WAKE_WORD_PATH = "porcupine/hey-nova_en_android_v2_2_0.ppn";

    private final Context context;
    private PorcupineManager porcupineManager;
    private NovaOrbController orbController;

    private static boolean isVoiceEnabled = true;

    public NovaVoiceController(Context context) {
        this.context = context.getApplicationContext();
        this.orbController = new NovaOrbController(this.context);
    }

    public static void setVoiceEnabled(boolean enabled) {
        isVoiceEnabled = enabled;
    }

    public static boolean isVoiceEnabled() {
        return isVoiceEnabled;
    }

    public void start() {
        if (!isVoiceEnabled) {
            Log.i(TAG, "Voice detection is disabled by settings.");
            return;
        }

        try {
            porcupineManager = new PorcupineManager.Builder()
                    .setAccessKey(ACCESS_KEY)
                    .setKeywordPath(WAKE_WORD_PATH)
                    .setSensitivity(0.7f)
                    .build(context, new PorcupineManagerCallback() {
                        @Override
                        public void invoke(int keywordIndex) {
                            Log.i(TAG, "✨ Wake word detected by Porcupine!");
                            orbController.pulse();
                            NovaResponder.respondToWakeWord(context);
                        }
                    });

            porcupineManager.start();
            orbController.showOrb();
            Log.i(TAG, "🔊 Wake word detection engine started.");

        } catch (PorcupineException e) {
            Log.e(TAG, "❌ Error initializing PorcupineManager", e);
        }
    }

    public void stop() {
        try {
            if (porcupineManager != null) {
                porcupineManager.stop();
                porcupineManager.delete();
                porcupineManager = null;
                Log.i(TAG, "🔇 Wake word engine stopped and released.");
            }
        } catch (PorcupineException e) {
            Log.e(TAG, "❌ Error stopping PorcupineManager", e);
        }

        orbController.hideOrb();
    }
}