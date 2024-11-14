package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SpecConfigMapper;
import com.autohome.car.api.data.popauto.SpecMapper;
import com.autohome.car.api.data.popauto.entities.SpecConfigSubItemEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ConfigSubItemValueRelationService extends BaseService<List<SpecConfigSubItemEntity>>{

    @Resource
    private SpecConfigMapper specConfigMapper;

    @Resource
    private SpecMapper specMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<SpecConfigSubItemEntity> getData(Map<String, Object> params) {
        int specId = (int)params.get("specId");
        return specConfigMapper.getSpecConfigSubItemValues(specId);
    }

    Map<String,Object> makeParams(int specId){
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("specId",specId);
        return params;
    }

    public Map<Integer,List<SpecConfigSubItemEntity>> getList(List<Integer> ids) {
        List<Map<String,Object>> params = ids.stream().map(x->makeParams(x)).collect(Collectors.toList());
        List<List<SpecConfigSubItemEntity>> list = mGet(params);
        if(list == null || list.size() == 0) {
            return new LinkedHashMap<>();
        }
        Map<Integer,List<SpecConfigSubItemEntity>> result = new LinkedHashMap<>();
        for (List<SpecConfigSubItemEntity> item : list) {
            if(item == null || item.size() == 0) {
                continue;
            }
            result.put(item.get(0).getSpecId(),item);
        }
        return result;
    }

    public int refreshAll(Consumer<String> log){
        int count = 0;
        for (Integer id : specMapper.getAllIds()) {
            try {
                Map<String,Object> params = makeParams(id);
                List<SpecConfigSubItemEntity> data = getData(params);
                if(!CollectionUtils.isEmpty(data)){
                    refresh(params,data);
                    count++;
                }
            }catch (Exception e){
                log.accept("error:"+id+" >>> " + ExceptionUtil.getStackTrace(e));
            }
        }
        return count;
    }
}
