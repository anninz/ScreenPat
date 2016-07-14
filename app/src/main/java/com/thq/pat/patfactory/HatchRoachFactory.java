package com.thq.pat.patfactory;

import com.thq.pat.FxService;

public class HatchRoachFactory implements HatchProvider {

    @Override
    public Pat doHatch(FxService fxService) {
        // TODO Auto-generated method stub
        return new RoachPat(fxService);
    }

}
