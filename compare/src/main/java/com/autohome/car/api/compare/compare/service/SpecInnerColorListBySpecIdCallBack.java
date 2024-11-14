package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class SpecInnerColorListBySpecIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> specIds = idsService.getAllSpecIds();
        String npath = path + "&specid=";
        compareById(paramContext, npath, specIds);
    }

}
