package com.thq.pat.plug.catgirl.patfactory;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thq.pat.plugapilib.IPat;
import com.thq.pat.plugapilib.IPlugAPI;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbsPat implements IPat {

    IPlugAPI mContext;
    public boolean isActive = true;

    public AbsPat(IPlugAPI fxService) {
        super();
        mContext = fxService;
    }

    public abstract class Action {
        public String actionName;
        public int actionDuration;
        Action(String name, int duration) {
            this.actionDuration = duration;
            this.actionName = name;
        }
        
        public abstract void Execute();

        public Action end() {return  null;};
    }
    
    public abstract class ActionManager {
        
        final static int MAX_ACTION_NUMS = 30; 
        
        final static int UNKNOW_ACTION = -1; 
        final static int STOP = 0; 
        final static int RUN = 1; 
        final static int QUESTION = 3; 
        final static int AMAZED = 4; 
        final static int IDLE = 5; 
        final static int SHIT = 6;
        final static int NEXTACTION = 6;

        HashMap<String, Action> oriActions = new HashMap<String, Action>();
        ArrayList<Action> actions = new ArrayList<Action>();

        Action defaultAction;
        Action currentAction;

        public void start() {
            handler.sendEmptyMessage(NEXTACTION);
        }

        private final Handler handler =new Handler(){
            public void handleMessage(Message msg){

                switch (msg.what) {
                    case NEXTACTION:
                        currentAction = getNextAction();
                        if (isActive && currentAction != null) {
                            currentAction.Execute();
                            sendEmptyMessageDelayed(IDLE, currentAction.actionDuration);
                        }
                        break;
                    case IDLE:
                        Action action = currentAction.end();
                        if (action == null) {
                            defaultAction.Execute();
                        }else {
//                            Log.d("THHQ2",currentAction.actionName + "");
                            action.Execute();
                            handler.sendEmptyMessageDelayed(NEXTACTION, action.actionDuration);
                            break;
                        }
                        handler.sendEmptyMessageDelayed(NEXTACTION, defaultAction.actionDuration);
                        break;
                    default:
                        break;
                }
            }
        };

        public void end() {
            handler.removeMessages(NEXTACTION);
            handler.removeMessages(IDLE);
        }

        public Action getNextAction() {
            if (actions.size() == 0) {
                updateAction();
            }
            Action action = actions.get(0);
            actions.remove(0);
            return action;
        }
        public abstract void updateAction();
        public abstract void initAction();
    }

}
