package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.ConfigSpecificMapper;
import com.autohome.car.api.data.popauto.entities.ConfigSpecificEntity;
import com.autohome.car.api.services.basic.models.SpecificConfigInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecificConfigBaseService extends BaseService<List<SpecificConfigInfo>> {

    @Resource
    private ConfigSpecificMapper configSpecificMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected List<SpecificConfigInfo> getData(Map params) {
        int specId = (int) params.get("specId");
        List<ConfigSpecificEntity> configSpecificEntities = configSpecificMapper.getConfigSpecificBySpecId(specId);
        return convert(configSpecificEntities);
    }

    /**
     * 外部使用 同步
     * @param specIds
     * @return
     */
    public List<SpecificConfigInfo> getList(List<Integer> specIds) {
        List<SpecificConfigInfo> specificConfigInfos = new ArrayList<>();
        if(CollectionUtils.isEmpty(specIds)) {
            return specificConfigInfos;
        }
        for(Integer specId : specIds){
            List<SpecificConfigInfo> configInfoList = get(makeParams(specId));
            if(!CollectionUtils.isEmpty(configInfoList)){
                specificConfigInfos.addAll(configInfoList);
            }
        }
        return specificConfigInfos.stream().sorted(Comparator.comparing(SpecificConfigInfo::getSort)).collect(Collectors.toList());
    }

//    /**
//     * 外部使用 异步
//     * @param specIds
//     * @return
//     */
//    public List<SpecificConfigInfo> getList(List<Integer> specIds) {
//        if(CollectionUtils.isEmpty(specIds)) {
//            return null;
//        }
//        List<CompletableFuture> tasks = new ArrayList<>();
//        Vector<SpecificConfigInfo> list = new Vector<>();
//        for (Integer specId : specIds) {
//            tasks.add(getAsync(makeParams(specId)).thenAccept(x-> {
//                if (x != null){
//                    list.addAll(x);
//                 }
//            }));
//        }
//        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
//        return list.stream().sorted(Comparator.comparing(SpecificConfigInfo::getSort)).collect(Collectors.toList());
//    }

    Map<String,Object> makeParams(int specId){
        Map<String,Object> params = new HashMap<>();
        params.put("specId",specId);
        return params;
    }

    /**
     * 转换
     * @param configSpecificEntities
     * @return
     */
    private List<SpecificConfigInfo> convert(List<ConfigSpecificEntity> configSpecificEntities){
        if(CollectionUtils.isEmpty(configSpecificEntities)){
            return null;
        }
        List<SpecificConfigInfo> specificConfigInfos = new ArrayList<>();
        configSpecificEntities.forEach(configSpecificEntity -> {
            SpecificConfigInfo specificConfigInfo = new SpecificConfigInfo();
            specificConfigInfo.setItemId(configSpecificEntity.getItemId());
            specificConfigInfo.setItemName(configSpecificEntity.getItemName());
            specificConfigInfo.setBaiKeId(configSpecificEntity.getBaiKeId());
            specificConfigInfo.setBaiKeUrl(configSpecificEntity.getBaiKeUrl());
            specificConfigInfo.setSpecId(configSpecificEntity.getSpecId());
            specificConfigInfo.setItemValue(configSpecificEntity.getItemValue());
            specificConfigInfo.setPrice(configSpecificEntity.getPrice());
            specificConfigInfo.setSort(configSpecificEntity.getSort());
            specificConfigInfos.add(specificConfigInfo);
        });
        return specificConfigInfos;
    }

    /**
     * 定时任务使用
     * @param log
     * @return
     */
    public int refreshAll(Consumer<String> log) {
        List<Integer> specIdRelation = configSpecificMapper.getSpecItemRelationAll().stream().sorted().collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(specIdRelation)){
            List<List<Integer>> specIdList = ToolUtils.splitList(specIdRelation, 100);
            for(List<Integer> specIds:specIdList){
                List<ConfigSpecificEntity> specificEntities = configSpecificMapper.getConfigSpecificBySpecIds(specIds);
                Map<Integer, List<ConfigSpecificEntity>> csMap = specificEntities.stream().collect(Collectors.groupingBy(ConfigSpecificEntity::getSpecId));
                for(Map.Entry<Integer, List<ConfigSpecificEntity>> configMap:csMap.entrySet()){
                    refresh(makeParams(configMap.getKey()),convert(configMap.getValue()));
                }
            }
            return specIdRelation.size();
        }
        log.accept("SpecificConfigBaseService specIds is null!" );
        return 0;
    }

}
