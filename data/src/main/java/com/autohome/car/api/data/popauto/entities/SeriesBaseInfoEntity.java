package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SeriesBaseInfoEntity  implements Serializable {
    int id;
    String name;

    String eName;//englishName
    int brandId;
    int factId;
    int levelId;
    private String logo;
    private String url;
    private String noBgLogo;
    private int seriesPriceMin;
    private int seriesPriceMax;
    private int seriesState;
    String fl;

    int rId;

    private String place;
    private Date editTime;
}
