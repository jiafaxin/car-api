package com.autohome.car.api.services.models.brand;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BrandFctSeriesInfo implements Serializable {

    private int id;

    private String name;

    private String logo;

    private List<FctInfo> fctlist;

    @Data
    public static class FctInfo implements Serializable{
        private int id;

        private String name;

        private String logo;

        private List<SeriesInfo> serieslist;
    }

    @Data
    public static class SeriesInfo implements Serializable{
        private int id;

        private String name;

        private String logo;

        private int levelid;

        private String levelname;

        private int minprice;

        private int maxprice;

        private int seriesState;

        private int seriesorders;

        private int isvr;

        private String pnglogo;

        private int containbookedspec;

        private int relationseriesid;
    }
}
