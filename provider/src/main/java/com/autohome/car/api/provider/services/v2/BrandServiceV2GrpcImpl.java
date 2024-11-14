package com.autohome.car.api.provider.services.v2;

import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.BrandV2Service;
import com.autohome.car.api.services.models.brand.AppBrandInfo;
import com.autohome.car.api.services.models.brand.BrandBaseItem;
import com.autohome.car.api.services.models.brand.BrandNameItem;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v2.brand.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@DubboService
public class BrandServiceV2GrpcImpl extends DubboBrandServiceTriple.BrandServiceImplBase {

    @Resource
    private BrandV2Service brandV2Service;
    /**
     * 根据品牌ID获取品牌名称
     * @param request
     * @return
     */
    @GetMapping("/v2/CarPrice/Brand_GetBrandNameById.ashx")
    @Override
    public GetBrandNameByIdResponse getBrandNameById(GetBrandNameByIdRequest request) {
        ApiResult<BrandNameItem> apiResult = brandV2Service.getBrandNameById(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || null == apiResult.getResult()){
            return GetBrandNameByIdResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetBrandNameByIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetBrandNameByIdResponse.Result.class);
        return GetBrandNameByIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    /**
     * 根据品牌集合获取品牌基本信息
     * @param request
     * @return
     */
    @GetMapping("/v2/App/Brand_GetBrandInfoByIdList.ashx")
    @Override
    public GetBrandInfoByIdListResponse getBrandInfoByIdList(GetBrandInfoByIdListRequest request) {
        ApiResult<List<AppBrandInfo>> apiResult = brandV2Service.getBrandInfoByIdList(request);
        GetBrandInfoByIdListResponse.Builder builder = GetBrandInfoByIdListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            List<GetBrandInfoByIdListResponse.Result> result = MessageUtil.toMessageList(apiResult.getResult(), GetBrandInfoByIdListResponse.Result.class);
            builder.addAllResult(result);
        }
        return builder.build();
    }
    /**
     * 根据品牌ID获取品牌model
     * @param request
     * @return
     */
    @GetMapping("/v2/CarPrice/Brand_GetBrandById.ashx")
    @Override
    public GetBrandByIdResponse getBrandById(GetBrandByIdRequest request) {
        ApiResult<BrandBaseItem> apiResult = brandV2Service.getBrandById(request);
        GetBrandByIdResponse.Builder builder = GetBrandByIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetBrandByIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetBrandByIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 获取全部品牌列表
     * @param request
     * @return
     */
    @GetMapping("/v2/Base/Brand_GetAllBrands.ashx")
    @Override
    public GetAllBrandsResponse getAllBrands(GetAllBrandsRequest request) {
        return brandV2Service.getAllBrands(request);
    }
}