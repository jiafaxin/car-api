package com.autohome.car.api.services.basic.series;

import autohome.rpc.car.car_api.v1.common.CarPhotoViewItemMessage;
import autohome.rpc.car.car_api.v1.common.CarPhotoViewMessage;
import com.autohome.car.api.common.*;
import com.autohome.car.api.data.popauto.CarPhotoViewMapper;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.CarPhotoViewEntity;
import com.autohome.car.api.data.popauto.entities.PicClassEntity;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.ColorBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.ShowBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PhotosService extends BaseService<byte[]> {

    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecBaseService specBaseService;
    @Autowired
    PicClassBaseService picClassBaseService;
    @Autowired
    ColorBaseService colorBaseService;

    @Autowired
    ShowBaseService showBaseService;

    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private InnerColorBaseService innerColorBaseService;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5_NC;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 48;
    }

    @Override
    protected String keyVersion() { return ":v5"; }

    @Override
    protected boolean canEhCache() {
        return false;
    }


    boolean gzip = true;

    /**
     * 重写set,保存bytes
     *
     * @param key
     * @param result
     */
    @Override
    protected void setToRedis(String key, byte[] result) {
        if (result == null)
            return;
        if (gzip) {
            result = GZIPUtils.compress(result);
        }
        bytesRedisTemplate.opsForValue().set(key, result, getRedisTimeoutMinutes(), TimeUnit.MINUTES);
    }

    public Map<Integer, List<CarPhotoViewItemMessage>> getMap(List<Integer> seriesIds) {
        List<CarPhotoViewItemMessage> list = getList(seriesIds);
        if(CollectionUtils.isEmpty(list)){
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.groupingBy(CarPhotoViewItemMessage::getSeriesId));
    }

    public List<CarPhotoViewItemMessage> getList(List<Integer> seriesIds) {
        List<byte[]> list = multiGet(seriesIds);
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<CarPhotoViewItemMessage> result = new ArrayList<>();
        for(byte[] br : list){
            List<CarPhotoViewItemMessage> item = parse(br);
            if(!CollectionUtils.isEmpty(item)){
                result.addAll(item);
            }
        }
        return result;
    }

    private List<byte[]> multiGet(List<Integer> seriesIds){
        if(CollectionUtils.isEmpty(seriesIds)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> params = seriesIds.stream()
                .filter(Objects::nonNull)
                .map(this::makeParams)
                .collect(Collectors.toList());
        return mGet(params);
    }

    Map<String,Object> makeParams(int seriesId){
        Map<String,Object> params = new HashMap<>();
        params.put("seriesId",seriesId);
        return params;
    }

    /**
     * 重写get，返回proto message
     *
     * @param key
     * @return
     */
    @Override
    protected byte[] getFromRedis(String key) {
        byte[] data = bytesRedisTemplate.opsForValue().get(key);
        if (data == null || data.length == 0) {
            return null;
        }
        if (gzip) {
            data = GZIPUtils.uncompress(data);
        }
        return data;
    }

    @Override
    protected Map<String, byte[]> mGetFromRedis(List<String> keys){
        List<byte[]> stringList = bytesRedisTemplate.opsForValue().multiGet(keys);
        if(org.apache.dubbo.common.utils.CollectionUtils.isEmpty(stringList)){
            return Collections.emptyMap();
        }
        Map<String, byte[]> resultMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            byte[] data = stringList.get(i);
            if (data == null || data.length == 0) {
                continue;
            }
            try {
                if (gzip) {
                    data = GZIPUtils.uncompress(data);
                }
                resultMap.put(key, data);
            }catch (Exception e) {
                log.error("反序列化失败", e);
            }
        }
        return resultMap;
    }

    public List<CarPhotoViewItemMessage> get(int seriesId) {
        byte[] br = get(makeParam(seriesId));
        return parse(br);
    }

    private List<CarPhotoViewItemMessage> parse(byte[] br){
        if(br == null || br.length == 0){
            return new ArrayList<>();
        }
        CarPhotoViewMessage result;
        try {
            result = CarPhotoViewMessage.parseFrom(br);
            if (result == null) {
                return new ArrayList<>();
            }
            return result.getListList();
        } catch (InvalidProtocolBufferException e) {
            return new ArrayList<>();
        }
    }

    @Override
    protected byte[] getData(Map<String, Object> params) {
        List<CarPhotoViewEntity> list = carPhotoViewMapper.getPhotoViewBySeries(getSeriesId(params));
        Map<Integer, PicClassEntity> classEntityMap = picClassBaseService.getList(list.stream().map(x -> x.getPicClass()).distinct().collect(Collectors.toList()));
        Map<Integer, ColorBaseInfo> colorBaseInfoMap = colorBaseService.getColorMap(list.stream().map(x -> x.getPicColorId()).distinct().collect(Collectors.toList()));
        Map<Integer, ShowBaseInfo> showMap = showBaseService.getList(list.stream().filter(x -> x.getShowId() > 0 && x.getPicClass() == 55).map(x -> x.getShowId()).distinct().collect(Collectors.toList()));
        Map<Integer, ColorBaseInfo> innerColorBaseInfoMap = innerColorBaseService.getColorMap(list.stream().map(x -> x.getInnerColorId()).distinct().collect(Collectors.toList()));
        Map<Integer, SpecBaseInfo> specBaseInfoMap = specBaseService.getMap(list.stream().map(x -> x.getSpecId()).distinct().collect(Collectors.toList()));
        Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = seriesBaseService.getMap(list.stream().map(x -> x.getSeriesId()).distinct().collect(Collectors.toList()));
        list.forEach(item -> {
            PicClassEntity classEntity = classEntityMap.get(item.getPicClass());
            ColorBaseInfo colorBaseInfo = colorBaseInfoMap.get(item.getPicColorId());
            //SpecBaseInfo specBaseInfo = specBaseService.get(item.getSpecId()).join();
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(item.getSpecId());
            ShowBaseInfo showBaseInfo = showMap.get(item.getShowId());
            //SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(item.getSeriesId()).join();
            SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(item.getSeriesId());
            ColorBaseInfo innerColorBaseInfo = innerColorBaseInfoMap.get(item.getInnerColorId());
            item.setTypename(classEntity == null ? "" : classEntity.getName());
            item.setSpecname(specBaseInfo == null ? "" : specBaseInfo.getSpecName());
            item.setColorname(colorBaseInfo == null ? "" : colorBaseInfo.getName());
            item.setShowname(item.getShowId() > 0 && item.getPicClass() == 55 ? (showBaseInfo == null ? "" : showBaseInfo.getName()) : "");
            item.setSeriesName(null == seriesBaseInfo ? "" : seriesBaseInfo.getName());
            item.setInnerColorName(null == innerColorBaseInfo ? "" : innerColorBaseInfo.getName());
        });
        return convert(list).toByteArray();
    }

    /**
     * 把对象转成Message
     *
     * @param datas
     * @return
     */
    CarPhotoViewMessage convert(List<CarPhotoViewEntity> datas) {
        CarPhotoViewMessage.Builder messageBuilder = CarPhotoViewMessage.newBuilder();
        if (datas == null || datas.size() == 0)
            return messageBuilder.build();
        for (CarPhotoViewEntity data : datas) {
            String json = JsonUtils.toString(data);
            try {
                CarPhotoViewItemMessage.Builder item = CarPhotoViewItemMessage.newBuilder();
                JsonFormat.parser().merge(json, item);
                messageBuilder.addList(item);
            } catch (InvalidProtocolBufferException e) {
                log.error("序列化失败", e);
            }
        }
        CarPhotoViewMessage result = messageBuilder.build();
        return result;
    }

    int getSeriesId(Map<String, Object> params) {
        return (int) params.get("seriesId");
    }

    Map<String, Object> makeParam(int seriesId) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("seriesId", seriesId);
        return param;
    }


    public void refreshAll(Consumer<String> log) {

        List<CompletableFuture<String>> tasks = new ArrayList<>();

        List<Integer> ids = seriesMapper.getAllSeriesIds().stream().sorted().collect(Collectors.toList());

        for (Integer seriesId : ids) {
            tasks.add(CompletableFuture.supplyAsync(() -> {
                try {
                    Map<String, Object> param = makeParam(seriesId);
                    byte[] datas = getData(param);
                    refresh(param, datas);
                    return "now :" + seriesId;
                } catch (Exception e) {
                    return "error " + seriesId + " >>>>" + ExceptionUtil.getStackTrace(e);
                }
            }));

            if (tasks.size() >= 20) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

                for (CompletableFuture<String> task : tasks) {
                    log.accept(task.join());
                }

                tasks = new ArrayList<>();
            }
        }
        if (!CollectionUtils.isEmpty(tasks)) {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

            for (CompletableFuture<String> task : tasks) {
                log.accept(task.join());
            }
        }
    }

    public void refreshNew(Consumer<String> log) {

        String versionKey = "car:api:basic:photosService:refreshNew:lastupdateverison";

        String v = redisTemplate.opsForValue().get(versionKey);
        if (v == null) {
            v = "0";
        }

        List<KeyValueDto<Integer, Long>> ids = carPhotoViewMapper.getUpdateSeriesIds(Long.parseLong(v));
        if (ids.size() == 0)
            return;

        Long mv = 0L;

        for (KeyValueDto<Integer, Long> seriesId : ids) {
            try {
                Map<String, Object> param = makeParam(seriesId.getKey());
                byte[] datas = getData(param);
                refresh(param, datas);

                if (mv < seriesId.getValue()) {
                    mv = seriesId.getValue();
                }
            } catch (Exception e) {
                log.accept("error:" + seriesId.getKey() + ">>>>>" + ExceptionUtil.getStackTrace(e));
            }
        }

        redisTemplate.opsForValue().set(versionKey, mv + "", 1, TimeUnit.DAYS);
    }
}
