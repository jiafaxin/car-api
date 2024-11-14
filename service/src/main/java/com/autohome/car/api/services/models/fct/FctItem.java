package com.autohome.car.api.services.models.fct;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FctItem implements Serializable {

    private int total;

    private List<FctDetailItem> fctitems;

    @Data
    public static class FctDetailItem implements Serializable {

        private int id;

        private String name;

        private String url;

        private String isimport;

        private String createtime;

        private String edittime;

        private String firstletter;

        private String logo;

    }
}
