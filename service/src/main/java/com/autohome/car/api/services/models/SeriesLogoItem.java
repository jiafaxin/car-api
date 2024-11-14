package com.autohome.car.api.services.models;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeriesLogoItem {

    private int id;
    private String logo;
    private int piccount;
}
