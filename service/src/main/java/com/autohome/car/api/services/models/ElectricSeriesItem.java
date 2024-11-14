package com.autohome.car.api.services.models;

import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.data.popauto.entities.SeriesViewElectricEntity;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.common.CommonFunction;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class ElectricSeriesItem {
    private int brandid;
    private String brandname;
    private String brandletter;
    private int fctid;
    private String fctname;
    private int seriesid;
    private String seriesname;
    private String serieslogo;
    private String seriespnglogo;
    private double minprice;
    private double maxprice;
    private List<String> seriesdescribe;
    private List<Integer> endurancemileage;
    private int seriesstate;
    private int subsidyminprice;
    private int subsidymaxprice;
    private int levelid;
    private String levelname;
    private int isforeigncar;

    //以下两个字段用于排序
    private int orderSeriesState;
    private int scoresRanks;

    public static ElectricSeriesItem buildElectricSeriesItem(SeriesInfo seriesInfo, SeriesConfig seriesConfig, SeriesBaseInfo seriesBaseInfo, List<SeriesViewElectricEntity> value) {
        ElectricSeriesItem electricSeriesItem = new ElectricSeriesItem();
        if (!CollectionUtils.isEmpty(value)) {
            List<String> fuelTypes = value.stream().map(seriesViewElectricEntity -> CommonFunction.carFuel(seriesViewElectricEntity.getFuelType())).filter(StringUtils::isNoneBlank).collect(Collectors.toList());
            SeriesViewElectricEntity seriesViewElectricEntity = value.get(0);
            if (Objects.nonNull(seriesViewElectricEntity)) {
                electricSeriesItem.setSeriesstate(seriesViewElectricEntity.getSeriesState());
                electricSeriesItem.setOrderSeriesState(seriesViewElectricEntity.getOrderSeriesState());
                String scoresRanks = seriesViewElectricEntity.getScoresRanks();
                electricSeriesItem.setScoresRanks(Integer.parseInt(scoresRanks));
            }
            electricSeriesItem.setSeriesdescribe(fuelTypes);
        }
        if (Objects.nonNull(seriesInfo)) {
            electricSeriesItem.setBrandid(seriesInfo.getBrandid());
            electricSeriesItem.setBrandname(seriesInfo.getBrandname());
            electricSeriesItem.setBrandletter(seriesInfo.getBrandfirstletter());
            electricSeriesItem.setFctid(seriesInfo.getFctid());
            electricSeriesItem.setFctname(seriesInfo.getFctname());
            electricSeriesItem.setSeriesid(seriesInfo.getSeriesid());
            electricSeriesItem.setSeriesname(seriesInfo.getSeriesname());
            electricSeriesItem.setLevelid(seriesInfo.getLevelid());
            electricSeriesItem.setLevelname(seriesInfo.getLevelname());
        }
        if (Objects.nonNull(seriesConfig)) {
            electricSeriesItem.setMaxprice(seriesConfig.getTempMaxPrice());
            electricSeriesItem.setMinprice(seriesConfig.getTempMinPrice());
            electricSeriesItem.setEndurancemileage(seriesConfig.getElectricmotormileage());
        }
        if (Objects.nonNull(seriesBaseInfo)) {
            electricSeriesItem.setSeriespnglogo(ImageUtil.getFullImagePath(seriesBaseInfo.getNoBgLogo()));
            electricSeriesItem.setSerieslogo(ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()));
            electricSeriesItem.setSubsidymaxprice(0);
            electricSeriesItem.setSubsidyminprice(0);
            electricSeriesItem.setIsforeigncar(seriesBaseInfo.getFc());
        }
        return electricSeriesItem;
    }
}
