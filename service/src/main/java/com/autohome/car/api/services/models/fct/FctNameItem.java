package com.autohome.car.api.services.models.fct;

import lombok.Data;

import java.io.Serializable;

@Data
public class FctNameItem implements Serializable {

    private int fctid;

    private String fctname;
}
