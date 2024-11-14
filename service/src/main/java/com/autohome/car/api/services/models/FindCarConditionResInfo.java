package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FindCarConditionResInfo implements Serializable {

    private int seriescount;

    private int pageindex;

    private int pagesize;

    private List<SeriesInfo> seriesGroupList;

    @Data
    public static class SeriesInfo{

        private int seriesId;

        private String seriesName;

        private String seriesImg;

        private int seriesFctMinPrice;

        private int seriesFctMaxPrice;

        private int specCount;

        private int seriesState;

    }
}
