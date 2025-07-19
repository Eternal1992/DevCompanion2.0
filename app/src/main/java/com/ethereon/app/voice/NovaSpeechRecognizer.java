package com.ethereon.app.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class NovaSpeechRecognizer {

    private static final String TAG = "NovaSpeechRecognizer";

    private final Context context;
    private final SpeechRecognizer speechRecognizer;
    private final Intent recognizerIntent;

    public NovaSpeechRecognizer(Context context) {
        this.context = context.getApplicationContext();
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        this.recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        this.recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
    }

    public void startListening() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.i(TAG, "Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Log.i(TAG, "End of speech detected.");
            }

            @Override
            public void onError(int error) {
                Log.e(TAG, "Speech recognition error: " + error);
                NovaResponder.speak(context, "Oops, I couldn't hear that. Could you try again?");
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedCommand = matches.get(0);
                    Log.i(TAG, "Recognized command: " + recognizedCommand);
                    NovaCommandRouter router = new NovaCommandRouter(context);
                    router.routeCommand(recognizedCommand);
                } else {
                    NovaResponder.speak(context, "I didn't catch that. Could you try again?");
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        speechRecognizer.startListening(recognizerIntent);
    }

    public void stopListening() {
        speechRecognizer.stopListening();
        speechRecognizer.cancel();
        speechRecognizer.destroy();
    }
}