package com.autohome.car.api.services.models;

import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class SeriesPhotoWhiteLogoItem {

    private int id;
    private String picpath;
    private String seriespnglogo;

}
