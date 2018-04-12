package com.thq.pat.plug.catgirl.patfactory;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.thq.pat.plug.R;
import com.thq.pat.plug.catgirl.PatSkin;
import com.thq.pat.plugapilib.AbsShitProvider;
import com.thq.pat.plugapilib.IPlugAPI;
import com.thq.pat.plugapilib.RoachShitFactory;
import com.thq.pat.plugapilib.Utils;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class CatGirlPat extends AbsPat {

    final static String TAG = "FlyPat";

    final static int UPDATE_PAT = 101;
    final static int UPDATE_PAT_DONE = 102;
    public final static String MY_ACTION_CHANGE_PAT_SIZE = "change.pat.size.MY_ACTION";
    public final static String MY_ACTION_CHANGE_PAT_ALPHA = "change.pat.alpha.MY_ACTION";

    //定义浮动窗口布局
    FrameLayout mFloatLayout;
    LayoutParams wmPatParams;
    PatSkin myPatView;


    int currentPatX,currentPatY;
    int nextPatX,nextPatY;
    int destX,destY;

    AbsShitProvider shitProvider;

    Random mRandomGenerator = new Random();

    boolean isPatDie = false;
    int patSize = 45;
    int patAlpha = 255;

    Map<String,List> mSkinMaps;

    public CatGirlPat(IPlugAPI fxService, Map<String, List> skinMaps) {
        super(fxService);
        mSkinMaps = skinMaps;
        initSkins();
        Utils.init(fxService);
    }

    @Override
    public void startLife() {
        wmPatParams = new LayoutParams();
        initPatParams(wmPatParams);
        //获取浮动窗口视图所在布局

        mFloatLayout = new FrameLayout(mContext.getContext());

        myPatView = new PatSkin(mContext.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(45,45);
        myPatView.setLayoutParams(layoutParams);
        mFloatLayout.addView(myPatView);

        //设置监听浮动窗口的触摸移动
        myPatView.setOnTouchListener(mPatTouchListener);
        myPatView.setOnClickListener(mPatClickListener);

//        SharedPreferences sp = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        patSize = mContext.getInt("size",45);
        patSize = Utils.dpToPixel(patSize);
        myPatView.setViewSize(patSize);

        mContext.addView(mFloatLayout, wmPatParams);

        shitProvider = new RoachShitFactory(mContext, R.drawable.shit);

        myActions.initAction();

    }

    @Override
    public void endLife() {
        myActions.end();
        isPatDie = true;
        myPatView.onDestroy();
        myPatView = null;
        mContext.removeView(mFloatLayout);
        if (shitProvider != null) {
            shitProvider.Clear();
        }
    }

    @Override
    public void sleep() {
        isActive = false;
        myActions.end();
    }

    @Override
    public void wakeUp() {
        isActive = true;
        myActions.start();
    }

    @Override
    public void command(String action) {
        if (MY_ACTION_CHANGE_PAT_SIZE.equals(action)) {
            patSize = mContext.getInt("size",45);
            patSize = Utils.dpToPixel(patSize);
            myPatView.setViewSize(patSize);
        } else if (MY_ACTION_CHANGE_PAT_ALPHA.equals(action)) {
            patAlpha = mContext.getInt("alpha",255);
            wmPatParams.alpha = patAlpha / 255f;
            mContext.updateView(mFloatLayout, wmPatParams);
        }
    }

    boolean isDraged = false;
    OnTouchListener mPatTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
            wmPatParams.x = (int) event.getRawX() - myPatView.getMeasuredWidth()/2;
            currentPatX = wmPatParams.x;
            nextPatX = wmPatParams.x;
            //减25为状态栏的高度
            wmPatParams.y = (int) event.getRawY() - myPatView.getMeasuredHeight()/2 - 25;
            currentPatY = wmPatParams.y;
            nextPatY = wmPatParams.y;
             //刷新
            mContext.updateView(mFloatLayout, wmPatParams);
            isDraged = true;
            return false;  //此处必须返回false，否则OnClickListener获取不到监听
        }
    };

    OnClickListener mPatClickListener = new OnClickListener() {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            commandPatToDo();
        }
    };


    private void initPatParams(LayoutParams wmPatParams) {
        //设置window type
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            wmPatParams.type = LayoutParams.TYPE_PHONE;
        } else {
            wmPatParams.type = LayoutParams.TYPE_TOAST;
        }
        //设置图片格式，效果为背景透明
        wmPatParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmPatParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | LayoutParams.FLAG_HARDWARE_ACCELERATED
                ;

        //调整悬浮窗显示的停靠位置为左侧置顶
        wmPatParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmPatParams.x = Utils.minPixels / 2;
        wmPatParams.y = Utils.maxPixels / 2;

        nextPatX = currentPatX = wmPatParams.x;
        nextPatY = currentPatY = wmPatParams.y;

        // 设置悬浮窗口长宽数据
        wmPatParams.width = LayoutParams.WRAP_CONTENT;
        wmPatParams.height = LayoutParams.WRAP_CONTENT;

        patAlpha = mContext.getInt("alpha",255);
        wmPatParams.alpha = patAlpha / 255f;
    }
    
    private void commandPatToDo() {
        int action = mRandomGenerator.nextInt(6);
        switch (action) {
        case 1:
        case 2:
        case 3:
            shitProvider.DoShit(currentPatX, currentPatY);
            break;
        case 4:
            mContext.updateShowInfo(26);
            break;
        case 5:
            mContext.updateShowInfo(27);
            break;
        case 6:
            mContext.updateShowInfo(21);
            break;

        default:
            break;
        }
    }

    List<Bitmap> happys;
    List<Bitmap> wakes;
    List<Bitmap> sleeps;
    List<Bitmap> runs;
    List<Bitmap> walks;
    List<Bitmap> normals;
    private void initSkins() {
        if (mSkinMaps == null) return;
        normals = mSkinMaps.get("normal");
        runs = mSkinMaps.get("runs");
        walks = mSkinMaps.get("walks");
        happys = mSkinMaps.get("happys");
        wakes = mSkinMaps.get("wakes");
        sleeps = mSkinMaps.get("sleeps");
    }


    ActionManager myActions = new ActionManager() {

        @Override
        public void updateAction() {
            for (int i = 0; i < MAX_ACTION_NUMS; i++) {
                int random = mRandomGenerator.nextInt(100);
                Log.i(TAG, "TTT----------------------------------------------------------- random = " + random);
                if (i == 0) actions.add(oriActions.get("wake"));
                if (random >= 0 && random < 20) {
                    actions.add(oriActions.get("run"));
                } else if (random >= 20 && random < 40) {
                    actions.add(oriActions.get("walk"));
                } else if (random >= 40 && random < 60) {
                    actions.add(oriActions.get("sleep"));
                    actions.add(oriActions.get("wake"));
                } else if (random >= 60 && random < 70) {
                    actions.add(oriActions.get("idle"));
                } else if (random >= 70 && random < 95) {
                    actions.add(oriActions.get("happy"));
                } else if (random >= 95 && random < 100) {
                    actions.add(oriActions.get("shit"));
                }
            }
        }

        @Override
        public void initAction() {

            defaultAction = new Action("idle", 1000) {
                public void Execute() {
                    myPatView.execute(new PatSkin.UpdateSkin() {
                        @Override
                        public Bitmap callback(View view) {
                            return normals.get(0);
                        }
                    });
                };
            };

            oriActions.put("run", new Action("run", 50) {
                int lastIndex = 0;
                public void Execute() {
                    mMoveDuration = mRandomGenerator.nextInt(4000) +500;
                    moveByAnim(mMoveDuration);
                    this.actionDuration = mMoveDuration;

                    myPatView.execute(new PatSkin.UpdateSkin() {
                        @Override
                        public Bitmap callback(View view) {
                            lastIndex = ++lastIndex % runs.size();
                            view.postInvalidateDelayed(150l);
                            return runs.get(lastIndex);
                        }
                    });
                };
            });
            oriActions.put("idle", defaultAction);
            oriActions.put("walk", new Action("walk", 3000) {
                int lastIndex = 0;
                int length = walks.size();
                public void Execute() {
                    mMoveDuration = mRandomGenerator.nextInt(5000) +500;
                    moveByAnim(mMoveDuration);
                    this.actionDuration = mMoveDuration;
                    myPatView.execute(new PatSkin.UpdateSkin() {
                        @Override
                        public Bitmap callback(View view) {
                            lastIndex = ++lastIndex % length;
                            view.postInvalidateDelayed(200l);
                            return walks.get(lastIndex);
                        }
                    });
                };
            });
            oriActions.put("happy", new Action("happy", 7000) {
                int lastIndex = 0;
                int length = happys.size();
                public void Execute() {
                    lastIndex = 0;
                    myPatView.execute(new PatSkin.UpdateSkin() {

                        @Override
                        public Bitmap callback(View view) {
                            lastIndex = ++lastIndex % length;
                            if (lastIndex < length)view.postInvalidateDelayed(200l);
                            return happys.get(lastIndex);
                        }
                    });
                };
            });
            oriActions.put("wake", new Action("wake", 7000) {
                int lastIndex = 0;
                int length = wakes.size();
                public void Execute() {
                    lastIndex = 0;
                    myPatView.execute(new PatSkin.UpdateSkin() {

                        @Override
                        public Bitmap callback(View view) {
                            lastIndex = ++lastIndex % length;
                            if (lastIndex < length)view.postInvalidateDelayed(200l);
                            return wakes.get(lastIndex);
                        }
                    });
                };
            });
            oriActions.put("sleep", new Action("sleep", 3000) {
                int lastIndex = 0;
                int length = sleeps.size();
                public void Execute() {
                    lastIndex = 0;
                    myPatView.execute(new PatSkin.UpdateSkin() {

                        @Override
                        public Bitmap callback(View view) {
                            lastIndex = ++lastIndex % length;
                            if (lastIndex < length)view.postInvalidateDelayed(200l);
                            return sleeps.get(lastIndex);
                        }
                    });
                };

                @Override
                public Action end() {
                    super.end();
                    return new Action("", 20000) {
                        @Override
                        public void Execute() {
                            myPatView.execute(new PatSkin.UpdateSkin() {

                                @Override
                                public Bitmap callback(View view) {
                                    return sleeps.get(length - 1);
                                }
                            });
                        };
                    };
                }
            });

            oriActions.put("shit", new Action("shit", 50) {
                public void Execute() {
                    shit();
                };
            });

            start();// first Execute.
        }
    };

    private void shit() {
        int shit = mRandomGenerator.nextInt(10);
//        Log.i(TAG, " shit " + shit);
        if (shit == 1 ) {
            shitProvider.DoShit(currentPatX,currentPatY);
        }
    }

    private class PointEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            if (isDraged) {
                oriX = (int) (currentPatX - fraction*(endPoint.x - startPoint.x));
                oriY = (int) (currentPatY - fraction*(endPoint.y - startPoint.y));
                isDraged = false;
            }
            int x = (int) (oriX + fraction*(endPoint.x - startPoint.x));
            int y = (int) (oriY + fraction*(endPoint.y - startPoint.y));
            return new Point(x, y);
        }
    }

    Point mPoint;
    private void moveByAnim(int moveDuration) {
        Log.i(TAG, "moveByLoyout: ");
        if (isPatDie) return;

        destX = mRandomGenerator.nextInt(Utils.minPixels);
        destY = mRandomGenerator.nextInt(Utils.maxPixels);

        oriX = currentPatX;
        oriY = currentPatY;

        myPatView.setDegree((float) Utils.getAngle(destX, destY, currentPatX, currentPatY));

        ValueAnimator xValue =  ValueAnimator.ofObject(new PointEvaluator(),new Point(oriX,oriY),new Point(destX,destY));
        xValue.setDuration(moveDuration);
        xValue.setInterpolator(new LinearInterpolator());
        xValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPoint = (Point) animation.getAnimatedValue();
                nextPatX = mPoint.x;
                nextPatY = mPoint.y;
                doMoveAnimByLoyout();
            }
        });
        xValue.setInterpolator(new LinearInterpolator());
        xValue.start();
    }
    
    int mMoveDuration = 10;
    int oriX,oriY;


    private void doMoveAnimByLoyout() {
        if (isPatDie) return;

        wmPatParams.x = nextPatX;
        currentPatX = wmPatParams.x;
        //减25为状态栏的高度
        wmPatParams.y = nextPatY;
        currentPatY = wmPatParams.y;
        mContext.updateView(mFloatLayout, wmPatParams);
    }
}
