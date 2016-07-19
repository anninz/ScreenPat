package com.thq.pat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class ActionListener {


    public final static String MY_ACTION_CHANGE_PAT_SIZE = "change.pat.size.MY_ACTION";

    private Context mContext;
    private ActionBroadcastReceiver mActionReceiver;
    private MyActionListener mActionListener;

    public ActionListener(Context context) {
        mContext = context;
        mActionReceiver = new ActionBroadcastReceiver();
    }

    /**
     * screen状态广播接收者
     */
    public class ActionBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (action.equals(MY_ACTION_CHANGE_PAT_SIZE)) {
                mActionListener.action(MY_ACTION_CHANGE_PAT_SIZE);
            }
        }
    }

    /**
     * 开始监听screen状态
     *
     * @param listener
     */
    public void begin(MyActionListener listener) {
        mActionListener = listener;
        registerListener();
    }

    /**
     * 停止screen状态监听
     */
    public void unregisterListener() {
        mContext.unregisterReceiver(mActionReceiver);
    }

    /**
     * 启动screen状态广播接收器
     */
    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MY_ACTION_CHANGE_PAT_SIZE);
        mContext.registerReceiver(mActionReceiver, filter);
    }

    public interface MyActionListener {// 返回给调用者屏幕状态信息
        public void action(String action);
    }
}


