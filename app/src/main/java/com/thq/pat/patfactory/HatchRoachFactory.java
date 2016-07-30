package com.thq.pat.patfactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.thq.pat.FxService;
import com.thq.pat.R;

import java.util.HashMap;
import java.util.Map;

public class HatchRoachFactory implements HatchProvider {

    @Override
    public Pat doHatch(FxService fxService) {
        // TODO Auto-generated method stub
        productSkin(fxService);
        return new RoachPat(fxService,skinMaps);
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
