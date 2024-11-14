package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class CrashTestBySeriesIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        int[] standardIds = new int[]{1,2,3};
        for (int standardid:standardIds) {
            String npath = path + "&standardId="+standardid+"&seriesid=";
            compareById(paramContext, npath, seriesIds);
        }
    }

}
