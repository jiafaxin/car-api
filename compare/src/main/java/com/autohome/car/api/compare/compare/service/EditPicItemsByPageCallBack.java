package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.CompareJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class EditPicItemsByPageCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        Collections.shuffle(seriesIds);
        seriesIds = seriesIds.stream().limit(1000).collect(Collectors.toList());
        List<Integer> specIds = idsService.getAllSpecIds();
        Collections.shuffle(specIds);
        specIds = specIds.stream().limit(1000).collect(Collectors.toList());
        List<Integer> classIds = idsService.getAllPicClassIds();
        Collections.shuffle(classIds);
        classIds = classIds.stream().limit(100).collect(Collectors.toList());

        List<Integer> sizeList = Arrays.asList(1, 5, 10, 10000);
        List<Integer> pageList = Arrays.asList(1, 5, 10, 10000);

        List<CompletableFuture> tasks = new ArrayList<>();

        int total = 0;
        for (Integer seriesId : seriesIds) {
            for(Integer classId : classIds){
                String url = path + "&page=" + 1 + "&size=" + 10 + "&specid=" + 0 + "&classid=" + classId + "&seriesid="+seriesId;
                tasks.add(new CompareJson().compareUrlAsyncCommon(url,getEnv()));
                if(tasks.size()>20){
                    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                    tasks = new ArrayList<>();
                }
                if(total++%100==0) {
                    System.out.println("总数：" + total);
                }
            }
        }

    }
}
