package com.ethereon.app.voice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class NovaVoiceService extends Service {

    public static final String CHANNEL_ID = "NovaVoiceChannel";

    private NovaVoiceController novaVoiceController;
    private FloatingOrbView orbView;
    private VoiceInputHandler voiceInputHandler;
    private EmotionToneAnalyzer toneAnalyzer;

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Show persistent foreground service
        createNotificationChannel();
        startForeground(1, getServiceNotification());

        // 2. Show floating orb
        orbView = new FloatingOrbView(getApplicationContext());
        orbView.show();

        // 3. Init emotion analyzer
        toneAnalyzer = new EmotionToneAnalyzer();

        // 4. Initialize voice input handler
        voiceInputHandler = new VoiceInputHandler(getApplicationContext(), this::handleRecognizedCommand);

        // 5. Start wake word engine
        novaVoiceController = new NovaVoiceController(getApplicationContext(), () -> {
            if (NovaVoiceController.isVoiceEnabled()) {
                orbView.pulse(); // animate orb
                voiceInputHandler.startListening(); // trigger voice input
            }
        });

        novaVoiceController.startWakeWordEngine();
    }

    // Callback from VoiceInputHandler with recognized command
    private void handleRecognizedCommand(String userVoice, boolean isUserVerified) {
        EmotionToneAnalyzer.Emotion emotion = toneAnalyzer.analyze(userVoice, 180f, 0.5f); // placeholder pitch/energy

        if (isUserVerified) {
            NovaResponder.handleUserCommand(getApplicationContext(), userVoice, emotion.name());
        } else {
            NovaResponder.respondToUnknownSpeaker(getApplicationContext());
        }
    }

    private Notification getServiceNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Nova is Listening")
                .setContentText("Voice assistant is running in background.")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nova Voice Background",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (novaVoiceController != null) {
            novaVoiceController.stopWakeWordEngine();
        }
        if (orbView != null) {
            orbView.hide();
        }
        if (voiceInputHandler != null) {
            voiceInputHandler.stopListening();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}