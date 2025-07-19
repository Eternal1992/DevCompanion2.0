package com.ethereon.app.voice;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.ethereon.app.ProjectDetailActivity;
import com.ethereon.app.MainActivity;
import com.ethereon.app.models.AppIdeaModel;
import com.ethereon.app.build.AppBuilderOrchestrator;
import com.ethereon.app.memory.MemoryRecallProcessor;
import com.ethereon.app.utils.Utils;

import java.util.List;
import java.util.Locale;

public class VoiceCommandProcessor {

    private static final String TAG = "VoiceCommandProcessor";

    private final Context context;
    private TextToSpeech tts;

    public VoiceCommandProcessor(Context context) {
        this.context = context;
        initTextToSpeech();
    }

    private void initTextToSpeech() {
        tts = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                tts.setPitch(1.1f);
                tts.setSpeechRate(1.0f);
            } else {
                Log.e(TAG, "TTS initialization failed");
            }
        });
    }

    public void handleCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            speak("Sorry, I didn't catch that.");
            return;
        }

        String lower = command.toLowerCase();
        String response;

        if (lower.contains("open project")) {
            context.startActivity(new Intent(context, ProjectDetailActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            response = "Opening your project now.";

        } else if (lower.contains("start building")) {
            context.startActivity(new Intent(context, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            response = "Jumping to the builder.";

        } else if (lower.contains("settings")) {
            response = "You can change your preferences in settings.";

        } else if (lower.contains("build") && lower.contains("app")) {
            buildAppFromIdea(command);
            return;

        } else if (lower.contains("recall") || lower.contains("remember") || lower.contains("what do you know about")) {
            String keyword = lower.replace("recall", "")
                                  .replace("remember", "")
                                  .replace("what do you know about", "")
                                  .replace("nova", "")
                                  .trim();
            MemoryRecallProcessor recall = new MemoryRecallProcessor(context);
            List<String> results = recall.recallByKeyword(keyword);
            if (!results.isEmpty()) {
                speak("Here's what I remember about " + keyword + ":");
                for (String s : results.subList(0, Math.min(3, results.size()))) {
                    speak(s);
                }
            } else {
                speak("I don't recall anything about " + keyword + ".");
            }
            return;

        } else {
            response = "I'm not sure how to help with that yet.";
        }

        speak(response);
    }

    private void buildAppFromIdea(String ideaText) {
        AppIdeaModel model = new AppIdeaModel();
        model.setAppName(Utils.generateAppNameFromIdea(ideaText));
        model.setAppDescription(ideaText);
        model.setCategory("Auto");

        AppBuilderOrchestrator orchestrator = new AppBuilderOrchestrator(context);
        boolean success = orchestrator.buildFullApp(model);

        String response = success
                ? "Your app has been created and compiled successfully."
                : "Something went wrong while trying to build the app.";
        speak(response);
    }

    private void speak(String message) {
        if (tts != null) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "NovaResponse");
        } else {
            Log.w(TAG, "TTS not available");
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}