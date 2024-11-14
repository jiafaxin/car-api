package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecMainItem implements Serializable {

    private int specid;
    private String specname;
    private int specstate;
    private int minprice;
    private int maxprice;
    private int horsepower;
    private int mileage;
    private Double officialfastchargetime;
    private Double officialslowchargetime;
    private Double batterycapacity;
    private int paramisshow;
    private int fueltypedetail;
}
