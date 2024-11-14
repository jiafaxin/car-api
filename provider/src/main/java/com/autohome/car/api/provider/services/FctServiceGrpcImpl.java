package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.fct.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.data.popauto.FactoryMapper;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.FctService;
import com.autohome.car.api.services.models.fct.FctCorrelateInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@DubboService
@RestController
public class FctServiceGrpcImpl extends DubboFctServiceTriple.FctServiceImplBase {

    @Resource
    private FctService fctService;

    /**
     * 根据厂商id获取厂商代表图
     */
    @Override
    @GetMapping("/v1/carprice/fct_logobyfctid.ashx")
    public GetFctLogoByFctIdResponse getFctLogoByFctId(GetFctLogoByFctIdRequest request) {
        return fctService.getFctLogoByFctId(request);
    }

    /**
     * 根据厂商id获取厂商名称
     */
    @Override
    @GetMapping("/v1/CarPrice/Fct_GetFctNameByFctId.ashx")
    public GetGetFctNameByFctIdResponse getFctNameByFctId(GetFctNameByFctIdRequest request) {
        return fctService.getFctNameByFctId(request);
    }

    /**
     * 获取全部厂商名称
     */
    @Override
    @GetMapping("/v1/carprice/factory_getallname.ashx")
    public GetGetFctNameResponse getFactoryName(GetFctNameRequest request) {
        return fctService.getFactoryNames(request);
    }

    @Override
    @GetMapping("/v1/javascript/factoryandseriesbybrand.ashx")
    public FactoryAndSeriesByBrandResponse factoryAndSeriesByBrand(FactoryAndSeriesByBrandRequest request){
        return fctService.factoryAndSeriesByBrand(request);
    }

    /**
     * 根据厂商id获取厂商及厂商下车系信息
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/fct_correlateinfobyfctid.ashx")
    @Override
    public GetFctCorrelateInfoByFctIdResponse getFctCorrelateInfoByFctId(GetFctCorrelateInfoByFctIdRequest request) {
        ApiResult<FctCorrelateInfo> apiResult = fctService.getFctCorrelateInfoByFctId(request);
        GetFctCorrelateInfoByFctIdResponse.Builder builder = GetFctCorrelateInfoByFctIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetFctCorrelateInfoByFctIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetFctCorrelateInfoByFctIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 根据厂商id获取厂商model
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/fct_infobyfctid.ashx")
    @Override
    public GetFctInfoByFctIdResponse getFctInfoByFctId(GetFctInfoByFctIdRequest request) {
        return fctService.getFctInfoByFctId(request);
    }

    @Override
    @GetMapping("/v1/carshow/show_fct.ashx")
    public ShowFctResponse showFct(ShowFctRequest request) {
        return fctService.showFct(request);
    }
}
