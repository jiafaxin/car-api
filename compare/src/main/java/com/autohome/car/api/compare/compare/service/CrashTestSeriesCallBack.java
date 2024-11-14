package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class CrashTestSeriesCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {

        List<Integer> orderTypes = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> standarIds = Arrays.asList(0, 1, 2, 3);

        for (Integer standarId : standarIds) {
            System.out.println("参数 standarId = " + standarId);
            for (Integer orderType : orderTypes) {
                System.out.println("参数orderType = " + orderType);
                String url = path + "&ordertype=" + orderType + "&standardid=" + standarId;
                this.compare(paramContext, url);
            }
        }
    }
}
