package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.brand.*;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.BrandMapper;
import com.autohome.car.api.data.popauto.FactoryMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.BrandService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.BFSBaseInfo;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.basic.models.FactoryBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.solr.SearchSeriesService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.brand.BrandCorrelateInfo;
import com.autohome.car.api.services.models.brand.BrandInfo;
import com.autohome.car.api.services.models.brand.BrandLogoItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandBaseService brandBaseService;

    @Resource
    private SeriesBrandBaseService seriesBrandBaseService;

    @Resource
    private CommService commService;

    @Autowired
    FactoryMapper factoryMapper;

    @Autowired
    FactoryBaseService factoryBaseService;

    @Autowired
    BrandFactorysBaseService brandFactorysBaseService;
    @Autowired
    BrandMapper brandMapper;

    @Autowired
    AutoCacheService autoCacheService;

    @Autowired
    SeriesBaseService seriesBaseService;

    /**
     * 根据品牌id获取品牌代表图
     *
     * @param request
     * @return
     */
    @Override
    public ApiResult<BrandLogoItem> GetBrandLogoByBrandId(GetBrandLogoByBrandIdRequest request) {
        ApiResult<BrandLogoItem> apiResult = new ApiResult<>();

        BrandLogoItem brandLogoItem = new BrandLogoItem();
        apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode());
        apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
        int brandId = request.getBrandid();
        if (brandId == 0) {
            apiResult.setReturncode(RETURN_MESSAGE_ENUM102.getReturnCode());
            apiResult.setMessage(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return apiResult;
        }
        CompletableFuture<BrandBaseInfo> completableFuture = brandBaseService.get(brandId);
        BrandBaseInfo brandBaseInfo = completableFuture.join();
        brandLogoItem.setBrandid(brandId);
        brandLogoItem.setBrandlogo(null != brandBaseInfo ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
        apiResult.setResult(brandLogoItem);
        return apiResult;
    }

    /**
     * 根据品牌获取关联厂商及车系信息
     *
     * @param request
     * @return
     */
    @Override
    public ApiResult<BrandCorrelateInfo> getBrandCorrelateInfoByBrandId(GetBrandCorrelateInfoByBrandIdRequest request) {
        int brandId = request.getBrandid();
        if (brandId == 0) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<BrandCorrelateInfo.FctAndSeriesInfo> fctList = new ArrayList<>(); //厂商集合
        AtomicInteger sellSpecCount = new AtomicInteger(); //在售车型数量
        List<BFSBaseInfo> baseInfos = seriesBrandBaseService.get(brandId).join();
        if (!CollectionUtils.isEmpty(baseInfos)) {
            //车系
            List<Integer> seriesIds = baseInfos.stream().map(BFSBaseInfo::getSeriesId).distinct().collect(Collectors.toList());
            Map<Integer, SeriesBaseInfo> seriesMap = commService.getSeriesBaseInfo(seriesIds);
            //厂商
            List<Integer> fctIds = baseInfos.stream().map(BFSBaseInfo::getFctId).distinct().collect(Collectors.toList());
            Map<Integer, FactoryBaseInfo> fctMap = commService.getFactoryBaseInfo(fctIds);

            //按照厂商分组 保持原来顺序
            Map<Integer, List<BFSBaseInfo>> bfsMap = baseInfos.stream().collect(Collectors.groupingBy(BFSBaseInfo::getFctId,
                    LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            for (Map.Entry<Integer, List<BFSBaseInfo>> bfsInfoMap : bfsMap.entrySet()) {
                //厂商和车系信息组装
                BrandCorrelateInfo.FctAndSeriesInfo fctAndSeriesInfo = new BrandCorrelateInfo.FctAndSeriesInfo();
                int fctId = bfsInfoMap.getKey();
                fctAndSeriesInfo.setFctid(fctId);
                FactoryBaseInfo factoryBaseInfo = fctMap.get(fctId);
                fctAndSeriesInfo.setFctname(null != factoryBaseInfo ? factoryBaseInfo.getName() : "");
                fctAndSeriesInfo.setFctlogo(null != factoryBaseInfo ? ImageUtil.getFullImagePath(factoryBaseInfo.getLogo()) : "");
                List<BFSBaseInfo> infoEntities = bfsInfoMap.getValue();
                //车系信息组装
                List<BrandCorrelateInfo.SeriesInfo> seriesList = new ArrayList<>(); //车系集合

                infoEntities.forEach(bfsInfo -> {
                    BrandCorrelateInfo.SeriesInfo seriesInfo = new BrandCorrelateInfo.SeriesInfo();
                    seriesInfo.setSeriesid(bfsInfo.getSeriesId());
                    SeriesBaseInfo seriesBaseInfo = seriesMap.get(bfsInfo.getSeriesId());
                    seriesInfo.setSeriesname(null == seriesBaseInfo ? "" : seriesBaseInfo.getName());
                    seriesList.add(seriesInfo);
                    sellSpecCount.addAndGet(bfsInfo.getSsns()); //在售车型数量
                });
                fctAndSeriesInfo.setSeriesitems(seriesList);
                fctAndSeriesInfo.setSellseriescount(seriesList.size());
                //厂商和车系信息
                fctList.add(fctAndSeriesInfo);
            }
        }
        //返回对象赋值
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        BrandCorrelateInfo brandCorrelateInfo = new BrandCorrelateInfo();
        brandCorrelateInfo.setBrandid(brandId);
        brandCorrelateInfo.setBrandname(null != brandBaseInfo ? brandBaseInfo.getName() : "");
        brandCorrelateInfo.setBrandlogo(null != brandBaseInfo ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
        brandCorrelateInfo.setBrandofficialurl(null != brandBaseInfo ? brandBaseInfo.getUrl() : "");
        brandCorrelateInfo.setSellseriescount(CollectionUtils.isEmpty(baseInfos) ? 0 : baseInfos.size());
        brandCorrelateInfo.setFctitems(fctList);
        brandCorrelateInfo.setSellspeccount(sellSpecCount.get());
        return new ApiResult<>(brandCorrelateInfo, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }


    /**
     * 根据品牌id获取品牌model
     *
     * @param request
     * @return
     */
    @Override
    public ApiResult<BrandInfo> GetBrandInfoByBrandId(GetBrandInfoRequest request) {
        int brandId = request.getBrandid();
        if (brandId == 0) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        ApiResult<BrandInfo> result = new ApiResult<>(null, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        if (brandBaseInfo != null) {
            BrandInfo info = new BrandInfo();
            info.setBrandid(brandBaseInfo.getId());
            info.setBrandname(brandBaseInfo.getName());
            info.setBrandlogo(ImageUtil.getFullImagePath(brandBaseInfo.getLogo()));
            info.setCountry(brandBaseInfo.getCountry());
            info.setBrandofficialurl(brandBaseInfo.getUrl());
            info.setBrandfirstletter(brandBaseInfo.getFirstLetter());
            result.setResult(info);
        }
        return result;
    }

    @Override
    public FactoryByBrandResponse getFactoryByBrand(FactoryByBrandRequest request) {
        FactoryByBrandResponse.Builder builder = FactoryByBrandResponse.newBuilder();
        FactoryByBrandResponse.Result.Builder result = FactoryByBrandResponse.Result.newBuilder();
        int brandId = request.getBrandid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        int typeId = request.getTypeid();
        int isFilterSpecImage = StringIntegerUtils.getIntOrDefault(request.getIsFilterSpecImage(), 0);
        typeId = typeId > 2 ? 0 : typeId;
        if (brandId == 0 || state == SpecStateEnum.NONE) {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
//        List<FactoryInfoEntity> list = factoryMapper.getAllFactoryInfos();
        List<FactoryInfoEntity> list = brandFactorysBaseService.get(brandId).join();
        if (org.apache.dubbo.common.utils.CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        boolean flag = false;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            //在售+停售(0X001E)
            case SELL_30:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if (!flag) {
            builder.setResult(result);
            builder.setReturnMsg("成功");
            return builder.build();
        }
        list = list.stream().filter(s -> s.getBrandId() == brandId).collect(Collectors.toList());
        if (typeId > 0) {
            int finalTypeId = typeId;
            list = list.stream().filter(s -> s.getIsCV() == finalTypeId).collect(Collectors.toList());
        }
        if (isFilterSpecImage == 1) {
            list = list.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        list = list.stream().sorted(Comparator.comparing(FactoryInfoEntity::getRankIndex)).collect(Collectors.toList());
        for (FactoryInfoEntity item : list) {
            FactoryBaseInfo fctBase = factoryBaseService.getFactory(item.getFactoryId());
            FactoryByBrandResponse.Factoryitems.Builder fct = FactoryByBrandResponse.Factoryitems.newBuilder();
            fct.setId(item.getFactoryId());
            fct.setName(fctBase == null ? "" : fctBase.getName());
            fct.setFfirstletter(item.getFFirstLetter());
            result.addFactoryitems(fct);
        }
        List<FactoryByBrandResponse.Factoryitems> fcts = result.getFactoryitemsList().stream().distinct().collect(Collectors.toList());
        result.clearFactoryitems();
        fcts.forEach(x -> {
            result.addFactoryitems(x);
        });
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }



    public BrandPriceMenuResponse brandPriceMenu(BrandPriceMenuRequest request) {
        BrandPriceMenuResponse.Builder builder = BrandPriceMenuResponse.newBuilder();

         List<Integer> el = Arrays.asList(134, 180, 171, 157, 177, 115, 170, 188, 131, 126, 136, 186, 137, 127, 138, 125, 135, 153, 92, 176, 121, 64, 178, 159);

        List<BrandPriceMenuEntity> list = brandMapper.getBrandPriceMenus();
        if(list==null||list.size()==0)
            return builder.build();
        Map<Integer,BrandBaseInfo> brands = brandBaseService.getMap(list.stream().map(x->x.getBrandId()).collect(Collectors.toList()));
        Map<String, List<BrandPriceMenuEntity>> map = list.stream().collect(Collectors.groupingBy(BrandPriceMenuEntity::getFirstLetter));

        map.forEach((k, v) -> {
            BrandPriceMenuResponse.Result.Builder result = BrandPriceMenuResponse.Result.newBuilder();
            result.setFirstletter(k);
            for (BrandPriceMenuEntity brand : v) {
                if(el.contains(brand.getBrandId())) continue;
                BrandBaseInfo info = brands.get(brand.getBrandId());
                if(info == null)
                    continue;
                result.addBranditems(
                        BrandPriceMenuResponse.Result.Branditem.newBuilder()
                                .setId(brand.getBrandId())
                                .setName(info.getName())
                                .setOrders(brand.getOrders())
                                .setLogo(ImageUtil.getFullImagePath(info.getLogo()))
                                .setSpeccount(brand.getBrandCount())
                );
            }
            builder.addResult(result);
        });

        return builder.build();
    }

    @Override
    public GetBrandLogoByIdsResponse getBrandLogoByIds(GetBrandLogoByIdsRequest request) {
        GetBrandLogoByIdsResponse.Builder builder = GetBrandLogoByIdsResponse.newBuilder();
        String brandStr = request.getBrandlist();
        List<Integer> brandList = CommonFunction.getListFromStr(brandStr);
//        if (!CommonFunction.check(brandList = CommonFunction.getListFromStr(brandStr))) {
//            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
//        }
        GetBrandLogoByIdsResponse.Result.Builder result = GetBrandLogoByIdsResponse.Result.newBuilder();

        Map<Integer, BrandBaseInfo> map = brandBaseService.getMap(brandList);
        for (Integer brandId : brandList) {
            BrandBaseInfo brandBaseInfo = map.get(brandId);
            if (Objects.isNull(brandBaseInfo)) {
                brandBaseInfo = new BrandBaseInfo();
                brandBaseInfo.setId(brandId);
            }
            result.addBranditems(GetBrandLogoByIdsResponse.Result.Branditems.newBuilder()
                    .setId(brandBaseInfo.getId())
                    .setLogo(StringUtils.defaultString(ImageUtil.getFullImagePathNew(brandBaseInfo.getLogo(), true)))
                    .setName(StringUtils.defaultString(brandBaseInfo.getName()))
                    .build());
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetAllBrandNameResponse getAllBrandName(GetAllBrandNameRequest request){
        GetAllBrandNameResponse.Builder builder = GetAllBrandNameResponse.newBuilder();
        GetAllBrandNameResponse.Result.Builder result = GetAllBrandNameResponse.Result.newBuilder();
        List<BrandBaseEntity> list = autoCacheService.getAllBrandName();
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(x ->{
                GetAllBrandNameResponse.Branditems.Builder item = GetAllBrandNameResponse.Branditems.newBuilder();
                item.setId(x.getId());
                item.setName(x.getName());
                result.addBranditems(item);
            });
            result.setTotal(list.size());
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public BrandInfoBySeriesIdResponse brandInfoBySeriesId(BrandInfoBySeriesIdRequest request){
        BrandInfoBySeriesIdResponse.Builder builder = BrandInfoBySeriesIdResponse.newBuilder();
        BrandInfoBySeriesIdResponse.Result.Builder result = BrandInfoBySeriesIdResponse.Result.newBuilder();
        SeriesBaseInfo seriesBase = seriesBaseService.get(request.getSeriesid()).join();
        BrandInfoBySeriesIdResponse.Item.Builder item = BrandInfoBySeriesIdResponse.Item.newBuilder();
        if(seriesBase!=null){
            BrandBaseInfo brandBase = brandBaseService.get(seriesBase.getBrandId()).join();
            if(brandBase != null){
                item.setBrandid(brandBase.getId());
                item.setBrandfirstletter(brandBase.getFirstLetter());
                item.setCountry(brandBase.getCountry());
                item.setBrandname(brandBase.getName());
                item.setBrandofficialurl(brandBase.getUrl());
                item.setBrandlogo(ImageUtil.getFullImagePath(brandBase.getLogo()));
            }
            else {
                item.setBrandfirstletter("");
                item.setCountry("");
                item.setBrandname("");
                item.setBrandofficialurl("");
                item.setBrandlogo("");
            }
        }
        result.setItem(item);
        result.setSeriesid(request.getSeriesid());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public BrandByStateAndTypeResponse brandByStateAndType(BrandByStateAndTypeRequest request){
        BrandByStateAndTypeResponse.Builder builder = BrandByStateAndTypeResponse.newBuilder();
        BrandByStateAndTypeResponse.Result.Builder result = BrandByStateAndTypeResponse.Result.newBuilder();

        SpecStateEnum state = Spec.getSpecState(request.getState());
        int typeId = request.getTypeid();
        int isFilterSpecImage = StringIntegerUtils.getIntOrDefault(request.getIsFilterSpecImage(),0);
        typeId = typeId > 2 ? 0 : typeId;
        if (state == SpecStateEnum.NONE)
        {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        List<BrandViewEntity> list = autoCacheService.getAllBrandItems();
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        boolean flag = false;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            case SELL_30:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if(!flag){
            builder.setResult(result);
            builder.setReturnMsg("成功");
            return builder.build();
        }
        if(typeId > 0){
            int finalTypeId = typeId;
            list = list.stream().filter(s -> s.getIsCV() == finalTypeId).collect(Collectors.toList());
        }
        if(isFilterSpecImage == 1){
            list = list.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        list = list.stream().sorted(Comparator.comparing(BrandViewEntity::getRankIndex)).collect(Collectors.toList());
        //分组
        LinkedHashMap<String, ArrayList<BrandViewEntity>> brandMap = list.stream().collect(Collectors.groupingBy(brandViewEntity -> brandViewEntity.getBrandId() + brandViewEntity.getBFirstLetter(),
                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        //品牌信息
        List<Integer> brandIds = list.stream().map(BrandViewEntity::getBrandId).distinct().collect(Collectors.toList());
        Map<Integer, BrandBaseInfo> brandBaseInfoMap = commService.getBrandBaseInfo(brandIds);
        //遍历
        for(Map.Entry<String, ArrayList<BrandViewEntity>> brandBaseMap : brandMap.entrySet()){
            BrandByStateAndTypeResponse.BrandItem.Builder item = BrandByStateAndTypeResponse.BrandItem.newBuilder();
            BrandViewEntity brandViewEntity = brandBaseMap.getValue().get(0);
            BrandBaseInfo brandBaseInfo = brandBaseInfoMap.get(brandViewEntity.getBrandId());
            item.setId(brandViewEntity.getBrandId());
            item.setName(null != brandBaseInfo && null != brandBaseInfo.getName() ? brandBaseInfo.getName() : "");
            item.setBfirstletter(brandViewEntity.getBFirstLetter());
            item.setLogo(null != brandBaseInfo && null != brandBaseInfo.getLogo() ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
            item.setCountry(null != brandBaseInfo && null != brandBaseInfo.getCountry() ? brandBaseInfo.getCountry() : "");
            item.setCountryid(null != brandBaseInfo ? brandBaseInfo.getCountryId() : 0);
            result.addBranditems(item);
        }
        return builder.
                setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode()).
                setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).
                setResult(result).
                build();
    }

    @Override
    public SeriesByBrandResponse seriesByBrand(SeriesByBrandRequest request){
        SeriesByBrandResponse.Builder builder = SeriesByBrandResponse.newBuilder();
        SeriesByBrandResponse.Result.Builder result = SeriesByBrandResponse.Result.newBuilder();
        int brandId = request.getBrandid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        int typeId = request.getTypeid();
        int isFilterSpecImage = StringIntegerUtils.getIntOrDefault( request.getIsFilterSpecImage(),0);
        typeId = typeId > 2 ? 0 : typeId;
        if (brandId == 0 || state == SpecStateEnum.NONE)
        {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        List<SeriesInfoEntity> list = autoCacheService.getAllSeriesItems();
        if (org.apache.dubbo.common.utils.CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        boolean flag = false;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if(!flag){
            builder.setResult(result);
            builder.setReturnMsg("成功");
            return builder.build();
        }
        if(brandId > 0){
            list = list.stream().filter(s -> s.getBrandId() == brandId).collect(Collectors.toList());
        }
        if(typeId > 0){
            int finalTypeId = typeId;
            list = list.stream().filter(s -> s.getIsCV() == finalTypeId).collect(Collectors.toList());
        }
        if(isFilterSpecImage == 1){
            list = list.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        list = list.stream().sorted(Comparator.comparing(SeriesInfoEntity::getRankIndex)).collect(Collectors.toList());
        for (SeriesInfoEntity item:list) {
            SeriesBaseInfo seriesBase = seriesBaseService.get(item.getSeriesId()).join();
            SeriesByBrandResponse.SeriesItem.Builder seriesItem = SeriesByBrandResponse.SeriesItem.newBuilder();
            seriesItem.setId(item.getSeriesId());
            seriesItem.setName(seriesBase == null?"":seriesBase.getName());
            seriesItem.setSfirstletter(item.getSFirstLetter());
            seriesItem.setSeriesstate(item.getSeriesstate());
            seriesItem.setSeriesorder(item.getSeriesOrder());
            result.addSeriesitems(seriesItem);
        }
        List<SeriesByBrandResponse.SeriesItem> series = result.getSeriesitemsList().stream().distinct().collect(Collectors.toList());
        result.clearSeriesitems();
        series.forEach(x->{
            result.addSeriesitems(x);
        });
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public BrandDingZhiElectricResponse brandDingZhiElectric(BrandDingZhiElectricRequest request){
        BrandDingZhiElectricResponse.Builder builder = BrandDingZhiElectricResponse.newBuilder();
        BrandDingZhiElectricResponse.Result.Builder result = BrandDingZhiElectricResponse.Result.newBuilder();

        SpecStateEnum state = Spec.getSpecState(request.getState());
        int typeId = request.getTypeid();
        int isFilterSpecImage = StringIntegerUtils.getIntOrDefault(request.getIsFilterSpecImage(),0);
        typeId = typeId > 2 ? 0 : typeId;
        if (state == SpecStateEnum.NONE) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        List<BrandViewEntity> elecList = autoCacheService.getAllElectricBrandItems();
        if (CollectionUtils.isEmpty(elecList)) {
            elecList = new ArrayList<>();
        }

        boolean flag = false;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售(0X000e)
            case SELL_14:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            //即将在售+在售+停售(0X001e)
            case SELL_30:
                flag = true;
                elecList = elecList.stream().filter(s -> s.getSpecState() >= 10).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if(!flag){
            builder.setResult(result);
            builder.setReturnMsg("成功");
            return builder.build();
        }

        if(typeId > 0){
            int finalTypeId = typeId;
            elecList = elecList.stream().filter(s -> s.getIsCV() == finalTypeId).collect(Collectors.toList());
        }
        if(isFilterSpecImage == 1){
            elecList = elecList.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
        }

        elecList = elecList.stream().sorted(Comparator.comparing(BrandViewEntity::getRankIndex)).collect(Collectors.toList());
        List<Integer> elecBrandIds = elecList.stream().map(BrandViewEntity::getBrandId).collect(Collectors.toList());
        Map<Integer, BrandBaseInfo> brandMap = brandBaseService.getMap(elecBrandIds);
        List<BrandDingZhiElectricResponse.BrandItem> items = new ArrayList<>();
        if(!CollectionUtils.isEmpty(elecList) && !CollectionUtils.isEmpty(brandMap)){
            for(BrandViewEntity x : elecList){
                BrandBaseInfo brandBase = brandMap.get(x.getBrandId());
                BrandDingZhiElectricResponse.BrandItem.Builder item = BrandDingZhiElectricResponse.BrandItem.newBuilder();
                item.setId(x.getBrandId());
                item.setName(brandBase == null ? "" : brandBase.getName());
                item.setBfirstletter(x.getBFirstLetter());
                item.setLogo(brandBase == null || brandBase.getLogo() == null ? "" : ImageUtil.getFullImagePath(brandBase.getLogo().replace("/brand/50/","/brand/100/")));
                items.add(item.build());
            }
            items = items.stream().distinct().collect(Collectors.toList());
        }
        result.addAllBranditems(items);
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    /**
     * 根据品牌首字母和品牌热度排序，返回品牌列表
     * @param request
     * @return
     */
    @Override
    public GetBrandListResponse getBrandList(GetBrandListRequest request) {
        GetBrandListResponse.Builder builder = GetBrandListResponse.newBuilder();
        List<BrandInfoEntity> brandInfoEntities = autoCacheService.getBrandInfoAll();
        GetBrandListResponse.Result.Builder resultBuilder = GetBrandListResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(brandInfoEntities)){
            List<Integer> brandIds = brandInfoEntities.stream().map(BrandInfoEntity::getBrandId).distinct().collect(Collectors.toList());
            Map<Integer, BrandBaseInfo> brandBaseInfoMap = commService.getBrandBaseInfo(brandIds);
            for(BrandInfoEntity brandInfoEntity : brandInfoEntities){
                GetBrandListResponse.BrandItem.Builder brandItem = GetBrandListResponse.BrandItem.newBuilder();
                brandItem.setBrandid(brandInfoEntity.getBrandId());
                BrandBaseInfo brandBaseInfo = brandBaseInfoMap.get(brandInfoEntity.getBrandId());
                if(null != brandBaseInfo){
                    brandItem.setBrandname(null != brandBaseInfo.getName() ? brandBaseInfo.getName() : "");
                    brandItem.setLogo(null != brandBaseInfo.getLogo() ?
                            ImageUtil.getFullImagePath(brandBaseInfo.getLogo(),"/brand/50/","/brand/100/") : "");
                }
                brandItem.setFirstletter(brandInfoEntity.getFirstLetter());
                brandItem.setOrdernum(brandInfoEntity.getOrderNum());
                resultBuilder.addBrandlist(brandItem);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }
    /**
     * 获取图片库品牌菜单
     * @param request
     * @return
     */
    @Override
    public GetBrandMenuResponse getBrandMenu(GetBrandMenuRequest request) {
        GetBrandMenuResponse.Builder builder = GetBrandMenuResponse.newBuilder();
        List<CarManueEntity> carManuePicEntities = autoCacheService.getCarManuePicBrandAll();

        if(!CollectionUtils.isEmpty(carManuePicEntities)){
            List<Integer> brandIds = carManuePicEntities.stream().map(CarManueEntity::getBrandId).distinct().collect(Collectors.toList());
            Map<Integer, BrandBaseInfo> brandBaseInfoMap = commService.getBrandBaseInfo(brandIds);
            //分组
            Map<String, List<CarManueEntity>> carManuePicMap = carManuePicEntities.stream().collect(Collectors.groupingBy(carManuePicEntity ->
                    carManuePicEntity.getFirstLetter(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            for(Map.Entry<String, List<CarManueEntity>> carManuePicEntityMap : carManuePicMap.entrySet()){
                GetBrandMenuResponse.Result.Builder resultBuilder = GetBrandMenuResponse.Result.newBuilder();
                resultBuilder.setFirstletter(carManuePicEntityMap.getKey());
                carManuePicEntityMap.getValue().forEach(carManuePicEntity -> {
                    GetBrandMenuResponse.BrandItem.Builder brandItem = GetBrandMenuResponse.BrandItem.newBuilder();
                    BrandBaseInfo brandBaseInfo = brandBaseInfoMap.get(carManuePicEntity.getBrandId());
                    brandItem.setId(carManuePicEntity.getBrandId());
                    if(null != brandBaseInfo){
                        brandItem.setName(null != brandBaseInfo.getName() ? brandBaseInfo.getName() : "");
                        brandItem.setLogo(null != brandBaseInfo.getLogo() ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
                    }
                    brandItem.setPiccount(carManuePicEntity.getBrandCount());
                    resultBuilder.addBranditems(brandItem);
                });
                resultBuilder.setTotal(resultBuilder.getBranditemsCount());
                builder.addResult(resultBuilder);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    /**
     * 根据品牌名称获取品牌id
     * @param request
     * @return
     */
    @Override
    public GetBrandIdByBrandNameResponse getBrandIdByBrandName(GetBrandIdByBrandNameRequest request) {
        GetBrandIdByBrandNameResponse.Builder builder = GetBrandIdByBrandNameResponse.newBuilder();
        String brandName = request.getBrandname();
        if(StringUtils.isBlank(brandName)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        KeyValueDto<Integer, String> keyValueDto = autoCacheService.getGroupByName(brandName);
        GetBrandIdByBrandNameResponse.Result.Builder resultBuilder = GetBrandIdByBrandNameResponse.Result.newBuilder();
        if(null != keyValueDto){
            resultBuilder.setBrandid(keyValueDto.getKey());
        }
        resultBuilder.setBrandname(brandName);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 获取报价库品牌菜单
     * @param request
     * @return
     */
    @Override
    public GetBrandMenuPriceResponse getBrandMenuPrice(GetBrandMenuPriceRequest request) {
        GetBrandMenuPriceResponse.Builder builder = GetBrandMenuPriceResponse.newBuilder();
        List<CarManueEntity> carManueEntities = autoCacheService.getAllCarManuePrice();

        if(!CollectionUtils.isEmpty(carManueEntities)){
            List<Integer> brandIds = carManueEntities.stream().map(CarManueEntity::getBrandId).distinct().collect(Collectors.toList());
            Map<Integer, BrandBaseInfo> brandBaseInfoMap = commService.getBrandBaseInfo(brandIds);
            //分组
            Map<String, List<CarManueEntity>> carManuePicMap = carManueEntities.stream().collect(Collectors.groupingBy(carManueEntity ->
                    carManueEntity.getFirstLetter(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            for(Map.Entry<String, List<CarManueEntity>> carManueEntityMap : carManuePicMap.entrySet()){
                GetBrandMenuPriceResponse.Result.Builder resultBuilder = GetBrandMenuPriceResponse.Result.newBuilder();
                resultBuilder.setFirstletter(carManueEntityMap.getKey());
                carManueEntityMap.getValue().forEach(carManueEntity -> {
                    GetBrandMenuPriceResponse.BrandItem.Builder brandItem = GetBrandMenuPriceResponse.BrandItem.newBuilder();
                    BrandBaseInfo brandBaseInfo = brandBaseInfoMap.get(carManueEntity.getBrandId());
                    brandItem.setId(carManueEntity.getBrandId());
                    if(null != brandBaseInfo){
                        brandItem.setName(null != brandBaseInfo.getName() ? brandBaseInfo.getName() : "");
                        brandItem.setLogo(null != brandBaseInfo.getLogo() ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
                    }
                    brandItem.setSpeccount(carManueEntity.getBrandCount());
                    resultBuilder.addBranditems(brandItem);
                });
                resultBuilder.setTotal(resultBuilder.getBranditemsCount());
                builder.addResult(resultBuilder);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    /**
     * 根据首字母获取报价库品牌菜单(分页)
     * @param request
     * @return
     */
    @Override
    public GetBrandMenuWithPageByFirstLetterResponse getBrandMenuWithPageByFirstLetter(GetBrandMenuWithPageByFirstLetterRequest request) {
        GetBrandMenuWithPageByFirstLetterResponse.Builder builder = GetBrandMenuWithPageByFirstLetterResponse.newBuilder();
        String firstLetter = request.getFirstletter();
        int size = request.getSize() == 0 ? 20 : request.getSize();//数据条数
        int page = request.getPage() == 0 ? 1 : request.getPage();//页码
        if(firstLetter.length() != 1 || page <= 0 || size <= 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        GetBrandMenuWithPageByFirstLetterResponse.Result.Builder resultBuilder = GetBrandMenuWithPageByFirstLetterResponse.Result.newBuilder();
        List<CarManueBaseEntity> carManueBaseEntities = autoCacheService.getCarManuePriceByFirstLetter(firstLetter);
        if(!CollectionUtils.isEmpty(carManueBaseEntities)){
            List<Integer> brandIds = carManueBaseEntities.stream().map(CarManueBaseEntity::getBrandId).distinct().collect(Collectors.toList());
            Map<Integer, BrandBaseInfo> brandBaseInfoMap = commService.getBrandBaseInfo(brandIds);
            int startIndex = (page - 1) * size;
            int endIndex = startIndex + size > carManueBaseEntities.size() ? carManueBaseEntities.size() : startIndex + size;
            for (; startIndex < endIndex; startIndex++){
                GetBrandMenuWithPageByFirstLetterResponse.BrandItem.Builder brandItem = GetBrandMenuWithPageByFirstLetterResponse.BrandItem.newBuilder();
                int brandId = carManueBaseEntities.get(startIndex).getBrandId();
                brandItem.setId(brandId);
                BrandBaseInfo brandBaseInfo = brandBaseInfoMap.get(brandId);
                if(null != brandBaseInfo){
                    brandItem.setName(null != brandBaseInfo.getName() ? brandBaseInfo.getName() : "");
                    brandItem.setLogo(null != brandBaseInfo.getLogo() ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
                }
                brandItem.setSpeccount(carManueBaseEntities.get(startIndex).getBrandCount());
                resultBuilder.addBranditems(brandItem);
            }
        }
        resultBuilder.setPageindex(page);
        resultBuilder.setSize(size);
        resultBuilder.setFirstletter(firstLetter);
        resultBuilder.setTotal(carManueBaseEntities.size());

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    @Override
    public GetBrandByPavilionIdResponse getBrandByPavilionId(GetBrandByPavilionIdRequest request) {
        GetBrandByPavilionIdResponse.Builder builder = GetBrandByPavilionIdResponse.newBuilder();
        int size = request.getSize();
        int showId = request.getShowid();
        int pavilionId = request.getPavilionid();
        if (showId == 0 || pavilionId ==0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<BrandInfoEntity> pavilionBrands = autoCacheService.getPavilionBrands(showId, pavilionId);
        GetBrandByPavilionIdResponse.Result.Builder result = GetBrandByPavilionIdResponse.Result.newBuilder();
        if (!CollectionUtils.isEmpty(pavilionBrands)) {
            if (size > 0) {
                pavilionBrands = pavilionBrands.stream().limit(size).collect(Collectors.toList());
            }
            Map<Integer, BrandBaseInfo> map = brandBaseService.getMap(pavilionBrands.stream().map(BrandInfoEntity::getBrandId).collect(Collectors.toList()));
            for (BrandInfoEntity pavilionBrand : pavilionBrands) {
                int brandId = pavilionBrand.getBrandId();
                BrandBaseInfo brandBaseInfo = CollectionUtils.isEmpty(map) ? null : map.get(brandId);
                String log = "";
                String name = "";
                if (Objects.nonNull(brandBaseInfo)) {
                    log = ImageUtil.getFullImagePathNew(brandBaseInfo.getLogo(), true);
                    name = brandBaseInfo.getName();
                }
                result.addBranditems(GetBrandByPavilionIdResponse.Result.Branditem.newBuilder().
                        setId(brandId).setFirstletter(pavilionBrand.getFirstLetter()).setLogo(log).setName(name).build());
            }
        }
        result.setTotal(result.getBranditemsCount());
        result.setPavilionid(pavilionId);
        result.setShowid(showId);
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public BrandShowByPavilionLetterResponse brandShowByPavilionLetter(BrandShowByPavilionLetterRequest request) {
        BrandShowByPavilionLetterResponse.Builder builder = BrandShowByPavilionLetterResponse.newBuilder();
        int showId = request.getShowid();
        String pavIds = request.getPavilionlist();
        String letter = request.getFirstletter();
        if (showId == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        BrandShowByPavilionLetterResponse.Result.Builder result = BrandShowByPavilionLetterResponse.Result.newBuilder();
        List<Integer> pavArr = CommonFunction.getListFromStr(pavIds);
        String pavStr =  String.join(",", pavArr.stream().map(Object::toString).toArray(String[]::new));

        List<BrandPicListEntity> brandShowList = autoCacheService.getBrandShowByPavLetter(showId, pavStr, letter);
        if(!CollectionUtils.isEmpty(brandShowList)){
            for(BrandPicListEntity x : brandShowList){
                BrandShowByPavilionLetterResponse.Result.BrandItem.Builder item = BrandShowByPavilionLetterResponse.Result.BrandItem.newBuilder();
                item.setId(x.getBrandId());
                item.setName(x.getBrandName() == null ? "" : x.getBrandName());
                item.setFirstletter(x.getFirstLetter() == null ? "" : x.getFirstLetter());
                item.setLogo(x.getImg() == null ? "" : ImageUtil.getFullImagePath(x.getImg()));
                result.addBranditems(item);
            }
        }
        result.setTotal(brandShowList.size()).setShowid(showId).setPavilionid(pavStr);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @Override
    public BrandHotResponse brandHot(BrandHotRequest request){
        BrandHotResponse.Builder builder = BrandHotResponse.newBuilder();
        BrandHotResponse.Result.Builder result = BrandHotResponse.Result.newBuilder();
        List<Integer> hbrands = autoCacheService.getHotBrand();
        hbrands.stream().forEach(x -> {
            BrandBaseInfo brandBase = brandBaseService.get(x).join();
            BrandHotResponse.BrandItem.Builder item = BrandHotResponse.BrandItem.newBuilder();
            item.setId(x);
            item.setName(brandBase == null?"":brandBase.getName());
            item.setLogo(brandBase == null?"":ImageUtil.getFullImagePath(brandBase.getLogo()));
            result.addBranditems(item);
            result.setTotal(hbrands.size());
        });
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

}
