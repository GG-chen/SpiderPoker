package com.chen.spiderpoker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        Log.d("MainActivity", "onCreate!!!!!!!!!!!!!!!!!!!!!!!");
        Log.d("MainActivity", "initData: width = " + SCREEN_WIDTH + "\n" + "height == " + SCREEN_HEIGHT);
        GameView gameView = new GameView(this);
        gameView.setWidth(SCREEN_WIDTH);
        gameView.setHeight(SCREEN_HEIGHT);
        gameView.initData();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gust);
        //drawable: width = 315  height == 450
       // Log.d("MainActivity", "drawable: width = " + bitmap.getWidth() + "\n" + "height == " + bitmap.getHeight());
        //((RelativeLayout)findViewById(R.id.activity_main)).addView(gameView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(gameView);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
