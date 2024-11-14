package com.autohome.car.api.services.models;

import lombok.Data;

import java.util.List;

@Data
public class CarPhotoViewPage {
    int pageindex;
    int size;
    int total;
    List<CarPhotoView> picitems;
}
