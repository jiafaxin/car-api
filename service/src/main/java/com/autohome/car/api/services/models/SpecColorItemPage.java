package com.autohome.car.api.services.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SpecColorItemPage {

    private int specid;
    private int total;
    private List<ColorItem> coloritems;

    @Data
    @Builder
    public static class ColorItem{
        private int id;
        private String name;
        private String value;
        private int picnum;
        private int clubpicnum;
        private int price;
        private String remark;
    }
}
