package com.autohome.car.api.services.models.year;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class YearViewItem implements Serializable {
    private int serieid;

    private List<YearView> yearitems;

    @Data
    public static class YearView implements Serializable{
        private int id;
        private int year;
        private int state;
        private int specnum;
        private int specnumunsold;
        private int specnumsale;
        private int specnumstop;
    }

}
