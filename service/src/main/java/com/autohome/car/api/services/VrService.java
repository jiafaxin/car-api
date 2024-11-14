package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.vr.*;

public interface VrService {
    /**
     * 车系id集合获取vr信息
     * @param request
     * @return
     */
    GetVRUrlAndCoverImgBySeriesIdListResponse getVRUrlAndCoverImgBySeriesIdList(GetVRUrlAndCoverImgBySeriesIdListRequest request);

    /**
     * 车系id或者车型id获取vr信息
     * @param request
     * @return
     */
    GetVRUrlAndCoverImageResponse getVRUrlAndCoverImage(GetVRUrlAndCoverImageRequest request);

    GetIndexSlideVrResponse getIndexSlideVr(GetIndexSlideVrRequest request);
}
