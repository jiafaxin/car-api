package com.autohome.car.api.provider.services.v2;

import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.FctService;
import com.autohome.car.api.services.basic.FactoryBaseService;
import com.autohome.car.api.services.basic.models.FactoryBaseInfo;
import com.autohome.car.api.services.models.fct.FctItem;
import com.autohome.car.api.services.models.fct.FctNameItem;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v2.fct.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@DubboService
public class FctServiceV2GrpcImpl extends DubboFctServiceTriple.FctServiceImplBase {

    @Resource
    private FctService fctService;

    @Resource
    private FactoryBaseService factoryBaseService;

    @GetMapping("/v2/Base/Fct_GetAllFcts.ashx")
    @Override
    public GetAllFactoryResponse getAllFactory(GetAllFactoryRequest request) {
        ApiResult<FctItem> apiResult = fctService.getAllFactory(request);
        GetAllFactoryResponse.Builder builder = GetAllFactoryResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetAllFactoryResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetAllFactoryResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 根据厂商ID获取厂商名称
     * @param request
     * @return
     */
    @GetMapping("/v2/CarPrice/Fct_GetFctNameById.ashx")
    @Override
    public GetFctNameByIdResponse getFctNameById(GetFctNameByIdRequest request) {
        ApiResult<FctNameItem> apiResult = fctService.getFctNameByIdV2(request);
        GetFctNameByIdResponse.Builder builder = GetFctNameByIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetFctNameByIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetFctNameByIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 根据品牌ID获取品牌下厂商列表
     * @param request
     * @return
     */
    @GetMapping("/v2/CarPrice/Fct_GetFctByBrandId.ashx")
    @Override
    public GetFctByBrandIdAndStateResponse getFctByBrandIdAndState(GetFctByBrandIdAndStateRequest request) {
        return fctService.getFctByBrandIdAndState(request);
    }

    @GetMapping("/v2/CarPrice/Fct_GetFctById.ashx")
    @Override
    public GetFctByIdResponse getFctById(GetFctByIdRequest request) {
        GetFctByIdResponse.Builder builder = GetFctByIdResponse.newBuilder();
        if(request.getFctid() == 0){
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        GetFctByIdResponse.Result.Builder result = GetFctByIdResponse.Result.newBuilder();
        FactoryBaseInfo fctInfo = factoryBaseService.getFactory(request.getFctid());
        if(fctInfo != null){
            result.setFctid(request.getFctid());
            result.setFctname(fctInfo.getName() != null ? fctInfo.getName() : "");
            result.setFctlogo(fctInfo.getLogo() != null ? fctInfo.getLogo().replace("~", "") : "");
            result.setFctofficialurl(fctInfo.getUrl() != null ? fctInfo.getUrl() : "");
            result.setFctfirstletter(fctInfo.getFirstletter() != null ?fctInfo.getFirstletter() : "");
            result.setFctisimport(fctInfo.getIsimport() != null ? fctInfo.getIsimport() : "");
            builder.setResult(result);
        }
        return builder.setReturnCode(0).setReturnMsg("成功").build();
    }
}