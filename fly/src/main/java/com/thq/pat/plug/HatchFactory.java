package com.thq.pat.plug;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.thq.pat.plug.patfactory.FlyPat;
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
        return new FlyPat(fxService,skinMaps);
    }

    public void productSkin(IPlugAPI fxService) {
        if (skinMaps.size() > 0) return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        Bitmap normal = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly1, options);
        normals.add(normal);
        skinMaps.put("normal",normals);

        Bitmap move0 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly2, options);
        moves.add(move0);
        Bitmap move1 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly3, options);
        moves.add(move1);
        Bitmap move2 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly4, options);
        moves.add(move2);
        Bitmap move3 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly5, options);
        moves.add(move3);
        skinMaps.put("move",moves);

        Bitmap twist0 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly11, options);
        others.add(twist0);
        Bitmap twist1 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly12, options);
        others.add(twist1);
        Bitmap twist2 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly13, options);
        others.add(twist2);
        Bitmap twist3 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly14, options);
        others.add(twist3);
        skinMaps.put("other",others);
    }

    List<Bitmap> others = new ArrayList<>();
    List<Bitmap> moves = new ArrayList<>();
    List<Bitmap> normals = new ArrayList<>();
    Map<String, List> skinMaps = new HashMap<>();


    static public String getAppName(Context context) {
        return /*"PlugApk";//*/context.getString(R.string.app_name);
    }

    static public Bitmap getAppIcon(Context context) {
        return /*"PlugApk";//*/ BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
    }
}
