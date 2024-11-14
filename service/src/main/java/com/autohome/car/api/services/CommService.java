package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.car.ConfigTypeItem;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.entities.EleSpecViewBaseEntity;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.models.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface CommService {

    List<SeriesBaseInfo> getSeriesBaseInfoNoMap(List<Integer> seriesIds);

    /**
     * 获取车系信息
     * @param seriesIds
     * @return
     */
    Map<Integer, SeriesBaseInfo> getSeriesBaseInfo(List<Integer> seriesIds);

    /**
     * 获取厂商信息
     * @param fctIds
     * @return
     */
    Map<Integer, FactoryBaseInfo> getFactoryBaseInfo(List<Integer> fctIds);

    /**
     * 获取车型信息
     * @param specIds
     * @return
     */
    Map<Integer, SpecBaseInfo> getSpecBaseInfo(List<Integer> specIds);

    /**
     * 获取品牌信息
     * @param brandIds
     * @return
     */
    Map<Integer, BrandBaseInfo> getBrandBaseInfo(List<Integer> brandIds);

    /**
     *
     * @param brandIds
     * @return
     */
    List<BrandBaseInfo> getBrandBaseInfoList(List<Integer> brandIds);

    /**
     * 获取级别信息
     * @param levelIds
     * @return
     */
    Map<Integer, LevelBaseInfo> getLevelBaseInfo(List<Integer> levelIds);

    /**
     *
     * @param seriesId 车系id
     * @param type 类型 ，解决差异化，暂时没有使用
     * @return 车系下车型列表
     */
    List<SpecViewEntity> getSpecViewEntities(int seriesId, int type);

     /**
     * 根据品牌ids获取车系ids
     * @param brandIds
     * @return
     */
    Map<Integer, List<Integer>> getSeriesIdListByBrands(List<Integer> brandIds);

    List<EleSpecViewBaseEntity> getEleSpecViewBaseEntities(List<Integer> seriesIds);

   Pair<ReturnMessageEnum, List<ConfigTypeItem>> getConfigListBySpecList(List<Integer> specIds, int dispType);


}
