package com.thq.pat;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class Utils {

    /**
     * @return The selected quadrant.
     */
    
    static public int getQuadrant(int currentPatX2, int currentPatY2, int destX2, int destY2) {
        if (destX2>currentPatX2) {
            if (destY2>currentPatY2) {
                return (destX2-currentPatX2)>(destY2-currentPatY2)?3:4;
            } else {
                return (destX2-currentPatX2)>(currentPatY2-destY2)?2:1;
            }
        } else {
            if (destY2>currentPatY2) {
                return (currentPatX2-destX2)>(destY2-currentPatY2)?6:5;
            } else {
                return (currentPatX2-destX2)>(currentPatY2-destY2)?7:8;
            }
        }
    }

    /**
     * @return The selected quadrant.
     */
    static public double getK(int x, int y, int x1, int y1, int destGuadrant) {
//        return (double)(y1 - y)/(x1 - x);
        
        double K = 1.0;
        switch (destGuadrant) {
        case 1:
        case 4:
        case 5:
        case 8:
              K = (double)(y1 - y)/(x1 - x);
            break;
        case 2:
        case 3:
        case 6:
        case 7:
            K = (double)(x1 - x)/(y1 - y);
            break;

        default:
            break;
        }
        return K;
    }

    /**
     * @return The angle of the unit circle with the image view's center
     */
    static public  double getAngle(double xTouch, double yTouch, int currentX, int currentY) {
        double x = xTouch - (currentX );
        double y = currentY - yTouch/* - (currentPatY / 2d)*/;

        switch (getQuadrant(x, y)) {
        case 1:
            return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

        case 2:
            
            return /*180*/ - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
        case 3:
            return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);

        case 4:
            return 180 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

        default:
            // ignore, does not happen
            return 0;
        }
    }

    /**
     * @return The selected quadrant.
     */
    static public int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
    
    
    /**
     * 回收ImageView占用的图像内存;
     * @param view
     */
    public static void recycleImageView(View view) {
        if(view==null) return;
        if(view instanceof ImageView){
            Drawable drawable=((ImageView) view).getDrawable();
            if(drawable instanceof BitmapDrawable){
                Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                if (bmp != null && !bmp.isRecycled()){
                    ((ImageView) view).setImageBitmap(null);
                    bmp.recycle();
                    bmp=null;
                }
            }
        }
    }

    /**
     * 回收ImageView占用的图像内存;
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if(bitmap==null) return;
        if (bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
    }

}
