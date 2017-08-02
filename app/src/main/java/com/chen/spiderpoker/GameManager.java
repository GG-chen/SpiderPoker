package com.chen.spiderpoker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Shinelon on 2017/4/11.
 */

public class GameManager {
    public static final int LEVEL_EASY = 0;
    public static final int LEVEL_ORDINARY = 1;
    public static final int LEVEL_HARD = 2;
    private int mCurrentLevel = LEVEL_EASY;

    private final GameView mGameView;
    private List<Poker> mPokerList = new ArrayList<>();
    private List<PokerGroup> mLists = new ArrayList<>();
    private List<PokerGroup> mStoreList = new ArrayList<>();
    private PokerGroup mMovingGroup;
    private int mWidth;
    private int mHeight;
    private Context mContext;
    private int mItemWidth;
    private int mStorePokerCount = 5;
    private int mDistance = 40;
    private int mStorePokerStartX;
    private int mStorePokerStartY;
    private Bitmap mBackBitmap;
    private Bitmap mReflashBitmap;
    private SoundPool mSoundPool;
    public static  int mHasFinishPokerCount = 0;

    public GameManager(Context context, int width, int height, GameView gameView) {
        this.mGameView = gameView;
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        mStorePokerStartX = 40;
        mStorePokerStartY = height - 180;
        initData();
    }

    private void initData() {
        mItemWidth = mWidth /10;
        for (int i = 1; i < 11; i++) {
            PokerGroup group = new PokerGroup(mContext, i, mCurrentLevel);
            group.setmStartX((i-1)* mItemWidth);
            group.setmWidth(mItemWidth);
            mLists.add(group);
        }
        for (int i = 11; i < 16; i++) {
            PokerGroup group = new PokerGroup(mContext, i, mCurrentLevel);
            mStoreList.add(group);
        }
        mMovingGroup = new PokerGroup(mContext, 16, mCurrentLevel);
        mBackBitmap =Utils.zoomImg(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back), 110, 157) ;
        mReflashBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.reflash);
        //加载声音
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        mSoundPool.load(mContext,R.raw.sound,1);
        createPoker();
    }

    public void onDraw(Canvas canvas, Paint paint) {
        drawList(canvas,paint);
        drawBackground(canvas, paint);
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        if (mStorePokerCount != 0) {
            for (int i = 0; i < mStorePokerCount; i++) {
                Rect des = new Rect();
                des.set(mStorePokerStartX + (mDistance * i), mStorePokerStartY, mStorePokerStartX + (mDistance * i) + 110 , mStorePokerStartY + 157);
                canvas.drawBitmap(mBackBitmap, null, des, paint);
            }
        }
        //画刷新图标
        Rect des = new Rect();
        des.set(mWidth - 200, mHeight - 200, mWidth - 200 + 128, mHeight - 200 + 128);
        canvas.drawBitmap(mReflashBitmap,null,des,paint);
    }


    private void drawList(Canvas canvas, Paint paint) {
        for (int i = 0; i < mLists.size(); i++) {
            mLists.get(i).onDraw(canvas,paint);
        }
        mMovingGroup.onDraw(canvas, paint);


    }
    private int whichOne = -1;
    int x = -1;
    int y = -1;
    int whichItem = -1;
    public void onTouch(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                Boolean isClickStore = isClickStore(x, y);
                if (isClickStore) {
                    sendPoker();
                    return;
                }
                if (isReflash(x, y)) {
                    Toast.makeText(mContext, "刷新！！", Toast.LENGTH_SHORT).show();
                    reflash();
                    return;
                }
                whichOne = x / mItemWidth;
                whichItem = mLists.get(whichOne).witchPoker(y);
                boolean moveable = mLists.get(whichOne).moveable(whichItem);
                if (whichItem != -1 && moveable) {
                    List<Poker> moving = getMovingList(whichOne, whichItem);
                    mMovingGroup.getmList().addAll(moving);
                    mMovingGroup.setmStartX(moving.get(0).getmStartX());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMovingGroup.getCount() != 0) {
                    int distanceX = (int) (event.getX() - x);
                    int distanceY = (int) (event.getY() - y);
                    mMovingGroup.setDistanceXY(distanceX, distanceY);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mMovingGroup.getCount() != 0) {
                    List<Poker> listA = mLists.get(whichOne).getmList();
                    int whichOne2 = (int) (event.getX() / mItemWidth);
                    if (mLists.get(whichOne2).isSuccess(mMovingGroup)) {
                        for (int i = 0; i < mMovingGroup.getCount(); i++) {
                            mMovingGroup.getmList().get(i).setDistanceXY(0,0);
                            mLists.get(whichOne2).addItem(mMovingGroup.getmList().get(i));
                        }
                        mLists.get(whichOne2).changeItemX();

                        if (listA.size() != 0) {
                            Log.d("GameManager", "ACTION_UP: setFace" + listA.size());
                            listA.get(listA.size() - 1).setFace(true);
                        }
                        reflashPoker(whichOne);
                    } else {
                        for (int i = 0; i < mMovingGroup.getCount(); i++) {
                            mLists.get(whichOne).addItem(mMovingGroup.getmList().get(i));
                            mMovingGroup.getmList().get(i).setDistanceXY(0,0);
                        }
                    }
                    if (mLists.get(whichOne2).getCount() > 12) {
                        checkIsFinish(whichOne2);
                    }
                    mMovingGroup.clearAllDate();
                    mGameView.postInvalidate();
                    whichOne = -1;
                    x = -1;
                    y = -1;
                }
                mSoundPool.play(1,1, 1, 0, 0, 1);
                break;
        }

    }

    private boolean isReflash(int x, int y) {
        if (x > mWidth - 200 && x < mWidth - 200 + 128 && y > mHeight - 200 && y < mHeight - 200 + 128) {
            return true;
        } else {
            return false;
        }
    }

    private void reflash() {
        clearList();
        switchPoker();
    }

    private void checkIsFinish(int whichOne2) {
        reflashPoker(whichOne2);
        List<Poker> listA = mLists.get(whichOne2).getmList();
        int count = listA.size();
        if (count == 0 || count < 13 || Integer.parseInt(listA.get(count - 1).getmNum()) != 1) {
            return;
        }
        Boolean isFinish = isFinish(listA, count -1);
        if (isFinish) {
            Log.d("GameManager", "完成了一组！！" + whichOne2 + " size " + count);
            //清除一组牌
            for (int i = count -13; i < count; i++) {
                listA.remove(count - 13);
            }
            if (listA.size() > 0 && !listA.get(listA.size() - 1).isFace()) {
                listA.get(listA.size() - 1).setFace(true);

            }
            reflashPoker(whichOne2);
            mHasFinishPokerCount++;
            if (mHasFinishPokerCount == 8) {
                //游戏全部完成
                Toast.makeText(mContext, "yeah~ 你完成了游戏! ^-^", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext,"恭喜！完成一组牌!!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private Boolean isFinish(List<Poker> listA, int count) {
        if (count != -1 && listA.get(count).isFace() && !listA.get(count).isShade()) {
            Log.d("GameManager", "isFinish" + count);
            if (Integer.parseInt(listA.get(count).getmNum()) == 13) {
                return true;
            }

            return isFinish(listA, count - 1);
        } else {
            return false;
        }


    }

    private void sendPoker() {
        PokerGroup group = mStoreList.get(mStoreList.size() - 1);
        for (int i = 0; i < group.getCount(); i++) {
            group.getmList().get(i).setFace(true);
            mLists.get(i).addItem(group.getmList().get(i));
        }
        mStoreList.remove(mStoreList.size() - 1);
        mStorePokerCount = mStoreList.size();
        for (int i = 0; i < mLists.size(); i++) {
            if (mLists.get(i).getCount() > 12) {
                checkIsFinish(i);
            }
        }
    }

    private Boolean isClickStore(int x, int y) {
        if (x > mStorePokerStartX && x < (((mStorePokerCount - 1) * mDistance) + 110) && y > mStorePokerStartY && y < mStorePokerStartY + 157) {
            return true;
        } else {
            return false;
        }
    }

    private List<Poker> getMovingList(int whichOne, int whichItem) {
        int count = mLists.get(whichOne).getCount();
        List<Poker> list = new ArrayList<>();
        //添加
        for (int i = whichItem; i < count; i++) {
            list.add(mLists.get(whichOne).getmList().get(i));
        }
        //删除
        for (int i = whichItem; i < count; i++) {
            mLists.get(whichOne).getmList().remove(whichItem);
        }
        return list;

    }

    private void reflashPoker(int whichOne) {
        List<Poker> listA = mLists.get(whichOne).getmList();
        int count = listA.size();
        if (count != 0) {
                Poker a = null;
                Poker b = null;
                for (int i = count -1; i > -1; i--) {
                    a = listA.get(i);
                    a.setShade(false);
                    if (i - 1 > -1 && listA.get(i - 1).isFace()) {
                        b = listA.get(i - 1);
                    }
                    if (b != null) {
                        if (mLists.get(whichOne).checkIsSuccession(b, a)) {
                           a.setShade(false);
                           b.setShade(false);

                        } else {
                            for (int j = 0; j < i; j++) {
                                if (!listA.get(j).isFace()) {
                                    listA.get(j).setShade(true);
                                }
                            }
                            return;
                        }

                    } else {
                        a.setShade(false);
                        return;
                    }
                }
        }
    }

    //发牌
    public void createPoker() {
        //两副牌
        for (int m = 0; m < 2; m++) {
            for (int i = 1; i <= 13; i++) {
                for (int j = 0; j < 2; j++) {
                    String num = i + "";
                    if (i < 10) {
                        num = "0" + i;
                    }
                    if (j == 0) {
                        String buffer1= num + j + 1;
                        String buffer2 = num + j + 4;
                        Poker poker1 = new Poker(mContext);
                        Poker poker2 = new Poker(mContext);
                        poker1.init(num,j,1,false,buffer1);
                        poker2.init(num,j,4,false,buffer2);
                        mPokerList.add(poker1);
                        mPokerList.add(poker2);
                    } else {
                        String buffer1 = num + j + 2;
                        String buffer2 = num + j + 3;
                        Poker poker1 = new Poker(mContext);
                        Poker poker2 = new Poker(mContext);
                        poker1.init(num,j,2,false,buffer1);
                        poker2.init(num,j,3,false,buffer2);
                        mPokerList.add(poker1);
                        mPokerList.add(poker2);
                    }

                }

            }
        }
        switchPoker();

    }

    private void switchPoker() {
        Collections.shuffle(mPokerList);
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        for(Iterator it = mPokerList.iterator(); it.hasNext(); )
        {
            if (k <4) {
                mLists.get(j).addInitItem((Poker) it.next());
                if (i == 5) {
                    mLists.get(j).getmList().get(i).setFace(true);
                    i = -1;
                    k++;
                    j++;

                }
            } else if (k >= 4 && k < 10){
                mLists.get(j).addInitItem((Poker) it.next());
                if (i == 4) {
                    mLists.get(j).getmList().get(i).setFace(true);
                    i = -1;
                    k++;
                    j++;
                }
            } else if (k >= 10) {
                mStoreList.get(l).addInitItem((Poker) it.next());
                if (i == 9) {
                    mStoreList.get(l).getmList().get(i).setFace(true);
                    i = -1;
                    k++;
                    l++;
                }
            }
            i++;
        }
    }


    public void clearList() {
        for (int i = 0; i < mLists.size(); i++) {
            mLists.get(i).clearAllDate();
        }
        mStoreList.clear();
        for (int i = 11; i < 16; i++) {
            PokerGroup group = new PokerGroup(mContext, i, mCurrentLevel);
            mStoreList.add(group);
        }
        mStorePokerCount = mStoreList.size();
        mMovingGroup.clearAllDate();
        for (Poker poker : mPokerList) {
            poker.clear();
        }
    }

    public void setmStorePokerStartX(int mStorePokerStartX) {
        this.mStorePokerStartX = mStorePokerStartX;
    }

    public void setStorePokerStarty(int storePokerStarty) {
        this.mStorePokerStartY = storePokerStarty;
    }

    public int getmCurrentLevel() {
        return mCurrentLevel;
    }

    public void setmCurrentLevel(int mCurrentLevel) {
        this.mCurrentLevel = mCurrentLevel;
    }
}
