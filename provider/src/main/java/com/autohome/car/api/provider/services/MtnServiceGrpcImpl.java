package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.mtn.*;
import autohome.rpc.car.car_api.v1.pic.Spec25PictureBySpecListResponse;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.DESUtil;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.common.StringIntegerUtils;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.MaintainService;
import com.autohome.car.api.services.models.IsHaveMaintain;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

@RestController
@DubboService
public class MtnServiceGrpcImpl extends DubboMtnServiceTriple.MtnServiceImplBase {

    @Autowired
    private MaintainService maintainService;

    @GetMapping("/mtn/IsHaveMaintain.ashx")
    @Override
    public IsHaveMaintainResponse isHaveMaintain(IsHaveMaintainRequest request) {
        ApiResult<IsHaveMaintain> apiResult = maintainService.isHaveMaintain(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || null == apiResult.getResult()){
            return IsHaveMaintainResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        IsHaveMaintainResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),IsHaveMaintainResponse.Result.class);
        return IsHaveMaintainResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();

    }

    @Override
    @GetMapping("/Verify/PD.ashx")
    public DesPDResponse desPD(DesPDRequest request){
        DesPDResponse.Builder builder = DesPDResponse.newBuilder();
        DesPDResponse.Result.Builder result = DesPDResponse.Result.newBuilder();
        String s = request.getS();
        if(StringUtils.isBlank(s)){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        result.setContent(DESUtil.decrypt(request.getS()));
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return  builder.build();
    }



}