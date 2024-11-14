package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.level.GetLevelInfoRequest;
import autohome.rpc.car.car_api.v1.level.GetLevelInfoResponse;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.services.LevelService;
import com.autohome.car.api.services.basic.LevelBaseService;
import com.autohome.car.api.services.basic.models.LevelBaseInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevelServiceImpl implements LevelService {

    @Resource
    private LevelBaseService levelBaseService;
    /**
     * 获取级别信息
     * @param request
     * @return
     */
    @Override
    public GetLevelInfoResponse getLevelInfo(GetLevelInfoRequest request) {
        GetLevelInfoResponse.Builder builder = GetLevelInfoResponse.newBuilder();
        List<LevelBaseInfo> levelBaseInfos = levelBaseService.getLevelAll();
        GetLevelInfoResponse.Result.Builder resultBuilder = GetLevelInfoResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(levelBaseInfos)){
            //过滤掉等于9的（suv）
            levelBaseInfos = levelBaseInfos.stream().filter(levelBaseInfo -> levelBaseInfo.getId() != 9).collect(Collectors.toList());
            for(LevelBaseInfo levelBaseInfo : levelBaseInfos){
                GetLevelInfoResponse.LevelItem.Builder levelItem = GetLevelInfoResponse.LevelItem.newBuilder();
                levelItem.setLevelid(levelBaseInfo.getId());
                levelItem.setLevelname(null != levelBaseInfo.getName() ? levelBaseInfo.getName() : "");
                levelItem.setLeveldir(null != levelBaseInfo.getDir() ? levelBaseInfo.getDir() : "");
                levelItem.setLeveldescription(null != levelBaseInfo.getDescription() ? levelBaseInfo.getDescription() : "");
                resultBuilder.addItems(levelItem);
            }
        }
        resultBuilder.setTotal(resultBuilder.getItemsCount());
        return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }
}
