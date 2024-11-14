package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v2.brand.*;
import com.autohome.car.api.common.*;
import com.autohome.car.api.data.popauto.entities.AppBrandInfoEntity;
import com.autohome.car.api.data.popauto.entities.BrandBaseEntity;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.BrandV2Service;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.basic.BrandBaseService;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.brand.AppBrandInfo;
import com.autohome.car.api.services.models.brand.BrandBaseItem;
import com.autohome.car.api.services.models.brand.BrandNameItem;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.LocalDateUtils.DATE_TIME_PATTERN_TWO;

@Service
public class BrandV2ServiceImpl implements BrandV2Service {

    @Resource
    private BrandBaseService brandBaseService;

    @Resource
    private CommService commService;

    @Resource
    private AutoCacheService autoCacheService;

    /**
     * 根据品牌ID获取品牌名称
     * @param request
     * @return
     */
    @Override
    public ApiResult<BrandNameItem> getBrandNameById(GetBrandNameByIdRequest request) {
        int brandId = request.getBrandid();
        if(brandId == 0){
            return new ApiResult<>(null, ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        BrandNameItem brandNameItem = new BrandNameItem();
        brandNameItem.setBrandid(brandId);
        brandNameItem.setBrandname(null != brandBaseInfo ? brandBaseInfo.getName():"");
        return new ApiResult<>(brandNameItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据品牌集合获取品牌基本信息
     * @param request
     * @return
     */
    @Override
    public ApiResult<List<AppBrandInfo>> getBrandInfoByIdList(GetBrandInfoByIdListRequest request) {
        List<Integer> brandIdList = CommonFunction.getListFromStr(request.getBrandid());
        if(CollectionUtils.isEmpty(brandIdList)){
            return new ApiResult<>(null,ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        List<AppBrandInfo> appBrandInfos = new ArrayList<>();
        //查询数据
        List<BrandBaseInfo> brandBaseInfos = commService.getBrandBaseInfoList(brandIdList);
        if(!CollectionUtils.isEmpty(brandBaseInfos)){
            //排序分组
            List<BrandBaseInfo> brandBaseInfoList = brandBaseInfos.stream().sorted(Comparator.comparing(BrandBaseInfo::getId)).
                    collect(Collectors.toList());
            //遍历
            for(BrandBaseInfo brandBaseInfo : brandBaseInfoList){
                AppBrandInfo appBrandInfo = new AppBrandInfo();
                appBrandInfo.setBrandid(brandBaseInfo.getId());
                List<AppBrandInfo.BrandInfo> brandInfos = new ArrayList<>();
                List<AppBrandInfoEntity> appBrandInfoEntities = brandBaseInfo.getAppBrandInfos();
                if(!CollectionUtils.isEmpty(appBrandInfoEntities)){
                    appBrandInfoEntities.forEach(appBrandInfoEntity -> {
                        AppBrandInfo.BrandInfo brandInfo = new AppBrandInfo.BrandInfo(appBrandInfoEntity.getTitle(),appBrandInfoEntity.getDescription());
                        brandInfos.add(brandInfo);
                    });
                    appBrandInfo.setList(brandInfos);
                    appBrandInfos.add(appBrandInfo);
                }
            }
        }
        return new ApiResult<>(appBrandInfos,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据品牌ID获取品牌model
     * @param request
     * @return
     */
    @Override
    public ApiResult<BrandBaseItem> getBrandById(GetBrandByIdRequest request) {
        int brandId = request.getBrandid();
        if(brandId == 0){
            return new ApiResult<>(null,ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        if(null != brandBaseInfo){
            BrandBaseItem brandBaseItem = new BrandBaseItem();
            brandBaseItem.setBrandid(brandBaseInfo.getId());
            brandBaseItem.setBrandname(brandBaseInfo.getName());
            brandBaseItem.setCountry(brandBaseInfo.getCountry());
            brandBaseItem.setBrandfirstletter(brandBaseInfo.getFirstLetter());
            brandBaseItem.setBrandlogo(ImageUtil.getFullImagePathWithoutReplace(brandBaseInfo.getLogo()));
            brandBaseItem.setBrandofficialurl(brandBaseInfo.getUrl());
            return new ApiResult<>(brandBaseItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }
        return new ApiResult<>(null,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 获取全部品牌列表
     * @param request
     * @return
     */
    @Override
    public GetAllBrandsResponse getAllBrands(GetAllBrandsRequest request) {
        GetAllBrandsResponse.Builder builder = GetAllBrandsResponse.newBuilder();
        GetAllBrandsResponse.Result.Builder resultBuilder = GetAllBrandsResponse.Result.newBuilder();
        List<BrandBaseEntity> baseEntities = autoCacheService.getAllBrandName();
        baseEntities = baseEntities.stream().sorted(Comparator.comparing(brandBaseEntity -> brandBaseEntity.getId())).collect(Collectors.toList());
        if(request.getId() > 0){
            baseEntities = baseEntities.stream().filter(brandBaseEntity -> brandBaseEntity.getId() > request.getId()).collect(Collectors.toList());
        }
        for(BrandBaseEntity brandBaseEntity : baseEntities){
            GetAllBrandsResponse.BrandItem.Builder brandItem = GetAllBrandsResponse.BrandItem.newBuilder();
            brandItem.setId(brandBaseEntity.getId());
            brandItem.setName(null != brandBaseEntity.getName() ? HtmlUtils.decode(brandBaseEntity.getName()) : "");
            brandItem.setUrl(null != brandBaseEntity.getUrl() ? brandBaseEntity.getUrl() : "");
            brandItem.setCountryid(String.valueOf(brandBaseEntity.getCountryId()));
            brandItem.setCountry(null != brandBaseEntity.getCountry() ? brandBaseEntity.getCountry() : "");
            brandItem.setFirstletter(null != brandBaseEntity.getFirstLetter() ? brandBaseEntity.getFirstLetter() : "");
            brandItem.setCreatetime(null != brandBaseEntity.getCreateTime() ?
                    LocalDateUtils.format(brandBaseEntity.getCreateTime(), DATE_TIME_PATTERN_TWO) : "");
            brandItem.setEdittime(null != brandBaseEntity.getEditTime() ?
                    LocalDateUtils.format(brandBaseEntity.getEditTime(), DATE_TIME_PATTERN_TWO) : "");
            brandItem.setLogo(null != brandBaseEntity.getLogo() ? ImageUtil.getFullImagePathWithoutReplace(brandBaseEntity.getLogo()) : "");
            resultBuilder.addBranditems(brandItem);
        }
        resultBuilder.setTotal(resultBuilder.getBranditemsCount());
        return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }
}
