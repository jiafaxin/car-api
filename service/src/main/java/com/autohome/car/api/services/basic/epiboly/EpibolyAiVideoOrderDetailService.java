package com.autohome.car.api.services.basic.epiboly;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.EpibolyAiVideoMapper;
import com.autohome.car.api.data.popauto.entities.epiboly.EpibolyAiVideoOrderDetailEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class EpibolyAiVideoOrderDetailService extends BaseService<List<EpibolyAiVideoOrderDetailEntity>> {

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
    protected List<EpibolyAiVideoOrderDetailEntity> getData(Map<String, Object> params) {
        int orderId = (int) params.get("orderId");
        List<EpibolyAiVideoOrderDetailEntity> orderDetailEntityList = epibolyAiVideoMapper.getEpibolyAiVideoOrderDetailByOrderId(orderId);
        if(CollectionUtils.isEmpty(orderDetailEntityList)){
            return null;
        }
        return orderDetailEntityList;
    }

    public Map<String,Object> makeParams(int orderId){
        Map<String,Object> params = new HashMap<>();
        params.put("orderId",orderId);
        return params;
    }

    public List<EpibolyAiVideoOrderDetailEntity> getByOrderId(int orderId){
        List<EpibolyAiVideoOrderDetailEntity> videoOrderEntityList = get(makeParams(orderId));
        return videoOrderEntityList;
    }

    public int refreshAll(Consumer<String> log){
        List<Integer> orderIds = epibolyAiVideoMapper.getOrderIdAll();
        if(!CollectionUtils.isEmpty(orderIds)){
            for(int orderId : orderIds){
                try {
                    List<EpibolyAiVideoOrderDetailEntity> videoOrderEntityList = getData(makeParams(orderId));
                    if(!CollectionUtils.isEmpty(videoOrderEntityList)){
                        refresh(makeParams(orderId),videoOrderEntityList);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.accept("error orderId ：" + orderId);
                }
            }
        }
        return orderIds.size();
    }
}
