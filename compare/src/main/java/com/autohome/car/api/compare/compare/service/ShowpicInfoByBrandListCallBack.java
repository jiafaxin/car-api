package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShowpicInfoByBrandListCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<KeyValueDto<Integer, Integer>> showIdsAndPavilionIds = idsService.getShowIdsAndPavilionIds();
        List<Integer> showIds = showIdsAndPavilionIds.stream().map(KeyValueDto::getKey).distinct().collect(Collectors.toList());

        List<Integer> evCarList = Arrays.asList(0, 1);
        for (Integer evCar : evCarList) {
            System.out.println("参数 evcar = " + evCar);
            for (Integer showId : showIds) {
                System.out.println("参数showId = " + showId);
                String url = path + "&showid=" + showId + "&evcar=" + evCar + "&brandlist=";
                compareByIds(paramContext, url, idsService.getBrandIdsFromShowCarsView());
            }
        }
    }
}
