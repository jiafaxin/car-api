package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.Car25PictureViewMapper;
import com.autohome.car.api.data.popauto.PicClassMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.Car25PictureViewEntity;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class Year25PictureByYearIdBaseService extends BaseService<List<Car25PictureViewEntity>> {

    @Resource
    private SpecViewMapper specViewMapper;

    @Resource
    Car25PictureViewMapper car25PictureViewMapper;

    @Resource
    private PicClassMapper picClassMapper;

    @Resource
    private SpecBaseService specBaseService;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected List<Car25PictureViewEntity> getData(Map<String, Object> params) {
        int seriesId = (int)params.get("seriesId");
        int yearId = (int)params.get("yearId");
        List<Car25PictureViewEntity> list = car25PictureViewMapper.getYear25PictureByYearId(seriesId,yearId);
        //组装其他信息
        markCar25PictureViewEntity(list);
        return list;
    }

    /**
     * 外部使用
     * @param seriesId
     * @return
     */
    public CompletableFuture<List<Car25PictureViewEntity>> get(int seriesId,int yearId){
        CompletableFuture<List<Car25PictureViewEntity>> completableFuture = getAsync(makeParams(seriesId,yearId));
        return completableFuture;
    }

    Map<String,Object> makeParams(int seriesId,int yearId){
        Map<String,Object> params = new HashMap<>();
        params.put("seriesId",seriesId);
        params.put("yearId",yearId);
        return params;
    }

    /**
     * 定时任务使用
     * @param log
     * @return
     */
    public int refreshAll(Consumer<String> log) {
        List<KeyValueDto<Integer,Integer>> seriesYear = specViewMapper.getAllSeriesYearIds();
        if(!CollectionUtils.isEmpty(seriesYear)){
            seriesYear.forEach(item -> {
                int seriesId = item.getKey();
                int yearId = item.getValue();
                try{
                    Map<String, Object> params = new HashMap<>();
                    params.put("seriesId", seriesId);
                    params.put("yearId", yearId);
                    List<Car25PictureViewEntity> list = car25PictureViewMapper.getYear25PictureByYearId(item.getKey(),item.getValue());
                    if(!CollectionUtils.isEmpty(list)){
                        //组装其他信息
                        markCar25PictureViewEntity(list);
                        refresh(params, list);
                    }
                }catch (Exception e){
                    log.accept("error >> " + "seriesId:"+seriesId +",yearId:"+yearId + " >> " + ExceptionUtil.getStackTrace(e));
                }
            });
            return seriesYear.size();
        }
        return 0;
    }

    /**
     * 组装List<Car25PictureViewEntity>信息
     * @param list
     */
    private void markCar25PictureViewEntity(List<Car25PictureViewEntity> list){
        if(!CollectionUtils.isEmpty(list)){
            //获取部位名称
            List<KeyValueDto<Integer,String>> pictureTypes = picClassMapper.getCar25PictureType();
            List<Integer> specIds = list.stream().map(Car25PictureViewEntity::getSpecId).distinct().collect(Collectors.toList());
            //获取车型名称
            Map<Integer, SpecBaseInfo> specBaseInfoMap = specBaseService.getMap(specIds);
            Map<Integer, String> picTypeMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(pictureTypes)){
                picTypeMap = pictureTypes.stream().collect(Collectors.toMap(KeyValueDto::getKey, keyValueDto -> keyValueDto.getValue(), (key1, key2) -> key1));
            }
            for(Car25PictureViewEntity car25PictureViewEntity : list){
                car25PictureViewEntity.setItemName(picTypeMap.get(car25PictureViewEntity.getId()));
                SpecBaseInfo specBaseInfo = specBaseInfoMap.get(car25PictureViewEntity.getSpecId());
                car25PictureViewEntity.setSpecName(null != specBaseInfo ? specBaseInfo.getSpecName() : "");
            }
        }
    }
}
