package com.ethereon.app.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class VoiceInputHandler {

    private static final String TAG = "VoiceInputHandler";
    private final Context context;
    private final SpeechRecognizer speechRecognizer;

    public VoiceInputHandler(Context context) {
        this.context = context;
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        initRecognizer();
    }

    private void initRecognizer() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "Ready for speech");
            }

            @Override public void onBeginningOfSpeech() {
                Log.d(TAG, "Speech started");
            }

            @Override public void onRmsChanged(float rmsdB) {}

            @Override public void onBufferReceived(byte[] buffer) {}

            @Override public void onEndOfSpeech() {
                Log.d(TAG, "Speech ended");
            }

            @Override public void onError(int error) {
                Log.e(TAG, "Recognition error: " + error);
            }

            @Override public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenCommand = matches.get(0).toLowerCase();
                    Log.d(TAG, "Heard command: " + spokenCommand);
                    NovaIntentHandler.handleCommand(context, spokenCommand);
                }
            }

            @Override public void onPartialResults(Bundle partialResults) {}

            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    public void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        speechRecognizer.startListening(intent);
        Log.d(TAG, "Listening started...");
    }

    public void stopListening() {
        speechRecognizer.stopListening();
        Log.d(TAG, "Listening stopped.");
    }

    public void destroy() {
        speechRecognizer.destroy();
    }
}