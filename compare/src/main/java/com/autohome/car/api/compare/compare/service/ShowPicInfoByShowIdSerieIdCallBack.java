package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.CompareJson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ShowPicInfoByShowIdSerieIdCallBack implements CallBack{
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<KeyValueDto<Integer, Integer>> keyValueDtos = idsService.getSeriesIdShowIdAll();
        List<CompletableFuture> tasks = new ArrayList<>();
        int i = 0;
        for(KeyValueDto keyValueDto: keyValueDtos){
            String npath = path + "&seriesid=" + keyValueDto.getKey() + "&showid=" + keyValueDto.getValue();
            String url = npath;
            tasks.add(new CompareJson().exclude(paramContext.getExclude()).compareUrlAsyncCommon(url, getEnv()));
            if (tasks.size() > 20) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i + "    "+url);
            }
        }
        if (!tasks.isEmpty()) {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        }
    }
}
