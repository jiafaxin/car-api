package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.Common;
import com.autohome.car.api.compare.tools.CompareJson;

import java.util.List;

public class FactoryByBrandCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> brandIds = idsService.getAllBrandIds();
        List<String> states = Const.yearState;
        String[] typeids = new String[]{"0","1"};
        String[] filterimages = new String[]{"0","1"};
        for (String state:states) {
            for (String typeid:typeids) {
                for (String filterimage:filterimages) {
                    String npath = path + "&state="+state+"&typeid="+typeid+"&IsFilterSpecImage="+filterimage+"&brandid=";
                    compareById(paramContext, npath, brandIds);
                }
            }
        }
    }

}
