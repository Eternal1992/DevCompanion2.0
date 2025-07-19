package com.ethereon.app.voice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ethereon.app.MainActivity;
import com.ethereon.app.ProjectDetailActivity;
import com.ethereon.app.SettingsActivity;

public class CommandRouter {

    private static final String TAG = "CommandRouter";

    public static void handleCommand(Context context, String command) {
        if (command == null || command.trim().isEmpty()) {
            Log.w(TAG, "Received empty command.");
            return;
        }

        command = command.toLowerCase().trim();

        if (command.contains("new project")) {
            openActivity(context, MainActivity.class);
        } else if (command.contains("open settings")) {
            openActivity(context, SettingsActivity.class);
        } else if (command.contains("continue project") || command.contains("resume project")) {
            openActivity(context, ProjectDetailActivity.class);
        } else {
            Log.i(TAG, "Unrecognized command: " + command);
            NovaResponder.speak(context, "Sorry, I didn't understand that. Try saying something like 'open settings' or 'start a new project'.");
        }
    }

    private static void openActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.i(TAG, "Launched activity: " + activityClass.getSimpleName());
    }
}