package com.example.fishingassistant;

import android.app.*;
import android.content.*;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    static final int REQ_CAPTURE=44;
    MediaProjectionManager projectionManager;
    TextView status;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        projectionManager=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        LinearLayout box=new LinearLayout(this); box.setOrientation(1); box.setPadding(28,36,28,24);
        TextView title=new TextView(this); title.setText("Fishing Assistant"); title.setTextSize(26); title.setTypeface(null,1);
        status=new TextView(this); status.setText("Versi itel RS4 • Android 13 • RAM 8 GB\n\n1. Buka game dan ubah ke landscape terlebih dahulu.\n2. Izinkan tampil di atas aplikasi lain.\n3. Aktifkan layanan Aksesibilitas.\n4. Tampilkan overlay, lalu geser bulatan ke tombol game.\n5. Aktifkan tangkapan layar dan setujui dialog Android.");
        Button overlay=btn("1. Izinkan overlay"); overlay.setOnClickListener(v->{ if(!Settings.canDrawOverlays(this)) startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))); else status.setText("Izin overlay sudah aktif."); });
        Button access=btn("2. Aktifkan aksesibilitas"); access.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        Button start=btn("3. Tampilkan tombol melayang"); start.setOnClickListener(v->{ if(!Settings.canDrawOverlays(this)){status.setText("Aktifkan izin overlay terlebih dahulu.");return;} startService(new Intent(this,OverlayService.class)); status.setText("Overlay aktif. Pastikan game sudah landscape. Geser bulatan MANCING dan TARIK tepat ke tombol game."); });
        Button capture=btn("4. Aktifkan deteksi layar"); capture.setOnClickListener(v->startActivityForResult(projectionManager.createScreenCaptureIntent(),REQ_CAPTURE));
        Button stop=btn("Hentikan overlay"); stop.setOnClickListener(v->stopService(new Intent(this,OverlayService.class)));
        box.addView(title); box.addView(status); box.addView(overlay); box.addView(access); box.addView(start); box.addView(capture); box.addView(stop);
        ScrollView sc=new ScrollView(this); sc.addView(box); setContentView(sc);
    }
    Button btn(String s){Button b=new Button(this);b.setText(s);return b;}
    @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==REQ_CAPTURE&&c==RESULT_OK&&d!=null){Intent i=new Intent(this,CaptureService.class);i.putExtra("code",c);i.putExtra("data",d);if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);status.setText("Deteksi layar aktif. Area indikator dipantau otomatis.");}}
}