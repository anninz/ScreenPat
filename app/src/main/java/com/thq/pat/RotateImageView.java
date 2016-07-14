package com.thq.pat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RotateImageView extends View {

        public float degree = 0;

        /**
         * @param context
         */
        public RotateImageView(Context context) {
            this(context, null);
            init();
        }

        /**
         * @param context
         * @param attrs
         */
        public RotateImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }
        
       int circleHeight ,circleWidth;
       
       
       @Override
       protected void onDraw(Canvas canvas) {
           
           if (isAniming) {
               rotatBitmap = bitmaps[index];
               index = index == 0 ? 1:0; 
               Log.d("THQ draw", "drawable = "+rotatBitmap + "  bitmap.getWidth() = " + rotatBitmap.getWidth());
           }
           if (rotatBitmap != null) {
               /*rotatBitmap = bitmap;Bitmap.createBitmap(bitmap, 0, 0,
                       20, 20,
                       matrix, false);*/
               Log.d("THQ draw33", "drawable = "+rotatBitmap + "  rotatBitmap.getWidth() = " + rotatBitmap.getWidth() + " degree = " + degree);
               initSize();

               Matrix matrix = new Matrix();
               // 设置转轴位置
               matrix.setTranslate((float)width / 2, (float)height / 2);

               // 开始转
               matrix.preRotate(degree);
               // 转轴还原
               matrix.preTranslate(-(float)width / 2, -(float)height / 2);

               // 将位置送到view的中心
               matrix.postTranslate((float)(maxwidth - width) / 2, (float)(maxwidth - height) / 2);

               //抗锯齿
               canvas.setDrawFilter(pfd);

               canvas.drawBitmap(rotatBitmap, matrix,paint);
           }
           super.onDraw(canvas);
           if (isAniming)postInvalidateDelayed(200);
       }

       Paint paint=new Paint();
       
       
        
/*        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            
//            final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
             Bitmap bitmap = this.getDrawingCache();
            
            circleHeight = getHeight();
            circleWidth = getWidth();
            
            if (bitmap == null) {
                BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
                if (bd != null) {
                    bitmap = bd.getBitmap();
                }
            }
            final Drawable drawable;
            if (isAniming) {
                drawable = drawables[index];BitmapDrawable bd = (BitmapDrawable) drawable;
                if (bd != null) {
                    bitmap = bd.getBitmap();
                }
                index = index == 0 ? 1:0; 
                Log.d("THQ draw", "drawable = "+bitmap + "  index = " + index);
            } else {
                drawable = this.getDrawable();
                bitmap = this.getDrawingCache();
                Log.d("THQ draw22", "drawable = "+bitmap + "  index = " + index);
            }
             int w=0,h=0;
            if (drawable != null) {
            final Rect bounds = drawable.getBounds();
             w= bounds.right - bounds.left;
             h= bounds.bottom - bounds.top;
            }

//            if (drawable != null) {
//                
//                final int saveCount = canvas.save();
//                canvas.rotate(degree, circleWidth / 2, circleHeight / 2);
//                drawable.draw(canvas);
//                canvas.restoreToCount(saveCount);
//            }


            canvas.rotate(degree, w + circleWidth / 2,h+ circleHeight / 2);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }
            super.onDraw(canvas);

            if (isAniming)invalidate();
        }*/

        public void setDegree(float degree) {
            this.degree = degree;
            invalidate();
        }


    public void onDestroy() {
        for (Bitmap bmp:bitmaps) {
            Utils.recycleBitmap(bmp);
        }
        Utils.recycleBitmap(rotatBitmap);
    }
        
        Bitmap[] bitmaps;
        boolean isAniming = false;
        int index = 0;
        
        public void startAnim(int[] drawables) {
            if (bitmaps == null) {
                Bitmap[] bitmaps1 = new Bitmap[drawables.length];
                for (int i = 0; i < drawables.length; i++) {
                    int j = drawables[i];
                    bitmaps1[i] = res2bitmap(j);
                }
                bitmaps = bitmaps1;
            }
            isAniming = true;
        }
        
        public void setImageResource(int id) {
            rotatBitmap = res2bitmap(id);
            initSize();
            postInvalidate();
        }
        
        private Bitmap res2bitmap(int resID) {
            int vWidth = getWidth();
            int vHight = getHeight();
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), resID, options);/* 这里返回的bmp是null */
            int height = options.outHeight * vHight / options.outWidth;

            options.outWidth = vWidth;

            options.outHeight = height; 

            options.inJustDecodeBounds = false;
            
            options.inSampleSize = options.outWidth / vHight; /*图片长宽方向缩小倍数*/

            Bitmap bigBitmap = BitmapFactory.decodeResource(getResources(), resID, options);
            
            bigBitmap = zoomImage(bigBitmap, vWidth-vWidth/5, vHight-vHight/5);

            Log.d("THQ res2bitmap", "getHeight() = "+getHeight() + "  bitmap.getWidth() = " + bigBitmap.getWidth());
            return bigBitmap;
        }
        
        public void stopAnim() {
            isAniming = false;
        }
        
//        private Bitmap bitmap;
//
//        private void drawableToBitamp(Drawable drawable)
//            {
//                BitmapDrawable bd = (BitmapDrawable) drawable;
//                bitmap = bd.getBitmap();
//            }
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
         * view的真实宽度与高度:因为是旋转，所以这个view是正方形，它的值是图片的对角线长度
         */
        double maxwidth;

        /**
         * 旋转的图片
         */
        Bitmap rotatBitmap;
        /**
         * 原心坐标x
         */
        float o_x;

        /**
         * 原心坐标y
         */
        float o_y; 
        
        private void initSize() {
            if (rotatBitmap == null) {
                return;
            }
            width = rotatBitmap.getWidth();
            height = rotatBitmap.getHeight();

//            maxwidth = Math.sqrt(width * width + height * height);//hongqi
            maxwidth = width;//hongqi
            
            o_x = o_y = (float)(maxwidth / 2);//确定圆心坐标
        }
        

        public void setRotatBitmap(Bitmap bitmap) {
            rotatBitmap = bitmap;
            initSize();
            postInvalidate();
        }

        public void setRotatResource(int id) {

            BitmapDrawable drawable = (BitmapDrawable)getContext().getResources().getDrawable(id);

            setRotatDrawable(drawable);
        }

        public void setRotatDrawable(BitmapDrawable drawable) {
            rotatBitmap = drawable.getBitmap();
            initSize();
            postInvalidate();
        }
        
        /***
         * 图片的缩放方法
         * 
         * @param bgimage
         *            ：源图片资源
         * @param newWidth
         *            ：缩放后宽度
         * @param newHeight
         *            ：缩放后高度
         * @return
         */
        public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                        double newHeight) {
                // 获取这个图片的宽和高
                float width = bgimage.getWidth();
                float height = bgimage.getHeight();
                // 创建操作图片用的matrix对象
                Matrix matrix = new Matrix();
                // 计算宽高缩放率
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;
                // 缩放图片动作
                matrix.postScale(scaleWidth, scaleHeight);
                Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                                (int) height, matrix, true);
                return bitmap;
        }
        
    }