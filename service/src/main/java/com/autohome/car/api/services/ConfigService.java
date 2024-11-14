package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.car.*;
import autohome.rpc.car.car_api.v1.config.GetConfigItemBaseInfoRequest;
import autohome.rpc.car.car_api.v1.config.GetConfigItemBaseInfoResponse;
import autohome.rpc.car.car_api.v3.GetSpecificConfigBySeriesIdRequest;
import autohome.rpc.car.car_api.v3.GetSpecificConfigBySpecListRequest;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.config.SeriesSpecificConfig;
import com.autohome.car.api.services.models.config.SpecificConfig;

public interface ConfigService {

    /**
     * 根据多个车型id获取多个配置信息
     * @param request
     * @return
     */
    ApiResult<SpecificConfig> getSpecificConfigBySpecList(GetSpecificConfigBySpecListRequest request);

    /**
     * 根据车系id获取多个配置信息
     * @param request
     * @return
     */
    ApiResult<SeriesSpecificConfig> getSpecificConfigBySeriesId(GetSpecificConfigBySeriesIdRequest request);

    /**
     * 获取配置项基本信息
     * @param request
     * @return
     */
    GetConfigItemBaseInfoResponse getConfigItemBaseInfo(GetConfigItemBaseInfoRequest request);

    /**
     * 根据车型id获取其同年代款下所有车型的配置（包含选装价格）
     * @param request
     * @return
     */
    GetConfigListOfYearBySpecIdResponse getConfigListOfYearBySpecId(GetConfigListOfYearBySpecIdRequest request);
    /**
     * 根据车型id获取多个参数信息
     * @param request
     * @return
     */
    GetSpecParamListBySpecIdResponse getSpecParamListBySpecId(GetSpecParamListBySpecIdRequest request);

    /**
     * 根据车系id获取多个参数信息
     * @param request
     * @return
     */
    GetSpecParamListBySeriesIdResponse getSpecParamListBySeriesId(GetSpecParamListBySeriesIdRequest request);

    /**
     * 根据车系id获取多个配置信息
     * @param request
     * @return
     */
    GetConfigListBySeriesIdResponse getConfigListBySeriesId(GetConfigListBySeriesIdRequest request);


}
