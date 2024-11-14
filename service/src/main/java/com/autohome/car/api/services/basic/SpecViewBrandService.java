package com.autohome.car.api.services.basic;


import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.NewEnergyBrandMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.NewEnergyBrandEntity;
import com.autohome.car.api.data.popauto.entities.SpecViewBrandEntity;
import com.autohome.car.api.services.basic.models.BrandDicInfo;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.auth.v1alpha1.Ca;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecViewBrandService extends BaseService<List<BrandDicInfo>> {

    /**
     * 这里的顺序千万不要改
     */
    private static final List<String> CACHE_KEY = Arrays.asList("brandFuelType", "enduranceMileage", "fastChargeTime");

    @Resource
    private SpecViewMapper specViewMapper;

    @Resource
    private NewEnergyBrandMapper newEnergyBrandMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24 * 60;
    }

    public List<String> getCacheKey() {
        return CACHE_KEY;
    }

    @Override
    protected List<BrandDicInfo> getData(Map<String, Object> params) {
        List<SpecViewBrandEntity> specViewBrandList = specViewMapper.getAllSpecViewBrand();
        String dicKey = params.get("brand").toString();
        List<BrandDicInfo> brandDicInfoList = Collections.emptyList();
        if (StringUtils.equals(dicKey, CACHE_KEY.get(0))) {
            brandDicInfoList = brandFuelType(specViewBrandList, CACHE_KEY.get(0));
        } else if (StringUtils.equals(dicKey, CACHE_KEY.get(1))) {
            brandDicInfoList = enduranceMileage(specViewBrandList, CACHE_KEY.get(1));
        } else if (StringUtils.equals(dicKey, CACHE_KEY.get(2))) {
            brandDicInfoList = fastChargeTime(specViewBrandList, CACHE_KEY.get(2));
        }
        return brandDicInfoList;
    }

    public Map<String, List<BrandDicInfo>> getMap() {
        List<Map<String, Object>> params = CACHE_KEY.stream()
                .map(this::makeParams).collect(Collectors.toList());
        List<List<BrandDicInfo>> lists = mGet(params);
        return lists.stream().flatMap(Collection::stream).collect(Collectors.groupingBy(BrandDicInfo::getK));
    }

    private List<BrandDicInfo> fastChargeTime(List<SpecViewBrandEntity> specViewBrandList, String cacheK) {
        List<BrandDicInfo> result = new ArrayList<>();
        List<Integer> sellBrandIdList = getSellBrandIdList();
        Map<Integer, List<SpecViewBrandEntity>> maps = specViewBrandList.stream().filter(Objects::nonNull).filter(e -> e.getOfficialFastChargetime() > 0).
                collect(Collectors.groupingBy(SpecViewBrandEntity::getBrandId));
        for (Map.Entry<Integer, List<SpecViewBrandEntity>> entry : maps.entrySet()) {
            Integer bId = entry.getKey();
            List<SpecViewBrandEntity> value = entry.getValue();
            String v;
            if (sellBrandIdList.contains(bId) && value.stream().anyMatch(e -> e.getSpecIspublic() == 1)) {
                v = value.stream().filter(e -> e.getSpecIspublic() == 1).map(SpecViewBrandEntity::getOfficialFastChargetime).min(Double::compareTo).orElse(0.0).toString();
            } else {
                v = value.stream().map(SpecViewBrandEntity::getOfficialFastChargetime).min(Double::compareTo).orElse(0.0).toString();
            }
            result.add(new BrandDicInfo(cacheK, bId, v));
        }
        return result;
    }

    private List<BrandDicInfo> enduranceMileage(List<SpecViewBrandEntity> specViewBrandList, String cacheK) {
        List<BrandDicInfo> result = new ArrayList<>();
        List<Integer> sellBrandIdList = getSellBrandIdList();
        Map<Integer, List<SpecViewBrandEntity>> maps = specViewBrandList.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(SpecViewBrandEntity::getBrandId));
        for (Map.Entry<Integer, List<SpecViewBrandEntity>> entry : maps.entrySet()) {
            Integer bId = entry.getKey();
            List<SpecViewBrandEntity> value = entry.getValue();
            String v;
            if (sellBrandIdList.contains(bId) && value.stream().anyMatch(e -> e.getSpecIspublic() == 1)) {
                v = value.stream().filter(e -> e.getSpecIspublic() == 1).map(SpecViewBrandEntity::getEndurancemileage).max(Integer::compareTo).orElse(0).toString();
            } else {
                v = value.stream().map(SpecViewBrandEntity::getEndurancemileage).max(Integer::compareTo).orElse(0).toString();
            }
            result.add(new BrandDicInfo(cacheK, bId, v));
        }
        return result;
    }

    private List<BrandDicInfo> brandFuelType(List<SpecViewBrandEntity> specViewBrandList, String cacheK) {
        List<BrandDicInfo> result = new ArrayList<>();
        Map<Integer, List<SpecViewBrandEntity>> maps = specViewBrandList.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(SpecViewBrandEntity::getBrandId));
        for (Map.Entry<Integer, List<SpecViewBrandEntity>> entry : maps.entrySet()) {
            List<Integer> collect = entry.getValue().stream().map(SpecViewBrandEntity::getFueltypedetail).distinct().collect(Collectors.toList());
            String v = Joiner.on(",").join(collect);
            result.add(new BrandDicInfo(cacheK, entry.getKey(), v));
        }
        return result;
    }

    private List<Integer> getSellBrandIdList() {
        List<NewEnergyBrandEntity> allList = newEnergyBrandMapper.getAllList();
        return allList.stream().map(NewEnergyBrandEntity::getBId).collect(Collectors.toList());
    }

    Map<String, Object> makeParams(String key) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("brand", key);
        return params;
    }

    public void refreshAll(Consumer<String> log) {
        CACHE_KEY.forEach(dic -> {
            try {
                refresh(makeParams(dic), getData(makeParams(dic)));
            } catch (Exception e) {
                log.accept("error：" + dic + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
    }

}
