package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.new_energy.*;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.ElectricSpecViewMapper;
import com.autohome.car.api.data.popauto.entities.ElectricSpecEntity;
import com.autohome.car.api.data.popauto.entities.ElectricSummaryEntity;
import com.autohome.car.api.data.popauto.querys.SearchParams;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.solr.SearchSeriesService;
import com.autohome.car.api.services.models.SeriesConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@DubboService
@RestController
public class NewEnergyServiceGrpcImpl extends DubboNewEnergyServiceTriple.NewEnergyServiceImplBase {

    @Autowired
    ElectricSpecViewMapper electricSpecViewMapper;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SearchSeriesService searchSeriesService;

    @Autowired
    SeriesConfigService seriesConfigService;

    @Override
    @GetMapping("/NewEnergy/dingzhi_Series_SearchSeriesByPriceLevelKMFueltype.ashx")
    public DingzhiSeriesSearchSeriesByPriceLevelKMFueltypeResponse dingzhiSeriesSearchSeriesByPriceLevelKMFueltype(DingzhiSeriesSearchSeriesByPriceLevelKMFueltypeRequest request) {
        DingzhiSeriesSearchSeriesByPriceLevelKMFueltypeResponse.Builder response = DingzhiSeriesSearchSeriesByPriceLevelKMFueltypeResponse.newBuilder();
        DingzhiSeriesSearchSeriesByPriceLevelKMFueltypeResponse.Result.Builder result = DingzhiSeriesSearchSeriesByPriceLevelKMFueltypeResponse.Result.newBuilder();

        String level = request.getLevel();
        String priceRegion = request.getPrice();
        ;
        String kmRegion = request.getKm();
        int fueltype = request.getFueltype();
        int topnum = request.getTopnum() == 0 ? 7 : request.getTopnum();
        if (StringUtils.isBlank(level) && StringUtils.isBlank(priceRegion) && StringUtils.isBlank(kmRegion) && fueltype <= 0) {
            return response.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }

        int minPrice=0, maxPrice = 0;
        if (priceRegion != "") {
            String[] ps = priceRegion.split("_");
            if (ps.length == 2) {
                minPrice = Integer.parseInt(priceRegion.split("_")[0]);
                maxPrice = Integer.parseInt(priceRegion.split("_")[1]);
                maxPrice = maxPrice == 0 ? 99999999 : maxPrice;
            }
        }

        int minKm=0, maxKm = 0;
        if (StringUtils.isNotBlank(kmRegion)) {
            String[] kms = kmRegion.split("_");
            minKm = Integer.parseInt(kms[0]);
            maxKm = Integer.parseInt(kms[1]);
        }

        List<KeyValueDto<Integer, Double>> list = electricSpecViewMapper.seriesSearchSeriesByPriceLevelKMFueltype(topnum, level, fueltype, minPrice, maxPrice, minKm, maxKm);
        if(list!=null && list.size() >0 ) {
            List<SeriesBaseInfo> seriesList = seriesBaseService.getList(list.stream().map(x -> x.getKey()).collect(Collectors.toList()));
            if(seriesList!=null && seriesList.size()>0) {
                for (SeriesBaseInfo series : seriesList) {
                    result.addSerieslist(
                            DingzhiSeriesSearchSeriesByPriceLevelKMFueltypeResponse.Result.Serieslist.newBuilder()
                                    .setMinprice(series.getSeriesPriceMin())
                                    .setMaxprice(series.getSeriesPriceMax())
                                    .setSeriesid(series.getId())
                                    .setSeriesname(series.getName())
                                    .setSeriesimg(ImageUtil.getFullImagePath(series.getLogo()))
                    );
                }
            }
        }
        return response.setResult(result).build();
    }

    @Override
    @GetMapping("/NewEnergy/Dingzhi_SeriesRankForElectric.ashx")
    public DingzhiSeriesRankForElectricResponse dingzhiSeriesRankForElectric(DingzhiSeriesRankForElectricRequest request) {
        DingzhiSeriesRankForElectricResponse.Builder response = DingzhiSeriesRankForElectricResponse.newBuilder();
        response.setReturnCode(0).setReturnMsg("成功");
        DingzhiSeriesRankForElectricResponse.Result.Builder result = DingzhiSeriesRankForElectricResponse.Result.newBuilder();
        List<ElectricSpecEntity> list = electricSpecViewMapper.getSeriesRand_ElectricZengCheng(request.getTypeid());
        Map<Integer,List<ElectricSpecEntity>> map = list.stream().collect(Collectors.groupingBy(x->x.getSeriesid()));
        List<List<ElectricSpecEntity>> ll = map.values().stream().filter(x->x.size() > 0).limit(8).collect(Collectors.toList());
        if(ll==null||ll.size()==0){
            return response.setResult(result).build();
        }
        List<Integer> seriesIds = list.stream().map(x->x.getSeriesid()).distinct().limit(8).collect(Collectors.toList());
        Map<Integer,SeriesBaseInfo> seriesList = seriesBaseService.getMap(seriesIds);

        seriesIds.forEach(seriesId->{
            List<ElectricSpecEntity> items = map.get(seriesId);
            if(items==null||items.size()==0)
                return;

            ElectricSpecEntity f = items.get(0);
            SeriesBaseInfo series = seriesList.get(f.getSeriesid());
            if(series==null) return;

            List<Integer> ms = items.stream().map(x->x.getMileage()).collect(Collectors.toList());

            result.addSerieslist(
                    DingzhiSeriesRankForElectricResponse.Result.Serieslist.newBuilder()
                            .setSeriesimg(ImageUtil.getFullImagePath(series.getLogo()))
                            .setSeriesname(series.getName())
                            .setZhengchezhibao(f.getZhengchezhibao())
                            .setMinprice(series.getSeriesPriceMin())
                            .setMaxprice(series.getSeriesPriceMax())
                            .addAllElectricmotormileage(ms)
                            .setSeriesrank(f.getSeriesrank())
                            .setDianchileixing(f.getDianchileixing())
                            .setElectricchargetime(f.getChongdianshijian())
                            .setSeriesid(f.getSeriesid())
                            .setOfficialFastChargetime(f.getOfficialFastChargetime())
                            .setOfficialSlowChargetime(f.getOfficialSlowChargetime())
            );
        });
        return response.setReturnCode(0).setReturnMsg("成功").setResult(result).build();
    }

    @GetMapping("/NewEnergy/Dingzhi_SNew/SeriesAndSpecNum.ashx")
    @Override
    public DingzhiSeriesSpecNumResponse dingzhiSeriesSpecNum(DingzhiSeriesSpecNumRequest request) {
        DingzhiSeriesSpecNumResponse.Builder response = DingzhiSeriesSpecNumResponse.newBuilder();
        DingzhiSeriesSpecNumResponse.Result.Builder result = DingzhiSeriesSpecNumResponse.Result.newBuilder();

        SearchParams params = new SearchParams();
        params.setLevel(request.getLevel());
        params.setPrice(request.getPrice());
        params.setBrand(request.getBrand());
        params.setStruct(request.getStruct());
        params.setDcap(request.getDcap());
        params.setGearbox(request.getGearbox());
        params.setCountry(request.getCountry());
        params.setIsImport(request.getIsimport());
        params.setSeats(request.getSeat());
        params.setEnergytype(request.getEnergytype());
        params.setFlowmode(request.getFlowmode());
        params.setDrivetype(request.getDrivetype());
        params.setConfig(request.getConfig());
        params.setMileage(request.getMileage());

        Pair<Integer, Integer> nums = searchSeriesService.getSeriesSpecNum(params);
        result.setSpecnum(nums.getKey());
        result.setSeriesnum(nums.getValue());
        return response.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/NewEnergy/Dingzhi_SNew/SeriesResult.ashx")
    @Override
    public DingzhiSeriesResultResponse dingzhiSeriesResult(DingzhiSeriesResultRequest request){
        SearchParams params = new SearchParams();
        params.setLevel(request.getLevel());
        params.setPrice(request.getPrice());
        params.setBrand(request.getBrand());
        params.setStruct(request.getStruct());
        params.setDcap(request.getDcap());
        params.setGearbox(request.getGearbox());
        params.setCountry(request.getCountry());
        params.setIsImport(request.getIsimport());
        params.setSeats(request.getSeat());
        params.setEnergytype(request.getEnergytype());
        params.setFlowmode(request.getFlowmode());
        params.setDrivetype(request.getDrivetype());
        params.setConfig(request.getConfig());
        params.setMileage(request.getMileage());
        if(Strings.isNotBlank(request.getSorttype())){
            params.setSorttype(Integer.parseInt(request.getSorttype()));
        }else{
            params.setSorttype(0);
        }
        if(Strings.isNotBlank(request.getSeriesid()) && !request.getSeriesid().equals("0")){
            params.setSeriesid(request.getSeriesid());
        }
        params.setPageindex(request.getPageindex() != 0 ? request.getPageindex() : 1);
        params.setPagesize(request.getPagesize() != 0 ? request.getPagesize() : 10);

        return searchSeriesService.getSeriesResult(params);
    }

    @GetMapping("/NewEnergy/Dingzhi_SNew/SpecsResult.ashx")
    @Override
    public DingzhiSpecResultResponse dingzhiSpecResult(DingzhiSpecResultRequest request){
        if(Strings.isBlank(request.getSeriesid())){
            return DingzhiSpecResultResponse.newBuilder().setReturnMsg("成功").build();
        }
        SearchParams params = new SearchParams();
        params.setLevel(request.getLevel());
        params.setPrice(request.getPrice());
        params.setBrand(request.getBrand());
        params.setStruct(request.getStruct());
        params.setDcap(request.getDcap());
        params.setGearbox(request.getGearbox());
        params.setCountry(request.getCountry());
        params.setIsImport(request.getIsimport());
        params.setSeats(request.getSeat());
        params.setEnergytype(request.getEnergytype());
        params.setFlowmode(request.getFlowmode());
        params.setDrivetype(request.getDrivetype());
        params.setConfig(request.getConfig());
        params.setMileage(request.getMileage());
        if(Strings.isNotBlank(request.getSorttype())){
            params.setSorttype(Integer.parseInt(request.getSorttype()));
        }else{
            params.setSorttype(0);
        }
        if(Strings.isNotBlank(request.getSeriesid()) && !request.getSeriesid().equals("0")){
            params.setSeriesid(request.getSeriesid());
        }

        return searchSeriesService.getSpecResult(params);
    }

    @GetMapping("/NewEnergy/Series_SeriesRankByMileage.ashx")
    @Override
    public DingzhiSeriesRankByMileageResponse dingzhiSeriesRankByMileage(DingzhiSeriesRankByMileageRequest request){
        DingzhiSeriesRankByMileageResponse.Builder builder = DingzhiSeriesRankByMileageResponse.newBuilder();
        builder.setReturnCode(0).setReturnMsg("成功");
        String fuelType = Strings.isBlank(request.getFueltype()) || request.getFueltype().equals("0") ? "4,5,6" : request.getFueltype();
        List<ElectricSummaryEntity> elecList = electricSpecViewMapper.getElectricSummary(fuelType);
        if(CollectionUtils.isEmpty(elecList)){
            return builder.build();
        }

        Map<Integer, List<ElectricSummaryEntity>> orderedData = new LinkedHashMap<>();
        for(ElectricSummaryEntity dr : elecList){
            if(orderedData.containsKey(dr.getSeriesId())){
                orderedData.get(dr.getSeriesId()).add(dr);
            }else{
                if(orderedData.size() == 50){
                    continue;
                }
                List<ElectricSummaryEntity> value = new ArrayList<>();
                value.add(dr);
                orderedData.put(dr.getSeriesId(), value);
            }
        }

        List<Integer> seriesIds =  new ArrayList<>(orderedData.keySet());
        Map<Integer, SeriesConfig> seriesMap = seriesConfigService.getMap(seriesIds);
        List<DingzhiSeriesRankByMileageResponse.Serieslist> list = new ArrayList<>();
        for(Map.Entry<Integer, List<ElectricSummaryEntity>> item : orderedData.entrySet()){
            DingzhiSeriesRankByMileageResponse.Serieslist.Builder row = DingzhiSeriesRankByMileageResponse.Serieslist.newBuilder();
            int seriesId = item.getKey();
            List<ElectricSummaryEntity> itemList = item.getValue();
            int minprice = itemList.stream()
                    .mapToInt(ElectricSummaryEntity::getSpecPrice)
                    .min().orElse(0);
            int maxprice = itemList.stream()
                    .mapToInt(ElectricSummaryEntity::getSpecPrice)
                    .max().orElse(0);
            int milage = itemList.stream()
                    .mapToInt(ElectricSummaryEntity::getEndurancemileage)
                    .max().orElse(0);
            SeriesConfig series = seriesMap != null ? seriesMap.getOrDefault(seriesId, null) : null;
            list.add(row
                    .setSeriesid(seriesId)
                    .setSeriesname(series != null ? series.getName() : "")
                    .setLevelname(series != null ? series.getLevelname() : "")
                    .setMaxprice(String.valueOf(maxprice))
                    .setMinprice(String.valueOf(minprice))
                    .setMileage(String.valueOf(milage))
                    .setPnglogo(series != null ? series.getPnglogo() : "")
                    .setLogo(series != null ? series.getLogo() : "")
                    .build());
        }
        list.sort((p1, p2) -> Integer.parseInt(p2.getMileage()) - Integer.parseInt(p1.getMileage()));
        builder.addAllResult(list);
        return builder.build();
    }
}