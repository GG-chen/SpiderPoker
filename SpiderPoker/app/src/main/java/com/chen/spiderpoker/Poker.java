package com.chen.spiderpoker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
    private Context mContext;
    //开始和结束坐标
    private int mStartX = -1;
    private int mStartY = -1;
    private int mWidth = 110;
    private int mHeight = 157;
    private boolean isFace = true;
    private boolean isShade = false;
    private Resources mRes;
    private int mBitmapID;
    private Bitmap mFaceBitmap;
    private Bitmap mCurrentBitmap;
    private Bitmap mBackBitmap;
    private Bitmap mShadeBitmap;
    private int mDistanceX = 0;
    private int mDistanceY = 0;

    public Poker(Context context) {
        this.mContext = context;
        mRes =context.getResources();
    }

    public void draw(Canvas canvas, Paint paint) {
        if (mCurrentBitmap != null) {
            Bitmap bitmap = null;
            if (isFace) {
                if (isShade) {
                    bitmap = mShadeBitmap;
                } else {
                    bitmap = mCurrentBitmap;
                }
            } else {
                bitmap = mBackBitmap;
            }
            Rect des = new Rect();
            des.set(mStartX + mDistanceX, mStartY + mDistanceY, mStartX + mWidth + mDistanceX, mStartY + mHeight + mDistanceY);
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
        mBitmapID =  mRes.getIdentifier("z"+ name,"drawable", mContext.getPackageName());
        mFaceBitmap = BitmapFactory.decodeResource(mContext.getResources(), mBitmapID);
        mFaceBitmap = Utils.zoomImg(mFaceBitmap, mWidth, mHeight);
        mShadeBitmap = Utils.zoomImg(BitmapFactory.decodeResource(mContext.getResources(), mRes.getIdentifier("b" + name, "drawable", mContext.getPackageName())), mWidth, mHeight);
        mBackBitmap = Utils.zoomImg(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back), mWidth, mHeight);
        mCurrentBitmap = mFaceBitmap;
        mWidth = mCurrentBitmap.getWidth();
        mHeight = mCurrentBitmap.getHeight();
    }

    public void setFace(boolean face) {
        isFace = face;
    }


    public void setmStartX(int mStartX) {
        this.mStartX = mStartX;
    }

    public void setmStartY(int mStartY) {
        this.mStartY = mStartY;
    }

    public int getmStartX() {
        return mStartX;
    }

    public int getmStartY() {
        return mStartY;
    }


    public void setDistanceXY(int distanceX, int distanceY) {
        this.mDistanceX = distanceX;
        this.mDistanceY = distanceY;
        //Log.d("Poker", "setDistanceXY: mStartX" + mStartX + "  mStartY  " + mStartY);
    }

    public void setDistanceLastXY(int distanceX, int distanceY) {
        this.mStartX += distanceX;
        this.mStartY += distanceY;
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

    public void clear() {
        isFace = false;
        isShade = false;
        mStartX = -1;
        mStartY = -1;
        mDistanceX = 0;
        mDistanceY = 0;
    }

}
