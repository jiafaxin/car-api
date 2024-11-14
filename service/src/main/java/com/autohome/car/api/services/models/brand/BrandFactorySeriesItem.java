package com.autohome.car.api.services.models.brand;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BrandFactorySeriesItem implements Serializable {

    private int id;

    private String name;

    private String logo;

    private String officialurl;
    private List<FactoryItem> factoryitems;

    @Data
    public static class FactoryItem implements Serializable {

        private int id;

        private String name;

        private List<SeriesItem> seriesitems;

    }
    @Data
    public static class SeriesItem implements Serializable {
        private int id;

        private String name;

        private int levelid;

        private String levelname;

        private int ispublic;

        private int seriesstate;
    }
}
