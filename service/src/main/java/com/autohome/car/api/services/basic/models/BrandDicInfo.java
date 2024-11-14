package com.autohome.car.api.services.basic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDicInfo implements Serializable {

    private String k;

    /**
     * 品牌id
     */
    private int bId;

    /**
     * 能源类型字典
     * 最大续航里程
     * 最快充电时间
     */
    private String value;
}
