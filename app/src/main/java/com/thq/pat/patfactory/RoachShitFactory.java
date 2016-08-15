package com.thq.pat.patfactory;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.thq.pat.FxService;
import com.thq.pat.R;

public class RoachShitFactory extends AbsShitProvider {
    
    String TAG = "RoachShit";

/*    ArrayList<ImageView> mRecyledShits = new ArrayList<ImageView>();
    HashMap<Integer, ImageView> mShitMap = new HashMap<Integer, ImageView>();
    int shitCount = 0;
    final int MAX_SHIT_NUM = 20;*/

    FrameLayout mShitLayout;
    LayoutParams wmShitParams;
    
    public RoachShitFactory(FxService fxService) {
        super(fxService);
        shitBitmap = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.shit);
        shitDrawable = new BitmapDrawable(fxService.getResources(), shitBitmap);
        createFloatView();
    }

    @Override
    public void DoShit(int currentPatX, int currentPatY) {

        if ((shitCount - mRecyledShits.size()) > MAX_SHIT_NUM) {
            mContext.updateShowInfo(100);
            return;
        }
        
//        if (mWindowManager == null) return;
        ImageView imageView;
        if (mRecyledShits.size() > 0) {
            imageView = mRecyledShits.get(0);
            mShitMap.put((Integer) imageView.getTag(), imageView);
            mRecyledShits.remove(0);
            wmShitParams.x = (int) currentPatX + 5;
            wmShitParams.y = (int) currentPatY + 16;
            ((FxService) mContext).updateView(imageView, wmShitParams);
            imageView.setVisibility(View.VISIBLE);
        } else {
            shitCount ++;
            imageView = new ImageView(mContext);
//            imageView.setImageResource(R.drawable.shit);//0728
            imageView.setImageDrawable(shitDrawable);
            imageView.setOnClickListener(new OnClickListener() 
            {
                
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onClick(View v) 
                {
                    mContext.updateShowInfo(-1);
                    recycleShit(v);
                }
            });
            imageView.setTag(shitCount);
            mShitMap.put(shitCount, imageView);
            
            wmShitParams.x = (int) currentPatX + 5;
            wmShitParams.y = (int) currentPatY + 16;
            mContext.addView(imageView, wmShitParams);
        }
    }

    @Override
    public void Clear() {
        recycleAllShit();
    }


    private void initPatParams(WindowManager.LayoutParams wmPatParams) {
        //设置window type
        wmPatParams.type = LayoutParams.TYPE_TOAST; 
        //设置图片格式，效果为背景透明
        wmPatParams.format = PixelFormat.RGBA_8888; 
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmPatParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_NO_LIMITS
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/
                | 0x00000080 |0x20000000;      
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmPatParams.gravity = Gravity.LEFT | Gravity.TOP;       
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmPatParams.x = 150;
        wmPatParams.y = 250;

        // 设置悬浮窗口长宽数据
        wmPatParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmPatParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }
    
    private void createFloatView() {
        wmShitParams = new LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
//        Toast.makeText(this, "" + maxPixels, Toast.LENGTH_LONG).show();
        
        LayoutInflater inflater = LayoutInflater.from(mContext.getApplication());
//        mShitLayout = (FrameLayout) inflater.inflate(R.layout.shit_layout, null);
        initPatParams(wmShitParams);

        SharedPreferences sp = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        int patSize =sp.getInt("size",45) / 2;

        wmShitParams.height = patSize;
        wmShitParams.width = patSize;
        wmShitParams.type = LayoutParams.TYPE_PHONE;
/*        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mMyCat.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mMyCat.getMeasuredHeight()/2);*/
        
    }
    
}
