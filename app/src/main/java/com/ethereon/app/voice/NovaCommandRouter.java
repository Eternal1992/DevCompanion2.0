package com.ethereon.app.voice;

import android.content.Context;
import android.util.Log;

import java.util.regex.Pattern;

public class NovaCommandRouter {

    private static final String TAG = "NovaCommandRouter";

    private final Context context;
    private final VoiceCommandProcessor commandProcessor;

    public NovaCommandRouter(Context context) {
        this.context = context;
        this.commandProcessor = new VoiceCommandProcessor(context);
    }

    public void routeCommand(String spokenText) {
        if (spokenText == null || spokenText.trim().isEmpty()) {
            Log.w(TAG, "Empty command received");
            return;
        }

        String lower = spokenText.toLowerCase().trim();
        Log.d(TAG, "Routing command: " + lower);

        if (matches(lower, "open (my )?project(s)?")) {
            commandProcessor.handleCommand("open project");

        } else if (matches(lower, "(start|begin) (building|build process)")) {
            commandProcessor.handleCommand("start building");

        } else if (matches(lower, "go to settings|open settings")) {
            commandProcessor.handleCommand("settings");

        } else if (matches(lower, "(create|generate|build|make).*(app|application)")) {
            commandProcessor.handleCommand("create app: " + lower);

        } else if (matches(lower, "launch (nova|assistant)")) {
            commandProcessor.handleCommand("launch assistant");

        } else if (matches(lower, "summarize.*memory|what do you remember")) {
            commandProcessor.handleCommand("summarize memory");

        } else if (matches(lower, "recall|remember|what do you know about")) {
            commandProcessor.handleCommand("recall: " + lower);

        } else if (matches(lower, "exit|shutdown|goodbye")) {
            commandProcessor.handleCommand("shutdown");

        } else {
            // Unknown command fallback
            Log.i(TAG, "Passing unknown command to handler: " + lower);
            commandProcessor.handleCommand(lower);
        }
    }

    private boolean matches(String input, String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(input).find();
    }

    public void shutdown() {
        commandProcessor.shutdown();
    }
}