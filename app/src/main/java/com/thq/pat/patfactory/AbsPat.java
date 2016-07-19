package com.thq.pat.patfactory;

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
        public String ActionName;
        public int ActionDuration;
        Action(String name, int duration) {
            this.ActionDuration = duration;
            this.ActionName = name;
        }
        
        public abstract void Execute();
    }
    
    public abstract class ActionSeries {
        
        final static int MAX_ACTION_NUMS = 30; 
        
        final static int UNKNOW_ACTION = -1; 
        final static int STOP = 0; 
        final static int RUN = 1; 
        final static int QUESTION = 3; 
        final static int AMAZED = 4; 
        final static int IDLE = 5; 
        final static int SHIT = 6; 
        
        HashMap<String, Action> oriActions = new HashMap<String, AbsPat.Action>();
        ArrayList<Action> actions = new ArrayList<AbsPat.Action>();

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
