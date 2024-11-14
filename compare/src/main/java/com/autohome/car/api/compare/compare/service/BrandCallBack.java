package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class BrandCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> typeIds = Arrays.asList(0,1,2,3);
        List<Integer> isFilterSpecImage = Arrays.asList(0,1,2);
        List<String> states = Const.yearState;
        for(String state: states){
            System.out.println("当前state为："+state);
            for(int filterSpecImage : isFilterSpecImage){
                String npath = path + "&state=" + state + "&IsFilterSpecImage=" + filterSpecImage +"&typeid=" ;
                compareById(paramContext, npath, typeIds);
            }
        }
    }

}
