package com.thq.pat.patfactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.thq.pat.FxService;
import com.thq.pat.R;
import com.thq.pat.plugapilib.IHatchProvider;
import com.thq.pat.plugapilib.IPat;
import com.thq.pat.plugapilib.IPlugAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HatchFlyFactory implements IHatchProvider {

    @Override
    public IPat doHatch(IPlugAPI fxService) {
        // TODO Auto-generated method stub
        productSkin((FxService)fxService);
        return new FlyPat((FxService)fxService,skinMaps);
    }

    public void productSkin(FxService fxService) {
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
        Bitmap twist1 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly22, options);
        others.add(twist1);
        Bitmap twist2 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly33, options);
        others.add(twist2);
        Bitmap twist3 = BitmapFactory.decodeResource(fxService.getResources(), R.drawable.fly44, options);
        others.add(twist3);
        skinMaps.put("other",others);
    }

    List<Bitmap> others = new ArrayList<>();
    List<Bitmap> moves = new ArrayList<>();
    List<Bitmap> normals = new ArrayList<>();
    Map<String, List> skinMaps = new HashMap<>();
}
