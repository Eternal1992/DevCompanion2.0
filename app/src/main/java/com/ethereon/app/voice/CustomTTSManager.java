package com.ethereon.app.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class CustomTTSManager {

    private static final String TAG = "CustomTTSManager";

    private final TextToSpeech tts;
    private boolean isReady = false;

    public CustomTTSManager(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;

                // Try to select a custom female voice with sweet tone if available
                if (isReady) {
                    Set<Voice> voices = tts.getVoices();
                    for (Voice voice : voices) {
                        if (voice.getLocale().equals(Locale.US)
                                && voice.getName().toLowerCase().contains("female")
                                && !voice.getName().toLowerCase().contains("network")) {
                            tts.setVoice(voice);
                            Log.i(TAG, "Custom voice selected: " + voice.getName());
                            break;
                        }
                    }
                }
            } else {
                Log.e(TAG, "TTS initialization failed");
            }
        });
    }

    public void speak(String message) {
        if (isReady) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "NovaTTS");
        } else {
            Log.w(TAG, "TTS not ready. Message skipped: " + message);
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}