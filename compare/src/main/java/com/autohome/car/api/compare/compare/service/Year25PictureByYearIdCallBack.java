package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.Common;

import java.util.ArrayList;
import java.util.List;

public class Year25PictureByYearIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<KeyValueDto<Integer,Integer>> seriesIds = idsService.getAllSeriesYearIds();
        List<String> params = new ArrayList<>();
        for (KeyValueDto<Integer,Integer> item:seriesIds) {
            String npath = "&yearid="+item.getValue()+"&seriesid="+item.getKey();
            params.add(npath);
        }
        compareByIdString(paramContext, path, params);
//        for (String yearid:yearids) {
//            String npath = "&yearid="+yearid+"&seriesid=";
//            compareById(paramContext, npath, seriesIds);
//        }
    }

}
