package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.OptParItemInfoMapper;
import com.autohome.car.api.data.popauto.entities.OptParItemInfoEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class OptParItemInfoService extends BaseService<List<OptParItemInfoEntity>> {

    private final static String OPT_PARAM_ITEM_INFO = "optionParamItemInfo";
    @Resource
    private OptParItemInfoMapper optParItemInfoMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24 * 60;
    }

    public CompletableFuture<List<OptParItemInfoEntity>> get() {
        Map<String, Object> params = new HashMap<>();
        params.put(OPT_PARAM_ITEM_INFO, OPT_PARAM_ITEM_INFO);
        return getAsync(params);
    }

    @Override
    protected List<OptParItemInfoEntity> getData(Map<String, Object> params) {
        return optParItemInfoMapper.getAll();
    }


    public int refreshAll(Consumer<String> log) {
        List<OptParItemInfoEntity> list = optParItemInfoMapper.getAll();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(OPT_PARAM_ITEM_INFO, OPT_PARAM_ITEM_INFO);
            refresh(params, list);
        } catch (Exception e) {
            log.accept("error >> " + OPT_PARAM_ITEM_INFO + " >> " + ExceptionUtil.getStackTrace(e));
        }
        return list.size();
    }
}
