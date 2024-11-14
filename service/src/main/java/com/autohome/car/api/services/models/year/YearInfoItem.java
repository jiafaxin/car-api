package com.autohome.car.api.services.models.year;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class YearInfoItem implements Serializable {
    private int seriesid;
    private int total;
    private List<YearInfo> yearitems;

    @Data
    public static class YearInfo implements Serializable{
        private int yearid;
        private int yearnumber;
        private String yearname;
        private int seriesid;
        private int yearispublic;
        private int yearstate;
    }

}
