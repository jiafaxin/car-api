package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.javascript.SeriesByFactoryRequest;
import autohome.rpc.car.car_api.v1.javascript.SeriesByFactoryResponse;
import autohome.rpc.car.car_api.v1.javascript.SyearAndSpecBySeriesRequest;
import autohome.rpc.car.car_api.v1.javascript.SyearAndSpecBySeriesResponse;
import autohome.rpc.car.car_api.v1.year.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.models.year.YearInfoItem;
import com.autohome.car.api.services.models.year.YearViewItem;

import java.util.List;

public interface YearService {
    /**
     * 根据车系获取年代款列表
     *
     * @param request
     * @return
     */
    ApiResult<YearViewItem> getYearItemsBySeriesId(GetYearItemsBySeriesIdRequest request);

    /**
     * 根据车系id获取年代款列表
     */
    ApiResult<YearInfoItem> getYearInfoBySeriesId(GetYearInfoBySeriesIdRequest request);

    SyearAndSpecBySeriesResponse syearAndSpecBySeries(SyearAndSpecBySeriesRequest request);

    YearParamByYearIdResponse yearParamByYearId(YearParamByYearIdRequest request);

    Year25PictureByYearIdResponse year25PictureByYearId(Year25PictureByYearIdRequest request);

    GetSYearBySeriesResponse getSYearBySeries(GetSYearBySeriesRequest request);

    GetYearInfoByYearIdResponse getYearInfoByYearId(GetYearInfoByYearIdRequest request);

    GetYearColorByYearIdResponse getYearColorByYearId(GetYearColorByYearIdRequest request);

    GetYearInnerColorByYearIdResponse getYearInnerColorByYearId(GetYearInnerColorByYearIdRequest request);

}