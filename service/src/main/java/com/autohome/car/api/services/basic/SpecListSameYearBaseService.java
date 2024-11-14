package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.SpecBaseEntity;
import com.autohome.car.api.data.popauto.entities.SpecStateEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecListSameYearBaseService extends BaseService<List<SpecStateEntity>> {

    @Resource
    SpecViewMapper specViewMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_30;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected List<SpecStateEntity> getData(Map<String, Object> params) {
        int specId = (int) params.get("specId");
        return getData(specId);
    }

    List<SpecStateEntity> getData(int specId) {
        List<SpecStateEntity> list = Spec.isCvSpec(specId)
                ? specViewMapper.getCvSpecListBySpecId(specId)  //电动车
                : specViewMapper.getSpecListBySpecId(specId);   //燃油

        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        return list;
    }

    /**
     * 外部使用
     * @param specId
     * @return
     */
    public CompletableFuture<List<SpecStateEntity>> get(int specId){
        return getAsync(makeParams(specId));
//        return CompletableFuture.supplyAsync(() -> {
//            return getData(specId);
//        });
    }

    Map<String,Object> makeParams(int specId){
        Map<String,Object> params = new HashMap<>();
        params.put("specId",specId);
        return params;
    }

    /**
     * 定时任务使用
     * @param log
     * @return
     */
    public int refreshAll(Consumer<String> log) {
        //所有车型id
        List<Integer> specIds = specViewMapper.getAllBase().stream()
                .map(SpecBaseEntity::getId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(specIds)){
            for(Integer specId:specIds){
                //同年贷款车型id
                List<SpecStateEntity> list = Spec.isCvSpec(specId)
                        ? specViewMapper.getCvSpecListBySpecId(specId)  //电动车
                        : specViewMapper.getSpecListBySpecId(specId);   //燃油
                refresh(makeParams(specId), list);
            }
            return specIds.size();
        }
        log.accept("SpecListSameYearBaseService SpecListSameYearIds is null!" );
        return 0;
    }
}
