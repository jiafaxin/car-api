package com.autohome.car.api.services.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SpecColorListItems {

    private int total;
    private List<SpecItem> specitems;

    @Data
    @Builder
    public static class SpecItem{
        private int specid;
        private List<ColorItem> coloritems;
    }

    @Data
    @Builder
    public static class ColorItem{
        private int id;
        private String name;
        private String value;
        private int picnum;
        private int clubpicnum;
        int price;
        String remark;
    }
}
