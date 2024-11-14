package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.data.popauto.entities.CarPhotoTestRowEntity;

import java.util.List;

public class PiPictureByConditionCallBack implements CallBack {

    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<CarPhotoTestRowEntity> colorList = idsService.getRound5000SeriesSpecColorPicRows();
        paramContext.setSlice(20);
        compareCustom(paramContext, path, colorList);
        paramContext.setUrlStrategy(2);
        compareCustom(paramContext, path, colorList);
        paramContext.setUrlStrategy(3);
        compareCustom(paramContext, path, colorList);
        paramContext.setUrlStrategy(4);
        compareCustom(paramContext, path, colorList);
        paramContext.setUrlStrategy(5);
        compareCustom(paramContext, path, colorList);
        paramContext.setUrlStrategy(6);
        compareCustom(paramContext, path, colorList);
        paramContext.setUrlStrategy(7);
        compareCustom(paramContext, path, colorList);
    }

    @Override
    public <T> String buildUrl(Param paramContext, String path, T item) {
        CarPhotoTestRowEntity entity = (CarPhotoTestRowEntity) item;
        int seriesId = entity.getSeriesId();
        int specId = entity.getSpecId();
        int colorId = entity.getPicColorId();
        int classId = entity.getPicClass();
        int picId = entity.getPicId();
        switch (paramContext.getUrlStrategy()){
            case 1:
                break;
            case 2:
                specId = 0;
                break;
            case 3:
                seriesId = 0;
                break;
            case 4:
                colorId = 0;
                break;
            case 5:
                specId = 0;
                colorId = 0;
                break;
            case 6:
                classId = 0;
                break;
            case 7:
                picId = 0;
            default:
                break;
        }
        String url = String.format(path + "&seriesid=%d&specid=%d&colorid=%d&classid=%d&imageId=%d",
                seriesId, specId, colorId, classId, picId);
        url += "&page=1&size=10";
        return url;
    }

}
