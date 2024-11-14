package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class ShowBrandInfoByPavilionIdCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<KeyValueDto<Integer, Integer>> showIdsAndPavilionIds = idsService.getShowIdsAndPavilionIds();
        List<Integer> showIds = showIdsAndPavilionIds.stream().map(KeyValueDto::getKey).distinct().collect(Collectors.toList());
        List<Integer> pavilionIds = showIdsAndPavilionIds.stream().map(KeyValueDto::getValue).distinct().collect(Collectors.toList());
        List<Integer> sizeList = Arrays.asList(1, 5, 10, 10, 10000);
        for (Integer size : sizeList) {
            for (Integer showId : showIds) {
                System.out.println("参数showId = " + showId);
                String url = path + "&showid=" + showId + "&size=" + size + "&pavilionid=";
                compareById(paramContext, url, pavilionIds);
            }
        }
    }
}
