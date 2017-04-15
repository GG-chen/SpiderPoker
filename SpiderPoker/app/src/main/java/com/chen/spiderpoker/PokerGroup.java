package com.chen.spiderpoker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shinelon on 2017/4/12.
 */
//数组
public class PokerGroup {
    private Context context;
    //第几组
    private int num;
    private List<Poker> list = new ArrayList<>();
    private int startX;
    private int width;
    private int distance = 40;
    private boolean success;
    private int currentLevel;

    public PokerGroup(Context context, int num, int currentLevel) {
        this.currentLevel = currentLevel;
        this.context = context;
        this.num = num;
    }

    public void onDraw(Canvas canvas, Paint paint) {
        if (getCount() != 0) {
            setPokerY();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).draw(canvas, paint);
            }

        }

    }

    private void setPokerY() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStartY() == -1) {
                list.get(i).setStartX(startX);
                list.get(i).setStartY(distance * i);

            }
        }
    }


    public int witchPoker(int y) {
        int one = -1;
        if (y < ((list.size() - 1) * distance) + 157) {
            if (y < ((list.size() - 1) * distance)) {
                one = y / distance;
            } else if (y > ((list.size() - 1) * distance) && y < ((list.size() - 1) * distance) + 157) {
                one = list.size() - 1;
            }

        }
        return one;

    }

    public void addList(List<Poker> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setStartX(startX);
                list.get(i).setStartY(-1);
            }
            this.list.addAll(list);
        }
        if (getCount() - list.size() > 0) {
            checkShade(list.size());
        }
    }
    public void addInitList(List<Poker> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setStartX(startX);
                list.get(i).setStartY(-1);
            }
            this.list.addAll(list);
        }

    }

    public void addItem(Poker poker) {
        if (poker != null) {
            poker.setStartX(startX);
            poker.setStartY(-1);
            this.list.add(poker);
            Log.d("PokerGroup", "addItem" + poker.getmNum());
        }
        if (getCount() - 1 > 0 ) {
            checkShade(1);
        }
    }
    public void addInitItem(Poker poker) {
        if (poker != null) {
            poker.setStartX(startX);
            poker.setStartY(-1);
            this.list.add(poker);
        }
    }

    private void checkShade(int count) {
        Poker poker = list.get(getCount() - count - 1);
        Poker poker2 = list.get(getCount() - count);
        if (poker.isFace()) {
            //判断是否连续
            boolean isSuccession = checkIsSuccession(poker, poker2);
            Log.d("PokerGroup", "checkShade: isSuccession" + isSuccession);
            if (!isSuccession) {
                for (int i = 0; i < getCount() - count; i++) {
                    if (list.get(i).isFace()) {
                        list.get(i).setShade(true);
                    }
                }
            }
        }

    }

    public boolean checkIsSuccession(Poker poker, Poker poker2) {
        boolean isSuccession = true;
        int p = Integer.parseInt(poker.getmNum());
        int p2 = Integer.parseInt(poker2.getmNum()) + 1;
        switch (currentLevel) {
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
        return list.size();
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getNum() {
        return num;
    }

    public List<Poker> getList() {
        return list;
    }

    public void setDistanceXY(int distanceX, int distanceY) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setDistanceXY(distanceX, distanceY);
        }
    }

    public void setDistanceLastXY(int distanceX, int distanceY) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setDistanceLastXY(distanceX, distanceY);
        }
    }

    public boolean isSuccess(PokerGroup movingGroup) {
        int c = Integer.parseInt(movingGroup.getList().get(0).getmNum()) + 1;
        if (list.size() == 0) {
            return true;
        }
        int b = Integer.parseInt(list.get(list.size() - 1).getmNum());
        if (b == c) {
            return true;
        }
        return false;
    }

    public void clearAllDate() {
        list.clear();
    }

    public boolean moveable(int whichItem) {
        if (whichItem == -1) {
            return false;
        }
        if ((!list.get(whichItem).isShade()) && list.get(whichItem).isFace()) {
            return true;
        } else {
            return false;
        }

    }
}
