package com.autohome.car.api.services.models.fct;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FctCorrelateInfo implements Serializable {

    private int fctid;

    private String fctname;

    private String fctlogo;

    private String officialurl;

    private int sellseriescount;

    private List<SeriesItem> serieslist;

    private int sellspeccount;

    @Data
    public static class SeriesItem implements Serializable{
        private int seriesid;

        private String seriesname;

        private String seriesLogo;

        private String minprice;

        private String maxprice;

    }

}
