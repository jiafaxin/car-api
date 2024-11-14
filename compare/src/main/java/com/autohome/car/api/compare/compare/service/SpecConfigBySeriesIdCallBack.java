package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class SpecConfigBySeriesIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        paramContext.setSlice(3);
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        List<String> states = Arrays.asList("0x0002","0x000c","0x0010");
        for(String state : states){
            System.out.println("当前状态为："+state);
            String npath = path + "&state="+state+"&seriesids=";
            compareByIds(paramContext, npath, seriesIds);
        }
    }

}
