package com.autohome.car.api.services.models.pic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PicClassItem implements Serializable {

    private int seriesid;

    private int officialpicisnew;

    private int innerColorid;

    private List<ClassItem> classitems;

    @Data
    public static class ClassItem implements Serializable{
        private int id;

        private String name;

        private int piccount;

        private int clubpiccount;
    }
}
