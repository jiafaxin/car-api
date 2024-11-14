package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.FeaturedPictureMapper;
import com.autohome.car.api.data.popauto.entities.FeaturedPictureEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class FeaturedPictureBaseService extends BaseService<List<FeaturedPictureEntity>>{

    @Resource
    private FeaturedPictureMapper featuredPictureMapper;
    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_30;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    public FeaturedPictureEntity get(int id){
        List<FeaturedPictureEntity> featuredPictureAll = get(null);
        if(CollectionUtils.isEmpty(featuredPictureAll)){
            return null;
        }
        return featuredPictureAll.stream().filter(x->x.getId() == id).findFirst().orElse(null);
    }

    public List<FeaturedPictureEntity> getAll(){
        List<FeaturedPictureEntity> featuredPictureAll = get(null);
        return featuredPictureAll;
    }

    @Override
    protected List<FeaturedPictureEntity> getData(Map<String, Object> params) {
        List<FeaturedPictureEntity> featuredPictureAll = featuredPictureMapper.getFeaturedPictureAll();
        return featuredPictureAll;
    }

    public int refreshAll(Consumer<String> log) {
        List<FeaturedPictureEntity> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
