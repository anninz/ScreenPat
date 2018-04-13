package com.thq.pat;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.thq.pat.ScreenListener.ScreenStateListener;
import com.thq.pat.contentfactory.ContentFactory;
import com.thq.pat.patfactory.HatchRoachFactory;
import com.thq.pat.plugapilib.IHatchProvider;
import com.thq.pat.plugapilib.IPat;
import com.thq.pat.plugapilib.IPlugAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.DexClassLoader;

public class FxService extends BaseService implements IPlugAPI {


    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    public static int maxPixels,minPixels;

    private static final String TAG = "FxService";

/*    final Handler handler =new Handler(){
        public void handleMessage(Message msg){

            switch (msg.what) {
*//*                case SHOW_INFO:
                    break;
                case HIDE_INFO:
                    break;*//*
                default:
                    break;
            }
        }
    };*/

    ScreenStateListener mScreenStateListener = new ScreenStateListener() {

        @Override
        public void onScreenOn() {
            for (IPat pat:pats) {
                pat.wakeUp();
            }
            Log.d(TAG, "onScreenOn ");
        }

        @Override
        public void onScreenOff() {
            for (IPat pat:pats) {
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
    ActionListener mActionListener;
    ContentFactory mContentFactory;
    ShowInfo showInfo;
    
    ArrayList<IPat> pats = new ArrayList<IPat>();
    private AlarmManager mAlarmManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "oncreat");

        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);//之前出现过因为minSDK 版本太低而分辨率很低，一直没有找到原因。
        maxPixels = Math.max(metrics.heightPixels, metrics.widthPixels);
        minPixels = Math.min(metrics.heightPixels, metrics.widthPixels);
        
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        mScreenListener = new ScreenListener(this);
        mScreenListener.begin(mScreenStateListener);

        mActionListener = new ActionListener(this);
        mActionListener.begin(new ActionListener.MyActionListener() {
            @Override
            public void action(String action) {
                for (IPat pat:pats) {
                    pat.command(action);
                }
            }
        });

        mContentFactory = ContentFactory.getInstance();
        mContentFactory.downloadContent();
        
        showInfo = new ShowInfo(this);

        initGestureDetect();

//        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
//        int patSize = sp.getInt("patnum",1);
//        patSize = patSize < 5?patSize:5;


//        IHatchProvider hatchProvider = new HatchRoachFactory();
//        for (int i = 0; i < patSize; i++) {
//            pats.add(hatchProvider.doHatch(this));
//        }
//        IHatchProvider FlyProvider = new HatchFlyFactory();
//        for (int i = 0; i < patSize; i++) {
//            pats.add(FlyProvider.doHatch(this));
//        }
        LoadClass();

        for (IPat pat:pats) {
            pat.startLife();
        }
    }

    DexClassLoader classLoader = null;
    private void LoadClass() {
//        String dexOutputDir = getApplicationInfo().dataDir;
//        String apkdir = "/data/data/" + getPackageName() + "/dynamicapk" ;
//        String DynamicApkPath = apkdir + File.separator + "plugapk-release-unsigned.apk";
//        Log.d("THHQ", dexOutputDir + " " + " " + new File(DynamicApkPath).exists() +" " + new File(apkdir).mkdir());

        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>();
        set = sp.getStringSet("PatSet",set);
        final File tmpDir = getDir("dex", 0);
        for (String string:set) {

            String[] strings = string.split("#");

            if ("Host".equals(strings[1])) {
                IHatchProvider hatchProvider = new HatchRoachFactory();
                for (int i = 0; i < Integer.decode(strings[0]); i++) {
                    pats.add(hatchProvider.doHatch(this));
                }
                continue;
            }

            File dir = new File(strings[1]);
            if (!dir.exists()) continue;
            String filePath = dir.getPath();
//        if (new File(DynamicApkPath).exists()) {
            classLoader = new DexClassLoader(filePath, tmpDir.getAbsolutePath(), null,getClassLoader());
//        }

//            String filePath = "/data/data/" + getPackageName() + "/dynamicapk" + "/plugapk-release-unsigned.apk";
            loadResources(filePath);

            try{
                if (classLoader != null) {
                    Class clazz = classLoader.loadClass("com.thq.pat.plug.HatchFactory");
//                Method method = clazz.getMethod("getTextString", Context.class);
//                String str = (String) method.invoke(null, this);
                    IHatchProvider FlyProvider = (IHatchProvider)clazz.newInstance();
                    for (int i = 0; i < Integer.decode(strings[0]); i++) {
                        pats.add(FlyProvider.doHatch(this));
                    }
                }
            }catch(Exception e){
                Log.i("Loader", "error:"+Log.getStackTraceString(e));
            }
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
        mActionListener.unregisterListener();

        if(pats != null) {
            for (IPat pat:pats) {
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

    @Override
    public int getInt(String key, int id) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        return sp.getInt(key,45);
    }

    public void updateView(ImageView view, LayoutParams layoutParams) {
        mWindowManager.updateViewLayout(view, layoutParams);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void updateShowInfo(int index) {
        showInfo.updateShowInfo(index);
    }

    @Override
    public void detectGesture(MotionEvent motionEvent) {
        mGestureBuilder.dispatchTouchEvent(motionEvent);
    }

    GestureBuilder mGestureBuilder = new GestureBuilder(this);    // 手势库
    GestureLibrary mGestureLib;
    private void initGestureDetect() {
        mGestureBuilder.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
//        mGestureBuilder.setFadeOffset(1000); // 多笔画每两次的间隔时间
//        mGestureBuilder.setOrientation(90);
        // 手势识别的监听器
        mGestureBuilder.addOnGesturePerformedListener(new GestureBuilder.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureBuilder overlay,
                                           Gesture gesture) {
                // 从手势库中查询匹配的内容，匹配的结果可能包括多个相似的结果，匹配度高的结果放在最前面
                ArrayList<Prediction> predictions = mGestureLib
                        .recognize(gesture);
//                Log.d(TAG, "onGesturePerformed: " + predictions.size());
                if (predictions.size() > 0) {
                    Prediction prediction = (Prediction) predictions.get(0);
                    // 匹配的手势
//                    showBitmap(gesture);
                    if (prediction.score > 3.0) { // 越匹配score的值越大，最大为10
                        Intent intent = new Intent();
                        intent.setClass(FxService.this, MainActivity.class);
                        FxService.this.startActivity(intent);
//                        Log.d(TAG, "onGesturePerformed: " + " "+prediction.score + "  -- " + prediction.name);
                    }
                }
            }
        });
        loadGestureLib();
    }

    private void loadGestureLib() {
        mGestureLib = GestureLibraries.fromFile("/sdcard/thqpat/sina/mygestures");
        //【2】导入自定义gesture lib。
        if(!mGestureLib.load()){
            Toast.makeText(this, "暂无自定义手势！", Toast.LENGTH_LONG).show();
        }
    }
/*
    private void showBitmap(Gesture gesture) {
            //加载save.xml界面布局代表的视图
        LayoutInflater inflater = LayoutInflater.from(getApplication());
            View saveDialog = inflater.inflate(
                    R.layout.save, null);
            // 获取saveDialog里的show组件
            ImageView imageView = (ImageView) saveDialog
                    .findViewById(R.id.show);
            // 获取saveDialog里的gesture_name组件
            final EditText gestureName = (EditText) saveDialog
                    .findViewById(R.id.gesture_name);
            // 根据Gesture包含的手势创建一个位图
            Bitmap bitmap = gesture.toBitmap(128, 128, 10, 0xFFFF0000);
            imageView.setImageBitmap(bitmap);
            //使用对话框显示saveDialog组件
        AlertDialog alert;
        alert =  new AlertDialog.Builder(FxService.this)
                    .setView(saveDialog)
                    .setPositiveButton("保存", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which)
                        {
                        }
                    })
                    .setNegativeButton("取消", null).create();
        alert.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));//service need add the flag.
        alert.show();
    }*/
}