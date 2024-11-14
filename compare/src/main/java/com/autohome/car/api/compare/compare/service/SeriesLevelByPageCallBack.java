package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.CommonUtils;
import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.services.common.CommonFunction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class SeriesLevelByPageCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<String> states = Const.stateList;
        List<Integer> levelIds = idsService.getAllLevelId();
        List<Integer> sizeList = Arrays.asList(1, 5, 10, 10000);
        List<Integer> pageList = Arrays.asList(1, 5, 10, 10000);
        for (Integer size : sizeList) {
            for(Integer page : pageList){
               for(String state : states){
                   System.out.println("参数pageId = " + page);
                   String url = path + "&page=" + page + "&size=" + size + "&state=" + state +  "&levelid=";
                   compareById(paramContext, url, levelIds);
               }
            }
        }
    }
}
