package com.thq.pat.plugapilib;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by user on 16-7-31.
 */
public interface IPlugAPI {
    public Context getContext();
    public void updateShowInfo(int id);
    public void updateView(View v, WindowManager.LayoutParams layoutParams);
    public void addView(View v, WindowManager.LayoutParams layoutParams);
    public void removeView(View v);
    public int getInt(String key, int id);
    public Resources getResources();
    public Application getApplication();
    public void detectGesture(MotionEvent motionEvent);
}
