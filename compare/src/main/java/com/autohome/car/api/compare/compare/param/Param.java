package com.autohome.car.api.compare.compare.param;

import com.autohome.car.api.compare.compare.enums.OperType;
import com.autohome.car.api.compare.compare.service.CallBack;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Param {

    /**
     * 排除的字段
     */
    private String[] exclude = new String[]{};

    /**
     * 操作类型
     */
    private OperType operType;
    /**
     * 参数值
     */
    private String field;

    /**
     * 自定义接口
     */

    private CallBack callBack;

    /**
     * 是有否有state
     */
    @Builder.Default
    boolean hasState = false;

    /**
     * 如果参数为list,进行切片数
     */
    @Builder.Default
    int slice = 2;

    /**
     * url生产策略
     */
    @Builder.Default
    int urlStrategy = 1;

}
