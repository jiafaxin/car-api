package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecBaseInfoItems implements Serializable {
    List<SpecBaseInfoItem> specitems;
}
