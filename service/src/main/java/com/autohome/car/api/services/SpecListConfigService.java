package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v3.spec.ConfigGetListBySpecListResponse;
import autohome.rpc.car.car_api.v3.spec.ConfigInfoBySpecIdsAndTypeIdsResponse;

import java.util.List;

public interface SpecListConfigService {
    ConfigGetListBySpecListResponse configGetListBySpecList(List<Integer> specIds);
    /**
     * 多个车型获取配置信息（新接口; .net没有）
     * @param specIds
     * @return
     */
    ConfigInfoBySpecIdsAndTypeIdsResponse getConfigInfoBySpecIdsAndTypeIds(List<Integer> specIds, List<Integer> typeIds);
}
