package com.autohome.car.api.services.models.brand;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BrandCorrelateInfo implements Serializable {

    private int brandid;

    private String brandname;

    private String brandlogo;

    private String brandofficialurl;

    private int sellseriescount;

    private List<FctAndSeriesInfo> fctitems;

    private int sellspeccount;

    @Data
    public static class FctAndSeriesInfo implements Serializable{
        private int fctid;

        private String fctname;

        private String fctlogo;

        private List<SeriesInfo> seriesitems;

        private int sellseriescount;
    }
    @Data
    public static class SeriesInfo implements Serializable{

        private int seriesid;

        private String seriesname;

    }

}
