package com.ethereon.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ethereon.app.voice.NovaVoiceController;
import com.ethereon.app.voice.NovaVoiceService;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "app_settings";
    private static final String PREF_DARK_MODE = "dark_mode";
    private static final String PREF_VOICE_COMMANDS = "voice_commands";

    private Switch switchDarkMode;
    private Switch switchVoiceCommands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchVoiceCommands = findViewById(R.id.switchVoiceCommands);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(PREF_DARK_MODE, false);
        boolean isVoiceCommands = prefs.getBoolean(PREF_VOICE_COMMANDS, true);

        switchDarkMode.setChecked(isDarkMode);
        switchVoiceCommands.setChecked(isVoiceCommands);

        applyTheme(isDarkMode);
        updateVoiceService(isVoiceCommands);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_DARK_MODE, isChecked).apply();
            applyTheme(isChecked);
            Toast.makeText(this, "Dark Mode " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        switchVoiceCommands.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_VOICE_COMMANDS, isChecked).apply();
            updateVoiceService(isChecked);
            Toast.makeText(this, "Voice Commands " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });
    }

    private void applyTheme(boolean enableDark) {
        AppCompatDelegate.setDefaultNightMode(enableDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void updateVoiceService(boolean enabled) {
        Intent serviceIntent = new Intent(this, NovaVoiceService.class);
        serviceIntent.setAction(enabled ? NovaVoiceService.ACTION_START_WAKE_WORD : NovaVoiceService.ACTION_STOP_WAKE_WORD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}