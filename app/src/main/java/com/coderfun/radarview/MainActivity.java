package com.coderfun.radarview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.coderfun.library.RadarView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadarView radarView = (RadarView) findViewById(R.id.radar);
        LinkedList<Bitmap> apps = new LinkedList<>();
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app1));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app2));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app3));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app4));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app5));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app6));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app7));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app8));
        apps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.app9));
        radarView.setBitmaps(apps);
        radarView.setBitmapSlotCount(7).setBitmapShowCount(4).setDuration(4000);
        radarView.startRadarAnimation();
    }
}
