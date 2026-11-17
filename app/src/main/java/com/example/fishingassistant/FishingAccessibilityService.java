package com.example.fishingassistant;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

public class FishingAccessibilityService extends AccessibilityService {
    static FishingAccessibilityService instance;
    @Override public void onServiceConnected(){instance=this;}
    @Override public void onAccessibilityEvent(AccessibilityEvent e){}
    @Override public void onInterrupt(){}
    @Override public void onDestroy(){instance=null;super.onDestroy();}
    public static void tap(float x,float y){gesture(x,y,90);}
    public static void hold(float x,float y,long ms){gesture(x,y,Math.max(150,Math.min(ms,1800)));}
    public static void release(float x,float y){tap(x,y);}
    private static void gesture(float x,float y,long duration){
        if(instance==null)return;
        Path p=new Path();p.moveTo(x,y);
        GestureDescription.StrokeDescription s=new GestureDescription.StrokeDescription(p,0,duration);
        instance.dispatchGesture(new GestureDescription.Builder().addStroke(s).build(),null,null);
    }
}