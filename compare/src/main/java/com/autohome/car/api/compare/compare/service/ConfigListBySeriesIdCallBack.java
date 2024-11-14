package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class ConfigListBySeriesIdCallBack implements CallBack{
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> disptypes = Arrays.asList(0);
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        for(Integer disptype: disptypes){
            String npath = path + "&disptype=" + disptype + "&seriesid=";
            compareById(paramContext, npath, seriesIds);
        }
    }
}
