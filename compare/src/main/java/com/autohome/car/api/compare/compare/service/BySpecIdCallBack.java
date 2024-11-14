package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class BySpecIdCallBack implements CallBack {

    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> ids = idsService.getAllSpecIds();
        List<Integer> type = Arrays.asList(1, 0);
        System.out.println("总共有：" + ids.size());
        for (Integer disptype : type) {
            System.out.println("开始：disptype = " + disptype);
            String tempPath = path + "&type=-1&disptype=" + disptype + "&specid=";
            compareById(paramContext, tempPath, ids);
        }
    }
}
