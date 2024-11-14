package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.javascript.SyearAndSpecBySeriesRequest;
import autohome.rpc.car.car_api.v1.javascript.SyearAndSpecBySeriesResponse;
import autohome.rpc.car.car_api.v1.year.*;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.Car25PictureViewMapper;
import com.autohome.car.api.data.popauto.PicClassMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.YearService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.Year25PictureByYearIdBaseService;
import com.autohome.car.api.services.basic.YearViewBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.models.YearViewBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.series.SeriesSpecYearService;
import com.autohome.car.api.services.basic.series.SeriesYearConfigService;
import com.autohome.car.api.services.basic.series.SeriesYearStateConfigService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.SeriesConfig;
import com.autohome.car.api.services.models.SeriesYearConfig;
import com.autohome.car.api.services.models.year.YearInfoItem;
import com.autohome.car.api.services.models.year.YearViewItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
public class YearServiceImpl implements YearService {

    @Resource
    private YearViewBaseService yearViewBaseService;

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    Car25PictureViewMapper car25PictureViewMapper;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    PicClassMapper picClassMapper;

    @Autowired
    SeriesConfigService seriesConfigService;

    @Autowired
    SeriesYearConfigService seriesYearConfigService;

    @Autowired
    SeriesYearStateConfigService seriesYearStateConfigService;

    @Autowired
    Year25PictureByYearIdBaseService year25PictureByYearIdBaseService;

    @Autowired
    AutoCacheService autoCacheService;

    @Resource
    private SeriesSpecYearService seriesSpecYearService;

    /**
     * 根据车系获取年代款列表
     * @param request
     * @return
     */
    @Override
    public ApiResult<YearViewItem> getYearItemsBySeriesId(GetYearItemsBySeriesIdRequest request) {
        YearViewItem yearViewItem = new YearViewItem();
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<YearViewBaseInfo> yearViewBaseInfos = yearViewBaseService.get(seriesId).join();
        List<YearViewItem.YearView> yearViews = new ArrayList<>();
        if(!CollectionUtils.isEmpty(yearViewBaseInfos)){
            for(YearViewBaseInfo yearViewBaseInfo: yearViewBaseInfos){
                YearViewItem.YearView yearView = new YearViewItem.YearView();
                yearView.setId(yearViewBaseInfo.getSYearId());
                yearView.setYear(yearViewBaseInfo.getSYear());
                yearView.setState(yearViewBaseInfo.getSYearState());
                yearView.setSpecnum(yearViewBaseInfo.getSYearSpecNum());
                yearView.setSpecnumunsold(yearViewBaseInfo.getSYearSpecNumUnsold());
                yearView.setSpecnumsale(yearViewBaseInfo.getSYearSpecNumSale());
                yearView.setSpecnumstop(yearViewBaseInfo.getSYearSpecNumStop());
                yearViews.add(yearView);
            }
        }
        yearViewItem.setSerieid(seriesId);
        yearViewItem.setYearitems(yearViews);
        return new ApiResult<>(yearViewItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据车系id获取年代款列表
     * @param request
     * @return
     */
    @Override
    public ApiResult<YearInfoItem> getYearInfoBySeriesId(GetYearInfoBySeriesIdRequest request) {
        YearInfoItem yearInfoItem = new YearInfoItem();
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<YearViewBaseInfo> yearViewBaseInfos = yearViewBaseService.get(seriesId).join();
        List<YearInfoItem.YearInfo> yearInfos = new ArrayList<>();
        if(!CollectionUtils.isEmpty(yearViewBaseInfos)){
            for(YearViewBaseInfo yearViewBaseInfo: yearViewBaseInfos){
                YearInfoItem.YearInfo yearInfo = new YearInfoItem.YearInfo();
                yearInfo.setYearid(yearViewBaseInfo.getSYearId());
                yearInfo.setYearnumber(yearViewBaseInfo.getSYear());
                yearInfo.setYearname(yearViewBaseInfo.getSYear() + "款");
                yearInfo.setSeriesid(seriesId);
                yearInfo.setYearispublic(yearViewBaseInfo.getSYearIsPublic());
                yearInfo.setYearstate(yearViewBaseInfo.getSYearState());
                yearInfos.add(yearInfo);
            }
        }
        yearInfoItem.setSeriesid(seriesId);
        yearInfoItem.setYearitems(yearInfos);
        yearInfoItem.setTotal(yearInfos.size());
        return new ApiResult<>(yearInfoItem, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    @Override
    public SyearAndSpecBySeriesResponse syearAndSpecBySeries(SyearAndSpecBySeriesRequest request){
        SyearAndSpecBySeriesResponse.Builder builder =  SyearAndSpecBySeriesResponse.newBuilder();
        SyearAndSpecBySeriesResponse.Result.Builder result =  SyearAndSpecBySeriesResponse.Result.newBuilder();
        int seriesId =  request.getSeriesid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        int isFilterSpecImage = StringIntegerUtils.getIntOrDefault(request.getIsFilterSpecImage(),0);
        if (seriesId == 0 || state == SpecStateEnum.NONE)
        {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        builder.setReturnMsg("成功");
        SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
        if(baseInfo==null){
            return builder.setResult(result).build();
        }
        boolean isCv = Level.isCVLevel(baseInfo.getLevelId());
//        List<SpecViewEntity> list = specViewMapper.getSpecItemsBySeries(seriesId,isCv);
        List<SpecViewEntity> list = autoCacheService.getSpecItemsBySeries(seriesId,isCv);
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
            //
            case SELL_22:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_24:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10).collect(Collectors.toList());
                break;
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if(isFilterSpecImage == 1){
            list = list.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        list = list.stream()
                .sorted(Comparator.comparing(SpecViewEntity::getSyear,Comparator.reverseOrder())
                .thenComparing(SpecViewEntity::getOrderBy,Comparator.reverseOrder())
                        .thenComparing(SpecViewEntity::getSpecOrdercls)).collect(Collectors.toList());

        Map<Integer, List<SpecViewEntity>> map = list.stream().collect(Collectors.groupingBy(SpecViewEntity::getSyear));
        List<Integer> years = map.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        List<Integer> specIds = list.stream().map(x->x.getSpecId()).collect(Collectors.toList());
        Map<Integer,SpecBaseInfo> specList = specBaseService.getMap(specIds);

        for (Integer year:years) {
            List<SpecViewEntity> val = map.get(year);
            if(val != null){
                SyearAndSpecBySeriesResponse.Yearitems.Builder yearItem = SyearAndSpecBySeriesResponse.Yearitems.newBuilder();
                yearItem.setId(val.get(0).getSyearId());
                yearItem.setName(year+"款");
                List<SpecViewEntity> specs = val.stream()
                        .sorted(Comparator.comparing(SpecViewEntity::getOrderBy,Comparator.reverseOrder())
                                .thenComparing(SpecViewEntity::getSpecOrdercls)).collect(Collectors.toList());
                for (SpecViewEntity spec:specs) {
                    int specId = spec.getSpecId();
                    SpecBaseInfo specBase = specList.containsKey(specId) ? specList.get(specId) : null;
                    SyearAndSpecBySeriesResponse.Specitems.Builder specItem = SyearAndSpecBySeriesResponse.Specitems.newBuilder();
                    specItem.setId(specId);
                    specItem.setName(specBase == null?"":specBase.getSpecName());
                    specItem.setState(spec.getSpecState());
                    specItem.setMinprice(specBase == null?0:specBase.getSpecMinPrice());
                    specItem.setMaxprice(specBase == null?0:specBase.getSpecMaxPrice());
                    yearItem.addSpecitems(specItem);
                }
                result.addYearitems(yearItem);
            }
        }
        builder.setResult(result);
        return builder.build();
    }

    @Override
    public YearParamByYearIdResponse yearParamByYearId(YearParamByYearIdRequest request){
        YearParamByYearIdResponse.Builder builder =  YearParamByYearIdResponse.newBuilder();
        YearParamByYearIdResponse.Result.Builder result =  YearParamByYearIdResponse.Result.newBuilder();
        int seriesId =  request.getSeriesid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        int yearId = request.getYearid();
        if (yearId == 0 || seriesId == 0 || state == SpecStateEnum.NONE) {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        int stateId = 0;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                stateId = 0;
                break;
            case WAIT_SELL:
                stateId = 10;
                break;
            case SELL_12:
                stateId = 20;
                break;
            case STOP_SELL:
                stateId = 40;
                break;
        }

        SeriesConfig configBase = seriesConfigService.get(seriesId);
        SeriesYearConfig config = seriesYearConfigService.get(seriesId,yearId,stateId);
        if(config != null){

            result.setId(yearId);
            result.setMinprice(config.getMinprice());
            result.setMaxprice(config.getMaxprice());
            result.setFctid(configBase.getFctid());
            result.setFctname(configBase.getFctname());
            result.setBrandid(configBase.getBrandid());
            result.setBrandname(configBase.getBrandname());
            result.setSeriesid(seriesId);
            result.setSeriesname(configBase.getName());

            result.addAllStructitems(config.getStructitems());
            result.addAllTransmissionitems(config.getTransmissionitems());
            result.addAllDisplacementitems(config.getDisplacementitems());
            result.setLevelid(configBase.getLevelid());
            result.setLevelname(configBase.getLevelname());
            result.addAllPicitems(config.getPicitems());
            for (SeriesYearConfig.SeriesLogo pic:config.getPicinfoitems()) {
                YearParamByYearIdResponse.Picinfoitems.Builder picinfoitem =  YearParamByYearIdResponse.Picinfoitems.newBuilder();
                picinfoitem.setPicid(pic.getPicid());
                picinfoitem.setPicpath(pic.getPicpath());
                picinfoitem.setSpecid(pic.getSpecid());
                picinfoitem.setSpecstate(pic.getSpecstate());
                result.addPicinfoitems(picinfoitem);
            }
            result.setSpecnum(config.getSpecnum());
            result.setPicnum(config.getPicnum());
            result.setIsshow(config.getIsshow());
            result.setShowelectricparam(config.getShowelectricparam());
            result.addAllElectricmotormileage(config.getElectricmotormileage());
            result.addAllElectricmotorkw(config.getElectricmotorkw());
            result.setElectricchargetime("");
            builder.setResult(result);
        }
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public Year25PictureByYearIdResponse year25PictureByYearId(Year25PictureByYearIdRequest request){
        Year25PictureByYearIdResponse.Builder builder =  Year25PictureByYearIdResponse.newBuilder();
        Year25PictureByYearIdResponse.Result.Builder result =  Year25PictureByYearIdResponse.Result.newBuilder();
        int seriesId =  request.getSeriesid();
        int yearId = request.getYearid();
        if (yearId == 0 || seriesId == 0)
        {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        SeriesBaseInfo seriesBase = seriesBaseService.get(seriesId).join();
//        List<Car25PictureViewEntity> list = car25PictureViewMapper.getYear25PictureByYearId(seriesId,yearId);
        List<Car25PictureViewEntity> list = year25PictureByYearIdBaseService.get(seriesId,yearId).join();
        if(!CollectionUtils.isEmpty(list)){
//            List<KeyValueDto<Integer,String>> map = picClassMapper.getCar25PictureType();
            List<KeyValueDto<Integer,String>> map = autoCacheService.getCar25PictureType();
            int itemId = 0;
            for (Car25PictureViewEntity item:list) {
                Year25PictureByYearIdResponse.Picitems.Builder pic =  Year25PictureByYearIdResponse.Picitems.newBuilder();
                if(itemId == item.getOrdercls()){
                    continue;
                }
                itemId = item.getOrdercls();
                SpecBaseInfo baseInfo = specBaseService.get(item.getSpecId()).join();
                Optional<KeyValueDto<Integer,String>> iname = map.stream().filter(v -> v.getKey() == item.getId()).findFirst();
                pic.setItemid(itemId);
                pic.setTypeid(item.getTopId());
                pic.setItemname(iname.isPresent()==true?iname.get().getValue():"");
                pic.setPicid(item.getPicId());
                pic.setPicpath(ImageUtil.getFullImagePath(item.getPicPath()));
                pic.setSpecid(item.getSpecId());
                pic.setSpecname(baseInfo == null?"":baseInfo.getSpecName());
                result.addPicitems(pic);
            }
        }
        result.setTotal(result.getPicitemsCount());
        result.setSeriesid(seriesId);
        result.setYearid(yearId);
        result.setSeriesname(seriesBase == null?"":seriesBase.getName());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetSYearBySeriesResponse getSYearBySeries(GetSYearBySeriesRequest request) {
        GetSYearBySeriesResponse.Builder builder = GetSYearBySeriesResponse.newBuilder();
        GetSYearBySeriesResponse.Result.Builder result = GetSYearBySeriesResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        String state = request.getState();
        if (seriesId == 0 || StringUtils.isBlank(state)) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<SpecYearEntity> list = seriesSpecYearService.get(seriesId);
        SpecStateEnum specState = Spec.getSpecState(state);
        boolean flag = false;
        switch (specState) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                list = list.stream().filter(s -> s.getState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 20 && s.getState() <= 30).collect(Collectors.toList());
                break;
            //0x000e
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 10 && s.getState() <= 30).collect(Collectors.toList());
                break;
                //0x001c
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 20).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getState() <= 30).collect(Collectors.toList());
                break;
            //(0x001e)
            case SELL_30:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 10).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if (!flag) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        if (request.getIsFilterSpecImage() == 1) {
            list = list.stream().filter(e -> e.getSImage() == 0).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(list)) {
            List<Integer> yIds = list.stream().map(SpecYearEntity::getYId).distinct().collect(Collectors.toList());
            Map<Integer, List<SpecYearEntity>> maps = list.stream().collect(Collectors.groupingBy(SpecYearEntity::getYId));
            for (Integer yId : yIds) {
                List<SpecYearEntity> yearEntityList = maps.get(yId);
                SpecYearEntity specYearEntity = null;
                if (!CollectionUtils.isEmpty(yearEntityList)) {
                    specYearEntity = yearEntityList.get(0);
                }
                if (Objects.isNull(specYearEntity)) {
                    specYearEntity = new SpecYearEntity();
                    specYearEntity.setYId(yId);
                }
                result.addYearitems(GetSYearBySeriesResponse.Result.Yearitem.newBuilder()
                        .setId(specYearEntity.getYId())
                        .setName(String.format("%s%s", specYearEntity.getSyear(), "款"))
                        .build());
            }
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetYearInfoByYearIdResponse getYearInfoByYearId(GetYearInfoByYearIdRequest request){
        GetYearInfoByYearIdResponse.Builder resp = GetYearInfoByYearIdResponse.newBuilder();
        GetYearInfoByYearIdResponse.Result.Builder result = GetYearInfoByYearIdResponse.Result.newBuilder();
        int yearId = request.getYearid();
        if(yearId == 0){
            return resp.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        List<SYearViewEntity> yearViewBaseInfos = autoCacheService.getSYearViewByYearId(yearId);
        if(!CollectionUtils.isEmpty(yearViewBaseInfos)){
            SYearViewEntity yearViewBaseInfo = yearViewBaseInfos.get(0);
            SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(yearViewBaseInfo.getSeriesId()).join();
            result.setYearid(yearViewBaseInfo.getSYearId());
            result.setYearnumber(yearViewBaseInfo.getSYear());
            result.setYearname(yearViewBaseInfo.getSYear() + "款");
            result.setSeriesid(yearViewBaseInfo.getSeriesId());
            result.setSeriesname(seriesBaseInfo != null ? seriesBaseInfo.getName() : "");
            result.setYearispublic(yearViewBaseInfo.getSYearIsPublic());
            result.setYearstate(yearViewBaseInfo.getSYearState());
            resp.setResult(result);
        }
        return resp.setReturnCode(0).setReturnMsg("成功").build();
    }

    @Override
    public GetYearColorByYearIdResponse getYearColorByYearId(GetYearColorByYearIdRequest request) {
        GetYearColorByYearIdResponse.Builder resp = GetYearColorByYearIdResponse.newBuilder();
        GetYearColorByYearIdResponse.Result.Builder result = GetYearColorByYearIdResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if(seriesId == 0 || yearId == 0 || state == SpecStateEnum.NONE){
            return resp.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        resp.setReturnCode(0).setReturnMsg("成功");
        result.setYearid(yearId);
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        if(isCV){
            return resp.setResult(result).build();
        }
        List<SpecYearColorEntity> yearColors = autoCacheService.getSpecColorPicByYearId(yearId, false);
        if(CollectionUtils.isEmpty(yearColors)){
            return resp.setResult(result).build();
        }
        int stateId = 0;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                stateId = 0;
                break;
            case WAIT_SELL:
                stateId = 10;
                break;
            case SELL_12:
                stateId = 20;
                break;
            case STOP_SELL:
                stateId = 40;
                break;
        }
        int finalStateId = stateId;
        List<SpecYearColorEntity> matchYearColors = yearColors.stream().filter(x -> x.getSpecState() == finalStateId)
                .sorted(Comparator.comparingInt(SpecYearColorEntity::getPicNum).reversed())
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(matchYearColors)){
            return resp.setResult(result).build();
        }
        for(SpecYearColorEntity item : matchYearColors){
            GetYearColorByYearIdResponse.Colors.Builder colors = GetYearColorByYearIdResponse.Colors.newBuilder();
            colors.setId(item.getColorId())
                    .setName(item.getColorName())
                    .setValue(item.getColorValue())
                    .setPicnum(item.getPicNum())
                    .setClubpicnum(item.getClubPicNum());
            result.addColoritems(colors);
        }
        return resp.setResult(result).build();
    }

    @Override
    public GetYearInnerColorByYearIdResponse getYearInnerColorByYearId(GetYearInnerColorByYearIdRequest request) {
        GetYearInnerColorByYearIdResponse.Builder resp = GetYearInnerColorByYearIdResponse.newBuilder();
        GetYearInnerColorByYearIdResponse.Result.Builder result = GetYearInnerColorByYearIdResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if(seriesId == 0 || yearId == 0 || state == SpecStateEnum.NONE){
            return resp.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        if(isCV){
            return resp.setResult(result).build();
        }
        List<SpecYearColorEntity> yearColors = autoCacheService.getSpecColorPicByYearId(yearId, true);
        if(CollectionUtils.isEmpty(yearColors)){
            return resp.setResult(result).build();
        }
        int stateId = 0;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                stateId = 0;
                break;
            case WAIT_SELL:
                stateId = 10;
                break;
            case SELL_12:
                stateId = 20;
                break;
            case STOP_SELL:
                stateId = 40;
                break;
        }
        int finalStateId = stateId;
        List<SpecYearColorEntity> matchYearColors = yearColors.stream().filter(x -> x.getSpecState() == finalStateId)
                .sorted(Comparator.comparingInt(SpecYearColorEntity::getPicNum).reversed())
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(matchYearColors)){
            return resp.setResult(result).build();
        }
        for(SpecYearColorEntity item : matchYearColors){
            GetYearInnerColorByYearIdResponse.Colors.Builder colors = GetYearInnerColorByYearIdResponse.Colors.newBuilder();
            colors.setId(item.getColorId())
                    .setName(item.getColorName())
                    .setValue(item.getColorValue())
                    .setPicnum(item.getPicNum())
                    .setClubpicnum(item.getClubPicNum());
            result.addColoritems(colors);
        }
        return resp.setResult(result).build();
    }
}
