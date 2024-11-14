package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.pingan.*;

public interface PingAnService {
    /**
     * 平安获取品牌信息
     * @param request
     * @return
     */
    BrandInfoResponse getBrandInfoAll(BrandInfoRequest request);
    /**
     * 平安根据品牌id获取车系信息
     * @param request
     * @return
     */
    SeriesInfoResponse getSeriesInfoByBrandId(SeriesInfoRequest request);


    /**
     * 平安根据车系id获取车型信息
     * @param request
     * @return
     */
    SpecInfoResponse getSpecInfoBySeriesId(SpecInfoRequest request);


}
