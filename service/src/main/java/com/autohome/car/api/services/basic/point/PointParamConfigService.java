package com.autohome.car.api.services.basic.point;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.VideoPointLocationMapper;
import com.autohome.car.api.data.popauto.entities.point.PointParamConfigEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class PointParamConfigService extends BaseService<List<PointParamConfigEntity>> {

    @Resource
    private VideoPointLocationMapper videoPointLocationMapper;


    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24 * 60;
    }

    public Map<String,Object> makeParams(int buId){
        Map<String,Object> params = new HashMap<>();
        params.put("buId",buId);
        return params;
    }

    @Override
    protected List<PointParamConfigEntity> getData(Map<String, Object> params) {
        int buId = (int) params.get("buId");
        List<PointParamConfigEntity> pointParamConfigEntities = videoPointLocationMapper.getPointRelationParamConfigByBuId(buId);
        if(CollectionUtils.isEmpty(pointParamConfigEntities)){
            return null;
        }
        return pointParamConfigEntities;
    }

    public List<PointParamConfigEntity> getByBuId(int buId){
        List<PointParamConfigEntity> pointParamConfigEntities = get(makeParams(buId));
        return pointParamConfigEntities;
    }

    public int refreshAll(Consumer<String> log){
        List<Integer> buIds = videoPointLocationMapper.getBuIdAll();
        if(!CollectionUtils.isEmpty(buIds)){
            for(int buId : buIds){
                try {
                    List<PointParamConfigEntity> pointParamConfigEntities = getData(makeParams(buId));
                    if(!CollectionUtils.isEmpty(pointParamConfigEntities)){
                        refresh(makeParams(buId),pointParamConfigEntities);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.accept("error buId ：" + buId);
                }
            }
        }
        return buIds.size();
    }

}
