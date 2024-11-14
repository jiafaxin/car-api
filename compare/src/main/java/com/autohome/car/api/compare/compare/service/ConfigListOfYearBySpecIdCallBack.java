package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class ConfigListOfYearBySpecIdCallBack implements CallBack{
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> disptypes = Arrays.asList(0);
        List<Integer> specIds = idsService.getAllSpecIds();
        for(Integer disptype: disptypes){
            String npath = path + "&disptype=" + disptype + "&specid=";
            compareById(paramContext, npath, specIds);
        }
    }
}
