package com.ethereon.app.voice;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.ethereon.app.ProjectDetailActivity;
import com.ethereon.app.memory.MemoryRecallProcessor;
import com.ethereon.app.memory.MemoryRetentionEngine;
import com.ethereon.app.memory.NovaMemoryManager;
import com.ethereon.voice.NovaAppCreationFlow;
import com.ethereon.voice.NovaAppCreationFlow.NovaAppCreationCallback;
import com.ethereon.chat.ChatSessionManager;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class NovaResponder {

    private static final String TAG = "NovaResponder";
    private static TextToSpeech tts;

    public static void respondToWakeWord(Context context) {
        if (!VoiceInputHandler.isRecognizedUser()) {
            speak(context, "Sorry, I don't recognize your voice.");
            return;
        }

        String emotion = EmotionToneAnalyzer.analyze("What would you like to do?");
        String prompt = applyEmotionTone("What would you like to do?", emotion);
        speak(context, prompt);
    }

    public static void handleUserCommand(Context context, String command, String emotion) {
        if (!VoiceInputHandler.isRecognizedUser()) {
            speak(context, "Sorry, I can only respond to my master.");
            return;
        }

        if (command == null || command.trim().isEmpty()) {
            String neutral = applyEmotionTone("Sorry, I didn't catch that.", "neutral");
            speak(context, neutral);
            return;
        }

        String lowerCmd = command.toLowerCase();
        String response;

        MemoryRecallProcessor recallProcessor = new MemoryRecallProcessor(context);
        NovaMemoryManager memoryManager = new NovaMemoryManager(context);
        ChatSessionManager sessionManager = new ChatSessionManager(context);

        if (lowerCmd.contains("new project")) {
            response = applyEmotionTone("Opening a new project!", "excited");
            speak(context, response);

            Intent intent = new Intent(context, ProjectDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else if (lowerCmd.contains("remember") || lowerCmd.contains("recall") || lowerCmd.contains("what do you know about")) {
            String keyword = extractKeyword(lowerCmd);
            List<String> results = recallProcessor.recallByKeyword(keyword);
            if (!results.isEmpty()) {
                speak(context, "Here's what I remember about " + keyword + ":");
                for (String result : results.subList(0, Math.min(3, results.size()))) {
                    speak(context, result);
                }
            } else {
                speak(context, "I don't recall anything about " + keyword);
            }
            response = "Memory recall attempted.";

        } else if (lowerCmd.contains("summarize memory") || lowerCmd.contains("summarize what you remember")) {
            String summary = recallProcessor.summarizeRecentMemory();
            speak(context, "Here's a summary of my memory:");
            speak(context, summary);
            response = "Memory summary shared.";

        } else if (lowerCmd.contains("list memory files") || lowerCmd.contains("show memory")) {
            List<String> files = recallProcessor.listAllMemoryFiles();
            speak(context, "I have the following memory files saved:");
            for (String file : files) {
                speak(context, file);
            }
            response = "Memory file listing complete.";

        } else if (lowerCmd.contains("build") && lowerCmd.contains("app")) {
            speak(context, "Alright! Give me just a moment...");
            new NovaAppCreationFlow(context, sessionManager, memoryManager).beginNewAppFlow(command, new NovaAppCreationCallback() {
                @Override
                public void onProgress(String status) {
                    speak(context, applyEmotionTone(status, "excited"));
                }

                @Override
                public void onSuccess(File apkFile) {
                    speak(context, applyEmotionTone("Your app is ready! I've built it successfully.", "excited"));
                }

                @Override
                public void onError(String message) {
                    speak(context, applyEmotionTone(message, "sad"));
                }
            });
            return; // skip final memory save below — handled in callback
        } else {
            response = applyEmotionTone("Sorry, I didn't understand that.", "confused");
            speak(context, response);
        }

        //  Trigger memory retention
        String chatLog = "[User]: " + command + "\n[Nova]: " + response;
        MemoryRetentionEngine.processMemoryRetention(chatLog);
    }

    private static void speak(Context context, String message) {
        if (tts == null) {
            tts = new TextToSpeech(context.getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "NovaPrompt");
                } else {
                    Log.e(TAG, "TTS initialization failed.");
                }
            });
        } else {
            tts.setLanguage(Locale.US);
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "NovaPrompt");
        }
    }

    private static String applyEmotionTone(String message, String emotion) {
        switch (emotion.toLowerCase()) {
            case "happy":
                return message + " ";
            case "sad":
                return message + " I'm here for you.";
            case "angry":
                return "Okay... let's just take a breath first. " + message;
            case "excited":
                return message + " Let's go!";
            case "confused":
                return "Hmm... " + message;
            default:
                return message;
        }
    }

    private static String extractKeyword(String command) {
        return command.replace("recall", "")
                .replace("remember", "")
                .replace("what do you know about", "")
                .replace("nova", "")
                .trim();
    }

    public static void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}