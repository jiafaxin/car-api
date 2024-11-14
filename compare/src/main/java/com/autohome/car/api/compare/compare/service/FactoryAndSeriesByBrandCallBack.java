package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class FactoryAndSeriesByBrandCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> brandIds = idsService.getAllBrandIds();
        List<String> states = Const.yearBySeriesState;
        String[] typeids = new String[]{"0","1","2"};
        String[] filterimages = new String[]{"0","1"};
        for (String state:states) {
            for (String filterimage:filterimages) {
                for (String typeid:typeids) {
                    String npath = path + "&typeid="+typeid+"&state="+state+"&IsFilterSpecImage="+filterimage+"&brandid=";
                    compareById(paramContext, npath, brandIds);
                }

            }
        }
    }

}
