package com.ethereon.models;

import java.util.Locale;

/**
 * Parses natural language input and generates a simplified app blueprint.
 */
public class AppBlueprintGenerator {

    public static AppBlueprint fromNaturalLanguage(String input) {
        AppBlueprint blueprint = new AppBlueprint();

        // Extract app name
        String lower = input.toLowerCase(Locale.ROOT);
        if (lower.contains("notepad") || lower.contains("notes")) {
            blueprint.setAppName("QuickNotepad");
            blueprint.setPackageName("com.example.quicknotepad");
            blueprint.setMainFeature("Note-taking");
        } else if (lower.contains("timer") || lower.contains("countdown")) {
            blueprint.setAppName("SimpleTimer");
            blueprint.setPackageName("com.example.simpletimer");
            blueprint.setMainFeature("Countdown Timer");
        } else if (lower.contains("chat") || lower.contains("messenger")) {
            blueprint.setAppName("ChatBuddy");
            blueprint.setPackageName("com.example.chatbuddy");
            blueprint.setMainFeature("Messaging");
        } else {
            blueprint.setAppName("MyNovaApp");
            blueprint.setPackageName("com.example.mynovaapp");
            blueprint.setMainFeature("Basic Template");
        }

        return blueprint;
    }
}