package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarPhotoTestRowEntity implements Serializable {
    int id;
    int specId;
    int seriesId;
    int picClass;
    int picId;
    int picColorId;
    int specState;
    int syearId;
    int innerColorId;
}
