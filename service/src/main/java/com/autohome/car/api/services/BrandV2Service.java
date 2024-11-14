package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v2.brand.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.brand.AppBrandInfo;
import com.autohome.car.api.services.models.brand.BrandBaseItem;
import com.autohome.car.api.services.models.brand.BrandNameItem;

import java.util.List;

public interface BrandV2Service {
    /**
     * 根据品牌ID获取品牌名称
     * @param request
     * @return
     */
    ApiResult<BrandNameItem> getBrandNameById(GetBrandNameByIdRequest request);

    /**
     *根据品牌集合获取品牌基本信息
     * @return
     */
    ApiResult<List<AppBrandInfo>> getBrandInfoByIdList(GetBrandInfoByIdListRequest request);

    /**
     * 根据品牌ID获取品牌model
     * @param request
     * @return
     */
    ApiResult<BrandBaseItem> getBrandById(GetBrandByIdRequest request);

    /**
     * 获取全部品牌列表
     * @param request
     * @return
     */
    GetAllBrandsResponse getAllBrands(GetAllBrandsRequest request);
}
