package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.Common;

import java.util.Arrays;
import java.util.List;

public class YearIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> specYearIds = idsService.getAllSpecYearIds();
        List<String> yearState = Const.yearState;
        List<Integer> isFilterSpecImages = Arrays.asList(0, 1);
        for (Integer isFilterSpecImage : isFilterSpecImages) {
            System.out.println("参数isFilterSpecImage = " + isFilterSpecImage);
            for (String state : yearState) {
                System.out.println("state = " + state);
                String url = path + "&IsFilterSpecImage=" + isFilterSpecImage + "&state=" + state + "&yearid=";
                compareById(paramContext, url, specYearIds);
            }
        }


    }

}
