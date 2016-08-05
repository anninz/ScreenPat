package com.thq.pat.plug;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.thq.pat.plug.catgirl.patfactory.CatGirlPat;
import com.thq.pat.plugapilib.IHatchProvider;
import com.thq.pat.plugapilib.IPat;
import com.thq.pat.plugapilib.IPlugAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HatchFactory implements IHatchProvider {

    @Override
    public IPat doHatch(IPlugAPI fxService) {
        // TODO Auto-generated method stub
        productSkin(fxService);
        return new CatGirlPat(fxService,skinMaps);
    }

    public void productSkin(IPlugAPI fxService) {
        if (skinMaps.size() > 0) return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;

        Bitmap normal = BitmapFactory.decodeResource(fxService.getResources(), R.drawable._28, options);
        normals.add(normal);
        skinMaps.put("normal",normals);

        int[] runIds =  getIds(fxService.getResources(), R.array.run);
        for (int i:runIds) {
            Bitmap run = BitmapFactory.decodeResource(fxService.getResources(), i, options);
            Log.d("THHQ2", "productSkin: " + i + " " + run);
            runs.add(run);
        }
        skinMaps.put("runs",runs);

        int[] walkIds =  getIds(fxService.getResources(), R.array.walk);
        for (int i:walkIds) {
            Bitmap run = BitmapFactory.decodeResource(fxService.getResources(), i, options);
            walks.add(run);
        }
        skinMaps.put("walks",walks);

        int[] happyIds =  getIds(fxService.getResources(), R.array.happy);
        for (int i:happyIds) {
            Bitmap run = BitmapFactory.decodeResource(fxService.getResources(), i, options);
            happys.add(run);
        }
        skinMaps.put("happys",happys);

        int[] wakeIds =  getIds(fxService.getResources(), R.array.wake_up);
        for (int i:wakeIds) {
            Bitmap run = BitmapFactory.decodeResource(fxService.getResources(), i, options);
            wakes.add(run);
        }
        skinMaps.put("wakes",wakes);

        int[] sleepIds =  getIds(fxService.getResources(), R.array.sleep);
        for (int i:sleepIds) {
            Bitmap run = BitmapFactory.decodeResource(fxService.getResources(), i, options);
            sleeps.add(run);
        }
        skinMaps.put("sleeps",sleeps);

    }

    private int[] getIds(Resources res, int iconsRes) {
        if (iconsRes == 0) {
            return null;
        }
        TypedArray array = res.obtainTypedArray(iconsRes);
        int n = array.length();
        int ids[] = new int[n];
        for (int i = 0; i < n; ++i) {
            ids[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return ids;
    }

    List<Bitmap> happys = new ArrayList<>();
    List<Bitmap> wakes = new ArrayList<>();
    List<Bitmap> sleeps = new ArrayList<>();
    List<Bitmap> runs = new ArrayList<>();
    List<Bitmap> walks = new ArrayList<>();
    List<Bitmap> normals = new ArrayList<>();
    Map<String, List> skinMaps = new HashMap<>();


    static public String getAppName(Context context) {
        return /*"PlugApk";//*/context.getString(R.string.app_name);
    }

    static public Bitmap getAppIcon(Context context) {
        return /*"PlugApk";//*/ BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
    }
}
