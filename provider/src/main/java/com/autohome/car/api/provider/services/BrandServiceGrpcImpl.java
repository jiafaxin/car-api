package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.brand.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.entities.BrandPicListEntity;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.basic.BrandBaseService;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.impls.AutoCacheServiceImpl;
import com.autohome.car.api.services.impls.BrandServiceImpl;
import com.autohome.car.api.services.models.brand.BrandCorrelateInfo;
import com.autohome.car.api.services.models.brand.BrandInfo;
import com.autohome.car.api.services.models.brand.BrandLogoItem;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@DubboService
@RestController
public class BrandServiceGrpcImpl extends DubboBrandServiceTriple.BrandServiceImplBase {

    @Autowired
    BrandBaseService brandBaseService;

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    AutoCacheServiceImpl autoCacheService;

    @Override
    @GetMapping("/v2/CarPrice/Brand_GetBrandLogo.ashx")
    public GetBrandLogoResponse getBrandLogo(GetBrandLogoRequest request) {
        if(request.getBrandid()<=0){
            return GetBrandLogoResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        BrandBaseInfo brandBaseInfo = brandBaseService.get(request.getBrandid()).join();

        GetBrandLogoResponse.Result result = GetBrandLogoResponse.Result.newBuilder()
                .setBrandid(request.getBrandid())
                .setBrandlogo(brandBaseInfo==null ? "" : ImageUtil.getFullImagePath(brandBaseInfo.getLogo()))
                .setBrandname(brandBaseInfo==null ? "" : brandBaseInfo.getName()).build();


        GetBrandLogoResponse response = GetBrandLogoResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(result)
                .build();
        return response;
    }

    @Override
    @GetMapping("/v1/carprice/brand_logobybrandid.ashx")
    public GetBrandLogoByBrandIdResponse getBrandLogoByBrandId(GetBrandLogoByBrandIdRequest request) {
        ApiResult<BrandLogoItem> apiResult = brandService.GetBrandLogoByBrandId(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()){
            return GetBrandLogoByBrandIdResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetBrandLogoByBrandIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),GetBrandLogoByBrandIdResponse.Result.class);
        return GetBrandLogoByBrandIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    /**
     * 根据品牌获取关联厂商及车系信息
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/brand_correlateinfobybrandid.ashx")
    @Override
    public GetBrandCorrelateInfoByBrandIdResponse getBrandCorrelateInfoByBrandId(GetBrandCorrelateInfoByBrandIdRequest request) {
        ApiResult<BrandCorrelateInfo> apiResult = brandService.getBrandCorrelateInfoByBrandId(request);
        if(apiResult.getReturncode() != ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() || null == apiResult.getResult()){
            return GetBrandCorrelateInfoByBrandIdResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetBrandCorrelateInfoByBrandIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),GetBrandCorrelateInfoByBrandIdResponse.Result.class);
        return GetBrandCorrelateInfoByBrandIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    /**
     * 根据品牌id获取品牌model
     * @param request
     * @return
     */
    @Override
    @GetMapping("/v1/carprice/brand_infobybrandid.ashx")
    public GetBrandInfoResponse getBrandInfo(GetBrandInfoRequest request) {
        ApiResult<BrandInfo> apiResult = brandService.GetBrandInfoByBrandId(request);
        GetBrandInfoResponse.Builder builder = GetBrandInfoResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || apiResult.getResult() == null) {
            return builder.build();
        }
        GetBrandInfoResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetBrandInfoResponse.Result.class);
        return builder.setResult(result).build();
    }

    @Override
    @GetMapping("/v1/javascript/factorybybrand.ashx")
    public FactoryByBrandResponse getFactoryByBrand(FactoryByBrandRequest request) {
        return brandService.getFactoryByBrand(request);
    }

    @Override
    @GetMapping("/v1/app/Brand_PriceMenu.ashx")
    public BrandPriceMenuResponse brandPriceMenu(BrandPriceMenuRequest request) {
        return brandService.brandPriceMenu(request);
    }
    @Override
    @GetMapping("/v1/carprice/brand_logobybrandlist.ashx")
    public GetBrandLogoByIdsResponse getBrandLogoByIds(GetBrandLogoByIdsRequest request) {
        return brandService.getBrandLogoByIds(request);
    }
    @Override
    @GetMapping("/v1/carprice/brand_getallname.ashx")
    public GetAllBrandNameResponse getAllBrandName(GetAllBrandNameRequest request) {
        return brandService.getAllBrandName(request);
    }

    @GetMapping("/v1/carprice/brand_infobyseriesid.ashx")
    @Override
    public BrandInfoBySeriesIdResponse brandInfoBySeriesId(BrandInfoBySeriesIdRequest request){
        return brandService.brandInfoBySeriesId(request);
    }

    @Override
    @GetMapping("/v1/javascript/brand.ashx")
    public BrandByStateAndTypeResponse brandByStateAndType(BrandByStateAndTypeRequest request){
        return brandService.brandByStateAndType(request);
    }

    @Override
    @GetMapping("/v1/javascript/seriesbybrand.ashx")
    public SeriesByBrandResponse seriesByBrand(SeriesByBrandRequest request){
        return brandService.seriesByBrand(request);
    }

    @Override
    @GetMapping("/NewEnergy/Dingzhi_BrandElectric.ashx")
    public BrandDingZhiElectricResponse brandDingZhiElectric(BrandDingZhiElectricRequest request){
        return brandService.brandDingZhiElectric(request);
    }
    /**
     * *根据品牌id获取品牌名称
     * @param request
     * @return
     */
    @GetMapping("/v1/CarPrice/Brand_GetBrandNameByBrandId.ashx")
    @Override
    public GetBrandNameByBrandIdResponse getBrandNameByBrandId(GetBrandNameByBrandIdRequest request) {
        GetBrandNameByBrandIdResponse.Builder builder = GetBrandNameByBrandIdResponse.newBuilder();
        int brandId = request.getBrandid();
        if(brandId <= 0){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        GetBrandNameByBrandIdResponse.Result.Builder resultBuilder = GetBrandNameByBrandIdResponse.Result.newBuilder();
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        resultBuilder.setBrandid(brandId)
                .setBrandname((null != brandBaseInfo && null != brandBaseInfo.getName()) ? brandBaseInfo.getName() : "");
        return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 根据品牌首字母和品牌热度排序，返回品牌列表
     * @param request
     * @return
     */
    @GetMapping("/v1/Mweb/Brand_List.ashx")
    @Override
    public GetBrandListResponse getBrandList(GetBrandListRequest request) {
        return brandService.getBrandList(request);
    }

    /**
     * 获取图片库品牌菜单
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/brand_menu.ashx")
    @Override
    public GetBrandMenuResponse getBrandMenu(GetBrandMenuRequest request) {
        return brandService.getBrandMenu(request);
    }

    @GetMapping("/v1/carshow/show_brandinfobypavilionid.ashx")
    @Override
    public GetBrandByPavilionIdResponse getBrandByPavilionId(GetBrandByPavilionIdRequest request) {
        return brandService.getBrandByPavilionId(request);
    }

    /**
     * 根据品牌名称获取品牌id
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/brand_idbybrandname.ashx")
    @Override
    public GetBrandIdByBrandNameResponse getBrandIdByBrandName(GetBrandIdByBrandNameRequest request) {
        return brandService.getBrandIdByBrandName(request);
    }

    /**
     * 获取报价库品牌菜单
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/brand_menu.ashx")
    @Override
    public GetBrandMenuPriceResponse getBrandMenuPrice(GetBrandMenuPriceRequest request) {
        return brandService.getBrandMenuPrice(request);
    }

    /**
     * 根据首字母获取报价库品牌菜单(分页)
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/brand_menuwithpagebyfirstletter.ashx")
    @Override
    public GetBrandMenuWithPageByFirstLetterResponse getBrandMenuWithPageByFirstLetter(GetBrandMenuWithPageByFirstLetterRequest request) {
        return brandService.getBrandMenuWithPageByFirstLetter(request);
    }

    @GetMapping("/v1/carshow/show_brandinfobypavilionlistfirstletter.ashx")
    @Override
    public BrandShowByPavilionLetterResponse brandShowByPavilionLetter(BrandShowByPavilionLetterRequest request){
        return brandService.brandShowByPavilionLetter(request);
    }

    @GetMapping("/v1/Mweb/Pic_Brand_List.ashx")
    @Override
    public GetPicBrandListHotResponse getPicBrandListHot(GetPicBrandListHotRequest request){
        GetPicBrandListHotResponse.Result.Builder result = GetPicBrandListHotResponse.Result.newBuilder();
        List<Integer> notShowBrand = Arrays.asList(215, 266);
        List<BrandPicListEntity> brandList = autoCacheService.getPicBrandListAll();
        for(BrandPicListEntity item : brandList){
            if(notShowBrand.contains(item.getBrandId())){
                continue;
            }
            result.addBrandlist(GetPicBrandListHotResponse.BrandItem.newBuilder()
                    .setBrandid(item.getBrandId())
                    .setBrandname(item.getBrandName() != null ? item.getBrandName() : "")
                    .setFirstletter(item.getFirstLetter() != null ? item.getFirstLetter() : "")
                    .setLogo(ImageUtil.getFullImagePathByPrefix(item.getImg() != null ? item.getImg() : "", "100x100_f40_"))
                    .setOrdernum(item.getBrandOrder()));
        }
        return GetPicBrandListHotResponse.newBuilder().setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }
    @Override
    @GetMapping("/v1/CarPrice/Brand_Hot.ashx")
    public BrandHotResponse brandHot(BrandHotRequest request){
        return brandService.brandHot(request);
    }

}