package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.BrandMapper;
import com.autohome.car.api.data.popauto.SeriesViewMapper;
import com.autohome.car.api.data.popauto.entities.BFSInfoEntity;
import com.autohome.car.api.services.basic.models.BFSBaseInfo;
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
public class SeriesBrandBaseService extends BaseService<List<BFSBaseInfo>>{

    @Resource
    private SeriesViewMapper seriesViewMapper;

    @Resource
    private BrandMapper brandMapper;


    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected List<BFSBaseInfo> getData(Map<String, Object> params) {
        int brandId = (int)params.get("brandId");
        List<BFSInfoEntity> bfsInfoEntities = seriesViewMapper.getBFSInfoByBrandId(brandId);
        return convert(bfsInfoEntities);
    }

    public CompletableFuture<List<BFSBaseInfo>> get(int brandId){
        CompletableFuture<List<BFSBaseInfo>> completableFuture = getAsync(makeParams(brandId));
        return completableFuture;
    }


    private List<BFSBaseInfo> convert(List<BFSInfoEntity> bfsInfoEntities){
        if(CollectionUtils.isEmpty(bfsInfoEntities)){
            return null;
        }
        List<BFSBaseInfo> bfsBaseInfos = new ArrayList<>();
        bfsInfoEntities.forEach(bfsInfoEntity->{
            BFSBaseInfo baseInfo = new BFSBaseInfo();
            baseInfo.setBrandId(bfsInfoEntity.getBrandId());
            baseInfo.setSeriesId(bfsInfoEntity.getSeriesId());
            baseInfo.setFctId(bfsInfoEntity.getFctId());
            baseInfo.setSsns(bfsInfoEntity.getSsns());
            bfsBaseInfos.add(baseInfo);
        });
        return bfsBaseInfos;
    }
    Map<String,Object> makeParams(int brandId){
        Map<String,Object> params = new HashMap<>();
        params.put("brandId",brandId);
        return params;
    }

    public int refreshAll(Consumer<String> log) {
        List<Integer> brandIds = brandMapper.getAllBrandIds().stream().sorted().collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(brandIds)){
            List<List<Integer>> splitList = ToolUtils.splitList(brandIds, 100);
            for(List<Integer> brandIdList:splitList){
                List<BFSInfoEntity> bfsInfoEntities = seriesViewMapper.getBFSInfoByBrandIds(brandIdList);
                Map<Integer, List<BFSInfoEntity>> csMap = bfsInfoEntities.stream().collect(Collectors.groupingBy(BFSInfoEntity::getBrandId));
                for(Map.Entry<Integer, List<BFSInfoEntity>> configMap:csMap.entrySet()){
                    refresh(makeParams(configMap.getKey()),convert(configMap.getValue()));
                }
            }
            return brandIds.size();
        }
        log.accept("SeriesBrandBaseService bfsBaseInfos is null!" );
        return 0;
    }


}
