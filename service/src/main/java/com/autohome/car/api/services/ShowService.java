package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.show.*;

public interface ShowService {
    GetShowInfoResponse getShowInfo(GetShowInfoRequest request);

    GetShowPicInfoByBrandListResponse getShowPicInfoByBrandList(GetShowPicInfoByBrandListRequest request);

    GetShowPicInfoByPavilionIdResponse getShowPicInfoByPavilionId(GetShowPicInfoByPavilionIdRequest request);
    /**
     * 根据车展ID,多个级别ID获取某车展前N条车系图片信息
     * @param request
     * @return
     */
    GetShowPicInfoByLevelListResponse getShowPicInfoByLevelList(GetShowPicInfoByLevelListRequest request);
    /**
     * 根据车展id,车系id 获取图片列表
     * @param request
     * @return
     */
    ShowPicInfoByShowIdSeriesIdResponse getShowPicInfoByShowIdSeriesId(ShowPicInfoByShowIdSeriesIdRequest request);

    /**
     * 根据厂商id 获取参展中车系列表
     * @param request
     * @return
     */
    ShowSeriesByFctIdResponse getShowSeriesByFctId(ShowSeriesByFctIdRequest request);
}
