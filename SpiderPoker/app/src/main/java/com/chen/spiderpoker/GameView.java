package com.chen.spiderpoker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Shinelon on 2017/4/12.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnClickListener {
    private GameManager mGameManager;
    private Context mContext;
    private SurfaceHolder mHolder;
    private MyThread myThread;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private AlertDialog mDialog;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    public void initData() {
        mGameManager = new GameManager(mContext, mWidth, mHeight,this);
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        myThread = new MyThread(mHolder);//创建一个绘图线程
    }

    public void onDrawPoker(Canvas canvas, Paint paint) {
        mGameManager.onDraw(canvas,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGameManager.onTouch(event);
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        showDialog();
    }

    private void threadStart() {
        myThread.isRun = true;
        myThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myThread.isRun = false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easy:
                mGameManager.setmCurrentLevel(GameManager.LEVEL_EASY);
                mDialog.cancel();
                threadStart();
                break;
            case R.id.ordinary:
                mGameManager.setmCurrentLevel(GameManager.LEVEL_ORDINARY);
                mDialog.cancel();
                threadStart();
                break;
            case R.id.hard:
                mGameManager.setmCurrentLevel(GameManager.LEVEL_HARD);
                mDialog.cancel();
                threadStart();
                break;
            case R.id.exit:
                ((MainActivity) mContext).exitActivity();
        }

    }

    private class MyThread extends Thread {
        private SurfaceHolder holder;
        public boolean isRun;

        public MyThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        @Override
        public void run() {
            while (isRun) {
                Canvas c = null;
                try {
                    synchronized (holder) {
                        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                        c.drawPaint(mPaint);
                        onDrawPoker(c , mPaint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。

                    }
                }
            }
        }
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }
    private void showDialog() {
         LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_dialog, null);
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.show();
        mDialog.getWindow().setContentView(layout);
        layout.findViewById(R.id.easy).setOnClickListener(this);
        layout.findViewById(R.id.ordinary).setOnClickListener(this);
        layout.findViewById(R.id.hard).setOnClickListener(this);
        layout.findViewById(R.id.exit).setOnClickListener(this);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_SEARCH)
                {
                    return true;
                }
                else
                {
                    return true; //默认返回 false，这里false不能屏蔽返回键，改成true就可以了
                }
            }
        });

    }
}
