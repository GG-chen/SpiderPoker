package com.chen.spiderpoker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shinelon on 2017/4/12.
 */
//数组
public class PokerGroup {
    private Context mContext;
    //第几组
    private int mNum;
    private List<Poker> mList = new ArrayList<>();
    private int mStartX;
    private int mWidth;
    private int mDistance = 40;
    private boolean isSuccess;
    private int mCurrentLevel;

    public PokerGroup(Context context, int num, int currentLevel) {
        this.mCurrentLevel = currentLevel;
        this.mContext = context;
        this.mNum = num;
    }

    public void onDraw(Canvas canvas, Paint paint) {
        if (getCount() != 0) {
            setPokerY();
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).draw(canvas, paint);
            }

        }

    }

    private void setPokerY() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getmStartY() == -1) {
                mList.get(i).setmStartX(mStartX);
                mList.get(i).setmStartY(mDistance * i);

            }
        }
    }


    public int witchPoker(int y) {
        int one = -1;
        if (y < ((mList.size() - 1) * mDistance) + 157) {
            if (y < ((mList.size() - 1) * mDistance)) {
                one = y / mDistance;
            } else if (y > ((mList.size() - 1) * mDistance) && y < ((mList.size() - 1) * mDistance) + 157) {
                one = mList.size() - 1;
            }

        }
        return one;

    }

    public void addList(List<Poker> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setmStartX(mStartX);
                list.get(i).setmStartY(-1);
            }
            this.mList.addAll(list);
        }
        if (getCount() - list.size() > 0) {
            checkShade(list.size());
        }
    }
    public void addInitList(List<Poker> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setmStartX(mStartX);
                list.get(i).setmStartY(-1);
            }
            this.mList.addAll(list);
        }

    }

    public void addItem(Poker poker) {
        if (poker != null) {
            poker.setmStartX(mStartX);
            poker.setmStartY(-1);
            this.mList.add(poker);
            Log.d("PokerGroup", "addItem" + poker.getmNum());
        }
        if (getCount() - 1 > 0 ) {
            checkShade(1);
        }
    }
    public void addInitItem(Poker poker) {
        if (poker != null) {
            poker.setmStartX(mStartX);
            poker.setmStartY(-1);
            this.mList.add(poker);
        }
    }

    private void checkShade(int count) {
        Poker poker = mList.get(getCount() - count - 1);
        Poker poker2 = mList.get(getCount() - count);
        if (poker.isFace()) {
            //判断是否连续
            boolean isSuccession = checkIsSuccession(poker, poker2);
            Log.d("PokerGroup", "checkShade: isSuccession" + isSuccession);
            if (!isSuccession) {
                for (int i = 0; i < getCount() - count; i++) {
                    if (mList.get(i).isFace()) {
                        mList.get(i).setShade(true);
                    }
                }
            }
        }

    }

    public boolean checkIsSuccession(Poker poker, Poker poker2) {
        boolean isSuccession = true;
        int p = Integer.parseInt(poker.getmNum());
        int p2 = Integer.parseInt(poker2.getmNum()) + 1;
        switch (mCurrentLevel) {
            case GameManager.LEVEL_EASY:
                if (p == p2) {
                    isSuccession = true;
                } else {
                    isSuccession = false;
                }
                break;
            case GameManager.LEVEL_ORDINARY:
                if (p == p2 && poker.getmColor() == poker2.getmColor()) {
                    isSuccession = true;
                } else {
                    isSuccession = false;
                }
                break;
            case GameManager.LEVEL_HARD:
                if (p == p2 && poker.getmType() == poker2.getmType()) {
                    isSuccession = true;
                } else {
                    isSuccession = false;
                }
                break;
        }
        return isSuccession;

    }

    public int getCount() {
        return mList.size();
    }

    public void setmStartX(int mStartX) {
        this.mStartX = mStartX;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmNum() {
        return mNum;
    }

    public List<Poker> getmList() {
        return mList;
    }

    public void setDistanceXY(int distanceX, int distanceY) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setDistanceXY(distanceX, distanceY);
        }
    }

    public void setDistanceLastXY(int distanceX, int distanceY) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setDistanceLastXY(distanceX, distanceY);
        }
    }

    public boolean isSuccess(PokerGroup movingGroup) {
        int c = Integer.parseInt(movingGroup.getmList().get(0).getmNum()) + 1;
        if (mList.size() == 0) {
            return true;
        }
        int b = Integer.parseInt(mList.get(mList.size() - 1).getmNum());
        if (b == c) {
            return true;
        }
        return false;
    }

    public void clearAllDate() {
        mList.clear();
    }

    public boolean moveable(int whichItem) {
        if (whichItem == -1) {
            return false;
        }
        if ((!mList.get(whichItem).isShade()) && mList.get(whichItem).isFace()) {
            return true;
        } else {
            return false;
        }

    }

    public void changeItemX() {
        for (int i = 0; i < mList.size(); i++) {
            Poker poker = mList.get(i);
            if (poker.getmStartX() != this.mStartX) {
                poker.setmStartX(this.mStartX);
            }
        }
    }
}
