package com.thq.pat.patfactory;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import com.thq.pat.FxService;

public abstract class AbsPat implements Pat {

    FxService mContext;
    public boolean isActive = true;

    public AbsPat(FxService fxService) {
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

        HashMap<String, Action> oriActions = new HashMap<String, AbsPat.Action>();
        ArrayList<Action> actions = new ArrayList<AbsPat.Action>();

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
                        defaultAction.Execute();
                        handler.sendEmptyMessage(NEXTACTION);
                        break;
                    default:
                        break;
                }
            }
        };

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
