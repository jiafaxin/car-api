package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.ParamConfigModelDetailMapper;
import com.autohome.car.api.data.popauto.entities.ParamConfigModelDetailEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Service
public class ParamConfigModelService extends BaseService<List<ParamConfigModelDetailEntity>> {

    @Resource
    private ParamConfigModelDetailMapper paramConfigModelDetailMapper;

    private static final String MODEL_ID = "modelId";

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 24;
    }

    @Override
    protected List<ParamConfigModelDetailEntity> getData(Map<String, Object> params) {
        int modelId = (int) params.get(MODEL_ID);
        return paramConfigModelDetailMapper.getListByModelId(modelId);
    }

    public List<ParamConfigModelDetailEntity> get(int modelId) {
        return getAsync(makeParams(modelId)).join();
    }

    public Map<String, Integer> getConfMap(int modelId) {
        List<ParamConfigModelDetailEntity> list = get(modelId);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(ParamConfigModelDetailEntity::getPcn, ParamConfigModelDetailEntity::getPCId, (existingValue, newValue) -> existingValue));
    }


    public void refreshAll(Consumer<String> log) {
        List<Integer> modelIds = getAllModelIds();
        for (Integer modelId : modelIds) {
            try {
                Map<String, Object> params = makeParams(modelId);
                refresh(params, getData(params));
            } catch (Exception e) {
                log.accept("modelId error:" + modelId + " >>> " + ExceptionUtil.getStackTrace(e));
            }
        }
    }

    /**
     * 由于目前只用到这个几个modelId, 所以写死，节省redis内存，
     * 后期如果有更多需求，修改此方法
     */
    private List<Integer> getAllModelIds() {
        return Arrays.asList(1, 2);
    }

    Map<String, Object> makeParams(int modelId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(MODEL_ID, modelId);
        return map;
    }

}
