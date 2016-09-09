package com.coderfun.radarview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.coderfun.library.RadarView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    RadarView radarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radarView = (RadarView) findViewById(R.id.radar);
        LinkedList<Bitmap> apps = new LinkedList<>();
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app1));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app2));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app3));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app4));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app5));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app6));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app7));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app8));
        apps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.app9));
        radarView.setBitmaps(apps);
        radarView.setBitmapSlotCount(8).setBitmapShowCount(5).setDuration(2000);
        radarView.startRadarAnimation();
    }


    @Override
    protected void onDestroy() {
        if (radarView != null) {
            radarView.destory();
        }
        super.onDestroy();
    }
}
