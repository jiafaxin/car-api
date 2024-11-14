package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.electric.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.ElectricService;
import com.autohome.car.api.services.models.ElectricParam;
import com.autohome.car.api.services.models.ElectricSeriesItemPage;
import com.autohome.car.api.services.models.FuelTypeItem;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@DubboService
@RestController
public class ElectricServiceGrpcImpl extends DubboElectricServiceTriple.ElectricServiceImplBase {

    @Autowired
    private ElectricService electricService;

    /**
     * 二期开发
     */
    @Override
    @GetMapping("/v1/App/Electric_ParamBySeriesId.ashx")
    public GetElectricParamBySeriesIdResponse getElectricParamBySeriesId(GetElectricParamBySeriesIdRequest request) {
        ApiResult<List<ElectricParam>> apiResult = electricService.getElectricParamBySeriesId(request);
        List<ElectricParam> result = apiResult.getResult();
        GetElectricParamBySeriesIdResponse.Builder builder = GetElectricParamBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(result)) {
            builder.addAllResult(MessageUtil.toMessageList(result, GetElectricParamBySeriesIdResponse.Result.class));
        }
        return builder.build();
    }

    @GetMapping("/v1/App/Electric_SeriesListByBrandId.ashx")
    @Override
    public GetElectricSeriesListByBrandIdResponse getElectricSeriesListByBrandId(GetElectricSeriesListByBrandIdRequest request) {
        ApiResult<ElectricSeriesItemPage> apiResult = electricService.getElectricSeriesListByBrandId(request);
        ElectricSeriesItemPage result = apiResult.getResult();
        GetElectricSeriesListByBrandIdResponse.Builder builder = GetElectricSeriesListByBrandIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(result)) {
            builder.setResult(MessageUtil.toMessage(result, GetElectricSeriesListByBrandIdResponse.Result.class));
        }
        return builder.build();
    }

    @Override
    @GetMapping("/v1/App/Electric_BrandList.ashx")
    public GetElectricBrandListResponse getElectricBrandList(GetElectricBrandListRequest request) {
        return electricService.getElectricBrandList(request);
    }

    /**
     * 根据车系id列表获取车系下所有燃料类型
     * @param request
     * @return
     */
    @GetMapping("/v1/App/Electric_FuelTypeBySeriesList.ashx")
    @Override
    public GetFuelTypeBySeriesListResponse getFuelTypeBySeriesList(GetFuelTypeBySeriesListRequest request) {
        ApiResult<List<FuelTypeItem>> apiResult = electricService.getFuelTypeBySeriesList(request);
        GetFuelTypeBySeriesListResponse.Builder builder = GetFuelTypeBySeriesListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            List<GetFuelTypeBySeriesListResponse.Result> result =  MessageUtil.toMessageList(apiResult.getResult(), GetFuelTypeBySeriesListResponse.Result.class);
            builder.addAllResult(result);
        }
        return builder.build();
    }

    /**
     *根据品牌id和其他条件获取车系信息
     * @param request
     * @return
     */
    @GetMapping("/v1/Mweb/Electric_GetSeriesListByBrandId.ashx")
    @Override
    public GetElectricSeriesListByBrandIdAndOtherResponse getElectricSeriesListByBrandIdAndOther(GetElectricSeriesListByBrandIdAndOtherRequest request) {
        return electricService.getElectricSeriesListByBrandIdAndOther(request);
    }

    @GetMapping("/v1/App/Electric_SeriesList.ashx")
    @Override
    public GetElectricSeriesListResponse getElectricSeriesList(GetElectricSeriesListRequest request) {
        return electricService.getElectricSeriesList(request);
    }
}