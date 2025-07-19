package com.ethereon.app.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
            boolean voiceEnabled = prefs.getBoolean("voice_commands", true);

            if (voiceEnabled) {
                Intent serviceIntent = new Intent(context, NovaVoiceService.class);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}