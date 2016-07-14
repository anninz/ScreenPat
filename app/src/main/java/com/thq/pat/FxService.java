package com.thq.pat;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.thq.pat.ScreenListener.ScreenStateListener;
import com.thq.pat.contentfactory.ContentFactory;
import com.thq.pat.patfactory.HatchProvider;
import com.thq.pat.patfactory.HatchRoachFactory;
import com.thq.pat.patfactory.Pat;

public class FxService extends Service {


    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    public static int maxPixels,minPixels;

    private static final String TAG = "FxService";

    final Handler handler =new Handler(){
        public void handleMessage(Message msg){

            switch (msg.what) {
/*                case SHOW_INFO:
                    break;
                case HIDE_INFO:
                    break;*/
                default:
                    break;
            }
        }
    };

    ScreenStateListener mScreenStateListener = new ScreenStateListener() {

        @Override
        public void onScreenOn() {
            for (Pat pat:pats) {
                pat.wakeUp();
            }
            Log.d(TAG, "onScreenOn ");
        }

        @Override
        public void onScreenOff() {
            for (Pat pat:pats) {
                pat.sleep();
            }
            Log.d(TAG, "onScreenOff ");
        }

        @Override
        public void onUserPresent() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onUserPresent ");
        }
        
    };

    ScreenListener mScreenListener;
    ContentFactory mContentFactory;
    ShowInfo showInfo;
    
    ArrayList<Pat> pats = new ArrayList<Pat>();
    private AlarmManager mAlarmManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "oncreat");

        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        maxPixels = Math.max(metrics.heightPixels, metrics.widthPixels);
        minPixels = Math.min(metrics.heightPixels, metrics.widthPixels);
        
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mScreenListener = new ScreenListener(this);
        mScreenListener.begin(mScreenStateListener);
        mContentFactory = ContentFactory.getInstance();
        mContentFactory.downloadContent();
        
        showInfo = new ShowInfo(this);

        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        int patSize = sp.getInt("patnum",1);
        patSize = patSize < 4?patSize:3;


        HatchProvider hatchProvider = new HatchRoachFactory();
        for (int i = 0; i < patSize; i++) {
            pats.add(hatchProvider.doHatch(this));
        }

        for (Pat pat:pats) {
            pat.startLife();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    


    /**
     * Context中有一个startActivity方法，Activity继承自Context，
     * 重载了startActivity方法。如果使用 Activity的startActivity方法，
     * 不会有任何限制，而如果使用Context的startActivity方法的话，
     * 就需要开启一个新的task，遇到上面那个异常的， 都是因为使用了Context的startActivity方法。
     * 解决办法是，加一个flag。intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
     */
    @Override
    public void startActivity(Intent intent) {
        // TODO Auto-generated method stub\
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    public ContentFactory getContentFactory() {
        return mContentFactory;
    }
    

    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mScreenListener.unregisterListener();

        if(pats != null) {
            for (Pat pat:pats) {
                pat.endLife();
            }
        }
        if(showInfo != null) {
            showInfo.destroy();
        }
    }
    
    public void updateView(View view, LayoutParams layoutParams) {
        mWindowManager.updateViewLayout(view, layoutParams);
    }
    
    public void addView(View view, LayoutParams layoutParams) {
        mWindowManager.addView(view, layoutParams);
    }
    
    public void removeView(View view) {
        mWindowManager.removeView(view);
    }

    public void updateView(ImageView view, LayoutParams layoutParams) {
        mWindowManager.updateViewLayout(view, layoutParams);
    }

    public void updateShowInfo(int index) {
        showInfo.updateShowInfo(index);
    }
}