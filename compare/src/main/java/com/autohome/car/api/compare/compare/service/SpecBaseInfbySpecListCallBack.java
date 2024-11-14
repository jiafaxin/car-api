package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class SpecBaseInfbySpecListCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> specIds = idsService.getAllSpecIds();
        String npath = path + "&speclist=";
        compareById(paramContext, npath, specIds);
    }

}