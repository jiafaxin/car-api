package com.autohome.car.api.services.models.pic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PicYearItem implements Serializable {

    private int yearid;

    private int seriesid;

    private String seriesname;

    private List<TypeItem> typeitems;
    @Data
    public static class TypeItem implements Serializable{
        private int typeid;

        private String typename;

        private int pictotal;

        private List<PicItem> picitems;
    }
    @Data
    public static class PicItem implements Serializable{
       private int id;

       private String filepath;

       private int specid;

       private String specname;
    }
}
