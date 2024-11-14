package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class SyearbyseriesCallBack implements CallBack{
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> ids = idsService.getAllSeriesIds();
        List<String> yearState = Const.yearBySeriesState;
        List<Integer> isFilterSpecImages = Arrays.asList(0, 1);
        for (Integer isFilterSpecImage : isFilterSpecImages) {
            System.out.println("参数isFilterSpecImage = " + isFilterSpecImage);
            for (String state : yearState) {
                System.out.println("state = " + state);
                String url = path + "&IsFilterSpecImage=" + isFilterSpecImage + "&state=" + state + "&seriesid=";
                compareById(paramContext, url, ids);
            }
        }
    }
}
