package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.brand.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.brand.BrandCorrelateInfo;
import com.autohome.car.api.services.models.brand.BrandInfo;
import com.autohome.car.api.services.models.brand.BrandLogoItem;

public interface BrandService {
    /**
     * 根据品牌id获取品牌代表图
     * @param request
     * @return
     */
    ApiResult<BrandLogoItem> GetBrandLogoByBrandId(GetBrandLogoByBrandIdRequest request);

    /**
     * 根据品牌获取关联厂商及车系信息
     * @param request
     * @return
     */
    ApiResult<BrandCorrelateInfo> getBrandCorrelateInfoByBrandId(GetBrandCorrelateInfoByBrandIdRequest request);

    /**
     * 根据品牌id获取品牌model
     */
    ApiResult<BrandInfo> GetBrandInfoByBrandId(GetBrandInfoRequest request);

    /**
     * 通过品牌获得产商信息
     * @param request
     * @return
     */
    FactoryByBrandResponse getFactoryByBrand(FactoryByBrandRequest request);

    BrandPriceMenuResponse brandPriceMenu(BrandPriceMenuRequest request);

    GetBrandLogoByIdsResponse getBrandLogoByIds(GetBrandLogoByIdsRequest request);

    GetAllBrandNameResponse getAllBrandName(GetAllBrandNameRequest request);

    BrandInfoBySeriesIdResponse brandInfoBySeriesId(BrandInfoBySeriesIdRequest request);

    BrandByStateAndTypeResponse brandByStateAndType(BrandByStateAndTypeRequest request);

    SeriesByBrandResponse seriesByBrand(SeriesByBrandRequest request);

    BrandDingZhiElectricResponse brandDingZhiElectric(BrandDingZhiElectricRequest request);

    /**
     * 根据品牌首字母和品牌热度排序，返回品牌列表
     * @param request
     * @return
     */
    GetBrandListResponse getBrandList(GetBrandListRequest request);

    /**
     * 获取图片库品牌菜单
     * @param request
     * @return
     */
    GetBrandMenuResponse getBrandMenu(GetBrandMenuRequest request);

    /**
     * 根据品牌名称获取品牌id
     * @param request
     * @return
     */
    GetBrandIdByBrandNameResponse getBrandIdByBrandName(GetBrandIdByBrandNameRequest request);

    /**
     * 获取报价库品牌菜单
     * @param request
     * @return
     */
    GetBrandMenuPriceResponse getBrandMenuPrice(GetBrandMenuPriceRequest request);

    /**
     * 根据首字母获取报价库品牌菜单(分页)
     * @param request
     * @return
     */
    GetBrandMenuWithPageByFirstLetterResponse getBrandMenuWithPageByFirstLetter(GetBrandMenuWithPageByFirstLetterRequest request);

    GetBrandByPavilionIdResponse getBrandByPavilionId(GetBrandByPavilionIdRequest request);

    BrandShowByPavilionLetterResponse brandShowByPavilionLetter(BrandShowByPavilionLetterRequest request);


    BrandHotResponse brandHot(BrandHotRequest request);
}
