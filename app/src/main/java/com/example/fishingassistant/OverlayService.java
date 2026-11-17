package com.example.fishingassistant;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.util.DisplayMetrics;
import android.widget.*;

public class OverlayService extends Service {
    WindowManager wm; WindowManager.LayoutParams panelLp, leftLp, rightLp;
    View panel,left,right; TextView state; Handler h=new Handler();
    boolean running=false, strike=false; float screenW,screenH;
    float leftX=90,leftY=500,rightX=1200,rightY=500; int screenWidth,screenHeight;
    Runnable loop=new Runnable(){public void run(){
        if(!running)return;
        if(!strike){
            FishingAccessibilityService.tap(leftX,leftY);
            h.postDelayed(()->{if(running&&!strike)FishingAccessibilityService.tap(leftX,leftY);},650);
            state.setText("MENUNGGU STRIKE");
            h.postDelayed(this,3500);
        } else {
            state.setText("STRIKE • MENARIK");
            FishingAccessibilityService.hold(rightX,rightY,450);
            h.postDelayed(()->{if(running&&strike)FishingAccessibilityService.release(rightX,rightY);},520);
            h.postDelayed(this,650);
        }
    }};
    @Override public void onCreate(){super.onCreate();wm=(WindowManager)getSystemService(WINDOW_SERVICE);showPanel();register();}
    void register(){registerReceiver(receiver,new IntentFilter("FISH_DETECTION"),RECEIVER_NOT_EXPORTED);}
    BroadcastReceiver receiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){strike=i.getBooleanExtra("strike",false); if(state!=null)state.setText(strike?"STRIKE TERDETEKSI":"MENUNGGU STRIKE");}};
    int type(){return Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;}
    TextView circle(String label,int color){
        TextView v=new TextView(this);v.setText(label);v.setTextColor(Color.WHITE);v.setTextSize(12);v.setGravity(Gravity.CENTER);
        GradientDrawable bg=new GradientDrawable();bg.setColor(color);bg.setShape(GradientDrawable.OVAL);bg.setStroke(3,Color.WHITE);v.setBackground(bg);v.setElevation(12);return v;
    }
    void showPanel(){
        int flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        panel=new LinearLayout(this);((LinearLayout)panel).setOrientation(LinearLayout.VERTICAL);panel.setPadding(14,10,14,10);
        GradientDrawable bg=new GradientDrawable();bg.setColor(0xEE18212B);bg.setCornerRadius(22);panel.setBackground(bg);
        LinearLayout row=new LinearLayout(this);Button start=new Button(this);start.setText("START");Button stop=new Button(this);stop.setText("STOP");
        start.setOnClickListener(v->{running=true;h.removeCallbacks(loop);loop.run();});stop.setOnClickListener(v->{running=false;h.removeCallbacks(loop);state.setText("STOPPED");});
        row.addView(start);row.addView(stop);state=new TextView(this);state.setTextColor(Color.WHITE);state.setText("SIAP • Atur posisi bulatan");state.setPadding(8,8,8,8);
        ((LinearLayout)panel).addView(row);((LinearLayout)panel).addView(state);
        panelLp=new WindowManager.LayoutParams(-2,-2,type,flags,PixelFormat.TRANSLUCENT);panelLp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;panelLp.y=80;wm.addView(panel,panelLp);
        left=circle("MANCING",0xFF2387D5);right=circle("TARIK",0xFF159447);
        leftLp=new WindowManager.LayoutParams(92,92,type,flags,PixelFormat.TRANSLUCENT);leftLp.gravity=Gravity.TOP|Gravity.LEFT;leftLp.x=28;leftLp.y=startY;
        rightLp=new WindowManager.LayoutParams(92,92,type,flags,PixelFormat.TRANSLUCENT);rightLp.gravity=Gravity.TOP|Gravity.LEFT;rightLp.x=rightStartX;rightLp.y=startY;
        wm.addView(left,leftLp);wm.addView(right,rightLp);
        drag(left,leftLp,true);drag(right,rightLp,false);
    }
    void drag(View v,WindowManager.LayoutParams lp,boolean isLeft){
        v.setOnTouchListener(new View.OnTouchListener(){int sx,sy,ox,oy;long down;
            public boolean onTouch(View view,MotionEvent e){
                if(e.getAction()==0){sx=(int)e.getRawX();sy=(int)e.getRawY();ox=lp.x;oy=lp.y;down=System.currentTimeMillis();return true;}
                if(e.getAction()==2){lp.x=ox+(int)e.getRawX()-sx;lp.y=oy+(int)e.getRawY()-sy;wm.updateViewLayout(v,lp);return true;}
                if(e.getAction()==1){if(isLeft){leftX=lp.x+46;leftY=lp.y+46;}else{rightX=lp.x+46;rightY=lp.y+46;}return true;}return true;
            }});
    }
    @Override public int onStartCommand(Intent i,int f,int id){return START_STICKY;}
    @Override public void onDestroy(){running=false;h.removeCallbacksAndMessages(null);try{unregisterReceiver(receiver);}catch(Exception ignored){} if(wm!=null){if(panel!=null)wm.removeView(panel);if(left!=null)wm.removeView(left);if(right!=null)wm.removeView(right);}super.onDestroy();}
    @Override public android.os.IBinder onBind(Intent i){return null;}
}
