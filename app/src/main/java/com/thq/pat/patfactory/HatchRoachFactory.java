package com.thq.pat.patfactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.thq.pat.FxService;
import com.thq.pat.R;
import com.thq.pat.plugapilib.IHatchProvider;
import com.thq.pat.plugapilib.IPat;
import com.thq.pat.plugapilib.IPlugAPI;

import java.util.HashMap;
import java.util.Map;

public class HatchRoachFactory implements IHatchProvider {

    @Override
    public IPat doHatch(IPlugAPI fxService) {
        // TODO Auto-generated method stub
        productSkin((FxService)fxService);
        return new RoachPat((FxService)fxService,skinMaps);
    }

    public void productSkin(FxService fxService) {
        if (skinMaps.size() > 0) return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 2;
        Bitmap normal = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.pic8, options);
        skinMaps.put("normal",normal);
        Bitmap question = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.pic8_question, options);
        skinMaps.put("question",question);
        Bitmap surprise = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.pic8_surprise, options);
        skinMaps.put("surprise",surprise);
        Bitmap move = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.pic8_move, options);
        skinMaps.put("move",move);
    }

    Map<String, Bitmap> skinMaps = new HashMap<>();
}
