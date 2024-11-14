package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.HtmlUtils;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.SeriesBaseInfoEntity;
import com.autohome.car.api.data.popauto.entities.SeriesLevelRankEntity;
import com.autohome.car.api.data.popauto.entities.SeriesViewEntity;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class SeriesBaseService extends BaseService<SeriesBaseInfo>  {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SeriesViewMapper seriesViewMapper;

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    private SpecMapper specMapper;

    @Autowired
    SpecPriceStopSellViewMapper specPriceStopSellViewMapper;

    @Autowired
    private CarSpecPhotoMapper carSpecPhotoMapper;

    @Autowired
    private BbsSeriesMapper bbsSeriesMapper;

    @Resource
    private VrSpecMapper vrSpecMapper;

    @Resource
    private AutoTagMapper autoTagMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected SeriesBaseInfo getData(Map<String, Object> params) {
        int seriesId = (int) params.get("seriesId");
        return getData(seriesId);
    }

    public List<SeriesBaseInfo> getList(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids))
            return new ArrayList<>();
        List<Map<String, Object>> params = ids.stream()
                .filter(Objects::nonNull)
                .map(this::makeParams)
                .collect(Collectors.toList());
        List<SeriesBaseInfo> list = mGet(params);
        if (list == null || list.size() == 0)
            return list;
        Map<Integer, SeriesBaseInfo> map = list.stream().filter(x -> x != null).collect(Collectors.toMap(x -> x.getId(), x -> x));
        return ids.stream().filter(x->map.containsKey(x)).map(x->map.get(x)).collect(Collectors.toList());
    }
    Map<String,Object> makeParams(int specId){
        Map<String,Object> params = new HashMap<>();
        params.put("seriesId",specId);
        return params;
    }
    public CompletableFuture<SeriesBaseInfo> get(int seriesId) {
        return getAsync(makeParam(seriesId));
    }

    Map<String, Object> makeParam(int seriesId){
        Map<String, Object> params = new HashMap<>();
        params.put("seriesId", seriesId);
        return params;
    }

    public Map<Integer,SeriesBaseInfo> getMap(List<Integer> seriesIds) {
        if(seriesIds==null||seriesIds.size() == 0)
            return new LinkedHashMap<>();

        List<Map<String, Object>> keys = seriesIds.stream().map(x -> makeParam(x)).collect(Collectors.toList());
        List<SeriesBaseInfo> list = mGet(keys);

        if(list==null||list.size()==0)
            return new LinkedHashMap<>();

        return list.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
    }

    SeriesBaseInfo getData(int seriesId) {
        int existMaintain = specViewMapper.existmaintain(seriesId);
        Integer containBookedSpec = specViewMapper.seriesContainBookedSpec(seriesId);
        Integer carStateSeriesStopSell = specPriceStopSellViewMapper.carStateSeriesStopSell(seriesId);
        SeriesViewEntity seriesView = seriesMapper.getSeriesView(seriesId);
        SeriesBaseInfoEntity baseEntity = seriesMapper.getBase(seriesId);
        KeyValueDto<Integer, String> seriesTypePhotoBySeriesId = carSpecPhotoMapper.getSeriesTypePhotoBySeriesId(seriesId);
        List<Integer> seriesForeignCar = specMapper.getAllSeriesForeignCar();//数据量不大
        Integer bbsShowSeriesId = bbsSeriesMapper.getBbsShowSeriesId(seriesId); //论坛展示
        SeriesLevelRankEntity seriesLevelRank = seriesViewMapper.getSeriesLevelRankById(seriesId);//关注度排名
        Integer vrBySeriesId = vrSpecMapper.getVrBySeriesId(seriesId);
        List<Integer> seriesTags = autoTagMapper.getSeriesTagsBySeriesId(seriesId);
        KeyValueDto<Integer,String> seriesStopLogo = specViewMapper.getSeriesStopLogoBySeriesId(seriesId);

        if(seriesView==null)
            return null;

        int ws = seriesMapper.waitSellSeries(seriesId);
        int ss = seriesMapper.stopSellSeries(seriesId);
        int si = seriesMapper.sellInSeries(seriesId);

        int show = 0;
        if (seriesView.getSeriesState() >= 20 && seriesView.getSeriesState() <= 30) {
            show = specViewMapper.specShowCount(seriesId, 1);
        } else if (ws>0) {
            show = specViewMapper.specShowCount(seriesId, 2);
        } else if (ss>0) {
            show = 1;
        }

        //官图七天之内有更新车系
        int officialPicIsNew = seriesViewMapper.getSeriesOfficialPicIsNewBySeriesId(seriesId);
        return convert(
                baseEntity,
                seriesView,
                existMaintain > 0,
                containBookedSpec != null && containBookedSpec > 0,
                carStateSeriesStopSell != null && carStateSeriesStopSell > 0,
                seriesTypePhotoBySeriesId,
                seriesForeignCar.contains(seriesId),
                bbsShowSeriesId != null && bbsShowSeriesId > 0,
                seriesLevelRank,
                vrBySeriesId != null && vrBySeriesId > 0 ? 1 : 0,
                show,
                seriesTags,
                officialPicIsNew >= 1 ? 1 : 0,
                ws > 0 ? 1 : 0,
                si > 0 ? 1 : 0,
                seriesStopLogo

        );
    }

    public int refreshAll(Consumer<String> log) {
        List<Integer> existMaintains = specViewMapper.existmaintainSeriesIds();
        List<Integer> containBookedSpec = specViewMapper.seriesAllContainBookedSpec();
        List<Integer> carStateSeriesStopSell = specPriceStopSellViewMapper.carStateSeriesStopSellAll();
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        List<SeriesBaseInfoEntity> baseEntities = seriesMapper.getAllBase();
        List<KeyValueDto<Integer, String>> seriesTypePhotos = carSpecPhotoMapper.getAllSeriesTypePhoto();
        List<Integer> seriesForeignCar = specMapper.getAllSeriesForeignCar();
        List<Integer> bbsShowSeriesIds = bbsSeriesMapper.getAllBbsShowSeriesIds();
        List<SeriesLevelRankEntity> seriesLevelRank = seriesViewMapper.getAllSeriesLevelRank();//关注度排名
        List<KeyValueDto<Integer,Integer>> sconsell = specViewMapper.allSpecShowCount(1);
        List<KeyValueDto<Integer,Integer>> scwaitsell = specViewMapper.allSpecShowCount(2);

        List<Integer> containsWaitSellSeriesIds = seriesMapper.containsWaitSellSeriesIds();
        List<Integer> containsStopSellSeriesIds = seriesMapper.containsStopSellSeriesIds();
        List<Integer> containsSellInSeriesIds = seriesMapper.containsSellInSeriesIds();
        List<KeyValueDto<Integer, String>> seriesStopLogoAll = specViewMapper.getSeriesStopLogoAll();
        Map<Integer,Integer> scmap4onsell = new LinkedHashMap<>();
        for (KeyValueDto<Integer, Integer> item : sconsell) {
            scmap4onsell.put(item.getKey(),item.getValue());
        }
        Map<Integer,Integer> scmap4waitsell = new LinkedHashMap<>();
        for (KeyValueDto<Integer, Integer> item : scwaitsell) {
            scmap4waitsell.put(item.getKey(),item.getValue());
        }

        List<Integer> vrAll = vrSpecMapper.getVrAll();
        List<KeyValueDto<Integer, Integer>> seriesTags = autoTagMapper.getSeriesTags();
        //官图七天之内有更新车系
        List<Integer> officialPicIsNewAll = seriesViewMapper.getSeriesOfficialPicIsNewAll();
        baseEntities.forEach(baseEntity -> {
            try {
                int seriesId = baseEntity.getId();
                Map<String, Object> params = new HashMap<>();
                params.put("seriesId", seriesId);
                SeriesViewEntity seriesView = seriesViews.stream().filter(x -> x.getSeriesid() == baseEntity.getId()).findFirst().orElse(null);
                SeriesLevelRankEntity seriesLevelRankRow = seriesLevelRank.stream().filter(x -> x.getSeriesId() == baseEntity.getId()).findFirst().orElse(null);
                KeyValueDto<Integer, String> seriesTypePhoto = seriesTypePhotos.stream().filter(x -> x.getKey() == baseEntity.getId()).findFirst().orElse(null);
                List<Integer> seriesTag = seriesTags.stream().filter(x -> x.getKey() == seriesId).map(KeyValueDto::getValue).collect(Collectors.toList());
                int show = 0;
                if(seriesView.getSeriesState()>=20 && seriesView.getSeriesState() <= 30){
                    show = scmap4onsell.containsKey(seriesId)?scmap4onsell.get(seriesId):0;
                }else if(containsWaitSellSeriesIds.contains(seriesId)){
                    show = scmap4waitsell.containsKey(seriesId)?scmap4waitsell.get(seriesId):0;
                }else if(containsStopSellSeriesIds.contains(seriesId)){
                    show = 1;
                }
                KeyValueDto<Integer, String> seriesStopLogo = seriesStopLogoAll.stream().filter(x -> x.getKey() == baseEntity.getId()).findFirst().orElse(null);
                refresh(params, convert(
                        baseEntity,
                        seriesView,
                        existMaintains.contains(seriesId),
                        containBookedSpec.contains(seriesId),
                        carStateSeriesStopSell.contains(seriesId),
                        seriesTypePhoto,
                        seriesForeignCar.contains(seriesId),
                        bbsShowSeriesIds != null && bbsShowSeriesIds.contains(seriesId),
                        seriesLevelRankRow,
                        vrAll.contains(seriesId) ? 1 : 0,
                        show,
                        seriesTag,
                        !CollectionUtils.isEmpty(officialPicIsNewAll) && officialPicIsNewAll.contains(seriesId) ? 1:0,
                        !CollectionUtils.isEmpty(containsWaitSellSeriesIds) && containsWaitSellSeriesIds.contains(seriesId) ? 1 : 0,
                        !CollectionUtils.isEmpty(containsSellInSeriesIds) && containsSellInSeriesIds.contains(seriesId) ? 1 : 0,
                        seriesStopLogo

                ));
            }catch (Exception e){
                log.accept("error >> " + baseEntity.getId() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return baseEntities.size();
    }

    public SeriesBaseInfo convert(
            SeriesBaseInfoEntity baseEntity,
            SeriesViewEntity seriesView,
            boolean existMaintain,
            boolean containBookedSpec,
            boolean carStateSeriesStopSell,
            KeyValueDto<Integer, String> seriesTypePhoto,
            boolean isForeignCar,
            boolean isBbsShow,
            SeriesLevelRankEntity seriesLevelRank,
            int isVr,
            int specShowCount,
            List<Integer> seriesTag,
            int officialPicIsNew,
            int ws,
            int si,
            KeyValueDto<Integer, String> seriesStopLogo
            ) {
        if (baseEntity == null) {
            return null;
        }
        SeriesBaseInfo baseInfo = new SeriesBaseInfo();
        baseInfo.setId(baseEntity.getId());
        baseInfo.setName(HtmlUtils.decode(baseEntity.getName()));
        baseInfo.setBrandId(baseEntity.getBrandId());
        baseInfo.setFactId(baseEntity.getFactId());
        baseInfo.setLevelId(baseEntity.getLevelId());
        baseInfo.setLogo(baseEntity.getLogo());
        baseInfo.setUrl(baseEntity.getUrl());
        baseInfo.setRId(baseEntity.getRId());
        baseInfo.setFl(baseEntity.getFl().toUpperCase());
        baseInfo.setNoBgLogo(baseEntity.getNoBgLogo());
        baseInfo.setFc(isForeignCar ? 1 : 0);
        baseInfo.setPlace(baseEntity.getPlace());
        baseInfo.setShowCount(specShowCount);
        baseInfo.setEditTime(baseEntity.getEditTime());
        if(null != seriesView){
            baseInfo.setSeriesRank(seriesView.getSeriesRank());
            baseInfo.setBrandName(seriesView.getBrandName());
            baseInfo.setSeriesPriceMin(seriesView.getSeriesPriceMin());
            baseInfo.setSeriesPriceMax(seriesView.getSeriesPriceMax());
            baseInfo.setSeriesState(seriesView.getSeriesState());
            baseInfo.setSpm(seriesView.getSeriesPhotoNum());
            baseInfo.setPricedescription(seriesView.getPricedescription());
            baseInfo.setSeriesSpecNum(seriesView.getSeriesSpecNum());
            baseInfo.setNewSeriesOrderCls(seriesView.getNewSeriesOrderCls());
            baseInfo.setSeriesIsPublic(seriesView.getSeriesIsPublic());
            baseInfo.setIne(seriesView.getSeriesisnewenergy());
            baseInfo.setFctName(seriesView.getFctName());
        }

        baseInfo.setEm(existMaintain ? 1 : 0);
        baseInfo.setCb(containBookedSpec ? 1 : 0);
        baseInfo.setCs(carStateSeriesStopSell ? 1 : 0);
        baseInfo.setBbsShow(isBbsShow ? 1 : 0);
        if (Objects.nonNull(seriesTypePhoto)) {
            baseInfo.setPp(seriesTypePhoto.getValue());
        }
        if(seriesLevelRank != null){
            baseInfo.setLevelRank(seriesLevelRank.getRowIndex());
        }
        baseInfo.setIsVr(isVr);
        if (CollectionUtils.isNotEmpty(seriesTag)) {
            baseInfo.setTag(seriesTag);
        }
        baseInfo.setOpin(officialPicIsNew);
        baseInfo.setWs(ws);
        baseInfo.setSi(si);
        if(null != seriesStopLogo){
            baseInfo.setStopLogo(seriesStopLogo.getValue());
        }
        return baseInfo;
    }


}
