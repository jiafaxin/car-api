package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.common.CarPhotoViewItemMessage;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.data.popauto.CarPhotoViewMapper;
import com.autohome.car.api.services.CarPhotoService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.PhotosService;
import com.autohome.car.api.services.common.SixtyPic;
import com.autohome.car.api.services.models.CarPhotoView;
import com.autohome.car.api.services.models.CarPhotoViewPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CarPhotoServiceImpl implements CarPhotoService {

    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;
    @Autowired
    PhotosService photosService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SpecBaseService specBaseService;
    @Autowired
    PicClassBaseService picClassBaseService;
    @Autowired
    ColorBaseService colorBaseService;
    @Autowired
    ShowBaseService showBaseService;


    public CarPhotoViewPage carPhoto(int seriesId, int specId, int classId, int colorId, int page, int size) {
        CarPhotoViewPage pageResult = new CarPhotoViewPage();
        pageResult.setPageindex(page);
        pageResult.setTotal(0);
        pageResult.setPicitems(new ArrayList<>());
        pageResult.setSize(size);
        List<CarPhotoViewItemMessage> picList;


        if (seriesId <= 0) {
            SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
            if (specBaseInfo == null) {
                return pageResult;
            }
            seriesId = specBaseInfo.getSeriesId();
        }



        List<CarPhotoViewItemMessage> list = getAll(seriesId, page * size);
        if (seriesId > 0 && specId == 0) {
            if (classId > 0 && colorId > 0) {
                picList = getSeriesAndClassAndColor(list, classId, colorId, true, pageResult, page, size);
            } else if (classId > 0 && colorId == 0) {
                picList = getSeriesAndClass(list, classId, true, pageResult, page, size);
            } else if (colorId > 0 && classId == 0) {
                picList = carPhotoBySeriesAndColor(list, colorId, true, pageResult, page, size);
            } else {
                picList = carPhotoBySeries(list, true, pageResult, page, size);
            }
        } else {
            if (classId > 0 && colorId > 0) {
                picList = carPhotoBySpecAndClassAndColor(list, specId, classId, colorId, true, pageResult, page, size);
            } else if (classId > 0 && colorId == 0) {
                picList = carPhotoBySpecAndClass(list, specId, classId, true, pageResult, page, size);
            } else if (colorId > 0 && classId == 0) {
                picList = carPhotoBySpecAndColor(list, specId, colorId, true, pageResult, page, size);
            } else {
                picList = carPhotoBySpec(list, specId, true, pageResult, page, size);
            }
        }

        if (picList == null || picList.size() == 0)
            return pageResult;



        List<CarPhotoView> result = new ArrayList<>();
        for (CarPhotoViewItemMessage item : picList) {
            CarPhotoView view = new CarPhotoView();
            view.setColorid(item.getPicColorId());
            view.setId(item.getPicId());
            view.setColorname(item.getColorname());
            view.setDealerid(item.getDealerid());
            view.setFilepath(ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()));
            view.setHeight(item.getHeight());
            view.setIshd(item.getIsHD());
            view.setIswallpaper(item.getIsWallPaper());
            view.setMemberid(0);
            view.setMembername("");
            view.setOptional(item.getOptional());
            view.setShowid(item.getShowId());
            view.setShowname(item.getShowname());
            view.setSixtypicsortid(SixtyPic.get(item.getPointlocatinid(), 0));
            view.setSpecid(item.getSpecId());
            view.setSpecname(item.getSpecname());
            view.setTypeid(item.getPicClass());
            view.setTypename(item.getTypename());
            view.setWidth(item.getWidth());
            view.setYearId(item.getSyearId());
            view.setSpecState(item.getSpecState());
            view.setYearName(item.getSyear() + "款");
            result.add(view);
        }

        pageResult.setPicitems(result);
        return pageResult;
    }


    public List<CarPhotoViewItemMessage> getSeriesAndClassAndColor(List<CarPhotoViewItemMessage> list, int classId, int colorId, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                        item -> {
                            if (item.getPicClass() == (classId) &&
                                    item.getPicColorId() == (colorId) &&
                                    item.getSpecPicNumber() > 2 &&
                                    (hasClub || item.getIsClubPhoto() == (0))) {
                                total.incrementAndGet();
                                return true;
                            } else {
                                return false;
                            }
                        })
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
                ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }


    public List<CarPhotoViewItemMessage> getSeriesAndClass(List<CarPhotoViewItemMessage> list, int classId, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getPicClass() == (classId) &&
                            item.getSpecPicNumber() > 2 &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                .thenComparing(CarPhotoViewItemMessage::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
        ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndColor(List<CarPhotoViewItemMessage> list, int colorId, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getPicColorId() == (colorId) &&
                            item.getSpecPicNumber() > 2 &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
        ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeries(List<CarPhotoViewItemMessage> list, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getSpecPicNumber() > 2 &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                .thenComparing(CarPhotoViewItemMessage::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
        ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClassAndColor(List<CarPhotoViewItemMessage> list, int specId, int classId, int colorId, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getSpecId() == (specId) &&
                            item.getPicClass() == (classId) &&
                            item.getPicColorId() == (colorId) &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
        ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpec(List<CarPhotoViewItemMessage> list, int specId, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getSpecId() == (specId) &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                .thenComparing(CarPhotoViewItemMessage::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
        ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndColor(List<CarPhotoViewItemMessage> list, int specId, int colorId, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getSpecId() == (specId) &&
                            item.getPicColorId() == (colorId) &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
        ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }


    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClass(List<CarPhotoViewItemMessage> list, int specId, int classId, boolean hasClub, CarPhotoViewPage pageResult, int page, int size) {
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getSpecId() == (specId) &&
                            item.getPicClass() == (classId) &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
        ).skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageResult.setTotal(total.get());
        return result;
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClass(List<CarPhotoViewItemMessage> list, int specId, int classId, boolean hasClub){
        AtomicInteger total = new AtomicInteger(0);
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getSpecId() == (specId) &&
                            item.getPicClass() == (classId) &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
                        total.incrementAndGet();
                        return true;
                    }
                    return false;
                }
        ).sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                .thenComparing(CarPhotoViewItemMessage::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
        ).collect(Collectors.toList());
        return result;
    }
    public List<CarPhotoViewItemMessage> getAll(int seriesId, int endIndex) {
        List<CarPhotoViewItemMessage> list = photosService.get(seriesId);
        if (list == null)
            list = new ArrayList<>();
        return list;
    }
}
