package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class SpecDetailBySpeclistCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> specids = idsService.getAllSpecIds();
        paramContext.setSlice(3);
        String npath = path + "&speclist=";
        compareByIds(paramContext, npath, specids);
    }

}
