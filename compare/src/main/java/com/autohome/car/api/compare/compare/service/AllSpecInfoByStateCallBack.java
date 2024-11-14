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
public class AllSpecInfoByStateCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<String> states = Const.yearState;
//        String url = path + "&state=";
//        compareByIdString(paramContext, url, states);
        List<Integer> typeIds = Arrays.asList(0,1,2,3);
        List<Integer> IsFilterSpecImage = Arrays.asList(0,1,2);
        for(int typeId :typeIds){
            for(int filter : IsFilterSpecImage){
                String url = path + "&typeid="+typeId+"&IsFilterSpecImage="+filter + "&state=";
                compareByIdString(paramContext, url, states);
            }
        }

    }
}
