package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.SpecYearEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 根据sYearId获取 specId
 */
@Service
public class SpecSYearService extends BaseService<List<SpecYearEntity>> {

    @Resource
    private SpecViewMapper specViewMapper;

    private static final String S_YEAR_ID_PARMA = "syearid";

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 24;
    }

    @Override
    protected List<SpecYearEntity> getData(Map<String, Object> params) {
        return specViewMapper.getSpecByYearId((int)params.get(S_YEAR_ID_PARMA));
    }

    public List<SpecYearEntity> get(int yearId) {
        List<SpecYearEntity> yearEntityList = get(makeParams(yearId));
        if (!CollectionUtils.isEmpty(yearEntityList)) {
            return yearEntityList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    Map<String, Object> makeParams(int yearId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(S_YEAR_ID_PARMA, yearId);
        return map;
    }

    public void refreshAll(Consumer<String> log) {
        for (Integer yearId : specViewMapper.getAllSpecYearIds()) {
            try {
                Map<String, Object> param = makeParams(yearId);
                refresh(param, getData(param));
            } catch (Exception e) {
                log.accept("error:" + yearId + ":" + ExceptionUtil.getStackTrace(e));
            }
        }
    }
}
