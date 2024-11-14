package com.autohome.car.api.services.basic.epiboly;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.EpibolyAiVideoMapper;
import com.autohome.car.api.data.popauto.entities.epiboly.EpibolyAiVideoOrderEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class EpibolyAiVideoOrderService extends BaseService<List<EpibolyAiVideoOrderEntity>> {

    @Resource
    private EpibolyAiVideoMapper epibolyAiVideoMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24 * 60;
    }

    @Override
    protected List<EpibolyAiVideoOrderEntity> getData(Map<String, Object> params) {
        List<EpibolyAiVideoOrderEntity> epibolyAiVideoOrderAll = epibolyAiVideoMapper.getEpibolyAiVideoOrderAll();
        if(CollectionUtils.isEmpty(epibolyAiVideoOrderAll)){
            return null;
        }
        return epibolyAiVideoOrderAll;
    }

    /**
     * 根据车系获取审核通过的最新一条订单信息
     * @param seriesId
     * @return
     */
    public EpibolyAiVideoOrderEntity getBySeriesId(int seriesId){
        List<EpibolyAiVideoOrderEntity> videoOrderEntityList = get(null);
        if(!CollectionUtils.isEmpty(videoOrderEntityList)){
            EpibolyAiVideoOrderEntity orderEntity = videoOrderEntityList.stream().filter(epibolyAiVideoOrderEntity ->
                    epibolyAiVideoOrderEntity.getSeriesId() == seriesId && epibolyAiVideoOrderEntity.getOrderStatus() == 2).findFirst().orElse(null);
            return orderEntity;
        }
        return null;
    }

    public int refreshAll(Consumer<String> log){
        List<EpibolyAiVideoOrderEntity> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
