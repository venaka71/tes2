package com.example.fishingassistant;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.hardware.display.*;
import android.media.*;
import android.media.projection.*;
import android.os.*;
import java.nio.*;

public class CaptureService extends Service {
    MediaProjection projection; ImageReader reader; VirtualDisplay display; HandlerThread thread; Handler handler;
    boolean last=false; int width,height;
    @Override public int onStartCommand(Intent intent,int flags,int id){
        createNotification();
        int code=intent.getIntExtra("code",0);Intent data=intent.getParcelableExtra("data");
        MediaProjectionManager m=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        projection=m.getMediaProjection(code,data);
        width=getResources().getDisplayMetrics().widthPixels;height=getResources().getDisplayMetrics().heightPixels;
        reader=ImageReader.newInstance(width,height,PixelFormat.RGBA_8888,2);
        thread=new HandlerThread("screen-analysis");thread.start();handler=new Handler(thread.getLooper());
        display=projection.createVirtualDisplay("FishingCapture",width,height,getResources().getDisplayMetrics().densityDpi,DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,reader.getSurface(),null,handler);
        reader.setOnImageAvailableListener(r->{Image image=null;try{image=r.acquireLatestImage();if(image!=null)analyze(image);}catch(Exception ignored){}finally{if(image!=null)image.close();}},handler);
        return START_NOT_STICKY;
    }
    void analyze(Image image){
        Image.Plane p=image.getPlanes()[0];ByteBuffer b=p.getBuffer();int row=p.getRowStride(),pix=p.getPixelStride();
        // ROI awal proporsional untuk game landscape pada itel RS4. Harus dikalibrasi dengan tampilan game nyata.
        int x1=(int)(width*(width>=height?0.48:0.40));
        int x2=(int)(width*(width>=height?0.82:0.82));
        int y1=(int)(height*(width>=height?0.16:0.28));
        int y2=(int)(height*(width>=height?0.88:0.78));
        int green=0,dark=0,samples=0;
        for(int y=y1;y<y2;y+=3)for(int x=x1;x<x2;x+=3){
            int pos=y*row+x*pix;if(pos+2>=b.limit())continue;
            int rr=b.get(pos)&255,gg=b.get(pos+1)&255,bb=b.get(pos+2)&255;
            if(gg>105&&gg>rr*1.18&&gg>bb*1.08)green++;
            if(rr<65&&gg<75&&bb<85)dark++;
            samples++;
        }
        // Strike diperkirakan ketika elemen hijau khas mini-game muncul pada ROI.
        boolean detected=green>Math.max(55,samples/95);
        if(detected!=last){last=detected;Intent out=new Intent("FISH_DETECTION");out.setPackage(getPackageName());out.putExtra("strike",detected);sendBroadcast(out);}
    }
    void createNotification(){
        String ch="fish_capture";if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel(ch,"Fishing screen detection",NotificationManager.IMPORTANCE_LOW);((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);}
        Notification n=new Notification.Builder(this,ch).setContentTitle("Fishing Assistant").setContentText("Deteksi layar sedang berjalan").setSmallIcon(android.R.drawable.ic_media_play).build();startForeground(22,n);
    }
    @Override public void onDestroy(){if(display!=null)display.release();if(reader!=null)reader.close();if(projection!=null)projection.stop();if(thread!=null)thread.quitSafely();super.onDestroy();}
    @Override public android.os.IBinder onBind(Intent i){return null;}
}