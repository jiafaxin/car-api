package com.autohome.car.api.services.basic.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ConfigTypeBaseInfo implements Serializable {
    int typeId;
    String typeName;
    List<ConfigItemBaseInfo> items;
}
