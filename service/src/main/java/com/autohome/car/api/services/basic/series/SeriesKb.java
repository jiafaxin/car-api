package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.HttpClient;
import com.autohome.car.api.common.HttpResult;
import com.autohome.car.api.data.popauto.entities.SeriesViewRankEntity;
import com.autohome.car.api.services.basic.BaseService;
import com.autohome.car.api.services.basic.models.KbScore;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SeriesKb  extends BaseService<Map<Integer,Double>> {

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.H_1;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 1440;
    }

    @Override
    protected boolean getFromDB(){
        return true;
    }

    @Override
    protected Map<Integer, Double> getData(Map<String, Object> params) {
        HttpResult<KbScore> score = HttpClient.get("http://k.sjz.autohome.com.cn/api/getAllSeriesAverage", new TypeReference<KbScore>() {},"gb2312").join();
        return score.getResult().getResult().getItems();
    }
}
