package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YearParamByYearIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        //yearid和车系id对齐
        List<KeyValueDto<Integer,Integer>> seriesIds = idsService.getAllSeriesYearIds();
//        List<String> states = Common.stateList;
//        List<String> states = Arrays.asList("0X0001", "0X0002", "0X0010", "0X000C");
        List<String> states = Arrays.asList("0X000C");
        List<String> params = new ArrayList<>();
        for (KeyValueDto<Integer,Integer> item:seriesIds) {
            String npath = "&yearid="+item.getValue()+"&seriesid="+item.getKey();
            params.add(npath);
        }
        for (String state:states) {
            String npath = path + "&state="+state;
            compareByIdString(paramContext, npath, params);
        }



//        for (String state:states) {
//            for (String yearid:yearids) {
//                String npath = path + "&state="+state+"&yearid="+yearid+"&seriesid=";
////                compareById(paramContext, npath, seriesIds);
//            }
//        }
    }

}
