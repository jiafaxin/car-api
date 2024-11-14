package com.autohome.car.api.provider.services.v3;

import autohome.rpc.car.car_api.v3.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.ConfigService;
import com.autohome.car.api.services.models.config.SeriesSpecificConfig;
import com.autohome.car.api.services.models.config.SpecificConfig;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@DubboService
public class ConfigServiceV3GrpcImpl extends DubboConfigServiceTriple.ConfigServiceImplBase {

    @Resource
    private ConfigService configService;

    @GetMapping("/v3/CarPrice/SpecificConfig_GetListBySpecList.ashx")
    @Override
    public GetSpecificConfigBySpecListResponse getSpecificConfigBySpecList(GetSpecificConfigBySpecListRequest request) {
        ApiResult<SpecificConfig> apiResult = configService.getSpecificConfigBySpecList(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || null == apiResult.getResult()){
            return GetSpecificConfigBySpecListResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetSpecificConfigBySpecListResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetSpecificConfigBySpecListResponse.Result.class);
        return GetSpecificConfigBySpecListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    /**
     * 根据车系id获取多个配置信息
     * @param request
     * @return
     */
    @GetMapping("/v3/CarPrice/SpecificConfig_GetListBySeriesId.ashx")
    @Override
    public GetSpecificConfigBySeriesIdResponse getSpecificConfigBySeriesId(GetSpecificConfigBySeriesIdRequest request) {
        ApiResult<SeriesSpecificConfig> apiResult = configService.getSpecificConfigBySeriesId(request);
        GetSpecificConfigBySeriesIdResponse.Builder builder = GetSpecificConfigBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetSpecificConfigBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetSpecificConfigBySeriesIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }
}