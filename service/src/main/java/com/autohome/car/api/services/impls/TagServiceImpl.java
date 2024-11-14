package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.app.AutoTagCarListAutoHomeRequest;
import autohome.rpc.car.car_api.v1.app.AutoTagCarListAutoHomeResponse;
import autohome.rpc.car.car_api.v1.app.AutoTagCarListPriceRequest;
import autohome.rpc.car.car_api.v1.app.AutoTagCarListPriceResponse;
import com.autohome.car.api.common.PriceUtils;
import com.autohome.car.api.data.popauto.AutoTagMapper;
import com.autohome.car.api.data.popauto.entities.AutoTagCarEntity;
import com.autohome.car.api.services.TagService;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.series.SeriesKb;
import com.autohome.car.api.services.models.SeriesConfig;
import com.autohome.car.api.services.models.SeriesSortDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    AutoTagMapper autoTagMapper;

    @Autowired
    SeriesKb seriesKb;

    @Autowired
    SeriesConfigService seriesConfigService;
    List<Integer> listTagids = Arrays.asList(13, 14, 16, 18, 19, 21, 22, 25, 28, 30, 36, 37, 38, 39, 40, 42, 43, 45, 47, 48, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 74, 75, 76, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 103, 104, 105, 106, 107, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126 );

    /// <summary>
    /// 功率换算成马力
    /// </summary>
    /// <param name="kw">功率</param>
    /// <returns></returns>
    public static int calcPowerByKw(Double kw){
        return new Double (kw * 1.36).intValue();
    }

    /// <summary>
    /// 车型排量
    /// </summary>
    /// <param name="dc">排量</param>
    /// <param name="fuelType">燃料类型</param>
    /// <returns></returns>
    public static String getDeliveryCapacityName(String dc, int fuelType) {
        if (new BigDecimal(dc).compareTo(BigDecimal.ZERO) == 0) {
            return fuelType == 4 ? "电动" : "";
        } else {
            return dc + "升";
        }
    }

    public AutoTagCarListAutoHomeResponse autoTagCarListAutoHome(AutoTagCarListAutoHomeRequest request){
        AutoTagCarListAutoHomeResponse.Builder response = AutoTagCarListAutoHomeResponse.newBuilder();
        AutoTagCarListAutoHomeResponse.Result.Builder resultBuilder = AutoTagCarListAutoHomeResponse.Result.newBuilder();
        response.setReturnCode(0).setReturnMsg("成功");

        Map<Integer,Double> kbs = seriesKb.get(null);

        List<Integer> tagIds = request.getTagidsList();
        int pageindex = request.getPageindex();
        int pagesize  = request.getPagesize();
        pageindex = pagesize < 1 ?1 :pageindex;
        pagesize = pagesize < 1?20:pagesize;
        pagesize = pagesize > 50 ? 50 : pagesize;
        int orderid = request.getOrderid(); //排序id
        List<Integer> Levels =request.getLevelsList();
        List<Integer> country = request.getCountriesList();
        int adSeriesId =request.getAdseriesid();//指定车系id排结果集第一位,如何结果集里有此车系，此车系第一行展示。 用途是七步找车广告投放。

        if (tagIds.size() == 0){
            return response.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        if(tagIds.stream().anyMatch(x-> !listTagids.contains(x))){
            return response.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        List<AutoTagCarEntity> listSpec = autoTagMapper.autoTagCarListAutoHome(tagIds,Levels,country,request.getMinprice(),request.getMaxprice(),request.getOrderid());

        Map<Integer,List<AutoTagCarEntity>> result =listSpec.stream().collect(Collectors.groupingBy(AutoTagCarEntity::getSeriesId));
        List<Integer> seriesIds = listSpec.stream().map(x->x.getSeriesId()).distinct().collect(Collectors.toList());

        if(kbs==null){
         log.error("kbs 为null");
        }
        List<SeriesSortDto> seriesSortDtos = seriesIds.stream().map(x->{
            double kb = kbs == null || !kbs.containsKey(x) ? 0 : kbs.get(x);
            return new SeriesSortDto(){{
                setSeriesId(x);
                setIsAd(adSeriesId == x ? 1 : 0);
                setKb(kb);
            }};
        }).collect(Collectors.toList());
        if (adSeriesId > 0) {
            if (orderid == 3) {
                seriesSortDtos.sort(Comparator.comparing(SeriesSortDto::getKb,Comparator.reverseOrder()).thenComparing(SeriesSortDto::getIsAd,Comparator.reverseOrder()).thenComparing(SeriesSortDto::getSeriesId));
            }
            else{
                seriesSortDtos.sort(Comparator.comparing(SeriesSortDto::getIsAd,Comparator.reverseOrder()).thenComparing(SeriesSortDto::getSeriesId));
            }
        }
        else{
            if (orderid == 3) {
                seriesSortDtos.sort(Comparator.comparing(SeriesSortDto::getKb,Comparator.reverseOrder()).thenComparing(SeriesSortDto::getSeriesId));
            }
        }

        int rowcount = result.size();
        int pagecount = rowcount % pagesize > 0 ? rowcount / pagesize + 1 : rowcount / pagesize;
        seriesIds = seriesSortDtos.stream().map(x->x.getSeriesId()).collect(Collectors.toList());
        seriesIds = seriesIds.stream().skip((pageindex-1)+pagesize+1).limit(pagesize).collect(Collectors.toList());
        List<SeriesConfig> series = seriesConfigService.getList(seriesIds);
        if (rowcount > 0){
            series.forEach(sc->{
                resultBuilder.addSeriesitems(
                        AutoTagCarListAutoHomeResponse.Result.Seriesitem.newBuilder()
                                .setImg(sc.getLogo()==null?"":sc.getLogo())
                                .setPngImg(sc.getPnglogo())
                                .setBrandname(sc.getBrandname())
                                .setPrice(PriceUtils.getStrPrice(sc.getMinprice(),sc.getMaxprice()))
                                .setBrandid(sc.getBrandid())
                                .setLevelid(sc.getLevelid())
                                .setKb(kbs.containsKey(sc.getId())?kbs.get(sc.getId()):0D)
                                .setName(sc.getName())
                                .setSpeccount(sc.getSpecnum())
                                .setId(sc.getId())
                                .setLevelname(sc.getLevelname())
                );
            });
        }

        resultBuilder.setPagecount(pagecount);
        resultBuilder.setSpeccount(listSpec.size());
        resultBuilder.setRowcount(rowcount);
        resultBuilder.setPageindex(pageindex);
        return response.setResult(resultBuilder).build();

    }

    @Override
    public AutoTagCarListPriceResponse autoTagCarListPrice(AutoTagCarListPriceRequest request) {

        AutoTagCarListPriceResponse.Builder response = AutoTagCarListPriceResponse.newBuilder();
        AutoTagCarListPriceResponse.Result.Builder result = AutoTagCarListPriceResponse.Result.newBuilder();

        int MinPrice = request.getMinprice();
        int MaxPrice = request.getMaxprice();
        int orderid = request.getOrderid();

        List<Integer> TagIds = request.getTagidsList();

        if(TagIds.stream().anyMatch(x-> !listTagids.contains(x) || x == 0)){
            return response.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        int pageindex = request.getPageindex();
        pageindex = pageindex<=1?1:pageindex;
        int pagesize = request.getPagesize();
        pagesize = pagesize <=1?20:pagesize;
        pagesize= pagesize > 50 ? 50 : pagesize;
        int rowcount = 0;
        int pagecount = 0;

        List<AutoTagCarEntity> dt = autoTagMapper.autoTagCarList(TagIds,request.getMinprice(),request.getMaxprice(),request.getOrderid());
        Map<Integer,List<AutoTagCarEntity>> map = dt.stream().collect(Collectors.groupingBy(x->x.getSeriesId()));
        List<Integer> seriesIds = dt.stream().map(x->x.getSeriesId()).distinct().collect(Collectors.toList());

        rowcount = seriesIds.size();
        pagecount = rowcount % pagesize > 0 ? rowcount / pagesize + 1 : rowcount / pagesize;

        List<Integer> list = seriesIds.stream().skip((pageindex - 1) * pagesize).limit(pagesize).collect(Collectors.toList());
        List<SeriesConfig> scs = seriesConfigService.getList(list);
        for (SeriesConfig sc : scs) {
            result.addSeriesitems(
                    AutoTagCarListPriceResponse.Result.Seriesitem.newBuilder()
                            .setSeriesimg(sc.getLogo()==null?"":sc.getLogo())
                            .setFctname(sc.getFctname())
                            .setSeriesfctminprice(sc.getMinprice())
                            .setBrandname(sc.getBrandname())
                            .setSeriesid(sc.getId())
                            .setSeriesname(sc.getName())
                            .setSeriesfctmaxprice(sc.getMaxprice())
                            .setBrandid(sc.getBrandid())
                            .setLevelid(sc.getLevelid())
                            .setSpeccount(map.get(sc.getId()).size())
                            .setLevelname(sc.getLevelname())
                            .setFctid(sc.getFctid())
            );
        }
        result.setPageindex(pageindex);
        result.setPagecount(pagecount);
        result.setRowcount(map.size());
        return response.setResult(result).build();
    }
}
