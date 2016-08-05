package com.thq.pat.plug.catgirl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class PatSkin extends View {

    public float degree = 0;

    /**
     * @param context
     */
    public PatSkin(Context context) {
        this(context, null);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public PatSkin(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    Paint paint=new Paint();

    public interface UpdateSkin {
        Bitmap callback(View view);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        rotatBitmap = myCall.callback(this);
        Log.d("THQ2", "onDraw: "  + " " +rotatBitmap + " " /*+ others.size()*/);

        if (rotatBitmap != null) {
            ifNeedUpdateMatrix();
            //抗锯齿
            canvas.setDrawFilter(pfd);

            canvas.drawBitmap(rotatBitmap, matrix, paint);
        }
        super.onDraw(canvas);
    }

    Matrix matrix = new Matrix();
    private void ifNeedUpdateMatrix() {

        if (oldDegree == degree && !sizeChange) {
            return;
        }

        bitmapSize = rotatBitmap.getHeight();
        scaleX = scaleY = (float) (height) / bitmapSize;
        transform = (bitmapSize - height) / 2f;

        oldDegree = degree;
        sizeChange = false;

//        // 设置转轴位置
//        matrix.setTranslate((float)bitmapSize / 2, (float)bitmapSize / 2);
//
//        // 开始转
//        matrix.preRotate(degree);
//        // 转轴还原
//        matrix.preTranslate(-(float)bitmapSize / 2, -(float)bitmapSize / 2);

        if (degree < 180) {
            float matrixValues[] = {-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
            matrix.setValues(matrixValues);
            //若是matrix.postTranslate(width,0);//表示将图片左右倒置
            matrix.postTranslate(bitmapSize , 0);
        } else {
            float matrixValues[] = {1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
            matrix.setValues(matrixValues);
            //若是matrix.postTranslate(width,0);//表示将图片左右倒置
//            matrix.postTranslate(bitmapSize , 0);
        }
        //scale view
        matrix.postScale(scaleX, scaleY, bitmapSize / 2f, bitmapSize / 2f);

        // 将位置送到view的中心
        matrix.postTranslate(-transform,  -transform);
        Log.d("THQ2", "onDraw: " + bitmapSize+ " " + width + " " +scaleX + " " + transform);
    }

    float oldDegree = 0;
    public void setDegree(float degree) {
        this.degree = degree;
//        invalidate();
    }


    public void onDestroy() {
    }

    /**
     * 抗锯齿
     */
    private PaintFlagsDrawFilter pfd;

    /**
     * 初始化handler与速度计算器
     */
    private void init() {
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        // 默认是有一张图片的

        initSize();
    }


    /**
     * 图片的宽度
     */
    int width;

    /**
     * 图片的高度
     */
    int height;

    /**
     * 旋转的图片
     */
    Bitmap rotatBitmap;

    private void initSize() {
        width = getWidth();
        height = getHeight();

//            maxwidth = Math.sqrt(width * width + height * height);//hongqi
    }

    boolean sizeChange = false;
    public void setViewSize(int size) {
        ViewGroup.LayoutParams mPatParams = getLayoutParams();
        mPatParams.height = size;
        mPatParams.width = size;
        setLayoutParams(mPatParams);
        initSize(size);
        sizeChange = true;
        postInvalidate();
    }

    float scaleX,scaleY;
    int bitmapSize;
    float transform;
    private void initSize(int size) {
        width = size;
        height = size;
    }

    UpdateSkin myCall;

    public void execute(UpdateSkin command) {
        myCall = command;
        invalidate();
    }
}