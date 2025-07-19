package com.ethereon.app.voice;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ethereon.app.R;

public class NovaOrbController {

    private final Context context;
    private final WindowManager windowManager;
    private View orbView;
    private boolean isVisible = false;

    private float initialTouchX, initialTouchY;
    private int initialX, initialY;

    public NovaOrbController(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showOrb() {
        if (isVisible) return;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 300;

        LayoutInflater inflater = LayoutInflater.from(context);
        orbView = inflater.inflate(R.layout.view_floating_orb, null);

        ImageView orbImage = orbView.findViewById(R.id.orbImage);
        Drawable orbDrawable = context.getResources().getDrawable(R.drawable.orb_pulse, null);
        orbImage.setImageDrawable(orbDrawable);

        orbView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(orbView, params);
                    return true;
            }
            return false;
        });

        windowManager.addView(orbView, params);
        isVisible = true;
    }

    public void hideOrb() {
        if (isVisible && orbView != null) {
            windowManager.removeView(orbView);
            isVisible = false;
        }
    }

    public boolean isOrbVisible() {
        return isVisible;
    }

    public void pulse() {
        if (orbView != null) {
            ImageView orbImage = orbView.findViewById(R.id.orbImage);
            orbImage.animate()
                    .scaleX(1.2f).scaleY(1.2f)
                    .setDuration(200)
                    .withEndAction(() -> orbImage.animate()
                            .scaleX(1.0f).scaleY(1.0f)
                            .setDuration(200)
                            .start())
                    .start();
        }
    }
}