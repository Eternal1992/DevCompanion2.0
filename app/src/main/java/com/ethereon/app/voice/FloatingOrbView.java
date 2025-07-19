package com.ethereon.app.voice;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class FloatingOrbView {

    private final Context context;
    private final WindowManager windowManager;
    private final ImageView orbView;
    private final WindowManager.LayoutParams params;

    private float initialX, initialY;
    private float initialTouchX, initialTouchY;

    public FloatingOrbView(Context context) {
        this.context = context.getApplicationContext();
        windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);

        orbView = new ImageView(this.context);
        orbView.setLayoutParams(new WindowManager.LayoutParams(120, 120));
        orbView.setImageDrawable(createOrbDrawable());

        int overlayType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        params = new WindowManager.LayoutParams(
                120, 120,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 200;

        orbView.setOnTouchListener(this::onTouchMove);
    }

    private GradientDrawable createOrbDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(0xFF00BFFF); // Light blue
        drawable.setAlpha(200); // Slight transparency
        drawable.setStroke(4, 0xFFFFFFFF); // White border
        return drawable;
    }

    private boolean onTouchMove(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                params.x = (int) (initialX + (event.getRawX() - initialTouchX));
                params.y = (int) (initialY + (event.getRawY() - initialTouchY));
                windowManager.updateViewLayout(orbView, params);
                return true;
        }
        return false;
    }

    public void show() {
        try {
            windowManager.addView(orbView, params);
        } catch (Exception ignored) {}
    }

    public void hide() {
        try {
            windowManager.removeView(orbView);
        } catch (Exception ignored) {}
    }
}