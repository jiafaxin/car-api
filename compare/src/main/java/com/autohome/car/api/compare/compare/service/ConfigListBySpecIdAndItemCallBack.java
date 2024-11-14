package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class ConfigListBySpecIdAndItemCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> specIds = idsService.getAllSpecIds();
        int[] itemId = {1,2,3,4,5,6,7,8,0};
        for(int item :itemId){
            String npath = path + "&itemid="+item+"&specid=";
            compareById(paramContext, npath, specIds);
        }
    }

}
