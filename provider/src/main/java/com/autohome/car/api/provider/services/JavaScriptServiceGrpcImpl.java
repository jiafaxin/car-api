package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.javascript.*;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.HtmlUtils;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.SpecStateEnum;
import com.autohome.car.api.data.popauto.entities.BrandSeriesStateBaseEntity;
import com.autohome.car.api.data.popauto.entities.BrandStateEntity;
import com.autohome.car.api.data.popauto.entities.FactoryInfoEntity;
import com.autohome.car.api.data.popauto.entities.SpecYearEntity;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.PictureService;
import com.autohome.car.api.services.SeriesService;
import com.autohome.car.api.services.YearService;
import com.autohome.car.api.services.basic.BrandBaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesSpecService;
import com.autohome.car.api.services.basic.specs.SpecSYearService;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@DubboService
@RestController
public class JavaScriptServiceGrpcImpl extends DubboJavaScriptServiceTriple.JavaScriptServiceImplBase {

    @Autowired
    SeriesSpecService seriesSpecService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SpecBaseService specBaseService;

    @Resource
    private SpecSYearService specSYearService;

    @Autowired
    private YearService yearService;

    @Autowired
    AutoCacheService autoCacheService;

    @Autowired
    SeriesService seriesService;

    @Autowired
    PictureService pictureService;

    @Resource
    private BrandBaseService brandBaseService;

    @Override
    @GetMapping("/v1/javascript/specbyseries.ashx")
    public SpecBySeriesResponse specBySeries(SpecBySeriesRequest request) {

        List<Integer> specIds = seriesSpecService.getSpecIds(request.getSeriesid());
        List<SpecBaseInfo> specs = specBaseService.getList(specIds);

        switch (request.getState().toLowerCase()) {
            case "0x0001":
                specs = specs.stream().filter(x -> x.getSpecState() == 0).collect(Collectors.toList());
                break;
            case "0x0002":
                specs = specs.stream().filter(x -> x.getSpecState() == 10).collect(Collectors.toList());
                break;
            case "0x0004":
                specs = specs.stream().filter(x -> x.getSpecState() == 20).collect(Collectors.toList());
                break;
            case "0x0008":
                specs = specs.stream().filter(x -> x.getSpecState() == 30).collect(Collectors.toList());
                break;
            case "0x0010":
                specs = specs.stream().filter(x -> x.getSpecState() == 40).collect(Collectors.toList());
                break;
            case "0x0003":
                specs = specs.stream().filter(x -> x.getSpecState() <= 10).collect(Collectors.toList());
                break;
            case "0x000c":
                specs = specs.stream().filter(x -> x.getSpecState() >= 20 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case "0x000e":
                specs = specs.stream().filter(x -> x.getSpecState() >= 10 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case "0x001c":
                specs = specs.stream().filter(x -> x.getSpecState() >= 20).collect(Collectors.toList());
                break;
            case "0x000f":
                specs = specs.stream().filter(x -> x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case "0x001e":
                specs = specs.stream().filter(x -> x.getSpecState() >= 10).collect(Collectors.toList());
                break;
        }

        if (request.getIsFilterSpecImage() == 1) {
            specs = specs.stream().filter(x -> x.getSpecIsImage() == 0).collect(Collectors.toList());
        }

        SpecBySeriesResponse.Result.Builder resultBuilder = SpecBySeriesResponse.Result.newBuilder();

        specs.sort(Comparator.comparing(SpecBaseInfo::getSpecOrder, Comparator.reverseOrder()).thenComparing(SpecBaseInfo::getSpecOrdercls).thenComparing(SpecBaseInfo::getId));

        for (SpecBaseInfo spec : specs) {

            resultBuilder.addSpecitems(
                    SpecBySeriesResponse.Result.Specitem.newBuilder()
                            .setId(spec.getId())
                            .setName(spec.getSpecName())
                            .setState(spec.getSpecState())
                            .setMinprice(spec.getSpecMinPrice())
                            .setMaxprice(spec.getSpecMaxPrice())
            );
        }


        return SpecBySeriesResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder)
                .build();
    }

    @Override
    @GetMapping("/v1/javascript/specbysyear.ashx")
    public SpecBySYearResponse specBySYear(SpecBySYearRequest request) {
        SpecBySYearResponse.Builder builder = SpecBySYearResponse.newBuilder();
        int yearId = request.getYearid();
        String state = request.getState();
        if (yearId == 0 || StringUtils.isBlank(state)) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        boolean flag = true;
        List<SpecYearEntity> yearEntityList = specSYearService.get(yearId);
        switch (state.toLowerCase()) {
            case "0x0001":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() == 0).collect(Collectors.toList());
                break;
            case "0x0002":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() == 10).collect(Collectors.toList());
                break;
            case "0x0004":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() == 20).collect(Collectors.toList());
                break;
            case "0x0008":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() == 30).collect(Collectors.toList());
                break;
            case "0x0010":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() == 40).collect(Collectors.toList());
                break;
            case "0x0003":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() <= 10).collect(Collectors.toList());
                break;
            case "0x000c":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() >= 20 && x.getState() <= 30).collect(Collectors.toList());
                break;
            case "0x000e":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() >= 10 && x.getState() <= 30).collect(Collectors.toList());
                break;
            case "0x001c":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() >= 20).collect(Collectors.toList());
                break;
            case "0x000f":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() <= 30).collect(Collectors.toList());
                break;
            case "0x001e":
                yearEntityList = yearEntityList.stream().filter(x -> x.getState() >= 10).collect(Collectors.toList());
                break;
            case "0x001f":
                break;
            default:
                flag = false;

        }
        if (!flag) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        if (request.getIsFilterSpecImage() == 1) {
            yearEntityList = yearEntityList.stream().filter(x -> x.getSImage() == 0).collect(Collectors.toList());
        }
        SpecBySYearResponse.Result.Builder result = SpecBySYearResponse.Result.newBuilder();
        if (CollectionUtils.isNotEmpty(yearEntityList)) {
            List<Integer> specIds = yearEntityList.stream().sorted(Comparator.comparing(SpecYearEntity::getRInd)).map(SpecYearEntity::getId).collect(Collectors.toList());
            Map<Integer, SpecBaseInfo> baseInfoMap = specBaseService.getMap(specIds);
            for (Integer specId : specIds) {
                if (baseInfoMap != null && baseInfoMap.containsKey(specId)) {
                    SpecBaseInfo spec = baseInfoMap.get(specId);
                    if (Objects.isNull(spec)) {
                        continue;
                    }
                    result.addSpecitems(SpecBySYearResponse.Result.Specitem.newBuilder()
                            .setId(spec.getId())
                            .setName(spec.getSpecName())
                            .setState(spec.getSpecState())
                            .setMinprice(spec.getSpecMinPrice())
                            .setMaxprice(spec.getSpecMaxPrice())
                    );
                }
            }
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }
    @Override
    @GetMapping("/v1/javascript/syearandspecbyseries.ashx")
    public SyearAndSpecBySeriesResponse syearAndSpecBySeries(SyearAndSpecBySeriesRequest request) {
        return yearService.syearAndSpecBySeries(request);
    }

    @Override
    @GetMapping({"/v2/javascript/brand.ashx", "//v2/javascript/brand.ashx"})
    public BrandListByConditionResponse brandListByCondition(BrandListByConditionRequest request) {
        BrandListByConditionResponse.Builder resultBuilder = BrandListByConditionResponse.newBuilder();
        BrandListByConditionResponse.Result.Builder result = BrandListByConditionResponse.Result.newBuilder();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if(state == SpecStateEnum.NONE){
            return resultBuilder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        int typeId = request.getTypeid();
        int isFilterImg = request.getIsFilterSpecImage();
        resultBuilder.setReturnCode(0).setReturnMsg("成功");
        List<BrandSeriesStateBaseEntity> data = autoCacheService.getAllSeriesBrands();
        List<BrandListByConditionResponse.Result.BrandItem> brandList = new ArrayList<>();
        if(CollectionUtils.isEmpty(data)){
            result.addAllBranditems(brandList);
            return resultBuilder.setResult(result).build();
        }

        if(typeId > 0 && typeId <= 2){
            data = data.stream().filter(x -> x.getIsCV() == typeId).collect(Collectors.toList());
        }
        if(isFilterImg == 1){
            data = data.stream().filter(x -> x.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        switch (state) {
            case NO_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 0).collect(Collectors.toList());
                break;
            case WAIT_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 10).collect(Collectors.toList());
                break;
            case SELL:
                data = data.stream().filter(x -> x.getSpecState() == 20).collect(Collectors.toList());
                break;
            case SELL_IN_STOP:
                data = data.stream().filter(x -> x.getSpecState() == 30).collect(Collectors.toList());
                break;
            case STOP_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 40).collect(Collectors.toList());
                break;
            case SELL_3:
                data = data.stream().filter(x -> x.getSpecState() <= 10).collect(Collectors.toList());
                break;
            case SELL_12:
                data = data.stream().filter(x -> x.getSpecState() >= 20 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                data = data.stream().filter(x -> x.getSpecState() >= 10 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_28:
                data = data.stream().filter(x -> x.getSpecState() >= 20).collect(Collectors.toList());
                break;
            case SELL_15:
                data = data.stream().filter(x -> x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_30:
                data = data.stream().filter(x -> x.getSpecState() >= 10).collect(Collectors.toList());
                break;
            case SELL_31:
                break;
            default:
                data = new ArrayList<>();
                break;
        }
        if(CollectionUtils.isEmpty(data)){
            result.addAllBranditems(brandList);
            return resultBuilder.setResult(result).build();
        }
        for (BrandSeriesStateBaseEntity brand : data) {
            brandList.add(BrandListByConditionResponse.Result.BrandItem.newBuilder()
                    .setId(brand.getBrandId())
                    .setName(HtmlUtils.decode(brand.getBrandName()))
                    .setBfirstletter(brand.getBFirstLetter() == null ? "" : brand.getBFirstLetter())
                    .setLogo(ImageUtil.getFullImagePath(brand.getLogo())).build()
            );
        }
        brandList = brandList.stream().distinct().collect(Collectors.toList());
        result.addAllBranditems(brandList);
        return resultBuilder.setResult(result).build();
    }

    @Override
    @GetMapping("/v1/javascript/seriesbyfactory.ashx")
    public SeriesByFactoryResponse seriesByFactory(SeriesByFactoryRequest request){
        return seriesService.seriesByFactory(request);
    }

    @Override
    @GetMapping("/v1/www/Index_Slidepic.ashx")
    public IndexSlidePicResponse indexSlidePic(IndexSlidePicRequest request){
        return pictureService.indexSlidePic(request);
    }

    @Override
    @GetMapping("/v1/javascript/factory.ashx")
    public FactoryByConditionResponse factoryByCondition(FactoryByConditionRequest request) {
        FactoryByConditionResponse.Builder resultBuilder = FactoryByConditionResponse.newBuilder();
        FactoryByConditionResponse.Result.Builder result = FactoryByConditionResponse.Result.newBuilder();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if(state == SpecStateEnum.NONE){
            return resultBuilder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        int typeId = request.getTypeid();
        int isFilterImg = request.getIsFilterSpecImage();
        resultBuilder.setReturnCode(0).setReturnMsg("成功");
        List<FactoryInfoEntity> data = autoCacheService.getAllFactoryInfoSortedLetter();
        List<FactoryByConditionResponse.FactoryItem> fctList = new ArrayList<>();
        if(CollectionUtils.isEmpty(data)){
            result.addAllFactoryitems(fctList);
            return resultBuilder.setResult(result).build();
        }

        if(typeId > 0 && typeId <= 2){
            data = data.stream().filter(x -> x.getIsCV() == typeId).collect(Collectors.toList());
        }
        if(isFilterImg == 1){
            data = data.stream().filter(x -> x.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        switch (state) {
            case NO_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 0).collect(Collectors.toList());
                break;
            case WAIT_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 10).collect(Collectors.toList());
                break;
            case SELL:
                data = data.stream().filter(x -> x.getSpecState() == 20).collect(Collectors.toList());
                break;
            case SELL_IN_STOP:
                data = data.stream().filter(x -> x.getSpecState() == 30).collect(Collectors.toList());
                break;
            case STOP_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 40).collect(Collectors.toList());
                break;
            case SELL_3:
                data = data.stream().filter(x -> x.getSpecState() <= 10).collect(Collectors.toList());
                break;
            case SELL_12:
                data = data.stream().filter(x -> x.getSpecState() >= 20 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                data = data.stream().filter(x -> x.getSpecState() >= 10 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_28:
                data = data.stream().filter(x -> x.getSpecState() >= 20).collect(Collectors.toList());
                break;
            case SELL_15:
                data = data.stream().filter(x -> x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_30:
                data = data.stream().filter(x -> x.getSpecState() >= 10).collect(Collectors.toList());
                break;
            case SELL_31:
                break;
            default:
                data = new ArrayList<>();
                break;
        }
        if(CollectionUtils.isEmpty(data)){
            result.addAllFactoryitems(fctList);
            return resultBuilder.setResult(result).build();
        }
        data = data.stream().sorted(Comparator.comparingInt(FactoryInfoEntity::getRankIndex)).collect(Collectors.toList());
        for (FactoryInfoEntity item : data) {
            fctList.add(FactoryByConditionResponse.FactoryItem.newBuilder()
                    .setId(item.getFactoryId())
                    .setName(null != item.getFactoryName() ? HtmlUtils.decode(item.getFactoryName()) : "")
                    .setFfirstletter(item.getFFirstLetter())
                    .build()
            );
        }
        fctList = fctList.stream().distinct().collect(Collectors.toList());
        result.addAllFactoryitems(fctList);
        return resultBuilder.setResult(result).build();
    }

    /**
     * 品牌列表、根据首字母和品牌热度排序
     * 和/v2/javascript/brand.ashx 有的sql不一样，不能共用
     * @param request
     * @return
     */
    @GetMapping("/v1/Mweb/Brand_RankList.ashx")
    @Override
    public BrandListByConditionResponse brandRankList(BrandListByConditionRequest request) {
        BrandListByConditionResponse.Builder resultBuilder = BrandListByConditionResponse.newBuilder();
        BrandListByConditionResponse.Result.Builder result = BrandListByConditionResponse.Result.newBuilder();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if(state == SpecStateEnum.NONE){
            return resultBuilder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        int typeId = request.getTypeid() > 2 ? 0 : request.getTypeid();
        int isFilterImg = request.getIsFilterSpecImage();
        resultBuilder.setReturnCode(0).setReturnMsg("成功");
        List<BrandStateEntity> data = autoCacheService.getBrandBaseAll();
        List<BrandListByConditionResponse.Result.BrandItem> brandList = new ArrayList<>();
        if(CollectionUtils.isEmpty(data)){
            result.addAllBranditems(brandList);
            return resultBuilder.setResult(result).build();
        }

        if(typeId > 0){
            data = data.stream().filter(x -> x.getIsCV() == typeId).collect(Collectors.toList());
        }
        if(isFilterImg == 1){
            data = data.stream().filter(x -> x.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        switch (state) {
            case NO_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 0).collect(Collectors.toList());
                break;
            case WAIT_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 10).collect(Collectors.toList());
                break;
            case SELL:
                data = data.stream().filter(x -> x.getSpecState() == 20).collect(Collectors.toList());
                break;
            case SELL_IN_STOP:
                data = data.stream().filter(x -> x.getSpecState() == 30).collect(Collectors.toList());
                break;
            case STOP_SELL:
                data = data.stream().filter(x -> x.getSpecState() == 40).collect(Collectors.toList());
                break;
            case SELL_3:
                data = data.stream().filter(x -> x.getSpecState() <= 10).collect(Collectors.toList());
                break;
            case SELL_12:
                data = data.stream().filter(x -> x.getSpecState() >= 20 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                data = data.stream().filter(x -> x.getSpecState() >= 10 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_28:
                data = data.stream().filter(x -> x.getSpecState() >= 20).collect(Collectors.toList());
                break;
            case SELL_15:
                data = data.stream().filter(x -> x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_30:
                data = data.stream().filter(x -> x.getSpecState() >= 10).collect(Collectors.toList());
                break;
            case SELL_31:
                break;
            default:
                data = new ArrayList<>();
                break;
        }
        if(CollectionUtils.isEmpty(data)){
            result.addAllBranditems(brandList);
            return resultBuilder.setResult(result).build();
        }
        //排序分组
        LinkedHashMap<Integer, ArrayList<BrandStateEntity>> linkedHashMap = data.stream().sorted(Comparator.comparing(BrandStateEntity::getRankIndex)).collect(Collectors.groupingBy(BrandStateEntity::getBrandId,
                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

        List<Integer> brandIds = data.stream().map(BrandStateEntity::getBrandId).distinct().collect(Collectors.toList());
        Map<Integer, BrandBaseInfo> brandBaseInfoMap = brandBaseService.getMap(brandIds);

        for (Map.Entry<Integer, ArrayList<BrandStateEntity>> brandMap : linkedHashMap.entrySet()) {
            int brandId = brandMap.getKey();
            BrandBaseInfo brandBaseInfo = brandBaseInfoMap.get(brandId);
            brandList.add(BrandListByConditionResponse.Result.BrandItem.newBuilder()
                    .setId(brandId)
                    .setName((null != brandBaseInfo && null != brandBaseInfo.getName()) ?
                            HtmlUtils.decode(brandBaseInfo.getName()) :"")
                    .setBfirstletter(null != brandMap.getValue().get(0).getBFirstLetter() ? brandMap.getValue().get(0).getBFirstLetter() : "")
                    .setLogo((null != brandBaseInfo && null != brandBaseInfo.getLogo()) ?
                            ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "").build()
            );
        }
        result.addAllBranditems(brandList);
        return resultBuilder.setResult(result).build();
    }
}