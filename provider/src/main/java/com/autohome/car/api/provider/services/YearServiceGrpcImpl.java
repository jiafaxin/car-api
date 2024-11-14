package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.javascript.SyearAndSpecBySeriesRequest;
import autohome.rpc.car.car_api.v1.javascript.SyearAndSpecBySeriesResponse;
import autohome.rpc.car.car_api.v1.pic.PicColorItemsBySeriesIdRequest;
import autohome.rpc.car.car_api.v1.pic.PicColorItemsBySeriesIdResponse;
import autohome.rpc.car.car_api.v1.year.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.YearService;
import com.autohome.car.api.services.models.year.YearInfoItem;
import com.autohome.car.api.services.models.year.YearViewItem;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DubboService
public class YearServiceGrpcImpl extends DubboYearServiceTriple.YearServiceImplBase {

    @Autowired
    private YearService yearService;

    /**
     * 根据车系获取年代款列表
     * @param request
     * @return
     */
    @GetMapping("/v1/www/Year_GetYearItemsBySeriesId.ashx")
    @Override
    public GetYearItemsBySeriesIdResponse getYearItemsBySeriesId(GetYearItemsBySeriesIdRequest request) {
        ApiResult<YearViewItem> apiResult = yearService.getYearItemsBySeriesId(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()){
            return GetYearItemsBySeriesIdResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetYearItemsBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),GetYearItemsBySeriesIdResponse.Result.class);
        return GetYearItemsBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }


    /**
     * 根据车系获取年代款列表
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/year_infobyseriesid.ashx")
    @Override
    public GetYearInfoBySeriesIdResponse getYearInfoBySeriesId(GetYearInfoBySeriesIdRequest request) {
        ApiResult<YearInfoItem> apiResult = yearService.getYearInfoBySeriesId(request);
        GetYearInfoBySeriesIdResponse.Builder builder = GetYearInfoBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || apiResult.getResult() == null) {
            return builder.build();
        }
        GetYearInfoBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetYearInfoBySeriesIdResponse.Result.class);
        return builder.setResult(result).build();
    }

    @Override
    @GetMapping("/v1/carprice/year_parambyyearId.ashx")
    public YearParamByYearIdResponse yearParamByYearId(YearParamByYearIdRequest request) {
        return yearService.yearParamByYearId(request);
    }

    @Override
    @GetMapping("/v1/carpic/year_25picturebyyearid.ashx")
    public Year25PictureByYearIdResponse year25PictureByYearId(Year25PictureByYearIdRequest request) {
        return yearService.year25PictureByYearId(request);
    }


    @Override
    @GetMapping("/v1/javascript/syearbyseries.ashx")
    public GetSYearBySeriesResponse getSYearBySeries(GetSYearBySeriesRequest request) {
        return yearService.getSYearBySeries(request);
    }

    @GetMapping("/v1/carprice/year_infobyyearid.ashx")
    @Override
    public GetYearInfoByYearIdResponse getYearInfoByYearId(GetYearInfoByYearIdRequest request) {
        return yearService.getYearInfoByYearId(request);
    }

    @GetMapping("/v1/carprice/year_colorbyyearid.ashx")
    @Override
    public GetYearColorByYearIdResponse getYearColorByYearId(GetYearColorByYearIdRequest request) {
        return yearService.getYearColorByYearId(request);
    }

    @GetMapping("/v1/carprice/year_innercolorbyyearid.ashx")
    @Override
    public GetYearInnerColorByYearIdResponse getYearInnerColorByYearId(GetYearInnerColorByYearIdRequest request) {
        return yearService.getYearInnerColorByYearId(request);
    }
}