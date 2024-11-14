package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.BrandMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class GBrandBaseService extends BaseService<List<KeyValueDto<Integer,String>>> {

    @Resource
    private BrandMapper brandMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<KeyValueDto<Integer, String>> getData(Map<String, Object> params) {
        return brandMapper.getGBrandAll();
    }

    public List<KeyValueDto<Integer, String>> getAll(){
        List<KeyValueDto<Integer,String>> datas = get(null);
        return datas;
    }

    public int refreshAll(Consumer<String> log){
        List<KeyValueDto<Integer,String>> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
