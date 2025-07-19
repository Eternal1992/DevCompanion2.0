// File: SettingsFragment.java
package com.ethereon.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private Switch switchPerformanceMode;
    private Switch switchAutoCompress;
    private Switch switchEnableLogs;
    private Switch switchEmotionDetection;
    private Switch switchVoiceProfile;
    private Switch switchMemoryBackup;
    private TextView versionInfo;

    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        preferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);

        switchPerformanceMode = view.findViewById(R.id.switchPerformanceMode);
        switchAutoCompress = view.findViewById(R.id.switchAutoCompress);
        switchEnableLogs = view.findViewById(R.id.switchEnableLogs);
        switchEmotionDetection = view.findViewById(R.id.switchEmotionDetection);
        switchVoiceProfile = view.findViewById(R.id.switchVoiceProfile);
        switchMemoryBackup = view.findViewById(R.id.switchMemoryBackup);
        versionInfo = view.findViewById(R.id.versionText);

        versionInfo.setText("Ethereon 3.0");

        // Load saved values
        switchPerformanceMode.setChecked(preferences.getBoolean("performance_mode", false));
        switchAutoCompress.setChecked(preferences.getBoolean("auto_compress", false));
        switchEnableLogs.setChecked(preferences.getBoolean("enable_logs", false));
        switchEmotionDetection.setChecked(preferences.getBoolean("emotion_detection", false));
        switchVoiceProfile.setChecked(preferences.getBoolean("voice_profile", false));
        switchMemoryBackup.setChecked(preferences.getBoolean("memory_backup_enabled", true));

        // Save updated values
        switchPerformanceMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean("performance_mode", isChecked).apply());

        switchAutoCompress.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean("auto_compress", isChecked).apply());

        switchEnableLogs.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean("enable_logs", isChecked).apply());

        switchEmotionDetection.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean("emotion_detection", isChecked).apply());

        switchVoiceProfile.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean("voice_profile", isChecked).apply());

        switchMemoryBackup.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean("memory_backup_enabled", isChecked).apply());

        return view;
    }
}