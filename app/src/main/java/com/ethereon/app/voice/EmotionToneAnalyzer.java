package com.ethereon.app.voice;

import android.util.Log;

import java.util.Locale;

public class EmotionToneAnalyzer {

    private static final String TAG = "EmotionToneAnalyzer";

    // Thresholds
    private static final float HIGH_PITCH = 230f;
    private static final float LOW_PITCH = 160f;
    private static final float VERY_LOW_PITCH = 140f;
    private static final float HIGH_ENERGY = 0.7f;
    private static final float VERY_HIGH_ENERGY = 0.8f;
    private static final float LOW_ENERGY = 0.3f;
    private static final float MID_ENERGY = 0.5f;

    public enum Emotion {
        CALM,
        HAPPY,
        SAD,
        ANGRY,
        STRESSED,
        UNKNOWN
    }

    /**
     * Primary tone analysis method using pitch and energy.
     */
    public Emotion analyzeTone(String transcript, float pitch, float energy) {
        String lower = transcript.toLowerCase(Locale.ROOT);

        if (lower.contains("tired") || lower.contains("sad") || lower.contains("down")) {
            return Emotion.SAD;
        } else if (lower.contains("angry") || lower.contains("mad") || lower.contains("frustrated")) {
            return Emotion.ANGRY;
        } else if (lower.contains("excited") || lower.contains("yay") || lower.contains("great")) {
            return Emotion.HAPPY;
        }

        // Analyze pitch/energy if keywords don’t apply
        if (pitch > HIGH_PITCH && energy > HIGH_ENERGY) {
            return Emotion.HAPPY;
        } else if (pitch < LOW_PITCH && energy < LOW_ENERGY) {
            return Emotion.SAD;
        } else if (energy > VERY_HIGH_ENERGY) {
            return Emotion.ANGRY;
        } else if (pitch < VERY_LOW_PITCH && energy > MID_ENERGY) {
            return Emotion.STRESSED;
        } else if (pitch >= VERY_LOW_PITCH && pitch <= HIGH_PITCH && energy <= MID_ENERGY) {
            return Emotion.CALM;
        }

        return Emotion.UNKNOWN;
    }

    /**
     * Lightweight keyword-only fallback method.
     */
    public static String analyze(String transcript) {
        String lower = transcript.toLowerCase(Locale.ROOT);
        if (lower.contains("yay") || lower.contains("great") || lower.contains("fun")) return "happy";
        if (lower.contains("tired") || lower.contains("depressed") || lower.contains("alone")) return "sad";
        if (lower.contains("mad") || lower.contains("angry") || lower.contains("irritated")) return "angry";
        if (lower.contains("busy") || lower.contains("overwhelmed")) return "stressed";
        if (lower.contains("okay") || lower.contains("fine")) return "calm";
        return "neutral";
    }

    public void logEmotionResult(String text, float pitch, float energy, Emotion emotion) {
        Log.i(TAG, "Transcript: \"" + text + "\" | Pitch: " + pitch + " | Energy: " + energy + " → Emotion: " + emotion);
    }
}