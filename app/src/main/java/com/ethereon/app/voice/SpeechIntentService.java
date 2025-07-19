// File: SpeechIntentService.java
package com.ethereon.app.voice;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

public class SpeechIntentService extends IntentService {

    private static final String TAG = "SpeechIntentService";
    private static final String ACTION_PROCESS_VOICE_COMMAND = "com.ethereon.app.action.PROCESS_VOICE_COMMAND";
    private static final String EXTRA_COMMAND = "com.ethereon.app.extra.COMMAND";

    public SpeechIntentService() {
        super("SpeechIntentService");
    }

    public static void startActionProcessVoiceCommand(Context context, String command) {
        Intent intent = new Intent(context, SpeechIntentService.class);
        intent.setAction(ACTION_PROCESS_VOICE_COMMAND);
        intent.putExtra(EXTRA_COMMAND, command);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && ACTION_PROCESS_VOICE_COMMAND.equals(intent.getAction())) {
            final String command = intent.getStringExtra(EXTRA_COMMAND);
            if (command != null && !command.isEmpty()) {
                new NovaCommandRouter(getApplicationContext()).routeCommand(command);
            }
        }
    }
}