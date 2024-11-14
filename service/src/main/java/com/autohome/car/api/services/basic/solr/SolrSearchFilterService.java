package com.autohome.car.api.services.basic.solr;


import autohome.rpc.car.car_api.v1.car.GetFindCarSeriesInfoByConditionRequest;
import com.autohome.car.api.data.popauto.querys.SearchParams;
import com.autohome.car.api.services.common.CommonFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 车系查找相关方法
 */
@Service
public class SolrSearchFilterService {

    //拼接查询条件
    public List<SimpleQuery> getFilterQueryByCondition(SearchParams sp) {
        List<SimpleQuery> fqList = new ArrayList<>();

        addSingleKey("SeriesId", sp.getSeriesid(), fqList);

        //价格
        if (StringUtils.isNotBlank(sp.getPrice())) {
            StringBuilder fq = new StringBuilder();
            String[] pricesArr = sp.getPrice().split("_");
            if (pricesArr.length % 2 == 0) {
                for (int i = 0; i < (pricesArr.length / 2); i++) {
                    String min = pricesArr[i * 2];
                    String max = pricesArr[(i + 1) * 2 - 1];
                    if (!StringUtils.isNumeric(min) || !StringUtils.isNumeric(max)) {
                        continue;
                    }
                    int minPrice = Integer.parseInt(min) * 10000;
                    int maxPrice = Integer.parseInt(max) * 10000;
                    if (minPrice != 0 && maxPrice != 0) {
                        fq.append(" OR FctMinPrice:[" + minPrice + " TO " + maxPrice + "] OR FctMaxPrice:[" + minPrice + " TO " + maxPrice + "]");
                    } else if (minPrice == 0 && maxPrice != 0) {
                        fq.append(" OR FctMaxPrice:{0 TO " + maxPrice + "]");
                    } else if (minPrice != 0 && maxPrice == 0) {
                        fq.append(" OR FctMinPrice:[" + minPrice + " TO *]");
                    }
                }
                fq.delete(0, 4);
                fqList.add(new SimpleQuery(fq.toString()));
            }
        }

        //级别
        if (StringUtils.isNotBlank(sp.getLevel())) {
            StringBuilder fq = new StringBuilder();
            String[] levelIdArr = sp.getLevel().split("_");
            for (String s : levelIdArr) {
                if(!StringUtils.isNumeric(s)){
                    continue;
                }
                int levelId = Integer.parseInt(s);
                if(levelId == 0){
                    continue;
                }
                if (levelId == 101) {
                    //全部SUV
                    fq.append(" OR LevelId:[16 TO 20]");
                } else if (levelId == 14) {
                    //皮卡
                    fq.append(" OR LevelId:14 OR LevelId:15");
                    //轿车
                } else if (levelId == 8) {//mpv
                    fq.append(" OR LevelId:[21 TO 24]");
                }else {
                    fq.append(" OR LevelId:").append(levelId);
                }
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        //排量
        if (StringUtils.isNotBlank(sp.getDcap())) {
            String[] dcap = sp.getDcap().split("_");
            StringBuilder fq = new StringBuilder();
            if (dcap.length % 2 == 0) {
                for (int i = 0; i < (dcap.length / 2); i++) {
                    String min = dcap[i * 2];
                    String max = dcap[(i + 1) * 2 - 1];
                    double minDCap = Double.parseDouble(min);
                    double maxDCap = Double.parseDouble(max);
                    if (minDCap != 0 && maxDCap != 0) {
                        fq.append(" OR DeliveryCapacity:[" + minDCap + " TO " + maxDCap + "]");
                    } else if (minDCap == 0 && maxDCap != 0) {
                        fq.append(" OR DeliveryCapacity:[0 TO " + maxDCap + "]");
                    } else if (minDCap != 0 && maxDCap == 0) {
                        fq.append(" OR DeliveryCapacity:[" + minDCap + " TO *]");
                    }
                }
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        //车身结构
        addSingleKey("StructId", sp.getStruct(), fqList);

        //变速箱
        if (StringUtils.isNotBlank(sp.getGearbox())) {
            String[] arr = sp.getGearbox().split("_");
            StringBuilder fq = new StringBuilder();
            for (String gearStr : arr) {
                if(!StringUtils.isNumeric(gearStr)){
                    continue;
                }
                int gear = Integer.parseInt(gearStr);
                if(gear == 0){
                    continue;
                }
                if (gear == 101) {
                    fq.append(" OR TransmissionTypeId:{1 TO *]");
                } else {
                    fq.append(" OR TransmissionTypeId:" + gear);
                }
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        //进气
        if (StringUtils.isNotBlank(sp.getFlowmode())) {
            String[] arr = sp.getFlowmode().split("_");
            StringBuilder fq = new StringBuilder();
            for (String flow : arr) {
                if(!StringUtils.isNumeric(flow)){
                    continue;
                }
                int flowNum = Integer.parseInt(flow);
                if(flowNum == 0){
                    continue;
                }
                if (flowNum == 1) {
                    fq.append(" OR FlowMode:" + flowNum);
                } else {
                    fq.append(" OR FlowMode:" + flowNum + " OR FlowMode:4");
                }
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        //国家
        if (StringUtils.isNotBlank(sp.getCountry())) {
            String[] arr = sp.getCountry().split("_");
            StringBuilder fq = new StringBuilder();
            for (String country : arr) {
                if(!StringUtils.isNumeric(country)){
                    continue;
                }
                int countrytype = Integer.parseInt(country);
                if(countrytype == 0){
                    continue;
                }
                //其他国家
                switch (countrytype)
                {
                    case 1001: { fq.append(" OR (!Country:3)"); } break;
                    case 1002: { fq.append(" OR (!Country:2)"); } break;
                    case 1003: { fq.append(" OR (!Country:1)"); } break;
                    case 1004: { fq.append(" OR (!Country:4)"); } break;
                    case 1005: { fq.append(" OR (!Country:5)"); } break;
                    default:
                        fq.append(" OR (Country:" + countrytype + ")");
                        break;
                }
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        //国产进口
        addSingleKey("isimport", sp.getIsImport(), fqList);
        //燃料类型
        addSingleKey("fueltypedetail", sp.getEnergytype(), fqList);
        //品牌
        addSingleKey("BrandId", sp.getBrand(), fqList);

        //配置
        if (StringUtils.isNotBlank(sp.getConfig())) {
            String[] arr = sp.getConfig().split("_");
            StringBuilder fq = new StringBuilder();
            for (String c : arr) {
                if(!StringUtils.isNumeric(c)){
                    continue;
                }
                int cfg = Integer.parseInt(c);
                if(cfg == 0){
                    continue;
                }
                fq.append(" AND c" + cfg + ":" + cfg);
            }
            fq.delete(0, 5);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        //驱动
        addSingleKey("DriveType", sp.getDrivetype(), fqList);

        //座椅数
        if (StringUtils.isNotBlank(sp.getSeats())) {
            String[] arr = sp.getSeats().split("_");
            StringBuilder fq = new StringBuilder();
            for (String value : arr) {
                if(!StringUtils.isNumeric(value)){
                    continue;
                }
                int seatNum = Integer.parseInt(value);
                if(seatNum == 0){
                    continue;
                }
                if (seatNum != 8) {
                    fq.append(" OR Seat:" + value);
                } else {
                    fq.append(" OR Seat:[" + value + " TO *]");
                }
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        //续航里程
        if (StringUtils.isNotBlank(sp.getMileage())) {
            String[] mil = sp.getMileage().split("_");
            StringBuilder fq = new StringBuilder();
            if (mil.length % 2 == 0) {
                for (int i = 0; i < (mil.length / 2); i++) {
                    String min = mil[i * 2];
                    String max = mil[(i + 1) * 2 - 1];
                    if (!StringUtils.isNumeric(min) || !StringUtils.isNumeric(max)) {
                        continue;
                    }
                    int minm = Integer.parseInt(min);
                    int maxm = Integer.parseInt(max);
                    if (minm != 0 && maxm != 0) {
                        fq.append(" OR endurancemileage:[" + minm + " TO " + maxm + "]");
                    } else if (minm == 0 && maxm != 0) {
                        fq.append(" OR endurancemileage:[0 TO " + maxm + "]");
                    } else if (minm != 0 && maxm == 0) {
                        fq.append(" OR endurancemileage:[" + minm + " TO *]");
                    }
                }
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }

        StringBuilder fq = new StringBuilder();
        fq.append("SpecState:[20 TO 30]");
        fqList.add(new SimpleQuery(fq.toString()));

        if(Strings.isBlank(sp.getEnergytype()) || "0".equals(sp.getEnergytype())){
            fq = new StringBuilder();
            fq.append("fueltypedetail:[4 TO 7]");
            fqList.add(new SimpleQuery(fq.toString()));
        }

        return fqList;
    }

    /**
     * 获取排序条件
     */
    public Sort getSortByCondition(SearchParams sp) {
        Sort sort = null;
        switch (sp.getSorttype()) {
            case 1: //关注度
                sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "SeriesRank"));
                break;
            case 2:  //关注度
                sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "SeriesSales"));
                break;
            case 3: //价格从低到高
                sort = Sort.by(Sort.Order.asc("SeriesFctMinPrice"),
                        Sort.Order.desc("syear"),
                        Sort.Order.asc("FctMinPrice")
                );;
                break;
            case 4:  //价格从高到低
                sort = Sort.by(Sort.Order.desc("SeriesFctMinPrice"),
                        Sort.Order.desc("syear"),
                        Sort.Order.desc("FctMinPrice")
                );
                break;
            case 5: //续航降序
                sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "endurancemileage"));
                break;
            case 6: //续航升序
                sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "endurancemileage"));
                break;
            default:
                sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "SeriesRank"));
                break;
        }
        return sort;
    }

    private void addSingleKey(String solrFieldName, String params, List<SimpleQuery> fqList) {
        if (StringUtils.isNotBlank(params)) {
            String[] arr = params.split("_");
            StringBuilder fq = new StringBuilder();
            for (String value : arr) {
                if (!StringUtils.isNumeric(value)) {
                    continue;
                }
                if(Integer.parseInt(value) == 0){
                    continue;
                }
                fq.append(" OR " + solrFieldName + ":" + value);

            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }
    }

    private void addRangeKey(String solrFieldName, String params, List<SimpleQuery> fqList) {
        if (StringUtils.isNotBlank(params)) {
            String[] edmArr = params.split("_");
            StringBuilder fq = new StringBuilder();
            for (String s : edmArr) {
                String[] arr = s.split("_");
                String min = (arr[0]);
                String max = (arr[1]);
                fq.append(" OR " + solrFieldName + ":" + (min.equals("0") ? "{" : "[") + (min) + " TO " + (max.equals("0") ? "*" : max) + "]");
            }
            fq.delete(0, 4);
            fqList.add(new SimpleQuery(fq.toString()));
        }
    }

    /**
     * 组装条件 pc 找车页面条件过滤
     * @param request
     * @param minPrice
     * @param maxPrice
     * @param minDisplacement
     * @param maxDisplacement
     * @param minEndurance
     * @param maxEndurance
     * @return
     */
    public List<SimpleQuery> getFindCarFilterByCondition(GetFindCarSeriesInfoByConditionRequest request,int minPrice,int maxPrice,double minDisplacement,
                                                         double maxDisplacement,int minEndurance,int maxEndurance){
        List<SimpleQuery> fqList = new ArrayList<>();
        //价格
        String priceStr = "";
        if (minPrice != 0 && maxPrice != 0) {
            priceStr = "FctMinPrice:[" + minPrice + " TO " + maxPrice + "] OR FctMaxPrice:[" + minPrice + " TO " + maxPrice + "]";
        } else if (minPrice == 0 && maxPrice != 0) {
            priceStr = "FctMaxPrice:{0 TO " + maxPrice + "]";
        } else if (minPrice != 0 && maxPrice == 0) {
            priceStr = "FctMinPrice:[" + minPrice + " TO *]";
        }
        if(StringUtils.isNotBlank(priceStr)){
            fqList.add(new SimpleQuery(priceStr));
        }

        //级别
        String levelStr = "";
        if(request.getLevel() != 0){
            if (request.getLevel() == 9) {//全部SUV
                levelStr = "LevelId:[16 TO 20]";
            } else if (request.getLevel() == 14) {////皮卡
                levelStr = "LevelId:14 OR LevelId:15";
            } else if(request.getLevel() == 101){//轿车
                levelStr = "LevelId:[1 TO 6]";
            }else if(request.getLevel() == 8){//全部mpv
                levelStr = "LevelId:8 OR LevelId:[21 TO 24]";
            }else{
                levelStr = "LevelId:" + request.getLevel();
            }
            fqList.add(new SimpleQuery(levelStr));
        }

        //国家
        if(request.getCountry() != 0){
            String countryStr = "";
            if(request.getCountry() == 201){//欧系
                countryStr = "Country:2 OR Country:11 OR Country:6 OR Country:7 OR Country:9 OR Country:8";
            }else{
                countryStr = "Country:" + request.getCountry();
            }
            fqList.add(new SimpleQuery(countryStr));
        }
        //品牌id
        if(request.getBrandid() > 0){
            String brandStr = "BrandId:" + request.getBrandid();
            fqList.add(new SimpleQuery(brandStr));
        }
        //结构类型
        if(request.getStruct() != 0){
            String structStr = "";
            if(request.getStruct() == 1){
                structStr = "StructId:1 OR StructId:14";
            }else if(request.getStruct() == 2){
                structStr = "StructId:2 OR StructId:13";
            }else if(request.getStruct() == 4){
                structStr = "StructId:4 OR StructId:15";
            }else if(request.getStruct() == 12){
                structStr = "StructId:12 OR StructId:16";
            }else if(request.getStruct() == 1000){
                structStr = "StructId:[13 TO 16]";
            }else{
                structStr = "StructId:" + request.getStruct();
            }
            fqList.add(new SimpleQuery(structStr));
        }
        //排量
        String displacementStr = "";
        if (minDisplacement != 0 && maxDisplacement != 0) {
            displacementStr = "DeliveryCapacity:[" + minDisplacement + " TO " + maxDisplacement + "]";
        } else if (minDisplacement == 0 && maxDisplacement != 0) {
            displacementStr = "DeliveryCapacity:[0 TO " + maxDisplacement + "]";
        } else if (minDisplacement != 0 && maxDisplacement == 0) {
            displacementStr = "DeliveryCapacity:[" + minDisplacement + " TO *]";
        }
        if(StringUtils.isNotBlank(displacementStr)){
            fqList.add(new SimpleQuery(displacementStr));
        }
        //座位数
        if(request.getSeat() != 0){
            String seatStr = "";
            if(request.getSeat() == 8){
                seatStr = "Seat:[8 TO *]";
            }else{
                seatStr = "Seat:" + request.getSeat();
            }
            fqList.add(new SimpleQuery(seatStr));
        }
        //进气形式
        if(request.getFlowmode() != 0){
            String flowModeStr = "";
            if(request.getFlowmode() == 1){
                flowModeStr = "FlowMode:" + request.getFlowmode();
            }else{
                flowModeStr = "FlowMode:" + request.getFlowmode() + " OR FlowMode:4";
            }
            fqList.add(new SimpleQuery(flowModeStr));
        }
        //能源类型
        if(request.getFueltype() != 0){
            String fuelTypeStr = "";
            if(request.getFueltype() == 1){
                fuelTypeStr = "fueltypedetail:1 OR fueltypedetail:11";
            }else if(request.getFueltype() == 701){
                fuelTypeStr = "fueltypedetail:[4 TO 7]";
            }else if(request.getFueltype() == 801){
                fuelTypeStr = "fueltypedetail:[8 TO 10]";
            }else{
                fuelTypeStr = "fueltypedetail:" + request.getFueltype();
            }
            fqList.add(new SimpleQuery(fuelTypeStr));
        }
        //驱动方式
        if(request.getDrivetype() != 0){
            String driveTypeStr = "DriveType:" + request.getDrivetype();
            fqList.add(new SimpleQuery(driveTypeStr));
        }
        //变速箱
        if(request.getGearbox() != 0){
            String gearboxStr = "";
            if(request.getGearbox() == 101){
                gearboxStr = "TransmissionTypeId:{1 TO *]";
            }else{
                gearboxStr = "TransmissionTypeId:" + request.getGearbox();
            }
            fqList.add(new SimpleQuery(gearboxStr));
        }
        //生产方式
        if(request.getIsimport() != 0){
            String isImportStr = "isimport:" + request.getIsimport();
            fqList.add(new SimpleQuery(isImportStr));
        }
        //配置
        if(StringUtils.isNotBlank(request.getConfig())){
            StringBuilder sb = new StringBuilder();
            String[] configStr = request.getConfig().split(",");
            for(String config : configStr){
                int configInt = CommonFunction.getStringToInt(config, 0);
                if(configInt != 0){
                    sb.append("c" + config +":" + config).append(" AND ");
                }
            }
            sb = sb.delete(sb.length() - 5, sb.length());
            fqList.add(new SimpleQuery(sb.toString()));
        }
        //新能源tab
        if(request.getNewenergytab() == 1){
            fqList.add(new SimpleQuery("fueltypedetail:[4 TO 7]"));
        }
        //车系状态 改成 1 停售   2 在售   3 即将销售 0全部
        if(request.getState() == 2){//在售
            fqList.add(new SimpleQuery("SpecState:[20 TO 30]"));
            fqList.add(new SimpleQuery("SeriesState:[20 TO 30]"));
        }else if(request.getState() == 3){//即将销售
            fqList.add(new SimpleQuery("SpecState:10"));
            fqList.add(new SimpleQuery("SeriesState:10"));
        }else if(request.getState() == 1){//停售
            fqList.add(new SimpleQuery("SpecState:40"));
            fqList.add(new SimpleQuery("SeriesState:40"));
        }
//        else{ // 传错值走这个
//            //fqList.add(new SimpleQuery("SpecState:[20 TO 30]"));
//        }
        //厂商id
        if(request.getFctid() > 0){
            String fctStr = "FctId:" + request.getFctid();
            fqList.add(new SimpleQuery(fctStr));
        }
        //续航
        String enduranceStr = "";
        if (minEndurance != 0 && maxEndurance != 0) {
            enduranceStr = "endurancemileage:[" + minEndurance + " TO " + maxEndurance + "]";
        } else if (minEndurance == 0 && maxEndurance != 0) {
            enduranceStr = "endurancemileage:{0 TO " + maxEndurance + "]";
        } else if (minEndurance != 0 && maxEndurance == 0) {
            enduranceStr = "endurancemileage:[" + minEndurance + " TO *]";
        }
        if(StringUtils.isNotBlank(enduranceStr)){
            fqList.add(new SimpleQuery(enduranceStr));
        }
        return fqList;
    }

    /**
     * pc找车页面排序
     * @param request
     * @return
     */
    public Sort getFindCarSortByCondition(GetFindCarSeriesInfoByConditionRequest request) {
        Sort sort = null;
        //停售排序规则
        if(request.getState() == 1){
            switch (request.getSort()) {
                case 1:
                    sort = Sort.by(Sort.Order.asc("SeriesFctMinPrice"),
                            Sort.Order.desc("syear"),
                            Sort.Order.asc("FctMinPrice"));
                    break;
                case 2:
                    sort = Sort.by(Sort.Order.desc("SeriesFctMaxPrice"),
                            Sort.Order.desc("syear"),
                            Sort.Order.asc("FctMaxPrice"));
                    break;
                default:
                    sort = Sort.by(Sort.Order.asc("SeriesRank"),
                            Sort.Order.desc("syear"));
                    break;
            }
        }else{
            switch (request.getSort()) {
                case 1:
                    sort = Sort.by(Sort.Order.asc("SeriesFctMinPrice"));
                    break;
                case 2:
                    sort = Sort.by(Sort.Order.desc("SeriesFctMaxPrice"));
                    break;
                default:
                    sort = Sort.by(Sort.Order.asc("SeriesRank"));
                    break;
            }
        }
        return sort;
    }

}
