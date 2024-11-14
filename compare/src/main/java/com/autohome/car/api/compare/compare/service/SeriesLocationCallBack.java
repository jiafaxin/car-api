package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class SeriesLocationCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        Collections.shuffle(seriesIds);
        seriesIds = seriesIds.stream().limit(1000).collect(Collectors.toList());
        int[] locationIds = new int[30];
        for (int i = 0; i < 30; i++) {
            locationIds[i] = i + 1;
        }
        List<Integer> sizeList = Arrays.asList(1, 5, 10, 10000);
        List<Integer> pageList = Arrays.asList(1, 5, 10000);
        for (Integer size : sizeList) {
            for(Integer page : pageList){
               for(int location : locationIds){
                   System.out.println("参数pageId = " + page);
                   String url = path + "&pageindex=" + page + "&pagesize=" + size + "&locationid=" + location + "&seriesid=";
                   compareById(paramContext, url, seriesIds);
               }
            }
        }
    }
}
