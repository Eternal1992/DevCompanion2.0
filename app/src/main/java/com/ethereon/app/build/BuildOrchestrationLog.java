package com.ethereon.app.build;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BuildOrchestrationLog {

    private static final String TAG = "BuildOrchestrationLog";

    private final List<String> logs;

    public BuildOrchestrationLog() {
        this.logs = new ArrayList<>();
    }

    public void add(String log) {
        logs.add(log);
        Log.d(TAG, log);
    }

    public void add(String step, String message) {
        String formatted = "[" + step + "] " + message;
        logs.add(formatted);
        Log.d(TAG, formatted);
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public String getFullLog() {
        return String.join("\n", logs);
    }

    public void clear() {
        logs.clear();
    }

    public boolean isEmpty() {
        return logs.isEmpty();
    }

    @Override
    public String toString() {
        return getFullLog();
    }
}