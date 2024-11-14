package com.autohome.car.api.provider.services.v2;

import autohome.rpc.car.car_api.v2.series.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.PriceUtils;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.SeriesService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SeriesSpecBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.models.SeriesConfig;
import com.autohome.car.api.services.models.SeriesParamItem;
import com.autohome.car.api.services.models.SeriesPhotoWhiteLogoPage;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@DubboService
@RestController
public class SeriesServiceV2GrpcImpl extends DubboSeriesServiceTriple.SeriesServiceImplBase {

    @Autowired
    private SeriesService seriesService;

    @Autowired
    SeriesConfigService seriesConfigService;

    @Override
    @GetMapping({"/v2/CarPrice/Series_ParamBySeriesId.ashx", "/v2/carprice/series_parambyseriesid.ashx"})
    public GetSeriesConfigResponseV2 getSeriesConfig(GetSeriesConfigRequestV2 request) {
        if(request.getSeriesid()<=0){
            return GetSeriesConfigResponseV2.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        SeriesConfig seriesConfig = seriesConfigService.get(request.getSeriesid());
        GetSeriesConfigResponseV2.Result result = MessageUtil.toMessage(seriesConfig, GetSeriesConfigResponseV2.Result.class);

        GetSeriesConfigResponseV2.Builder builder = GetSeriesConfigResponseV2.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");
        if (result != null) {
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 获取车系的白底车图，最多支持50个车系同时查询
     */
    @Override
    @GetMapping("/v2/carpic/series_photowhitelogobyseriesid.ashx")
    public GetSeriesPhotoWhiteLogoBySeriesIdResponse getSeriesPhotoWhiteLogoBySeriesId(SeriesIdRequest request) {
        ApiResult<SeriesPhotoWhiteLogoPage> apiResult = seriesService.getSeriesPhotoWhiteLogoBySeriesId(request);
        SeriesPhotoWhiteLogoPage result = apiResult.getResult();
        GetSeriesPhotoWhiteLogoBySeriesIdResponse.Builder builder = GetSeriesPhotoWhiteLogoBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(result)) {
            builder.setResult(MessageUtil.toMessage(result, GetSeriesPhotoWhiteLogoBySeriesIdResponse.Result.class));
        }
        return builder.build();
    }


    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SeriesSpecBaseService seriesSpecBaseService;

    @Autowired
    SpecBaseService specBaseService;


    @Override
    @GetMapping("/v2/App/Spec_SpecItmesBySeriesId.ashx")
    public SpecSpecItmesBySeriesIdResponse specSpecItmesBySeriesId(SpecSpecItmesBySeriesIdRequest request) {

        SpecSpecItmesBySeriesIdResponse.Builder builder = SpecSpecItmesBySeriesIdResponse.newBuilder();
        builder.setReturnCode(0).setReturnMsg("成功");
        SpecSpecItmesBySeriesIdResponse.Result.Builder resultBuilder = SpecSpecItmesBySeriesIdResponse.Result.newBuilder();
        resultBuilder.setSeriesid(request.getSeriesid());

        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(request.getSeriesid()).join();
        if(seriesBaseInfo==null){
            return builder.setResult(resultBuilder).build();
        }
        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        List<SpecViewEntity> specs = seriesSpecBaseService.get(request.getSeriesid(), isCV).join();

        if(specs == null){
            specs = new ArrayList<>();
        }

        if(specs.size()>0) {
            switch (request.getState().toLowerCase()) {
                //未上市(0X0001)
                case "0x0001":
                    specs = specs.stream().filter(x -> x.getSpecState() == 0).collect(Collectors.toList());
                    break;
                //即将上市(0X0002)
                case "0x0002":
                    specs = specs.stream().filter(x -> x.getSpecState() == 10).collect(Collectors.toList());
                    break;
                //在产在售(0X0004)
                case "0x0004":
                    specs = specs.stream().filter(x -> x.getSpecState() == 20).collect(Collectors.toList());
                    break;
                //停产在售(0X0008)
                case "0x0008":
                    specs = specs.stream().filter(x -> x.getSpecState() == 30).collect(Collectors.toList());
                    break;
                //停售(0X0010)
                case "0x0010":
                    specs = specs.stream().filter(x -> x.getSpecState() == 40).collect(Collectors.toList());
                    break;
                case "0x000c":
                    specs = specs.stream().filter(x -> x.getSpecState() >= 20 && x.getSpecState() <= 30).collect(Collectors.toList());
                    break;
                case "0x000f":
                    specs = specs.stream().filter(x -> x.getSpecState() <= 30).collect(Collectors.toList());
                    break;
                case "0x001c":
                    specs = specs.stream().filter(x -> x.getSpecState() >= 20).collect(Collectors.toList());
                    break;
                case "0x001f":
                    break;
                default:
                    return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
            }
        }


        if (seriesBaseInfo != null) {
            resultBuilder.setPricedescription(seriesBaseInfo.getPricedescription() == null ? "" : seriesBaseInfo.getPricedescription());
            resultBuilder.setSeriestips("厂商指导价");
            resultBuilder.setSeriesprice(PriceUtils.getStrPrice(seriesBaseInfo.getSeriesPriceMin(), seriesBaseInfo.getSeriesPriceMax()));
            if (seriesBaseInfo.getSeriesState() == 10) {
                if (seriesBaseInfo.getCb() == 1) {
                    resultBuilder.setSeriesprice(PriceUtils.getStrPrice(0, 0));
                    resultBuilder.setSeriestips("订金");
                } else {
                    resultBuilder.setSeriestips("预售价");
                }
            }
        }
        if (specs != null && specs.size() > 0) {
            for (SpecViewEntity dr : specs) {
                SpecBaseInfo specBaseInfo = specBaseService.get(dr.getSpecId()).join();
                String specTitle = "";
                if (dr.getSpecState() == 10) {
                    specTitle = specBaseInfo !=null && specBaseInfo.getIsBooked() == 1 ? "订金" : "预售价";
                } else {
                    specTitle = "厂商指导价";
                }

                resultBuilder.addSpeclist(
                        SpecSpecItmesBySeriesIdResponse.Result.Speclist.newBuilder()
                                .setSpecid(dr.getSpecId())
                                .setSpectitle(specTitle)
                                .setSpecprice(PriceUtils.getStrPrice(dr.getMinPrice(), dr.getMaxPrice()))
                                .setPricedescription(specBaseInfo == null ? "" :( specBaseInfo.getPricedescription()==null?"":specBaseInfo.getPricedescription()))
                );
            }
        }

        return builder.setResult(resultBuilder).build();
    }

    /**
     * 二期开发
     * 根据多个车系id获取车系相关参数信息
     */
    @Override
    @GetMapping({"/v2/carprice/series_parambyserieslist.ashx","/v2/CarPrice/Series_parambyserieslist.ashx"})
    public GetSeriesParamBySeriesListResponse getSeriesParamBySeriesList(SeriesIdRequest request) {
        ApiResult<List<SeriesParamItem>> apiResult = seriesService.getSeriesParamBySeriesListV2(request);
        List<SeriesParamItem> result = apiResult.getResult();
        GetSeriesParamBySeriesListResponse.Builder builder = GetSeriesParamBySeriesListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (CollectionUtils.isNotEmpty(result)) {
            builder.addAllResult(MessageUtil.toMessageList(result, GetSeriesParamBySeriesListResponse.Result.class));
        }
        return builder.build();
    }

    @GetMapping("/v2/car/Config_BagBySeriesId.ashx")
    @Override
    public GetBagInfoBySeriesIdResponse getBagInfoBySeriesId(GetSeriesConfigRequestV2 request) {
        return seriesService.getBagInfoBySeriesIdV2(request);
    }

    @Override
    @GetMapping("/v2/CarPic/Series_PngLogoBySeriesId.ashx")
    public SeriesPngLogoBySeriesIdResponse getSeriesPngLogoBySeriesId (SeriesPngLogoBySeriesIdRequest request){
        return seriesService.getSeriesPngLogoBySeriesId(request);
    }

    /**
     * 根据多个车型id及城市id获取补贴金额
     * @param request
     * @return
     */
    @GetMapping("/v2/CarPrice/Series_BuTieJiaBySeriesId.ashx")
    @Override
    public GetSeriesBuTieJiaBySeriesIdResponse getSeriesBuTieJiaBySeriesId(GetSeriesBuTieJiaBySeriesIdRequest request) {
        return GetSeriesBuTieJiaBySeriesIdResponse.newBuilder()
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @Override
    @GetMapping("/v2/CarPrice/Series_OnlyElectricList.ashx")
    public GetSeriesElectricListResponse getSeriesElectricList(GetSeriesElectricListRequest request) {
        return seriesService.getSeriesElectricList(request);
    }

    @Override
    @GetMapping("/v2/Base/Spec_GetSpecBySeries.ashx")
    public GetSpecBySeriesResponse getSpecBySeries(GetSpecBySeriesRequest request){
        return seriesService.getSpecBySeries(request);
    }

    @Override
    @GetMapping("/v2/Base/Series_GetAllSeries.ashx")
    public GetAllSeriesResponse getAllSeries(GetAllSeriesRequest request){
        return seriesService.getAllSeries(request);
    }

    @Override
    @GetMapping({"/v2/carprice/Series_AllBaseInfo.ashx","/v2/CarPrice/Series_AllBaseInfo.ashx"})
    public GetSeriesAllBaseInfoResponse getSeriesAllBaseInfo(GetSeriesAllBaseInfoRequest request){
        return seriesService.getSeriesAllBaseInfo(request);
    }

    /**
     * 获取包含电动车车系列表 分类
     * @param request
     * @return
     */
    @Override
    @GetMapping("/v2/CarPrice/Select_ElecticList.ashx")
    public SelectElectricListResponse getSelectElectricList(SelectElectricListRequest request) {
        return seriesService.getSelectElectricList(request);
    }
}