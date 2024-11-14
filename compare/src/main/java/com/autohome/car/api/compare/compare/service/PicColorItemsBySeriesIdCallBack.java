package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.Common;

import java.util.List;

public class PicColorItemsBySeriesIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        List<String> states = Const.stateList;
        for (String state:states) {
            String npath = path + "&state="+state+"&seriesid=";
            compareById(paramContext, npath, seriesIds);
        }
    }

}
