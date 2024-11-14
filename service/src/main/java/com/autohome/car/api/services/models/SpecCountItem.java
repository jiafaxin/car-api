package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecCountItem implements Serializable {
    private int seriesid;

    private int total;

    private List<CountItem> items;
    @Data
    public static class CountItem implements Serializable{
        private int state;

        private int count;

        private int noimgcount;
    }
}
