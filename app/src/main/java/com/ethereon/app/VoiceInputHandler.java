package com.ethereon.app;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceInputHandler {

    private static final int REQUEST_CODE_VOICE_INPUT = 102;
    private final Activity activity;
    private VoiceInputListener listener;

    public interface VoiceInputListener {
        void onVoiceResult(String result);
    }

    public VoiceInputHandler(Activity activity) {
        this.activity = activity;
    }

    public void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your command...");

        try {
            activity.startActivityForResult(intent, REQUEST_CODE_VOICE_INPUT);
        } catch (Exception e) {
            Toast.makeText(activity, "Voice input not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_VOICE_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                if (listener != null) {
                    listener.onVoiceResult(results.get(0));
                }
            } else {
                Toast.makeText(activity, "No voice input detected.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setVoiceInputListener(VoiceInputListener listener) {
        this.listener = listener;
    }

    public static int getRequestCode() {
        return REQUEST_CODE_VOICE_INPUT;
    }
}