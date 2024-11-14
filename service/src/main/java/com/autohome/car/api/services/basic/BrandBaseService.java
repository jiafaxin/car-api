package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.HtmlUtils;
import com.autohome.car.api.data.popauto.AppBrandInfoMapper;
import com.autohome.car.api.data.popauto.BrandMapper;
import com.autohome.car.api.data.popauto.entities.AppBrandInfoEntity;
import com.autohome.car.api.data.popauto.entities.BrandBaseEntity;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class BrandBaseService extends BaseService<BrandBaseInfo>{

    @Autowired
    private BrandMapper brandMapper;

    @Resource
    private AppBrandInfoMapper appBrandInfoMapper;


    @Override
    public EhCacheName getCacheName() {
        return EhCacheName.M_30;
    }

    @Override
    public Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    public Map<Integer, BrandBaseInfo> getMap(List<Integer> ids) {
        List<BrandBaseInfo> list = getList(ids);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(BrandBaseInfo::getId, x -> x));
    }

    public List<BrandBaseInfo> getList(List<Integer> ids){
        if(CollectionUtils.isEmpty(ids))
            return new ArrayList<>();
        List<Map<String, Object>> params = ids.stream()
                .filter(Objects::nonNull)
                .map(this::makeParams)
                .collect(Collectors.toList());
        return mGet(params);
    }
    Map<String,Object> makeParams(int specId){
        Map<String,Object> params = new HashMap<>();
        params.put("brandId",specId);
        return params;
    }

    public CompletableFuture<BrandBaseInfo> get(int brandId) {
        Map<String, Object> params = new HashMap<>();
        params.put("brandId", brandId);
        return getAsync(params);
    }

    @Override
    public BrandBaseInfo getData(Map<String, Object> params) {
        int brandId = (int)params.get("brandId");
        BrandBaseEntity brandBaseEntity = brandMapper.getBrandInfo(brandId);
        List<AppBrandInfoEntity> appBrandInfoEntities = appBrandInfoMapper.getAppBrandInfoByBrandId(brandId);
        return convert(brandBaseEntity,appBrandInfoEntities);
    }

    public int refreshAll(Consumer<String> log) {
        List<BrandBaseEntity> brands = brandMapper.getAllBrandInfo();
        brands.forEach(item -> {
            try {
                List<AppBrandInfoEntity> appBrandInfoEntities = appBrandInfoMapper.getAppBrandInfoByBrandId(item.getId());
                Map<String, Object> params = new HashMap<>();
                params.put("brandId", item.getId());
                refresh(params, convert(item,appBrandInfoEntities));
            }catch (Exception e){
                log.accept("error：" + item.getId() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return brands.size();
    }

    BrandBaseInfo convert(BrandBaseEntity brandBaseEntity,List<AppBrandInfoEntity> appBrandInfoEntities){
        if(Objects.isNull(brandBaseEntity)){
            return null;
        }
        BrandBaseInfo brandBaseInfo = new BrandBaseInfo();
        brandBaseInfo.setId(brandBaseEntity.getId());
        brandBaseInfo.setName(HtmlUtils.decode(brandBaseEntity.getName()));
        brandBaseInfo.setLogo(brandBaseEntity.getLogo().replace("~",""));
        brandBaseInfo.setUrl(brandBaseEntity.getUrl());
        brandBaseInfo.setCountry(brandBaseEntity.getCountry());
        brandBaseInfo.setFirstLetter(brandBaseEntity.getFirstLetter());
        brandBaseInfo.setCountryId(brandBaseEntity.getCountryId());
        brandBaseInfo.setCreateTime(brandBaseEntity.getCreateTime());
        brandBaseInfo.setEditTime(brandBaseEntity.getEditTime());
        //不为空
        if(!CollectionUtils.isEmpty(appBrandInfoEntities)){
            brandBaseInfo.setAppBrandInfos(appBrandInfoEntities);
        }
        return brandBaseInfo;
    }

}
