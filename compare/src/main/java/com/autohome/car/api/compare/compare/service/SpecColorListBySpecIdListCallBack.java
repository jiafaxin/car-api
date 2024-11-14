package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class SpecColorListBySpecIdListCallBack implements CallBack {

    @Override
    public String getEnv(){
        return "dev";
    }

    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> specIds = idsService.getAllSpecIds();
        String npath = path + "&specIdlist=";
        compareById(paramContext, npath, specIds);
    }

}
