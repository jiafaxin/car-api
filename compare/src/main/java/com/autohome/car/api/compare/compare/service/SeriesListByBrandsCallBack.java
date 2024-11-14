package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class SeriesListByBrandsCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> brandIds = idsService.getAllBrandIds();
        List<String> states = Const.stateList;
        for (String state:states) {
            String npath = path + "&state="+state+"&brandids=";
            paramContext.setSlice(5);
            compareByIds(paramContext, npath, brandIds);
        }
    }

}
