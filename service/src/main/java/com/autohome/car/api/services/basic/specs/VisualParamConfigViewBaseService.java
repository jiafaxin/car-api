package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.VisualParamConfigViewMapper;
import com.autohome.car.api.data.popauto.entities.VisualParamConfigViewEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class VisualParamConfigViewBaseService extends BaseService<List<VisualParamConfigViewEntity>> {

    @Resource
    private VisualParamConfigViewMapper visualParamConfigViewMapper;

    private final static String SPEC_ID = "specId";

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<VisualParamConfigViewEntity> getData(Map<String, Object> params) {
        int specId = (int) params.get(SPEC_ID);
        return visualParamConfigViewMapper.getVisualParamConfigBySpecId(specId);
    }

    Map<String, Object> makeParams(int specId){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put(SPEC_ID,specId);
        return map;
    }

    /**
     * 单个获取
     * @param specId
     * @return
     */
    public List<VisualParamConfigViewEntity> get(int specId) {
        return get(makeParams(specId));
    }

    /**
     * 批量获取
     * @param ids
     * @return
     */
    public Map<Integer,List<VisualParamConfigViewEntity>> getList(List<Integer> ids) {
        List<Map<String,Object>> params = ids.stream().map(x->makeParams(x)).collect(Collectors.toList());
        List<List<VisualParamConfigViewEntity>> list = mGet(params);
        if(list==null||list.size()==0) {
            return new LinkedHashMap<>();
        }
        Map<Integer,List<VisualParamConfigViewEntity>> result = new LinkedHashMap<>();
        for (List<VisualParamConfigViewEntity> item : list) {
            if(item==null||item.size()==0) {
                continue;
            }
            result.put(item.get(0).getSpecId(),item);
        }
        return result;
    }

    /**
     * 定时任务使用
     * @param log
     * @return
     */
    public int refreshAll(Consumer<String> log){
        List<Integer> specIdAll = visualParamConfigViewMapper.getSpecIdAll();
        if(!CollectionUtils.isEmpty(specIdAll)){
            for(Integer specId : specIdAll){
                try {
                    Map<String, Object> params = makeParams(specId);
                    refresh(params,getData(params));
                }catch (Exception e){
                    log.accept("error:"+specId+":"+ ExceptionUtil.getStackTrace(e));
                }
            }
            return specIdAll.size();
        }
        return 0;
    }
}
