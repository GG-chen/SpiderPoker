package com.chen.spiderpoker;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Shinelon on 2017/4/11.
 */

public class GameManager {
    public static final int LEVEL_EASY = 0;
    public static final int LEVEL_ORDINARY = 1;
    public static final int LEVEL_HARD = 2;
    private int currentLevel = LEVEL_EASY;

    private final GameView gameView;
    private Set<Poker> pokerSet = new ArraySet<>();
    private List<PokerGroup> lists = new ArrayList<>();
    private List<PokerGroup> storeList = new ArrayList<>();
    private PokerGroup movingGroup;
    private int width;
    private int height;
    private Context context;
    private int itemWidth;
    private int storePokerCount = 5;
    private int distance = 40;
    private int storePokerStartX;
    private int storePokerStartY;
    private Bitmap backBitmap;

    public GameManager(Context context, int width, int height, GameView gameView) {
        this.gameView = gameView;
        this.context = context;
        this.width = width;
        this.height = height;
        storePokerStartX = 40;
        storePokerStartY = height - 180;
        initData();
    }

    private void initData() {
        itemWidth = width/10;
        for (int i = 1; i < 11; i++) {
            PokerGroup group = new PokerGroup(context, i, currentLevel);
            /*Log.d("GameManager", "initData: itemWidth " + itemWidth + "  width" + width);
            Log.d("GameManager", "initData: " + i + "  ---  " + (i-1)*itemWidth);*/
            group.setStartX((i-1)*itemWidth);
            group.setWidth(itemWidth);
            lists.add(group);
        }
        for (int i = 11; i < 16; i++) {
            PokerGroup group = new PokerGroup(context, i,currentLevel);
            storeList.add(group);
        }
        movingGroup  = new PokerGroup(context, 16, currentLevel);
        createPoker();
        backBitmap =Utils.zoomImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.back), 110, 157) ;

    }

    public void onDraw(Canvas canvas, Paint paint) {
        drawList(canvas,paint);
        drawBackground(canvas, paint);
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        if (storePokerCount != 0) {
            for (int i = 0; i < storePokerCount; i++) {
                Rect des = new Rect();
                des.set(storePokerStartX + (distance * i),storePokerStartY ,storePokerStartX + (distance * i) + 110 ,storePokerStartY + 157);
                canvas.drawBitmap(backBitmap, null, des, paint);
            }
        }
    }


    private void drawList(Canvas canvas, Paint paint) {
        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).onDraw(canvas,paint);
        }
        movingGroup.onDraw(canvas, paint);
        /*for (int i = 0; i < storeList.size(); i++) {
            storeList.get(i).onDraw(canvas);
        }*/

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
                 whichOne = x / itemWidth;
                whichItem = lists.get(whichOne).witchPoker(y);
                boolean moveable = lists.get(whichOne).moveable(whichItem);
                if (whichItem != -1 && moveable) {
                    List<Poker> moving = getMovingList(whichOne, whichItem);
                    //Poker p = lists.get(whichOne).getList().get(whichItem);
                    //lists.get(whichOne).getList().remove(lists.get(whichOne).witchPoker(y));
                    movingGroup.getList().addAll(moving);
                    movingGroup.setStartX(moving.get(0).getStartX());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (movingGroup.getCount() != 0) {
                    int distanceX = (int) (event.getX() - x);
                    int distanceY = (int) (event.getY() - y);
                    movingGroup.setDistanceXY(distanceX, distanceY);
                    //Log.d("GameManager", "onTouch: ACTION_MOVE！！！！ distanceX" + distanceX+ "  distanceY  " + distanceY);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (movingGroup.getCount() != 0) {
                    List<Poker> listA = lists.get(whichOne).getList();
                    int whichOne2 = (int) (event.getX() / itemWidth);
                    if (lists.get(whichOne2).isSuccess(movingGroup)) {
                        for (int i = 0; i < movingGroup.getCount(); i++) {
                            movingGroup.getList().get(i).setDistanceXY(0,0);
                            lists.get(whichOne2).addItem(movingGroup.getList().get(i));
                        }

                        if (listA.size() != 0) {
                            Log.d("GameManager", "ACTION_UP: setFace" + listA.size());
                            listA.get(listA.size() - 1).setFace(true);
                        }
                        reflashPoker(whichOne);
                    } else {
                        for (int i = 0; i < movingGroup.getCount(); i++) {
                            lists.get(whichOne).addItem(movingGroup.getList().get(i));
                            movingGroup.getList().get(i).setDistanceXY(0,0);
                        }
                    }
                    if (lists.get(whichOne2).getCount() > 12) {
                        checkIsFinish(whichOne2);
                    }
                    movingGroup.clearAllDate();
                    gameView.postInvalidate();
                    whichOne = -1;
                    x = -1;
                    y = -1;
                    //Log.d("GameManager", "onTouch: up！！！！" + movingGroup.getCount()+ "lists size" + lists.get(whichOne).getCount());
                }
                break;
        }

    }

    private void checkIsFinish(int whichOne2) {
        reflashPoker(whichOne2);
        List<Poker> listA = lists.get(whichOne2).getList();
        int count = listA.size();
        Log.d("GameManager", "开始检查是否完成1");
        if (count == 0 || count < 13 || Integer.parseInt(listA.get(count - 1).getmNum()) != 1) {
            Log.d("GameManager", "开始检查是否完成2");
            return;
        }
        Log.d("GameManager", "开始检查是否完成3");
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
            Toast.makeText(context,"恭喜！完成一组牌!!", Toast.LENGTH_SHORT).show();
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
        PokerGroup group = storeList.get(storeList.size() - 1);
        for (int i = 0; i < group.getCount(); i++) {
            group.getList().get(i).setFace(true);
            lists.get(i).addItem(group.getList().get(i));
        }
        storeList.remove(storeList.size() - 1);
        storePokerCount = storeList.size();
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getCount() > 12) {
                checkIsFinish(i);
            }
        }
    }

    private Boolean isClickStore(int x, int y) {
        if (x > storePokerStartX && x < ((storePokerCount - 1) * distance) + 120 && y > storePokerStartY && y < storePokerStartY + 157) {
            return true;
        } else {
            return false;
        }
    }

    private List<Poker> getMovingList(int whichOne, int whichItem) {
        int count = lists.get(whichOne).getCount();
        List<Poker> list = new ArrayList<>();
        //添加
        for (int i = whichItem; i < count; i++) {
            list.add(lists.get(whichOne).getList().get(i));
        }
        //删除
        for (int i = whichItem; i < count; i++) {
            lists.get(whichOne).getList().remove(whichItem);
        }
        return list;

    }

    private void reflashPoker(int whichOne) {
        List<Poker> listA = lists.get(whichOne).getList();
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
                        if (lists.get(whichOne).checkIsSuccession(b, a)) {
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
                        Poker poker1 = new Poker(context);
                        Poker poker2 = new Poker(context);
                        poker1.init(num,j,1,false,buffer1);
                        poker2.init(num,j,4,false,buffer2);
                        pokerSet.add(poker1);
                        pokerSet.add(poker2);
                    } else {
                        String buffer1 = num + j + 2;
                        String buffer2 = num + j + 3;
                        Poker poker1 = new Poker(context);
                        Poker poker2 = new Poker(context);
                        poker1.init(num,j,2,false,buffer1);
                        poker2.init(num,j,3,false,buffer2);
                        pokerSet.add(poker1);
                        pokerSet.add(poker2);
                    }

                }

            }
        }
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        for(Iterator it = pokerSet.iterator(); it.hasNext(); )
        {
            if (k <4) {
                lists.get(j).addInitItem((Poker) it.next());
                if (i == 5) {
                    lists.get(j).getList().get(i).setFace(true);
                    i = -1;
                    k++;
                    j++;

                }
            } else if (k >= 4 && k < 10){
                lists.get(j).addInitItem((Poker) it.next());
                if (i == 4) {
                    lists.get(j).getList().get(i).setFace(true);
                    i = -1;
                    k++;
                    j++;
                }
            } else if (k >= 10) {
                Log.d("GameManager", "createPoker: " + "i=" + i + "  k=" + k + "  l=" + l);
                storeList.get(l).addInitItem((Poker) it.next());
                if (i == 9) {
                    storeList.get(l).getList().get(i).setFace(true);
                    i = -1;
                    k++;
                    l++;
                }
            }
            i++;
        }
        for (int m = 0; m < lists.size(); m++) {
            Log.d("GameManager", "createPoker: lists" + m + "-------" + lists.get(m).getCount());
        }
        for (int m = 0; m < storeList.size(); m++) {
            Log.d("GameManager", "createPoker: storeList" + m + "-------" + storeList.get(m).getCount());
        }

    }


    public void clearList() {
        lists.clear();
        storeList.clear();
        pokerSet.clear();
    }

    public void setStorePokerStartX(int storePokerStartX) {
        this.storePokerStartX = storePokerStartX;
    }

    public void setStorePokerStarty(int storePokerStarty) {
        this.storePokerStartY = storePokerStarty;
    }
}
