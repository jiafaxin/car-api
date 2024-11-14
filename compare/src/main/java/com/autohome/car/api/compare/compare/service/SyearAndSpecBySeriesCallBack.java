package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.Common;

import java.util.Arrays;
import java.util.List;

public class SyearAndSpecBySeriesCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        List<String> states = Const.stateList;
        String[] filterimages = new String[]{"0","1"};
        for (String state:states) {
            for (String filterimage:filterimages) {
                String npath = path + "&state="+state+"&IsFilterSpecImage="+filterimage+"&seriesid=";
                compareById(paramContext, npath, seriesIds);
            }
        }
    }

}
