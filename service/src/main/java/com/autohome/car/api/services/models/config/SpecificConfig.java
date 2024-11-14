package com.autohome.car.api.services.models.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecificConfig implements Serializable {

    private List<ConfigItem> configitems;

    @Data
    public static class ConfigItem implements Serializable{

        private int baikeid;

        private String baikeurl;

        private int configid;

        private String name;

        private List<ValueItem> valueitems;

    }
    @Data
    public static class ValueItem implements Serializable{

        private String specid;

        private String value;

        private String price;
    }
}
