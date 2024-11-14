package com.autohome.car.api.services.basic.models;

import com.autohome.car.api.data.popauto.entities.SpecColorEntity;
import lombok.Data;
import org.apache.dubbo.common.utils.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class SpecColorInfo implements Serializable {

    /**
     * colorId
     */
    private int ci;
    /**
     * colorName
     */
    private String cn;
    /**
     * colorValue
     */
    private String cv;
    /**
     * picNum
     */
    private int pn;
    /**
     * clubPicNum
     */
    private int cpn;

    public static SpecColorInfo getSpecColorInfo(SpecColorEntity specColorEntity) {
        return Optional.ofNullable(specColorEntity)
            .map(entity -> new SpecColorInfo())
            .map(specColorInfo -> {
                specColorInfo.setCi(specColorEntity.getColorId());
                specColorInfo.setCn(specColorEntity.getColorName());
                specColorInfo.setCv(specColorEntity.getColorValue());
                specColorInfo.setPn(specColorEntity.getPicNum());
                specColorInfo.setCpn(specColorEntity.getClubPicNum());
                return specColorInfo;
            })
            .orElse(null);
    }

    public static List<SpecColorInfo> getSpecColorInfoList(List<SpecColorEntity> list) {
        List<SpecColorInfo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (SpecColorEntity specColorEntity : list) {
                SpecColorInfo specColorInfo = getSpecColorInfo(specColorEntity);
                if (Objects.nonNull(specColorInfo)) {
                    result.add(specColorInfo);
                }
            }
        }
        return result;
    }
}
