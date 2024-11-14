package com.autohome.car.api.services.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ElectricParam {

    private String key;
    private int type;
    private List<String> value;
    /*private List<ConfigModel> list;
    @Data
    static class ConfigModel{

    }*/
}
