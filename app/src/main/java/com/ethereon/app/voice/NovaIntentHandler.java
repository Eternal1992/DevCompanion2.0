package com.ethereon.app.voice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ethereon.app.MainActivity;
import com.ethereon.app.ProjectDetailActivity;

public class NovaIntentHandler {

    private static final String TAG = "NovaIntentHandler";

    public static void handleCommand(Context context, String command) {
        Log.d(TAG, "Handling command: " + command);

        if (command.contains("open project") || command.contains("start project")) {
            Intent intent = new Intent(context, ProjectDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (command.contains("new project")) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.d(TAG, "Unknown command: " + command);
        }
    }
}