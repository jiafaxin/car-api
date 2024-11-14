package com.autohome.car.api.services.basic.models;

import com.autohome.car.api.data.popauto.entities.AppBrandInfoEntity;
import com.autohome.car.api.data.popauto.entities.BrandBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BrandBaseInfo extends BrandBaseEntity implements Serializable {

   private List<AppBrandInfoEntity> appBrandInfos;
}
