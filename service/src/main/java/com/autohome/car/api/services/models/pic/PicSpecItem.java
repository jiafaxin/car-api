package com.autohome.car.api.services.models.pic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class PicSpecItem implements Serializable{

    private int specid;

    private String specname;

    private int seriesid;

    private String seriesname;

    private List<TypeItem> typeitems;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class TypeItem implements Serializable{
        private int typeid;

        private String typename;

        private int pictotal;

        private List<PicItem> picitems;

    }
    @AllArgsConstructor
    @Data
    public static class PicItem implements Serializable{
        private int id;
        private String filepath;

    }
}
