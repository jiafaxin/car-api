package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class SeriesRankByFuelCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<String> states = Arrays.asList("","0","1","2","3","4","5","6","7","8","9","10","11","12", "4,5");
        String url = path + "&fueltype=";
        compareByIdString(paramContext, url, states);
    }
}
