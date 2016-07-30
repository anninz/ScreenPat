package com.thq.pat.patfactory;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.thq.pat.ActionListener;
import com.thq.pat.FxService;
import com.thq.pat.R;
import com.thq.pat.RotateImageView;
import com.thq.pat.Utils;

import java.util.Map;
import java.util.Random;

@SuppressLint("ClickableViewAccessibility")
public class RoachPat extends AbsPat {

    final static String TAG = "RoachPat";
    
    final static int UPDATE_PAT = 101;
    final static int UPDATE_PAT_DONE = 102;
    
    int currentStatus = ActionManager.IDLE;
//    int nextStatus = ActionSeries.UNKNOW_ACTION;
    
    //定义浮动窗口布局
    FrameLayout mFloatLayout;
    LayoutParams wmPatParams;
    RotateImageView myPatView;
    
    
    int currentPatX,currentPatY;
    int nextPatX,nextPatY;
    int destX,destY;
    int stepX = 0,stepY = 0;
    
    AbsShitProvider shitProvider;
    
    Random mRandomGenerator = new Random();
    
    boolean isPatDie = false;
    boolean isDoneCurrentTask = true;
    int patSize = 45;

    Map<String,Bitmap> mSkinMaps;
        
    public RoachPat(FxService fxService, Map<String, Bitmap> skinMaps) {
        super(fxService);
        mSkinMaps = skinMaps;
    }

    @Override
    public void startLife() {
        wmPatParams = new LayoutParams();
        initPatParams(wmPatParams);
        //获取浮动窗口视图所在布局
        LayoutInflater inflater = LayoutInflater.from(mContext.getApplication());
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.pat_layout, null);
//        myPatView = (RotateImageView)mFloatLayout.findViewById(R.id.float_id);
        myPatView = new RotateImageView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(45,45);
        myPatView.setLayoutParams(layoutParams);
        mFloatLayout.addView(myPatView);
        
        //设置监听浮动窗口的触摸移动
        myPatView.setOnTouchListener(mPatTouchListener);
        myPatView.setOnClickListener(mPatClickListener);

        SharedPreferences sp = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        patSize =sp.getInt("size",45);
        myPatView.setViewSize(patSize);

        myPatView.setSkin(mSkinMaps);

        mContext.addView(mFloatLayout, wmPatParams);
        
        shitProvider = new RoachShitFactory(mContext);
        
        myActions.initAction();
        
//        mPatLifeThread.start();
    }

    @Override
    public void endLife() {
        isPatDie = true;

        handler.removeMessages(ActionManager.RUN);
        handler.removeMessages(ActionManager.AMAZED);
        handler.removeMessages(ActionManager.SHIT);
        handler.removeMessages(UPDATE_PAT);
        handler.removeMessages(ActionManager.QUESTION);
        handler.removeMessages(UPDATE_PAT_DONE);
//        Utils.recycleImageView(myPatView);
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
        handler.removeMessages(ActionManager.IDLE);
        handler.removeMessages(ActionManager.RUN);
        handler.removeMessages(ActionManager.AMAZED);
        handler.removeMessages(ActionManager.SHIT);
        handler.removeMessages(UPDATE_PAT);
        handler.removeMessages(ActionManager.QUESTION);
        handler.removeMessages(UPDATE_PAT_DONE);
    }

    @Override
    public void wakeUp() {
        isActive = true;
        handler.sendEmptyMessage(ActionManager.IDLE);
    }

    @Override
    public void command(String action) {
        if (ActionListener.MY_ACTION_CHANGE_PAT_SIZE.equals(action)) {
            SharedPreferences sp = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
            patSize =sp.getInt("size",45);
            myPatView.setViewSize(patSize);
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
            Log.i(TAG, "RawX" + event.getRawX());
            Log.i(TAG, "X" + event.getX());
            //减25为状态栏的高度
            wmPatParams.y = (int) event.getRawY() - myPatView.getMeasuredHeight()/2 - 25;
            currentPatY = wmPatParams.y;
            nextPatY = wmPatParams.y;
            Log.i(TAG, "RawY" + event.getRawY());
            Log.i(TAG, "Y" + event.getY());
             //刷新
            mContext.updateView(mFloatLayout, wmPatParams);
            isDraged = true;
            return false;  //此处必须返回false，否则OnClickListener获取不到监听
        }
    };
    
    OnClickListener mPatClickListener = new OnClickListener() {
        
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) 
        {
            commandPatToDo();
        }
    };
    

    private void initPatParams(WindowManager.LayoutParams wmPatParams) {
        //设置window type
        wmPatParams.type = LayoutParams.TYPE_TOAST; 
        //设置图片格式，效果为背景透明
        wmPatParams.format = PixelFormat.RGBA_8888; 
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmPatParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_NO_LIMITS
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | 0x00000080 |0x20000000*/
                |WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                ;      
        
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmPatParams.gravity = Gravity.LEFT | Gravity.TOP;       
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmPatParams.x = mContext.minPixels / 2;
        wmPatParams.y = mContext.maxPixels / 2;

        nextPatX = currentPatX = wmPatParams.x;
        nextPatY = currentPatY = wmPatParams.y;

        // 设置悬浮窗口长宽数据
        wmPatParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmPatParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
    
    ActionManager myActions = new ActionManager() {
        
        @Override
        public void updateAction() {
            for (int i = 0; i < MAX_ACTION_NUMS; i++) {
                int random = mRandomGenerator.nextInt(100);
                Log.i(TAG, "TTT----------------------------------------------------------- random = " + random);
                if (i == 0) actions.add(oriActions.get("question"));
                if (random >= 0 && random < 20) {
                    actions.add(oriActions.get("run"));
                } else if (random >= 20 && random < 60) {
                    actions.add(oriActions.get("idle"));
                } else if (random >= 60 && random < 70) {
                    actions.add(oriActions.get("shit"));
                } else if (random >= 70 && random < 85) {
                    actions.add(oriActions.get("question"));
                } else if (random >= 85 && random < 100) {
                    actions.add(oriActions.get("amazed"));
                }
            }
        }

        @Override
        public void initAction() {
            oriActions.put("run", new Action("run", 50) {
                public void Execute() {
                    handler.sendEmptyMessageDelayed(ActionManager.RUN, 10);
                };
            });
            oriActions.put("idle", new Action("idle", 50) {
                public void Execute() {
                    handler.sendEmptyMessageDelayed(ActionManager.IDLE, 1000);
                };
            });
            oriActions.put("question", new Action("question", 50) {
                public void Execute() {
                    handler.sendEmptyMessageDelayed(ActionManager.QUESTION, 10);
                };
            });
            oriActions.put("amazed", new Action("amazed", 50) {
                public void Execute() {
                    handler.sendEmptyMessageDelayed(ActionManager.AMAZED, 10);
                };
            });
            oriActions.put("shit", new Action("shit", 50) {
                public void Execute() {
                    handler.sendEmptyMessageDelayed(ActionManager.SHIT, 10);
                };
            });
            getNextAction().Execute();// first Execute.
        }
    };

    final Handler handler =new Handler(){
        public void handleMessage(Message msg){

            switch (msg.what) {
                case ActionManager.RUN:
                    destX = mRandomGenerator.nextInt(mContext.minPixels);
                    destY = mRandomGenerator.nextInt(mContext.maxPixels);
//                    moveByLoyout();
                    moveByAnim();
                    break;
                case ActionManager.IDLE:
//                    nextStatus = ActionSeries.IDLE;
//                    isDoneCurrentTask = true;
                    Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++task is done.");
                    Action action = myActions.getNextAction();
                    if (isActive && action != null) {
                        action.Execute();
                    }
                    break;
                case ActionManager.QUESTION:
                    handler.postDelayed(mSwitchPatPicRunnable, 100);
                    break;
                case ActionManager.AMAZED:
                    if (!isPatDie) {
                        myPatView.execute(myPatView.action_surprise);
//                        myPatView.setImageResource(R.drawable.pic8_surprise);
//                        myPatView.refreshDrawableState();
                        handler.sendEmptyMessageDelayed(ActionManager.IDLE, 3000);
                    }
                    break;
                case ActionManager.SHIT:
                    shit();
                    handler.sendEmptyMessageDelayed(ActionManager.IDLE, 3000);
                    break;
                case UPDATE_PAT:
                    doMoveAnimByLoyout();
                    break;
                case UPDATE_PAT_DONE:
                    stopPatAnimByLayout();
                    break;
                default:
                    break;
            }
        }
    };
    
    Runnable mSwitchPatPicRunnable = new Runnable() {
        public void run() {
            if (isPatDie) return;
            AnimatorSet mShowAnimatorSet = new AnimatorSet();
            Animator showAnimator =  ObjectAnimator.ofFloat(myPatView, "alpha",
                    new float[] { 0.9F, 1.0F });
            mShowAnimatorSet.playTogether(showAnimator);
            mShowAnimatorSet.setDuration(100l);
            Log.i(TAG, "TTT------------=======---- mThrowable ");
//            myPatView.setImageResource(R.drawable.pic8_question);
            myPatView.execute(myPatView.action_question);
            handler.sendEmptyMessageDelayed(ActionManager.IDLE, 3000);
        }
    };
    
    private void updateNextPosition(int position) {
        switch (destGuadrant) {
        case 1:
            if (isDraged) oriY = nextPatY - (int) (position * K);
            nextPatX++;
            nextPatY = oriY + (int) (position * K);

            break;
        case 2:
            if (isDraged) oriX = nextPatX + (int) (position * K);
            nextPatY--;
            nextPatX = oriX - (int) (position * K);

            break;
        case 3:
            if (isDraged) oriX = nextPatX - (int) (position * K);
            nextPatY++;
            nextPatX = oriX + (int) (position * K);

            break;
        case 4:
            if (isDraged) oriY = nextPatY - (int) (position * K);
            nextPatX++;
            nextPatY = oriY + (int) (position * K);

            break;
        case 5:
            if (isDraged) oriY = nextPatY + (int) (position * K);
            nextPatX--;
            nextPatY = oriY - (int) (position * K);

            break;
        case 6:
            if (isDraged) oriX = nextPatX - (int) (position * K);
            nextPatY++;
            nextPatX = oriX + (int) (position * K);

            break;
        case 7:
            if (isDraged) oriY = nextPatY + (int) (position * K);
            nextPatX--;
            nextPatY = oriY - (int) (position * K);

            break;
        case 8:
            if (isDraged) oriX = nextPatX + (int) (position * K);
            nextPatY--;
            nextPatX = oriX - (int) (position * K);

            break;

        default:
            break;
        }
        isDraged = false;
    }
    
    
    private void shit() {
        int shit = mRandomGenerator.nextInt(5);

        Log.i(TAG, " shit " + shit);
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
    private void moveByAnim() {
        Log.i(TAG, "moveByLoyout: ");
        if (isPatDie) return;
//        countStep = Math.min(Math.abs(destX - currentPatX), Math.abs(destY - currentPatY));
//        stepX = Math.round(((float) destX - nextPatX)/countStep);
//        stepY = Math.round(((float) destY - nextPatY)/countStep);

        oriX = currentPatX;
        oriY = currentPatY;

        mMoveDuration = mRandomGenerator.nextInt(4000) +500;
        K = Utils.getK(oriX, oriY, destX, destY, destGuadrant);

        destGuadrant = Utils.getQuadrant(oriX, oriY, destX, destY);

        myPatView.setDegree((float) Utils.getAngle(destX, destY, currentPatX, currentPatY));

        ValueAnimator xValue =  ValueAnimator.ofObject(new PointEvaluator(),new Point(oriX,oriY),new Point(destX,destY));
        xValue.setDuration(mMoveDuration);
        xValue.setInterpolator(new LinearInterpolator());
        xValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPoint = (Point) animation.getAnimatedValue();
                nextPatX = mPoint.x;
                nextPatY = mPoint.y;
                handler.sendEmptyMessageDelayed(UPDATE_PAT, 10);
//                System.out.println("debug:(x,y) = " + nextPatX + "," + nextPatY);
            }
        });
        xValue.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                handler.sendEmptyMessageDelayed(UPDATE_PAT_DONE, 10);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        xValue.setInterpolator(new LinearInterpolator());
        xValue.start();

        shit();
        doPatAnim();

    }
    
    int mMoveDuration = 10;
    int countStep = 1;
    int destGuadrant = 1;
    int oriX,oriY;
    Double K = 1.0;
    private void moveByLoyout() {
        Log.i(TAG, "moveByLoyout: ");
        if (isPatDie) return;
        countStep = Math.min(Math.abs(destX - currentPatX), Math.abs(destY - currentPatY));
        stepX = Math.round(((float) destX - nextPatX)/countStep);
        stepY = Math.round(((float) destY - nextPatY)/countStep);

        oriX = currentPatX;
        oriY = currentPatY;

        mMoveDuration = mRandomGenerator.nextInt(40) +10;
        K = Utils.getK(oriX, oriY, destX, destY, destGuadrant);

        destGuadrant = Utils.getQuadrant(oriX, oriY, destX, destY);

        
//        一种方法是用代码
//        Matrix matrix=new Matrix();
//        mMyPat.setScaleType(ScaleType.MATRIX); //required
//        matrix.postRotate((float) 30, 10, 10);
//        mMyPat.setImageMatrix(matrix);
        
//        myPatView.setRotation((float) Utils.getAngle(destX, destY, currentPatX, currentPatY));
//        myPatView.setRotationX(myPatView.getWidth()/2);
//        myPatView.setRotationY(myPatView.getHeight()/2);
        
        myPatView.setDegree((float) Utils.getAngle(destX, destY, currentPatX, currentPatY));
        
/*        
        Log.i(TAG, " moveByLoyout stepX = " + stepX
                + " stepY = " + stepY +
                " oriX = " + oriX +
                " oriY = " + oriY +
                " destX = " + destX +
                " destY = " + destY +
                " mMoveDuration = " + mMoveDuration +
                " K = " + K +
                " maxPixels = " + mContext.maxPixels +
                " minPixels = " + mContext.minPixels 
                );*/
        shit();
        doPatAnim();
        
        new Thread(new UpdatePatMove()).start();
    }

/*    private void move(int x, int y) {
        nextPatX =  x;
        nextPatY =  y;
//        Log.i(TAG, " X " + currentPatX
//                + " minPixels = " + minPixels
//        + " maxPixels = " + maxPixels);
//        Log.i(TAG, "X" + event.getX());
        //减25为状态栏的高度
//        Log.i(TAG, "RawY" + event.getRawY());
//        Log.i(TAG, "Y" + event.getY());
         //刷新
//        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        // 设置悬浮窗口长宽数据
//        wmPatParams.width = 20;
//        wmPatParams.height = 20;
//        mWindowManager.updateViewLayout(mMyPat, wmParams);
//        doPatAnim();
        int duration = (mRandomGenerator.nextInt(6)+2)*1000;
        doMoveAnim(currentPatX, nextPatX, currentPatY, nextPatY, duration);
        Log.i(TAG, "doMoveAnim done" );
        shit();
    }*/
    
    @SuppressLint("NewApi")
    private void doPatAnim() {

//        int[] drawables = {R.drawable.pic8_move,R.drawable.pic8};
//        myPatView.startAnim(drawables);
        myPatView.execute(myPatView.action_move);
        
//      myPatView.setBackgroundResource(R.drawable.move_anim);
//        AnimationDrawable animator = (AnimationDrawable) myPatView.getBackground();
//        mMyPat.setBackgroundDrawable(null);
//        myPatView.setImageDrawable(null);
//        animator.start();
    }
    
    public void stopPatAnimByLayout() {
        if (isPatDie) return;
//        currentPatX = nextPatX;
//        currentPatY = nextPatY;
//        AnimationDrawable animator = (AnimationDrawable) myPatView.getBackground();  
//        animator.stop();
//        myPatView.setBackgroundDrawable(null);
        if (myPatView == null) {
            return;
        }
//        myPatView.setImageResource(R.drawable.pic8);
        myPatView.execute(myPatView.action_normal);
//        myPatView.stopAnim();
        handler.sendEmptyMessageDelayed(ActionManager.IDLE, 3000);
    }
    
    private void doMoveAnimByLoyout() {
        if (isPatDie) return;
        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
//        nextPatX+=stepX;
//        nextPatY+=stepY;

        wmPatParams.x = nextPatX;
        currentPatX = wmPatParams.x;
        //减25为状态栏的高度
        wmPatParams.y = nextPatY;
        currentPatY = wmPatParams.y;
        mContext.updateView(mFloatLayout, wmPatParams);
    }
    
    class UpdatePatMove  implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i < countStep ; i++) {

                if (isPatDie) break;
                try {
                    Thread.sleep(mMoveDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateNextPosition(i);
                handler.sendEmptyMessageDelayed(UPDATE_PAT, 10);
            }
            handler.sendEmptyMessageDelayed(UPDATE_PAT_DONE, 10);
        }
    }
}
