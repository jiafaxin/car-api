package com.autohome.car.api.services.models.pic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Pic25YearItem implements Serializable {

    private int yearid;

    private int seriesid;

    private String seriesname;

    private int total;


    private List<PicItem> picitems;
    @Data
    public static class PicItem implements Serializable {
        private int itemid;

        private int typeid;

        private String itemname;

        private int picid;

        private String picpath;

        private int specid;

        private String specname;
    }
}
