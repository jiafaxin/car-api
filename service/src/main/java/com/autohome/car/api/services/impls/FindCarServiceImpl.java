package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.car.GetFindCarSeriesInfoByConditionRequest;
import autohome.rpc.car.car_api.v1.car.GetFindCarSeriesInfoByConditionResponse;
import autohome.rpc.car.car_api.v1.car.LevelFindCarRequest;
import autohome.rpc.car.car_api.v1.car.LevelFindCarResponse;
import autohome.rpc.car.car_api.v1.sou.SeriesFindCarRequest;
import autohome.rpc.car.car_api.v1.sou.SeriesFindCarResponse;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.FindCarMapper;
import com.autohome.car.api.data.popauto.LevelSeriesViewMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.LevelSeriesEntity;
import com.autohome.car.api.data.popauto.properties.SearchSeriesProperties;
import com.autohome.car.api.services.FindCarService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesInfoService;
import com.autohome.car.api.services.basic.solr.SolrSearchFilterService;
import com.autohome.car.api.services.basic.solr.searchSeriesResult;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.LevelFindCarParam;
import com.autohome.car.api.services.models.SeriesInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.*;

@Service
@Slf4j
public class FindCarServiceImpl implements FindCarService {

    @Autowired
    FindCarMapper findCarMapper;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SeriesInfoService seriesInfoService;

    @Autowired
    LevelSeriesViewMapper levelSeriesViewMapper;

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    private SolrSearchFilterService solrSearchFilterService;

    @Autowired
    private SolrTemplate solrTemplate;

    @Resource
    private SearchSeriesProperties searchSeriesProperties;

    public static final Map<Integer, ClubTopicMap> CLUB_TOPIC_MAP = initClubTopicMap();

    private static Map<Integer, ClubTopicMap> initClubTopicMap() {
        Map<Integer, ClubTopicMap> dic = new HashMap<>();
        dic.put(633, new ClubTopicMap(1, 633, "宝来", "", 633, "全新宝来论坛", 633, "宝来论坛"));
        dic.put(834, new ClubTopicMap(2, 834, "君越", "", 834, "全新君越论坛", 834, "君越论坛"));
        dic.put(333, new ClubTopicMap(3, 333, "途安", "", 333, "全新途安L论坛", 333, "途安论坛"));
        dic.put(135, new ClubTopicMap(5, 135, "思域", "", 135, "新思域论坛", 135, "思域论坛"));
        return dic;
    }

    @Data
    public static class ClubTopicMap {
        public int Id;
        public int SeriesId;
        public String SeriesName;
        public String Bbs;
        public int BbsId;
        public String BbsName;
        public int BbsIdOld;
        public String BbsNameOld;

        public ClubTopicMap(int _id, int _seriesId, String _seriesName, String _bbs, int _bbsId, String _bbsName, int _bbsIdOld, String _bbsNameOld) {
            this.Id = _id;
            this.SeriesId = _seriesId;
            this.SeriesName = _seriesName;
            this.Bbs = _bbs;
            this.BbsId = _bbsId;
            this.BbsName = _bbsName;
            this.BbsIdOld = _bbsIdOld;
            this.BbsNameOld = _bbsNameOld;
        }
    }

    public SeriesFindCarResponse finCar(SeriesFindCarRequest request){
        int minPrice=0,maxPrice = 0;
        List<Integer> levelIds = new ArrayList<>();
        SeriesFindCarResponse.Builder builder = SeriesFindCarResponse.newBuilder();


        if(StringUtils.isNotBlank(request.getPrice())){
            String[] arrPrice = request.getPrice().split("_");
            if(arrPrice.length == 1 && !arrPrice[0].equals("0")) {
                String strprice = getPriceById(Integer.parseInt(arrPrice[0]));
                if(strprice == null || strprice.isEmpty()) {
                    return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
                }
                arrPrice = strprice.split("_");
            }
            if(arrPrice.length == 2) {
                if(arrPrice[0].isEmpty() || arrPrice[1].isEmpty()) {
                    return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
                }
                int imin = Integer.parseInt(arrPrice[0]) > 10000 ? 10000 : Integer.parseInt(arrPrice[0]);
                int imax = Integer.parseInt(arrPrice[1]) > 10000 ? 10000 : Integer.parseInt(arrPrice[1]);

                minPrice = (int)(Double.parseDouble(String.valueOf(imin)) * 10000);
                maxPrice = (int)(Double.parseDouble(String.valueOf(imax)) * 10000);
            }
        }

        if (StringUtils.isNotBlank(request.getLevelid())) {
            String strLevel = request.getLevelid();
            String[] arrlevel = strLevel.split(",");

            if(arrlevel.length > 1 && !strLevel.equals("16,17,18,19,20")) {
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
            }

            for(String s : arrlevel) {
                if(!s.matches("[0-9]+")) {
                    return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
                }
                levelIds.add(Integer.parseInt(s));
            }
        }
        Double minDeliveryCapacity = 0D;
        Double maxDeliveryCapacity = 0D;
        if(StringUtils.isNotBlank(request.getDisplacementid())) {
            String[] dcap = request.getDisplacementid().split("_");

            if(dcap.length == 1 && !dcap[0].equals("0")) {
                String strcap = getDCById(Integer.parseInt(dcap[0]));
                if(strcap == null || strcap.isEmpty()) {
                    return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
                }
                dcap = strcap.split("_");
            }
            if(dcap.length == 2) {
                if(dcap[0].isEmpty() || dcap[1].isEmpty()) {
                    return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
                }
                minDeliveryCapacity = (Double.parseDouble(dcap[0]));
                maxDeliveryCapacity = (Double.parseDouble(dcap[1]));
            }
        }

        List<Integer> structId = new ArrayList<>();
        if(StringUtils.isNotBlank(request.getStructureid())) {
            String strStruct = request.getStructureid();
            String[] arrStruct = strStruct.split(",");
            if(arrStruct.length > 1 && !(strStruct.equals("5,6") || strStruct.equals("6,5"))) {
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
            }
            for(String s : arrStruct) {
                if(!s.matches("[0-9]+")) {
                    return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
                }
                structId.add(Integer.parseInt(s));
            }
        }

        List<Integer> configIds = new ArrayList<>();
        if(StringUtils.isNotBlank(request.getConfigid())){
            for (String s : request.getConfigid().split("_")) {
                if(!s.matches("[0-9]+")){
                    continue;
                }
                configIds.add(Integer.parseInt(s));
            }
        }

        List<Integer> seriesIds = findCarMapper.findSeries(
                minPrice,
                maxPrice,
                levelIds,
                request.getBrandid(),
                request.getGearboxid(),
                minDeliveryCapacity,
                maxDeliveryCapacity,
                structId,
                request.getCountryid(),
                configIds,
                request.getPropertyid(),
                request.getFueltype(),
                request.getSeat(),
                request.getDrivetype()
        );

        builder.setReturnCode(0).setReturnMsg("成功");

        if(seriesIds==null||seriesIds.size()==0)
            return builder.build();


        Map<Integer, SeriesBaseInfo> list = seriesBaseService.getMap(seriesIds);
        if(list==null||list.size()==0)
            return builder.build();
        SeriesFindCarResponse.Result.Builder result = SeriesFindCarResponse.Result.newBuilder();
        list.forEach((k,v)->{
            SeriesFindCarResponse.Result.Item.Builder item = SeriesFindCarResponse.Result.Item.newBuilder();
            item.setSeriesid(k);
            item.setSeriesname(v.getName());
            item.setBrandname(v.getBrandName());
            item.setSeriesrank(v.getSeriesRank()) ;
            item.setSeriesminprice(v.getSeriesPriceMin());
            item.setSeriesmaxprice(v.getSeriesPriceMax());

            result.addItems(item);
        });

        result.setTotal(list.size());

        return builder.setResult(result).build();
    }

    /**
     * pc 找车页面重构后源接口
     * @param request
     * @return
     */
    @Override
    public GetFindCarSeriesInfoByConditionResponse getFindCarSeriesInfoByCondition(GetFindCarSeriesInfoByConditionRequest request) {
        GetFindCarSeriesInfoByConditionResponse.Builder builder = GetFindCarSeriesInfoByConditionResponse.newBuilder();
        int minPrice = 0;
        int maxPrice = 0;
        if(StringUtils.isNotBlank(request.getPrice())){
            String[] priceStr = request.getPrice().split("_");
            if(priceStr.length != 2){
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                        .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                        .build();

            }
            minPrice = CommonFunction.getStringToInt(priceStr[0],0) * 10000;
            maxPrice = CommonFunction.getStringToInt(priceStr[1],0) * 10000;
        }
        //过滤
        if(!CommonFunction.FIND_CAR_LEVEL.contains(request.getLevel()) || !CommonFunction.FIND_CAR_COUNTRY.contains(request.getCountry()) ||
                !CommonFunction.FIND_CAR_STRUCT.contains(request.getStruct()) || !CommonFunction.FIND_CAR_SEAT.contains(request.getSeat()) ||
                !CommonFunction.FIND_CAR_FLOW_MODE.contains(request.getFlowmode()) || !CommonFunction.FIND_CAR_FUEL_TYPE.contains(request.getFueltype())){

            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        //过滤
        if(!CommonFunction.FIND_CAR_DRIVE_TYPE.contains(request.getDrivetype()) || !CommonFunction.FIND_CAR_GEAR_BOX.contains(request.getGearbox()) ||
                !CommonFunction.FIND_CAR_IS_IMPORT.contains(request.getIsimport()) || !CommonFunction.FIND_CAR_STATE.contains(request.getState())){

            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        double minDisplacement = 0;
        double maxDisplacement = 0;
        if(StringUtils.isNotBlank(request.getDisplacement())){
            String[] displacementStr = request.getDisplacement().split("_");
            if(displacementStr.length != 2){
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                        .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                        .build();
            }
            minDisplacement = CommonFunction.getStringToDouble(displacementStr[0],0);
            maxDisplacement = CommonFunction.getStringToDouble(displacementStr[1],0);
        }
        int minEndurance = 0;
        int maxEndurance = 0;
        if(StringUtils.isNotBlank(request.getEndurance())){
            String[] enduranceStr = request.getEndurance().split("_");
            if(enduranceStr.length != 2){
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                        .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                        .build();
            }
            minEndurance = CommonFunction.getStringToInt(enduranceStr[0],0);
            maxEndurance = CommonFunction.getStringToInt(enduranceStr[1],0);
        }
        int pageIndex = request.getPageindex() <= 0 ? 1 :request.getPageindex();
        int pageSize = request.getPagesize() <= 0 ? 30 : request.getPagesize();
        //条件过滤
        List<SimpleQuery> fqs = solrSearchFilterService.getFindCarFilterByCondition(request,minPrice,maxPrice,minDisplacement,maxDisplacement,minEndurance,maxEndurance);
        SimpleQuery query = new SimpleQuery("*:*");
        for (SimpleQuery fq : fqs) {
            query.addFilterQuery(fq);
        }
        //排序
        Sort orders = solrSearchFilterService.getFindCarSortByCondition(request);
        if(null != orders){
            query.addSort(orders);
        }
        query.addProjectionOnFields(CommonFunction.FIELDS_ARRAY);

        //按照车系分组
        GroupOptions groupOptions = new GroupOptions().setTotalCount(true).addGroupByField("SeriesId").setOffset(0).setLimit(10000);
        query.setGroupOptions(groupOptions);
        //分页
        long offSet = (pageIndex - 1) * pageSize;
        query.setOffset(offSet).setRows(pageSize);
        GroupResult<searchSeriesResult> groupResult = null;
        try {
            long start = System.currentTimeMillis();
            groupResult = solrTemplate.queryForGroupPage(searchSeriesProperties.getCorename(), query, searchSeriesResult.class).getGroupResult("SeriesId");
            long end = System.currentTimeMillis();
            if(end - start > 1000){
                log.warn("findCarSeriesInfoByCondition请求条件：{} , 耗时：{} ms" ,JsonUtils.toString(fqs),(end-start));
            }
        } catch (Exception e) {
            log.error("findCarSeriesInfoByCondition请求条件：{} ,异常信息：{}" ,JsonUtils.toString(fqs), e.getMessage());
            return builder.setReturnCode(RETURN_MESSAGE_ENUM500.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM500.getReturnMsg())
                    .build();
        }
        GetFindCarSeriesInfoByConditionResponse.Result.Builder result = GetFindCarSeriesInfoByConditionResponse.Result.newBuilder();

        if(null != groupResult){
            int matches = groupResult.getMatches();
            if(matches <= 0){
                log.warn("findCarSeriesInfoByCondition请求无数据条件：{} " ,JsonUtils.toString(fqs));
            }
            Page<GroupEntry<searchSeriesResult>> entries = groupResult.getGroupEntries();
            int seriesCount = groupResult.getGroupsCount();
            List<Integer> seriesIds = entries.stream().map(x -> Integer.parseInt(x.getGroupValue())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(seriesIds)){
                //批量获取车系信息
                Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = seriesBaseService.getMap(seriesIds);
                for (GroupEntry<searchSeriesResult> sub : entries) {
                    GetFindCarSeriesInfoByConditionResponse.SeriesInfo.Builder seriesInfo = GetFindCarSeriesInfoByConditionResponse.SeriesInfo.newBuilder();
                    SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(Integer.parseInt(sub.getGroupValue()));
                    seriesInfo.setSeriesId(Integer.parseInt(sub.getGroupValue()));
                    seriesInfo.setSeriesName(null != seriesBaseInfo && null != seriesBaseInfo.getName() ? seriesBaseInfo.getName() : "");
                    seriesInfo.setSeriesImg(null != seriesBaseInfo ? ImageUtil.getFullImagePath(seriesBaseInfo.getNoBgLogo()) : "");
                    seriesInfo.setSeriesFctMinPrice(sub.getResult().getContent().get(0).getSeriesFctMinPrice());
                    seriesInfo.setSeriesFctMaxPrice(sub.getResult().getContent().get(0).getSeriesFctMaxPrice());
                    seriesInfo.setSeriesState(null != seriesBaseInfo ? seriesBaseInfo.getSeriesState() : 0);
                    List<Integer> specIds = sub.getResult().getContent().stream().map(searchSeriesResult::getSpecId).collect(Collectors.toList());
                    seriesInfo.addAllSpecIds(specIds);
                    seriesInfo.setSpecCount(sub.getResult().getContent().size());
                    result.addSeriesGroupList(seriesInfo);
                }
            }
            result.setPageindex(pageIndex);
            result.setPagesize(pageSize);
            result.setSeriescount(seriesCount);
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }



    public static String getPriceById(int rangeId) {
        String strResult = "";
        switch(rangeId) {
            case 10:
                strResult = "0_5";
                break;
            case 20:
                strResult = "5_8";
                break;
            case 30:
                strResult = "8_10";
                break;
            case 40:
                strResult = "10_15";
                break;
            case 50:
                strResult = "15_20";
                break;
            case 60:
                strResult = "20_25";
                break;
            case 70:
                strResult = "25_35";
                break;
            case 80:
                strResult = "35_50";
                break;
            case 90:
                strResult = "50_70";
                break;
            case 100:
                strResult = "70_100";
                break;
            case 110:
                strResult = "100_0";
                break;
        }
        return strResult;
    }

    public static String getDCById(int id) {
        String strResult = "";
        switch(id) {
            case 1:
                strResult = "0_1.0";
                break;
            case 2:
                strResult = "1.1_1.6";
                break;
            case 3:
                strResult = "1.7_2.0";
                break;
            case 4:
                strResult = "2.1_2.5";
                break;
            case 5:
                strResult = "2.6_3.0";
                break;
            case 6:
                strResult = "3.1_4.0";
                break;
            case 7:
                strResult = "4.0_0";
                break;
        }
        return strResult;
    }

    public LevelFindCarResponse levelFindCar(LevelFindCarRequest request){
        LevelFindCarResponse.Builder builder = LevelFindCarResponse.newBuilder();
        long startTime = System.currentTimeMillis();

        LevelFindCarParam param = new LevelFindCarParam(request);
        AtomicReference<List<LevelSeriesEntity>> seriesListTask = new AtomicReference<>();
        AtomicReference<List<Integer>> newSeriesIdsTask = new AtomicReference<>();
        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(CompletableFuture.supplyAsync(()->levelSeriesViewMapper.getSeriesInfoByLevelId(param.getLevelId())).thenAccept(x->{
            seriesListTask.set(x);
        }));
        tasks.add(CompletableFuture.supplyAsync(()->specViewMapper.getNewSeriesList()).thenAccept(x->{
            newSeriesIdsTask.set(x);
        }));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        List<LevelSeriesEntity> seriesList = seriesListTask.get();
        List<Integer> newSeriesIds = newSeriesIdsTask.get();
        seriesList = specialHandling(seriesList);
        if (!Objects.equals(param.getPrice(), "0_0") && Strings.isNotBlank(param.getPrice())) {
            if (param.getPriceMin() == 0) {
                seriesList = seriesList.stream().filter(x -> x.getSeriesPriceMin() > 0 && x.getSeriesPriceMax() <= param.getPriceMax()).collect(Collectors.toList());
            } else if (param.getPriceMax() == 0) {
                seriesList = seriesList.stream().filter(x -> x.getSeriesPriceMin() >= param.getPriceMin()).collect(Collectors.toList());
            } else {
                seriesList = seriesList.stream().filter(x -> (x.getSeriesPriceMin() >= param.getPriceMin() && x.getSeriesPriceMin() <= param.getPriceMax())
                                || (x.getSeriesPriceMax() >= param.getPriceMin() && x.getSeriesPriceMax() <= param.getPriceMax())
                                || (x.getSeriesPriceMax() >= param.getPriceMax() && x.getSeriesPriceMin() <= param.getPriceMin()))
                        .collect(Collectors.toList());
            }
        }
        if (!Objects.equals(param.getDisplacement(), "0.0_0.0") && Strings.isNotBlank(param.getDisplacement())) {
            if (param.getDisplacementMin() == 0) {
                seriesList = seriesList.stream().filter(x -> x.getMinDisplacement() > 0 && x.getMaxDisplacement() <= param.getDisplacementMax()).collect(Collectors.toList());
            } else if (param.getDisplacementMax() == 0) {
                seriesList = seriesList.stream().filter(x -> x.getMinDisplacement() >= param.getDisplacementMin()).collect(Collectors.toList());
            } else {
                seriesList = seriesList.stream().filter(x -> (x.getMinDisplacement() >= param.getDisplacementMin() && x.getMinDisplacement() <= param.getDisplacementMax())
                                || (x.getMaxDisplacement() >= param.getDisplacementMin() && x.getMaxDisplacement() <= param.getDisplacementMax())
                                || (x.getMaxDisplacement() >= param.getDisplacementMax() && x.getMinDisplacement() <= param.getDisplacementMin()))
                        .collect(Collectors.toList());
            }
        }

        if (param.getDriver() != CarDriveEnum.全部) {
            switch (param.getDriver()) {
                case 前驱:
                    seriesList = seriesList.stream().filter(x -> x.getQianQu() == 1).collect(Collectors.toList());
                    break;
                case 后驱:
                    seriesList = seriesList.stream().filter(x -> x.getHouQu() == 1).collect(Collectors.toList());
                    break;
                case 四驱:
                    seriesList = seriesList.stream().filter(x -> x.getSiQu() == 1).collect(Collectors.toList());
                    break;
            }
        }

        if (param.getGear() != CarGearBoxEnum.全部) {
            switch (param.getGear()) {
                case 手动:
                    seriesList = seriesList.stream().filter(x -> x.getGearboxManual() == 1).collect(Collectors.toList());
                    break;
                case 自动:
                    seriesList = seriesList.stream().filter(x -> x.getGearboxAuto() == 1).collect(Collectors.toList());
                    break;
            }
        }

        if (param.getStruct() != CarStructEnum.全部) {
            switch (param.getStruct()) {
                case 两厢:
                    seriesList = seriesList.stream().filter(x -> x.getLiangXiang() == 1 || x.getKuajieLiangXiang() == 1).collect(Collectors.toList());
                    break;
                case 三厢:
                    seriesList = seriesList.stream().filter(x -> x.getSanXiang() == 1 || x.getKuajieSanXiang() == 1).collect(Collectors.toList());
                    break;
                case 掀背:
                    seriesList = seriesList.stream().filter(x -> x.getXianBei() == 1).collect(Collectors.toList());
                    break;
                case 旅行版:
                    seriesList = seriesList.stream().filter(x -> x.getLvXing() == 1 || x.getKuajieLvXing() == 1).collect(Collectors.toList());
                    break;
                case 硬顶敞篷车:
                    seriesList = seriesList.stream().filter(x -> x.getYingDingChangPeng() == 1).collect(Collectors.toList());
                    break;
                case 软顶敞篷车:
                    seriesList = seriesList.stream().filter(x -> x.getRuanDingChangPeng() == 1).collect(Collectors.toList());
                    break;
                case 硬顶跑车:
                    seriesList = seriesList.stream().filter(x -> x.getYingDingPaoChe() == 1).collect(Collectors.toList());
                    break;
                case 客车:
                    seriesList = seriesList.stream().filter(x -> x.getKeChe() == 1).collect(Collectors.toList());
                    break;
                case 货车:
                    seriesList = seriesList.stream().filter(x -> x.getHuoChe() == 1).collect(Collectors.toList());
                    break;
                case 皮卡:
                    seriesList = seriesList.stream().filter(x -> x.getPika() == 1).collect(Collectors.toList());
                    break;
                case MPV:
                    seriesList = seriesList.stream().filter(x -> x.getMpv() == 1).collect(Collectors.toList());
                    break;
                case SUV:
                    seriesList = seriesList.stream().filter(x -> x.getSuv() == 1).collect(Collectors.toList());
                    break;
                case 跨界车:
                    seriesList = seriesList.stream().filter(x -> x.getKuajieLiangXiang() == 1
                            || x.getKuajieSanXiang() == 1
                            || x.getKuajieLvXing() == 1
                            || x.getKuajieSuv() == 1
                    ).collect(Collectors.toList());
                    break;
            }
        }

        if (param.getAttribute() > 0) {
            seriesList = seriesList.stream().filter(x -> x.getIsImport() == param.getAttribute()).collect(Collectors.toList());
        }

        if (param.getFuel() != CarFuelEnum.全部) {
            switch (param.getFuel()) {
                case 汽油:
                    seriesList = seriesList.stream().filter(x -> x.getQiYou() == 1).collect(Collectors.toList());
                    break;
                case 柴油:
                    seriesList = seriesList.stream().filter(x -> x.getChaiYou() == 1).collect(Collectors.toList());
                    break;
                case 油电混合:
                    seriesList = seriesList.stream().filter(x -> x.getYouDianHunHe() == 1).collect(Collectors.toList());
                    break;
                case 纯电动:
                    seriesList = seriesList.stream().filter(x -> x.getDianDong() == 1).collect(Collectors.toList());
                    break;
                case 插电式混动:
                    seriesList = seriesList.stream().filter(x -> x.getChaDianHunDong() == 1).collect(Collectors.toList());
                    break;
                case 增程式:
                    seriesList = seriesList.stream().filter(x -> x.getZengcheng() == 1).collect(Collectors.toList());
                    break;
                case 氢燃料:
                    seriesList = seriesList.stream().filter(x -> x.getQingranliao() == 1).collect(Collectors.toList());
                    break;
                case 汽油48V轻混系统:
                    seriesList = seriesList.stream().filter(x -> x.getQinghunsiba() == 1).collect(Collectors.toList());
                    break;
                case 汽油24V轻混系统:
                    seriesList = seriesList.stream().filter(x -> x.getQinghunersi() == 1).collect(Collectors.toList());
                    break;
                case 全部新能源:
                    seriesList = seriesList.stream().filter(x -> x.getDianDong() == 1 || x.getChaDianHunDong() == 1 ||
                            x.getZengcheng() == 1 || x.getQingranliao() == 1).collect(Collectors.toList());
                    break;
                case 全部轻混系统:
                    seriesList = seriesList.stream().filter(x -> x.getQinghunsiba() == 1 || x.getQinghunersi() == 1 ).collect(Collectors.toList());
                    break;
            }
        }

        if (param.getCountry() > 0) {
            //前台外展示非欧系国别，包括德国，捷克，法国，英国，瑞典，意大利
            //上述国别代码分别为，2/11/6/7/9/8
            if (param.getCountry() == 201) {
                seriesList = seriesList.stream().filter(x -> Arrays.asList("2,6,7,8,9,11".split(",")).contains(String.valueOf(x.getCountryId()))).collect(Collectors.toList());
            } else {
                seriesList = seriesList.stream().filter(x -> x.getCountryId() == param.getCountry()).collect(Collectors.toList());
            }

        }

        if (param.getSeat() != CarSeatEnum.全部) {
            switch (param.getSeat()) {
                case Seat2:
                    seriesList = seriesList.stream().filter(x -> x.getSeat2() == 1).collect(Collectors.toList());
                    break;
                case Seat4:
                    seriesList = seriesList.stream().filter(x -> x.getSeat4() == 1).collect(Collectors.toList());
                    break;
                case Seat5:
                    seriesList = seriesList.stream().filter(x -> x.getSeat5() == 1).collect(Collectors.toList());
                    break;
                case Seat6:
                    seriesList = seriesList.stream().filter(x -> x.getSeat6() == 1).collect(Collectors.toList());
                    break;
                case Seat7:
                    seriesList = seriesList.stream().filter(x -> x.getSeat7() == 1).collect(Collectors.toList());
                    break;
                case SeatMax:
                    seriesList = seriesList.stream().filter(x -> x.getSeat8() == 1).collect(Collectors.toList());
                    break;
            }
        }

        if (!param.isNoParas()) {
            seriesList = seriesList.stream().filter(x -> x.getSeriesIsImgSpec() == 0).collect(Collectors.toList());
        }

        if (Strings.isNotBlank(param.getConfig())) {
            if (param.getConfig().contains("_1_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig1()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_2_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig2()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_3_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig3()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_4_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig4()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_5_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig5()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_6_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig6()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_7_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig7()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_8_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig8()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_9_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig9()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_10_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig10()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_11_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig11()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_12_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig12()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_13_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig13()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_14_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig14()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_15_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig15()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_16_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig16()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_17_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig17()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_18_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig18()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_19_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig19()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_20_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig20()) == 1).collect(Collectors.toList());
            }
            if (param.getConfig().contains("_21_")) {
                seriesList = seriesList.stream().filter(x -> Integer.parseInt(x.getConfig21()) == 1).collect(Collectors.toList());
            }
        }

        List<Integer> seriesIds = seriesList.stream().map(LevelSeriesEntity::getSeriesId).collect(Collectors.toList());
        Map<Integer, SeriesInfo> seriesMap = seriesInfoService.getMap(seriesIds, false, false);

        LevelFindCarResponse.Result.Builder result = LevelFindCarResponse.Result.newBuilder();
        List<LevelFindCarResponse.SeriesItem> seriesInfos = new ArrayList<>();
        for(LevelSeriesEntity item : seriesList){
            LevelFindCarResponse.SeriesItem.Builder seriesInfo = LevelFindCarResponse.SeriesItem.newBuilder();
            SeriesInfo series = CollectionUtils.isEmpty(seriesMap) ? null : seriesMap.getOrDefault(item.getSeriesId(), null);
            int seriesid = item.getSeriesId();
            int seriesstate = newSeriesIds != null && newSeriesIds.contains(seriesid) ? 11 : item.getSeriesState();
            int seriespricemin = (int) item.getSeriesPriceMin();
            int seriespricemax = (int) item.getSeriesPriceMax();
            int brandid = series != null ? series.getBrandid() : 0;
            int fctid = series != null ? series.getFctid() : 0;
            String fctname = series != null ? series.getFctname() : "";
            int bbsisshow = item.getBbsIsShow();
            int isPhotoSeries = item.getSeriesIsImgSpec();

            int priceUrlIsShow = 0;
            String priceUrl = "";
            if ((seriesstate == 40 && isPhotoSeries == 0) || ((seriesstate == 10 || seriesstate == 11 || seriesstate == 20 || seriesstate == 30) && (seriespricemin > 0 || seriespricemax > 0))) {
                priceUrlIsShow = 1;
                priceUrl = String.format("https://car.autohome.com.cn/price/series-%d.html", seriesid);
            }

            int clubUrlIsShow = 0;
            String clubUrl = "";
            if (bbsisshow == 1) {
                clubUrlIsShow = 1;
                if (CLUB_TOPIC_MAP.containsKey(seriesid)) {
                    clubUrl = String.format("https://club.autohome.com.cn/bbs/forum-c-%d-1.html", CLUB_TOPIC_MAP.get(seriesid).getBbsId());
                } else {
                    clubUrl = String.format("https://club.autohome.com.cn/bbs/forum-c-%d-1.html", seriesid);
                }
            }

            seriesInfo.setSeriesid(seriesid);
            seriesInfo.setSeriesname(series != null ? series.getSeriesname() : "");
            seriesInfo.setLogo(series != null && Strings.isNotBlank(series.getRawSeriesLogo()) ? ImageUtil.getFullImagePathByPrefix(series.getRawSeriesLogo().replace("~", ""), "160x120_0_q87_") : "");
            seriesInfo.setState(seriesstate);
            seriesInfo.setMinprice(seriespricemin);
            seriesInfo.setMaxprice(seriespricemax);
            seriesInfo.setBrandid(brandid);
            seriesInfo.setFctid(fctid);
            seriesInfo.setFctname(fctname);
            seriesInfo.setShowclub(bbsisshow);
            seriesInfo.setIsphotoseries(isPhotoSeries);
            seriesInfo.setOrder(item.getNewSeriesOrderCls());
            seriesInfo.setPriceurlisshow(priceUrlIsShow);
            seriesInfo.setPriceurl(priceUrl);
            seriesInfo.setCluburlisshow(clubUrlIsShow);
            seriesInfo.setCluburl(clubUrl);

            seriesInfos.add(seriesInfo.build());
        }
        result.setTimer((int) (System.currentTimeMillis() - startTime)).setSeriescount(seriesInfos.size()).addAllSerieslist(seriesInfos);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    private List<LevelSeriesEntity> specialHandling(List<LevelSeriesEntity> list) {
        List<LevelSeriesEntity> result = ToolUtils.deepCopyList(list);

        LevelSeriesEntity target = null;
        if (!result.isEmpty()) {
            for (LevelSeriesEntity entity : list) {
                if (entity.getSeriesId() == 4691) {
                    target = entity;
                    break;
                }
            }

            if (target != null) {
                //东风日产
                List<LevelSeriesEntity> drsFct = result.stream().filter(x -> x.getFctId() == 92).collect(Collectors.toList());
                //逍客
                List<LevelSeriesEntity> drsTarget = result.stream().filter(x -> x.getSeriesId() == 564).collect(Collectors.toList());
                double seriesorder = result.size() + 1;
                if(!CollectionUtils.isEmpty(drsFct)){
                    LevelSeriesEntity temp = new LevelSeriesEntity();
                    temp.genLevelSeriesEntity(target);
                    //如果有逍客，添加的行放它后面
                    if (!CollectionUtils.isEmpty(drsTarget)) {
                        seriesorder = drsTarget.get(0).getNewSeriesOrderCls() + 0.1;
                    }
                    temp.setFctId(92);
                    temp.setNewSeriesOrderCls((int)seriesorder);
                    result.add(temp);
                }
            }
        }

        return result;
    }
}
