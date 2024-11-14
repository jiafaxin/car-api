package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.electric.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.ElectricParam;
import com.autohome.car.api.services.models.ElectricSeriesItemPage;
import com.autohome.car.api.services.models.FuelTypeItem;

import java.util.List;

public interface ElectricService {
    ApiResult<List<ElectricParam>> getElectricParamBySeriesId(GetElectricParamBySeriesIdRequest request);

    ApiResult<ElectricSeriesItemPage> getElectricSeriesListByBrandId(GetElectricSeriesListByBrandIdRequest request);

    GetElectricBrandListResponse getElectricBrandList(GetElectricBrandListRequest request);

    /**
     * 根据车系id列表获取车系下所有燃料类型
     * @param request
     * @return
     */
    ApiResult<List<FuelTypeItem>> getFuelTypeBySeriesList(GetFuelTypeBySeriesListRequest request);

    /**
     * 根据品牌id和其他条件获取车系信息
     * @param request
     * @return
     */
    GetElectricSeriesListByBrandIdAndOtherResponse getElectricSeriesListByBrandIdAndOther(GetElectricSeriesListByBrandIdAndOtherRequest request);

    GetElectricSeriesListResponse getElectricSeriesList(GetElectricSeriesListRequest request);

 }
