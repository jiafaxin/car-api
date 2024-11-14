package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class SeriesPictureEntity {
    int seriesId;
    int specid;
    int photoId;
    String photoFilepath;
    int orderIndex;
    int syearId;
    int state;
}
