package com.example.fishingassistant;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class OverlayService extends Service {

    private WindowManager wm;
    private View panel;
    private WindowManager.LayoutParams panelLp, leftLp, rightLp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        int startY = 300;
        int rightStartX = 800;

        // Membuat Panel Utama
        panel = new LinearLayout(this);
        GradientDrawable panelBg = new GradientDrawable();
        panelBg.setColor(0xEE18212B);
        panelBg.setCornerRadius(22);
        panel.setBackground(panelBg);

        panelLp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type, flags, PixelFormat.TRANSLUCENT
        );
        panelLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        panelLp.y = 80;
        
        try {
            wm.addView(panel, panelLp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wm != null && panel != null) {
            try {
                wm.removeView(panel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
