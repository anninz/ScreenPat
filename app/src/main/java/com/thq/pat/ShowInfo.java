package com.thq.pat;

import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thq.pat.image.ui.TouchView;
import com.thq.pat.image.util.ImageDownload;
import com.thq.pat.plugapilib.Utils;
import com.thq.pat.sina.provider.Tweet;

import java.util.ArrayList;
import java.util.Random;

public class ShowInfo {
    
    String TAG = "ShowInfo";
    
    FxService mContext;
    
    LayoutParams wmInfoParams;
//    LayoutParams wmNetImageParams;
    TextView mShowInfo;
    TouchView netImage;
    FrameLayout mLayout;

    ImageButton mClose;
    
    Random mRandomGenerator;
    
    ImageDownload imageDownload;
    
    final static int SHOW_INFO = 8;
    final static int HIDE_INFO = 9;
    
    public ShowInfo(FxService fxService) {
        this.mContext = fxService;
        imageDownload = new ImageDownload();
        mRandomGenerator = new Random();
        createFloatView();
    }
    
    public TextView getInfoView() {
        return mShowInfo;
    }
    
    public TouchView getNetImageView() {
        return netImage;
    }
    
    public void destroy() {
        if (mShowInfo != null) {
            mContext.removeView(mLayout);
        }
        if (netImage != null) {
            Utils.recycleImageView(netImage);
//            mContext.removeView(netImage);
        }
    }

    private void createFloatView() {
        wmInfoParams = new LayoutParams();
//        wmNetImageParams = new LayoutParams();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mLayout = (FrameLayout) inflater.inflate(R.layout.show_info, null);
        mShowInfo = (TextView) mLayout.findViewById(R.id.showinfo);
        netImage = (TouchView) mLayout.findViewById(R.id.image_info);
        mClose = (ImageButton) mLayout.findViewById(R.id.close_info);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(HIDE_INFO);
            }
        });

        //add for link
        mShowInfo.setAutoLinkMask(Linkify.WEB_URLS);
        mShowInfo.setMovementMethod(LinkMovementMethod.getInstance());
        
//        mShowInfo.setBackgroundColor(0xFFCCE8CF);
//        mShowInfo.setBackgroundColor(0xDDCCE8CF);
//        mShowInfo.setTextColor(0xFFF008CF);//fen se.
        mShowInfo.setTextColor(0xFF000000);
        wmInfoParams.format = PixelFormat.RGBA_8888; 
//        mShowInfo.getBackground().setAlpha(0);
//        wmInfoParams.copyFrom(wmInfoParams);
        wmInfoParams.type = LayoutParams.TYPE_PHONE;
/*        wmInfoParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;*/
        wmInfoParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        wmInfoParams.x = 0;
        wmInfoParams.y = 0;
        wmInfoParams.width = mContext.minPixels;
//        wmInfoParams.height = minPixels/2;
        wmInfoParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        wmInfoParams.gravity = Gravity.LEFT | Gravity.TOP;
//        mShowInfo.setVisibility(View.GONE);
        mContext.addView(mLayout, wmInfoParams);
        
//        netImage = new TouchView(mContext);
////        mShowInfo.setBackgroundColor(0xFFCCE8CF);
////        netImage.setBackgroundColor(0x90CCE8CF);
//        wmNetImageParams.format = PixelFormat.RGBA_8888;
//        wmNetImageParams.type = LayoutParams.TYPE_PHONE;
//        wmNetImageParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//        wmNetImageParams.x = 0;
//        wmNetImageParams.y = 0;
//        wmNetImageParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
////        wmInfoParams.height = minPixels/2;
//        wmNetImageParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        wmNetImageParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
//        netImage.setVisibility(View.GONE);
//        mContext.addView(netImage, wmNetImageParams);
    }
    

    String mShowInfoText = "";
    int mShowInfoDuration = 5000;
    boolean hasImage = false;
    public  void updateShowInfo(int needInfoIndex) {
        hasImage = false;
        
        ArrayList<String> mQiushiList = mContext.getContentFactory().getQiuShiList();
        int indexOfQiuShi = (mQiushiList != null && mQiushiList.size() > 0)?mRandomGenerator.nextInt(mQiushiList.size()):-1;
        
        ArrayList<Tweet> mSinaList = mContext.getContentFactory().getSinaList();
        int indexOfSina = (mSinaList != null && mSinaList.size() > 0)?mRandomGenerator.nextInt(mSinaList.size()):-1;
        int action = mRandomGenerator.nextInt(20);
        if (needInfoIndex > 0) action = needInfoIndex;
        switch (action) {
        case 1:
        case 2:
        case 9:
        case 10:
        case 12:
        case 13:
        case 14:
        case 15:
            if (indexOfSina >= 0) {
                Tweet tweet = mSinaList.get(indexOfSina);
                String videoLink = tweet.getMiaopaiLink();
                mShowInfoText = "作者：" + tweet.getUserName() + "\n\n"
               + tweet.getTweetContent() + "\n"
               + (!videoLink.equals("null")?videoLink:"")  + "\n\n" 
               +  "来源：新浪微博"
               ;
                mShowInfoDuration = 60000;
                if (tweet.getPicLink() != null && !tweet.getPicLink().contains("null")) {
                    Log.i(TAG, "THQ hasiamge loading ...");
                    hasImage = true;
                    imageDownload.init(mContext.maxPixels, mContext.minPixels, netImage, mContext, tweet.getPicLink());
                }
            } else {
                mShowInfoText = "下次遛我的时候，记得开网络哦，有惊喜...";
            }
            break;
        case 3:
        case 5:
        case 6:
        case 7:
        case 11:
            if (indexOfQiuShi >= 0) {
                mShowInfoText = mQiushiList.get(indexOfQiuShi)
                        /*+  "来源：糗事百科" + "\n" */;
                mShowInfoDuration = 60000;
            } else {
                mShowInfoText = "下次遛我的时候，记得开网络哦，有惊喜...";
            }
            break;
        case 4:
//            mShowInfoText = "你踩到屎了！！！！！";
            mShowInfoText = "“老婆，今天饭菜很丰富呐，不出去吃？”出差七天，刚回来的老公说。\n" +
                    "女人抬了抬头，却不回答他。\n" +
                    "他在屋里转悠转悠，看见了自己的遗照。\n" +
                    "——《出差七天》";
            break;
/*        case 5:
            mShowInfoText = "憋踩了!!!";
            break;*/
        case 26:
//            mShowInfoText = "疼!!!好疼!!!踩!!!踩了!!!!";
            mShowInfoText = "1 error，1 warning";
            break;
        case 27:
//            mShowInfoText = "开开心心的哦!!!!";
            mShowInfoText = "If you are still looking for that one person who will change your life,take look in the mirror.\n\n --unknown";
        case 21:
//            mShowInfoText = "虚脱了，拉不粗来了!!!!";
            mShowInfoText = "小时候刮奖刮出‘谢’字还不扔，非要把‘谢谢惠顾’都刮的干干净净才舍得放手，和后来太多的事一模一样。";
            break;
        case 100:
            mShowInfoText = "拉的屎太多了，赶紧去扫一下!!!!";
            break;

        default:
//            mShowInfoText = "你要走蟑螂屎运！！！！！";
            mShowInfoText = "\n" +
                    "       广告位招租！\n";
            break;
        }
        handler.sendEmptyMessage(SHOW_INFO);
//        return mShowInfoText;
    }
    
    private void showInfo() {
        mShowInfo.setText(mShowInfoText);
        mLayout.setVisibility(View.VISIBLE);
        if (hasImage) {
            netImage.setVisibility(View.VISIBLE);
        } else if (netImage.getVisibility() == View.VISIBLE) {
            hasImage = false;
            netImage.setVisibility(View.GONE);
        } 
    }
    private void hideInfo() {
        mLayout.setVisibility(View.GONE);
        if (hasImage) {
            netImage.setVisibility(View.GONE);
            hasImage = false;
        }
    }
    
    final Handler handler =new Handler(){
        public void handleMessage(Message msg){

            switch (msg.what) {
                case SHOW_INFO:
                    showInfo();
                    handler.removeMessages(HIDE_INFO);
                    handler.sendEmptyMessageDelayed(HIDE_INFO, mShowInfoDuration);
                    mShowInfoDuration = 5000;
                    break;
                case HIDE_INFO:
                    hideInfo();
                    break;
                default:
                    break;
            }
        }
    };
}
