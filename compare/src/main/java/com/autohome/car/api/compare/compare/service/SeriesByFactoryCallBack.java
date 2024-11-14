package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeriesByFactoryCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<KeyValueDto<Integer,Integer>> fcts = idsService.getAllFactoryBrands();
        List<String> states = Const.stateList;
        String[] typeids = new String[]{"0","1","2"};
        String[] filterimages = new String[]{"0","1"};
        List<String> params = new ArrayList<>();
        for (String filterimage:filterimages) {
            for (KeyValueDto<Integer,Integer> fct:fcts) {
                for (String typeid:typeids) {
                    String npath = "&IsFilterSpecImage="+filterimage+"&factoryid="+fct.getKey()+"&brandid="+fct.getValue()+"&typeid="+typeid;
                    params.add(npath);
                }
            }
        }
        for (String state:states) {
            String npath = path + "&state="+state;
            compareByIdString(paramContext, npath, params);
        }

//        List<KeyValueDto<Integer,Integer>> seriesIds = idsService.getAllSeriesYearIds();
////        List<String> states = Common.stateList;
////        List<String> states = Arrays.asList("0X0001", "0X0002", "0X0010", "0X000C");
//        List<String> states = Arrays.asList("0X000C");
//        List<String> params = new ArrayList<>();
//        for (KeyValueDto<Integer,Integer> item:seriesIds) {
//            String npath = "&yearid="+item.getValue()+"&seriesid="+item.getKey();
//            params.add(npath);
//        }
//        for (String state:states) {
//            String npath = path + "&state="+state;
//            compareByIdString(paramContext, npath, params);
//        }
    }

}
