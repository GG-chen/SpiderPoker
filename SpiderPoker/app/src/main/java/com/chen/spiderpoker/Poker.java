package com.chen.spiderpoker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Shinelon on 2017/4/11.
 */

public class Poker {
    //数字
    private String mNum;
    //花色
    private int mColor;
    //类型  梅花 方块 桃心 黑桃
    private int mType;
    private Context context;
    //开始和结束坐标
    private int startX = -1;
    private int startY = -1;
    private int width = 110;
    private int height = 157;
    private boolean isFace = true;
    private boolean isShade = false;
    private Resources res;
    private int bitmapID;
    private Bitmap faceBitmap;
    private boolean isClickable = false;
    private Bitmap currentBitmap;
    private Bitmap backBitmap;
    private Bitmap shadeBitmap;
    private int distanceX = 0;
    private int distanceY = 0;

    public Poker(Context context) {
        this.context = context;
        res =context.getResources();
    }

    public void draw(Canvas canvas, Paint paint) {
        if (currentBitmap != null) {
            Bitmap bitmap = null;
            if (isFace) {
                if (isShade) {
                    bitmap = shadeBitmap;
                } else {
                    bitmap = currentBitmap;
                }
            } else {
                bitmap = backBitmap;
            }
            Rect des = new Rect();
            des.set(startX + distanceX,startY + distanceY,startX + width + distanceX,startY + height + distanceY);
            canvas.drawBitmap(bitmap, null, des, paint);
        }


    }

    /*
    * name   图片的名字
    * */
    public void init(String mNum, int mColor, int mType, boolean isFace, String name) {
        this.mNum = mNum;
        this.mColor = mColor;
        this.mType = mType;
        this.isFace = isFace;
        bitmapID =  res.getIdentifier("z"+ name,"drawable",context.getPackageName());
        faceBitmap = BitmapFactory.decodeResource(context.getResources(), bitmapID);
        faceBitmap = Utils.zoomImg(faceBitmap, width ,height);
        shadeBitmap = Utils.zoomImg(BitmapFactory.decodeResource(context.getResources(), res.getIdentifier("b" + name, "drawable", context.getPackageName())), width, height);
        backBitmap = Utils.zoomImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.back), width, height);
        currentBitmap = faceBitmap;
        width = currentBitmap.getWidth();
        height = currentBitmap.getHeight();
    }

    public void setFace(boolean face) {
        isFace = face;
    }


    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public void setDistanceXY(int distanceX, int distanceY) {
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        //Log.d("Poker", "setDistanceXY: startX" + startX + "  startY  " + startY);
    }

    public void setDistanceLastXY(int distanceX, int distanceY) {
        this.startX += distanceX;
        this.startY += distanceY;
    }

    public String getmNum() {
        return mNum;
    }

    public int getmColor() {
        return mColor;
    }

    public int getmType() {
        return mType;
    }

    public boolean isShade() {
        return isShade;
    }

    public void setShade(boolean shade) {
        isShade = shade;
    }

    public boolean isFace() {
        return isFace;
    }
}
