package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.vr.*;
import com.autohome.car.api.services.VrService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@DubboService
public class VrServiceGrpcImpl extends DubboVrServiceTriple.VrServiceImplBase {

    @Resource
    private VrService vrService;
    /**
     * 车系id集合获取vr信息
     * @param request
     * @return
     */
    @GetMapping("/VR/App/Series_GetVRUrlAndCoverImgBySeriesIdList.ashx")
    @Override
    public GetVRUrlAndCoverImgBySeriesIdListResponse getVRUrlAndCoverImgBySeriesIdList(GetVRUrlAndCoverImgBySeriesIdListRequest request) {
       return vrService.getVRUrlAndCoverImgBySeriesIdList(request);
    }

    /**
     * 车系id或者车型id获取vr信息
     * @param request
     * @return
     */
    @GetMapping("/VR/App/Series_GetVRUrlAndCoverImage.ashx")
    @Override
    public GetVRUrlAndCoverImageResponse getVRUrlAndCoverImage(GetVRUrlAndCoverImageRequest request) {
        return vrService.getVRUrlAndCoverImage(request);
    }

    @GetMapping("/v1/www/Index_Slidevr.ashx")
    @Override
    public GetIndexSlideVrResponse getIndexSlideVr(GetIndexSlideVrRequest request) {
        return vrService.getIndexSlideVr(request);
    }
}