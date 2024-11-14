package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.common.CarPhotoViewItemMessage;
import com.autohome.car.api.data.popauto.entities.CarPhotoViewEntity;
import com.autohome.car.api.services.models.CarPhotoViewPage;

import java.util.List;

public interface CarPhotoService {
    CarPhotoViewPage carPhoto(int seriesId, int specId, int classId , int colorId, int page, int size);

    List<CarPhotoViewItemMessage> carPhotoBySpecAndClass(List<CarPhotoViewItemMessage> list, int specId, int classId, boolean hasClub);
}
