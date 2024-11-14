package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.pingan.*;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.LocalDateUtils;
import com.autohome.car.api.data.popauto.entities.BrandBaseEntity;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.PingAnService;
import com.autohome.car.api.services.basic.BrandSeriesRelationBaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesSpecService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.autohome.car.api.common.LocalDateUtils.DATE_TIME_PATTERN;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
public class PingAnServiceImpl implements PingAnService {

    @Resource
    private AutoCacheService autoCacheService;

    @Resource
    private BrandSeriesRelationBaseService brandSeriesRelationBaseService;

    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private SeriesSpecService seriesSpecService;

    @Resource
    private SpecBaseService specBaseService;

    /**
     * 平安获取品牌信息
     * @param request
     * @return
     */
    @Override
    public BrandInfoResponse getBrandInfoAll(BrandInfoRequest request) {
        BrandInfoResponse.Builder builder = BrandInfoResponse.newBuilder();
        List<BrandBaseEntity> brandBaseEntities = autoCacheService.getAllBrandName();
        if(CollectionUtils.isEmpty(brandBaseEntities)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                    .build();
        }
        for(BrandBaseEntity brandBaseEntity : brandBaseEntities){
            BrandInfoResponse.Result.Builder result = BrandInfoResponse.Result.newBuilder();
            result.setBrandid(brandBaseEntity.getId());
            result.setBrandname(null != brandBaseEntity.getName() ? brandBaseEntity.getName() : "");
            result.setFirstletter(null != brandBaseEntity.getFirstLetter() ? brandBaseEntity.getFirstLetter() : "");
            result.setBrandlogo(ImageUtil.getFullImagePath(brandBaseEntity.getLogo()));
            result.setEdittime(null != brandBaseEntity.getEditTime() ? LocalDateUtils.format(brandBaseEntity.getEditTime(),DATE_TIME_PATTERN) : "");
            builder.addResult(result);
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }
    /**
     * 平安根据品牌id获取车系信息
     * @param request
     * @return
     */
    @Override
    public SeriesInfoResponse getSeriesInfoByBrandId(SeriesInfoRequest request) {
        SeriesInfoResponse.Builder builder = SeriesInfoResponse.newBuilder();
        if(request.getBrandid() <= 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<Integer> seriesIds = brandSeriesRelationBaseService.getSeriesIds(request.getBrandid());
        if(CollectionUtils.isEmpty(seriesIds)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                    .build();
        }
        List<SeriesBaseInfo> seriesBaseInfos = seriesBaseService.getList(seriesIds);
        if(!CollectionUtils.isEmpty(seriesBaseInfos)){
            for(SeriesBaseInfo seriesBaseInfo : seriesBaseInfos){
                SeriesInfoResponse.Result.Builder result = SeriesInfoResponse.Result.newBuilder();
                result.setSeriesid(seriesBaseInfo.getId());
                result.setSeriesname(null != seriesBaseInfo.getName() ? seriesBaseInfo.getName() : "");
                result.setBrandid(seriesBaseInfo.getBrandId());
                result.setSeriesimg(ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()));
                result.setNobglogo(ImageUtil.getFullImagePath(seriesBaseInfo.getNoBgLogo()));
                result.setSeriespricemin(seriesBaseInfo.getSeriesPriceMin());
                result.setSeriespricemax(seriesBaseInfo.getSeriesPriceMax());
                result.setFirstletter(null != seriesBaseInfo.getFl() ? seriesBaseInfo.getFl() : "");
                result.setSeriesstate(seriesBaseInfo.getSeriesState());
                result.setNewenergy(seriesBaseInfo.getIne());
                result.setEdittime(null != seriesBaseInfo.getEditTime() ? LocalDateUtils.format(seriesBaseInfo.getEditTime(),DATE_TIME_PATTERN) : "");
                result.setFctid(seriesBaseInfo.getFactId());
                result.setFctname(null != seriesBaseInfo.getFctName() ? seriesBaseInfo.getFctName() : "");
                builder.addResult(result);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }
    /**
     * 平安根据车系id获取车型信息
     * @param request
     * @return
     */
    @Override
    public SpecInfoResponse getSpecInfoBySeriesId(SpecInfoRequest request) {
        SpecInfoResponse.Builder builder = SpecInfoResponse.newBuilder();
        if(request.getSeriesid() <= 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<Integer> specIds = seriesSpecService.getSpecIds(request.getSeriesid());
        if(CollectionUtils.isEmpty(specIds)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                    .build();
        }
        List<SpecBaseInfo> specBaseInfos = specBaseService.getList(specIds);
        if(!CollectionUtils.isEmpty(specBaseInfos)){
            for(SpecBaseInfo specBaseInfo : specBaseInfos){
                SpecInfoResponse.Result.Builder result = SpecInfoResponse.Result.newBuilder();
                result.setSpecid(specBaseInfo.getId());
                result.setSpecname(null != specBaseInfo.getSpecName() ? specBaseInfo.getSpecName() : "");
                result.setYear(specBaseInfo.getSYear());
                result.setSeriesid(specBaseInfo.getSeriesId());
                result.setBrandid(specBaseInfo.getBrandId());
                result.setSpecimg(ImageUtil.getFullImagePath(specBaseInfo.getLogo()));
                result.setSpecpricemin(specBaseInfo.getSpecMinPrice());
                result.setSpecpricemax(specBaseInfo.getSpecMaxPrice());
                result.setSpecstate(specBaseInfo.getSpecState());
                result.setSpecdisplacement(specBaseInfo.getDisplacement());
                result.setHorsepower(specBaseInfo.getHorsepower());
                result.setFlowmode(specBaseInfo.getFlowMode());
                result.setDriveform(specBaseInfo.getId() > 1000000 ? specBaseInfo.getDriveForm() : Spec.DriveModeByString(specBaseInfo.getSpecDrivingMode()));
                result.setTransmissionname(null != specBaseInfo.getGearBox() ? specBaseInfo.getGearBox() : "");
                result.setSeats(StringUtils.isNotBlank(specBaseInfo.getSeats()) ? specBaseInfo.getSeats() : "");
                result.setEdittime(null != specBaseInfo.getEditTime() ? LocalDateUtils.format(specBaseInfo.getEditTime(),DATE_TIME_PATTERN) : "");
                builder.addResult(result);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }
}
