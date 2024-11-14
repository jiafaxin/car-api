package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.fct.*;
import autohome.rpc.car.car_api.v2.fct.GetAllFactoryRequest;
import autohome.rpc.car.car_api.v2.fct.GetFctByBrandIdAndStateRequest;
import autohome.rpc.car.car_api.v2.fct.GetFctByBrandIdAndStateResponse;
import autohome.rpc.car.car_api.v2.fct.GetFctNameByIdRequest;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.fct.FctCorrelateInfo;
import com.autohome.car.api.services.models.fct.FctItem;
import com.autohome.car.api.services.models.fct.FctNameItem;

public interface FctService {
    GetFctLogoByFctIdResponse getFctLogoByFctId(GetFctLogoByFctIdRequest request);

    GetGetFctNameByFctIdResponse getFctNameByFctId(GetFctNameByFctIdRequest request);

    GetGetFctNameResponse getFactoryNames(GetFctNameRequest request);

    FactoryAndSeriesByBrandResponse factoryAndSeriesByBrand(FactoryAndSeriesByBrandRequest request);

    ApiResult<FctItem> getAllFactory(GetAllFactoryRequest request);

    /**
     * 据厂商ID获取厂商名称
     * @param request
     * @return
     */
    ApiResult<FctNameItem> getFctNameByIdV2(GetFctNameByIdRequest request);

    /**
     * 根据厂商id获取厂商及厂商下车系信息
     * @param request
     * @return
     */
    ApiResult<FctCorrelateInfo> getFctCorrelateInfoByFctId(GetFctCorrelateInfoByFctIdRequest request);

    /**
     * 根据品牌ID获取品牌下厂商列表
     * @param request
     * @return
     */
    GetFctByBrandIdAndStateResponse getFctByBrandIdAndState(GetFctByBrandIdAndStateRequest request);

    /**
     * 根据厂商id获取厂商model
     * @param request
     * @return
     */
    GetFctInfoByFctIdResponse getFctInfoByFctId(GetFctInfoByFctIdRequest request);

    ShowFctResponse showFct(ShowFctRequest request);
}
