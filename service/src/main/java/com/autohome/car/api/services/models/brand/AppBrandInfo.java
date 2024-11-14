package com.autohome.car.api.services.models.brand;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AppBrandInfo implements Serializable {

    private int brandid;

    private List<BrandInfo> list;

    @Data
    @AllArgsConstructor
    public static class BrandInfo implements Serializable{

        private String title;

        private String info;

    }

}
