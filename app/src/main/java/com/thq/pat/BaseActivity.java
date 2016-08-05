package com.thq.pat;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Method;

public class BaseActivity extends AppCompatActivity{
	
	protected AssetManager mAssetManager;//资源管理器  
	protected Resources mResources;//资源  
	protected Theme mTheme;//主题
	boolean isHost = false;
	

	protected void loadResources(String dexPath) {
        try {  
            AssetManager assetManager = AssetManager.class.newInstance();  
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);  
            addAssetPath.invoke(assetManager, dexPath);  
            mAssetManager = assetManager;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        Resources superRes = super.getResources();  
        superRes.getDisplayMetrics();
        superRes.getConfiguration();  
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),superRes.getConfiguration());  
        mTheme = mResources.newTheme();  
        mTheme.setTo(super.getTheme());
    }
	
	@Override  
	public AssetManager getAssets() {  
	    return (mAssetManager == null || isHost) ? super.getAssets() : mAssetManager;
	}  
	
	@Override  
	public Resources getResources() {  
	    return (mResources == null || isHost) ? super.getResources() : mResources;
	}  
	
	@Override  
	public Theme getTheme() {  
	    return (mTheme == null || isHost) ? super.getTheme() : mTheme;
	}

}
