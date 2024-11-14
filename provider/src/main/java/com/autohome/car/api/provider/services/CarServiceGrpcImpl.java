package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.car.*;
import com.autohome.car.api.data.popauto.OptimizeParamItemInfoMapper;
import com.autohome.car.api.data.popauto.entities.OptimizeParamItemInfoEntity;
import com.autohome.car.api.services.ConfigService;
import com.autohome.car.api.services.FindCarService;
import com.autohome.car.api.services.basic.ConfigListService;
import com.autohome.car.api.services.basic.models.ConfigTypeBaseInfo;
import com.autohome.car.api.services.impls.FindCarServiceImpl;
import com.autohome.car.api.services.impls.SpecServiceImpl;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService
@RestController
public class CarServiceGrpcImpl extends DubboCarServiceTriple.CarServiceImplBase {


    @Autowired
    ConfigListService configListService;

    @Autowired
    OptimizeParamItemInfoMapper optimizeParamItemInfoMapper;

    @Autowired
    ConfigService configService;

    @Autowired
    SpecServiceImpl specServiceImpl;

    @Autowired
    FindCarServiceImpl findCarServiceImpl;

    @Autowired
    private FindCarService findCarService;


    @Override
    @GetMapping("/v1/car/ParamConfigBaseInfo.ashx")
    public ParamConfigBaseInfoResponse paramConfigBaseInfo(ParamConfigBaseInfoRequest request) {
        ParamConfigBaseInfoResponse.Builder response = ParamConfigBaseInfoResponse.newBuilder();
        response.setReturnCode(0);
        response.setReturnMsg("成功");

        if(request.getType()==2) {
            List<ConfigTypeBaseInfo> list = configListService.get();
            if (list == null || list.size() == 0)
                return response.build();

            for (ConfigTypeBaseInfo typeItem : list) {
                ParamConfigBaseInfoResponse.Result.Builder item = ParamConfigBaseInfoResponse.Result.newBuilder();
                item.setName(typeItem.getTypeName());
                item.addAllList(typeItem.getItems().stream().map(x -> x.getItemName()).collect(Collectors.toList()));
                response.addResult(item);
            }
        }else{
            List<OptimizeParamItemInfoEntity> list = optimizeParamItemInfoMapper.getOptimizeParamItemInfoEntity();
            if(list==null||list.size()==0)
                return response.build();

            Map<String,List<OptimizeParamItemInfoEntity>> map = new LinkedHashMap<>();
            for (OptimizeParamItemInfoEntity optimizeParamItemInfoEntity : list) {
                if(!map.containsKey(optimizeParamItemInfoEntity.getItem())){
                    map.put(optimizeParamItemInfoEntity.getItem(),new ArrayList<>());
                }
                map.get(optimizeParamItemInfoEntity.getItem()).add(optimizeParamItemInfoEntity);
            }


            map.forEach((k,v)->{
                if(StringUtils.isBlank(k))
                    return;
                if(v==null||v.size()==0)
                    return;
                ParamConfigBaseInfoResponse.Result.Builder item = ParamConfigBaseInfoResponse.Result.newBuilder();
                item.setName(k);
                item.addAllList(v.stream().filter(x->StringUtils.isNotBlank(x.getName())).map(x->x.getName()).collect(Collectors.toList()));
                response.addResult(item);
            });
        }
        return response.build();
    }

    @GetMapping("/v1/car/config_listbyyearid.ashx")
    @Override
    public ConfigListByYearIdResponse configListByYearId(ConfigListByYearIdRequest request) {
        return specServiceImpl.getConfigListByYearId(request);
    }

    @Override
    @GetMapping("/v1/car/Www_LevelFindCar.ashx")
    public LevelFindCarResponse levelFindCar(LevelFindCarRequest request) {
        return findCarServiceImpl.levelFindCar(request);
    }

    /**
     * 根据车型id获取其同年代款下所有车型的配置（包含选装价格）
     * @param request
     * @return
     */
    @GetMapping("/v1/car/config_listofyearbyspecid.ashx")
    @Override
    public GetConfigListOfYearBySpecIdResponse getConfigListOfYearBySpecId(GetConfigListOfYearBySpecIdRequest request) {
        return configService.getConfigListOfYearBySpecId(request);
    }
    /**
     * 根据车型id获取多个参数信息
     * @param request
     * @return
     */
    @GetMapping("/v1/car/spec_paramlistbyspecId.ashx")
    @Override
    public GetSpecParamListBySpecIdResponse getSpecParamListBySpecId(GetSpecParamListBySpecIdRequest request) {
        return configService.getSpecParamListBySpecId(request);
    }

    /**
     * 根据车系id获取多个参数信息
     * @param request
     * @return
     */
    @GetMapping("/v1/car/spec_paramlistbyseriesid.ashx")
    @Override
    public GetSpecParamListBySeriesIdResponse getSpecParamListBySeriesId(GetSpecParamListBySeriesIdRequest request) {
        return configService.getSpecParamListBySeriesId(request);
    }

    /**
     * 根据车系id获取多个配置信息
     * @param request
     * @return
     */
    @GetMapping("/v1/car/config_listbyseriesId.ashx")
    @Override
    public GetConfigListBySeriesIdResponse getConfigListBySeriesId(GetConfigListBySeriesIdRequest request) {
        return configService.getConfigListBySeriesId(request);
    }

    /**
     * pc 找车页面重构后源接口
     * @param request
     * @return
     */
    @GetMapping("/v1/car/findCarSeriesInfoByCondition")
    @Override
    public GetFindCarSeriesInfoByConditionResponse getFindCarSeriesInfoByCondition(GetFindCarSeriesInfoByConditionRequest request) {
        GetFindCarSeriesInfoByConditionResponse response = findCarService.getFindCarSeriesInfoByCondition(request);
        return response;
    }
}