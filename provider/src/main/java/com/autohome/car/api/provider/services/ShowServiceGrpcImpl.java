package com.autohome.car.api.provider.services;

import com.autohome.car.api.services.ShowService;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v1.show.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@DubboService
@RestController
public class ShowServiceGrpcImpl extends DubboShowServiceTriple.ShowServiceImplBase {

    @Resource
    private ShowService showService;

    @GetMapping("/v1/carshow/show_info.ashx")
    @Override
    public GetShowInfoResponse getShowInfo(GetShowInfoRequest request) {
        return showService.getShowInfo(request);
    }

    @GetMapping("/v1/carshow/showpic_infobybrandlist.ashx")
    @Override
    public GetShowPicInfoByBrandListResponse getShowPicInfoByBrandList(GetShowPicInfoByBrandListRequest request) {
        return showService.getShowPicInfoByBrandList(request);
    }
    @GetMapping("/v1/carshow/showpic_infobypavilionid.ashx")
    @Override
    public GetShowPicInfoByPavilionIdResponse getShowPicInfoByPavilionId(GetShowPicInfoByPavilionIdRequest request) {
        return showService.getShowPicInfoByPavilionId(request);
    }

    /**
     * 根据车展ID,多个级别ID获取某车展前N条车系图片信息
     * @param request
     * @return
     */
    @GetMapping("/v1/carshow/showpic_infobylevellist.ashx")
    @Override
    public GetShowPicInfoByLevelListResponse getShowPicInfoByLevelList(GetShowPicInfoByLevelListRequest request) {
        return showService.getShowPicInfoByLevelList(request);
    }

    /**
     * 根据车展id,车系id 获取图片列表
     * @param request
     * @return
     */
    @GetMapping("/v1/carshow/showpic_infobyshowidserieid.ashx")
    @Override
    public ShowPicInfoByShowIdSeriesIdResponse getShowPicInfoByShowIdSeriesId(ShowPicInfoByShowIdSeriesIdRequest request) {
        return showService.getShowPicInfoByShowIdSeriesId(request);
    }

    /**
     * 根据厂商id 获取参展中车系列表
     * @param request
     * @return
     */
    @GetMapping("/v1/carshow/show_seriesbyfctid.ashx")
    @Override
    public ShowSeriesByFctIdResponse getShowSeriesByFctId(ShowSeriesByFctIdRequest request) {
        return showService.getShowSeriesByFctId(request);
    }
}