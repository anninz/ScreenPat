package com.thq.pat.plugapilib;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.thq.pat.plugapilib.IPlugAPI;

public abstract class AbsShitProvider {

    IPlugAPI mContext;
    ArrayList<ImageView> mRecyledShits = new ArrayList<ImageView>();
    HashMap<Integer, ImageView> mShitMap = new HashMap<Integer, ImageView>();
    static int shitCount = 0;
    final int MAX_SHIT_NUM = 20;

    Bitmap shitBitmap;
    BitmapDrawable shitDrawable;

    public abstract void DoShit(int currentPatX, int currentPatY) ;
    public abstract void Clear() ;
    
    public AbsShitProvider(IPlugAPI fxService) {
        super();
        mContext = fxService;
    }

    boolean recycleShit(View v) {
        int position = (Integer)v.getTag();
        v.setVisibility(View.GONE);
        mRecyledShits.add(mShitMap.get(position));
        mShitMap.remove(position);
//        mContext.removeView(v);
        return true;
    }
    
    boolean recycleAllShit() {
//        Iterator iter = mShitMap.entrySet().iterator();
        Iterator iter = mShitMap.keySet().iterator();
        while (iter.hasNext()) {
          //Map.Entry entry = (Map.Entry) iter.next();
            Object key = iter.next();
//            Object key = entry.getKey();
//            Object val = entry.getValue();
            ImageView imageView = mShitMap.get(key);

            //added by hongqi @ 0712
            if (imageView != null) {
                Utils.recycleImageView(imageView);
                mContext.removeView(imageView);
                imageView = null;
            }
//            mShitMap.remove(key);
            iter.remove();//fixed java.util.ConcurrentModificationException by hongqi
        }

        while (mRecyledShits.size() > 0) {
            ImageView imageView = mRecyledShits.get(0);
            if (imageView != null) {
                Utils.recycleImageView(imageView);
                mContext.removeView(imageView);
                imageView = null;
            }
            mRecyledShits.remove(0);
        }
        shitCount = 0;
        Log.d("THQ11", "recycleAllShit: mShitMap.size = " + mShitMap.size());
        return true;
    }
}
