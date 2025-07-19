package com.ethereon.app;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;

import java.util.HashMap;

public class DebugOverlayManager {

    private final Activity activity;
    private final LinearLayout debugContainer;
    private final HashMap<String, TextView> debugStreams = new HashMap<>();

    public DebugOverlayManager(Activity activity, ViewGroup rootView) {
        this.activity = activity;

        debugContainer = new LinearLayout(activity);
        debugContainer.setOrientation(LinearLayout.VERTICAL);
        debugContainer.setBackgroundColor(Color.parseColor("#CC000000"));
        debugContainer.setPadding(16, 16, 16, 16);

        ScrollView scroll = new ScrollView(activity);
        scroll.addView(debugContainer);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM;

        rootView.addView(scroll, params);
    }

    public void addLog(String category, String message) {
        TextView stream = debugStreams.get(category);
        if (stream == null) {
            stream = new TextView(activity);
            stream.setTextColor(Color.GREEN);
            stream.setText(category + ":\n");
            debugContainer.addView(stream);
            debugStreams.put(category, stream);
        }

        String updatedText = stream.getText().toString() + "- " + message + "\n";
        stream.setText(updatedText);
    }

    public void clearCategory(String category) {
        if (debugStreams.containsKey(category)) {
            debugStreams.get(category).setText(category + ":\n");
        }
    }

    public void clearAll() {
        for (TextView stream : debugStreams.values()) {
            stream.setText("");
        }
    }
}