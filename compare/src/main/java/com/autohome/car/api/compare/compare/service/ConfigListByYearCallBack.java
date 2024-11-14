package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class ConfigListByYearCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        List<Integer> yearIds = idsService.getAllSpecYearIds();
        int[] disptype = new int[]{0,1,2};
        for (Integer year : yearIds) {
            for(int type : disptype){
                String url = path + "&yearid=" + year + "&disptype=" + type +  "&seriesid=";
                compareById(paramContext, url, seriesIds);
            }
        }
    }
}
