package com.autohome.car.api.services.basic.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConfigItemBaseInfo implements Serializable {
    int itemId;
    String itemName;
    int dynamicShow;
    int cVIsShow;
    int isShow;
    int displayType;
}
