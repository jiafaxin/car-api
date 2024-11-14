package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.common.CarPhotoViewItemMessage;
import autohome.rpc.car.car_api.v1.javascript.IndexSlidePicRequest;
import autohome.rpc.car.car_api.v1.javascript.IndexSlidePicResponse;
import autohome.rpc.car.car_api.v1.pic.*;
import autohome.rpc.car.car_api.v2.pic.*;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.CarSpecInnerColorStatisticsMapper;
import com.autohome.car.api.data.popauto.CarSpecPicColorStatisticsMapper;
import com.autohome.car.api.data.popauto.PicClassMapper;
import com.autohome.car.api.data.popauto.SpecColorMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.CarPhotoService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.PictureService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.basic.models.ColorBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.PhotosService;
import com.autohome.car.api.services.basic.series.SeriesInfoService;
import com.autohome.car.api.services.basic.series.SeriesSpecPicColorStatistics;
import com.autohome.car.api.services.basic.series.SeriesSpecPicInnerColorStatistics;
import com.autohome.car.api.services.basic.specs.CarSpecColorBaseService;
import com.autohome.car.api.services.basic.specs.CarSpecInnerColorBaseService;
import com.autohome.car.api.services.basic.specs.InnerColorSpecService;
import com.autohome.car.api.services.basic.specs.SpecColorService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.common.SixtyPic;
import com.autohome.car.api.services.models.PicColorInfo;
import com.autohome.car.api.services.models.PicColorInfoResult;
import com.autohome.car.api.services.models.SeriesInfo;
import com.autohome.car.api.services.models.pic.Pic25YearItem;
import com.autohome.car.api.services.models.pic.PicClassItem;
import com.autohome.car.api.services.models.pic.PicSpecItem;
import com.autohome.car.api.services.models.pic.PicYearItem;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.LocalDateUtils.DATE_TIME_PATTERN_TWO;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
@Slf4j
public class PictureServiceImpl implements PictureService {


    @Autowired
    CarSpecPicColorStatisticsMapper carSpecPicColorStatisticsMapper;


    @Autowired
    CarSpecInnerColorStatisticsMapper carSpecInnerColorStatisticsMapper;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    ColorBaseService colorBaseService;

    @Autowired
    InnerColorBaseService innerColorBaseService;

    @Autowired
    PicClassMapper picClassMapper;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    PictureTypeBaseService pictureTypeBaseService;

    @Autowired
    Series25PictureBaseService series25PictureBaseService;

    @Autowired
    SeriesSpecPicColorStatistics seriesSpecPicColorStatistics;

    @Autowired
    SeriesSpecPicInnerColorStatistics seriesSpecPicInnerColorStatistics;

    @Autowired
    private PhotosService photosService;

    @Autowired
    private CarPhotoService carPhotoService;

    @Autowired
    private PicClassBaseService picClassBaseService;

    @Autowired
    SpecPicClassStatisticsBaseService specPicClassStatisticsBaseService;

    @Resource
    SpecPicClassBaseBaseService specPicClassBaseBaseService;

    @Autowired
    SpecColorService specColorService;

    @Autowired
    ShowBaseService showBaseService;

    @Autowired
    SpecColorMapper specColorMapper;

    @Autowired
    Spec25PicBaseService spec25PicBaseService;

    @Autowired
    AutoCacheService autoCacheService;

    @Autowired
    private BrandBaseService brandBaseService;

    @Resource
    private CommService commService;

    @Resource
    SeriesInfoService seriesInfoService;

    @Resource
    SpecListSameYearBaseService specListSameYearBaseService;

    @Resource
    SpecListSameYearByYearService specListSameYearByYearService;

    @Resource
    SpecPicColorStatisticsBaseService specPicColorStatisticsBaseService;

    @Resource
    CarSpecColorBaseService carSpecColorBaseService;

    @Resource
    CarSpecInnerColorBaseService carSpecInnerColorBaseService;


    @Resource
    private Year25PictureByYearIdBaseService year25PictureByYearIdBaseService;
    @Autowired
    InnerColorSpecService specInnerColorService;

    @Resource
    private FeaturedPictureBaseService featuredPictureBaseService;

    /**
     * 根据车型id,图片类别id获取颜色图片数量
     * @param specId
     * @param classId
     * @return
     */
    public PicColorInfoResult getPicColorInfo(int specId,int classId) {
        PicColorInfoResult result = new PicColorInfoResult();
        result.setClassid(classId);
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        if (specBaseInfo == null)
            return result;

        int seriesId = specBaseInfo.getSeriesId();
        result.setSeriesid(seriesId);

        List<SpecPicColorStatisticsEntity> picColorsInfos = seriesSpecPicColorStatistics.get(seriesId);
        if (CollectionUtils.isEmpty(picColorsInfos))
            return result;

        Map<Integer, List<SpecPicColorStatisticsEntity>> query = picColorsInfos.stream().filter(x -> {
            boolean r = x.getSpecId() == specId;
            if (classId > 0) {
                r = r && (classId == x.getPicClass());
            }
            return r;
        }).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber).reversed()).collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getColorId));

        List<PicColorInfo> list = new ArrayList<>();
        List<ColorBaseInfo> colorList = colorBaseService.getColorList(query.keySet().stream().collect(Collectors.toList()));
        Map<Integer,ColorBaseInfo> colorMap = new LinkedHashMap<>();
        for (ColorBaseInfo colorBaseInfo : colorList) {
            colorMap.put(colorBaseInfo.getId(),colorBaseInfo);
        }
        query.forEach((k, v) -> {
            ColorBaseInfo colorBaseInfo = colorMap.get(k);
            list.add(new PicColorInfo() {{
                setId(k);
                setName(null != colorBaseInfo ? colorBaseInfo.getName() : "");
                setValue(null != colorBaseInfo ? colorBaseInfo.getValue() : "");
                setClubpiccount(v.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum());
                setPiccount(v.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum());
            }});
        });

        result.setColoritems(list.stream().sorted(Comparator.comparing(PicColorInfo::getPiccount).reversed()).collect(Collectors.toList()));

        return result;
    }

    /**
     * 根据车型id,图片类别id获取颜色图片数量
     * @param specId
     * @param classId
     * @return
     */
    public PicColorInfoResult getInnerPicColorInfo(int specId,int classId) {
        PicColorInfoResult result = new PicColorInfoResult();
        result.setClassid(classId);
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        if (specBaseInfo == null)
            return result;

        int seriesId = specBaseInfo.getSeriesId();
        result.setSeriesid(seriesId);

        List<SpecPicColorStatisticsEntity> picColorsInfos = seriesSpecPicInnerColorStatistics.get(seriesId);
        if (CollectionUtils.isEmpty(picColorsInfos))
            return result;

        Map<Integer, List<SpecPicColorStatisticsEntity>> query = picColorsInfos.stream().filter(x -> {
            boolean r = x.getSpecId() == specId;
            if (classId > 0) {
                r = r && (classId == x.getPicClass());
            }
            return r;
        }).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber).reversed()).collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getColorId));

        List<PicColorInfo> list = new ArrayList<>();
        Map<Integer,ColorBaseInfo> colorList = innerColorBaseService.getColorMap(query.keySet().stream().collect(Collectors.toList()));

        query.forEach((k, v) -> {
            ColorBaseInfo colorBaseInfo = colorList.get(k);
            list.add(new PicColorInfo() {{
                setId(k);
                setName(colorBaseInfo==null?"":colorBaseInfo.getName());
                setValue(colorBaseInfo==null?"":colorBaseInfo.getValue());
                setClubpiccount(v.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum());
                setPiccount(v.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum());
            }});
        });

        result.setColoritems(list.stream().sorted(Comparator.comparing(PicColorInfo::getPiccount).reversed()).collect(Collectors.toList()));

        return result;
    }

    /**
     * 根据车系id获取对应25图
     * @param request
     * @return
     */
    @Override
    public GetSeries25PictureBySeriesIdResponse getSeries25PictureBySeriesId(GetSeries25PictureBySeriesIdRequest request) {
        GetSeries25PictureBySeriesIdResponse.Builder builder = GetSeries25PictureBySeriesIdResponse.newBuilder();
        int seriesid = request.getSeriesid();
        if(seriesid == 0){
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        GetSeries25PictureBySeriesIdResponse.Result.Builder result = GetSeries25PictureBySeriesIdResponse.Result.newBuilder();
        result.setSeriesid(seriesid);
        SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesid).join();
        if(seriesInfo != null){
            List<PicInfoEntity> list = series25PictureBaseService.get(seriesid).join();
            if (!CollectionUtils.isEmpty(list)){
                for (PicInfoEntity item:list) {
                    SpecBaseInfo specInfo = specBaseService.get(item.getSpecid()).join();
                    String name = pictureTypeBaseService.get(item.getId()).join();
                    GetSeries25PictureBySeriesIdResponse.Result.PicItems info = GetSeries25PictureBySeriesIdResponse.Result.PicItems.newBuilder()
                            .setItemid(item.getOrdercls())
                            .setTypeid(item.getTopid())
                            .setPicid(item.getPicid())
                            .setSpecid(item.getSpecid())
                            .setSpecname((null != specInfo && null != specInfo.getSpecName()) ? specInfo.getSpecName() : "")
                            .setPicpath(StringUtils.defaultString(ImageUtil.getFullImagePath(item.getPicpath()),""))
                            .setItemname(StringUtils.defaultString(name,""))
                            .build();
                    result.addPicitems(info);
                }
                result.setTotal(list.size());
            }
            result.setSeriesname(seriesInfo.getName());
        }
        else {
            result.setSeriesname("");
            log.info("getSeries25PictureBySeriesId seriesInfo is null,id:"+seriesid);
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetPicPictureItemsByConditionV1Response getPicPictureItemsByConditionV1(GetPicPictureItemsByConditionV1Request request){
        GetPicPictureItemsByConditionV1Response.Builder builder = GetPicPictureItemsByConditionV1Response.newBuilder();
        GetPicPictureItemsByConditionV1Response.Result.Builder result = GetPicPictureItemsByConditionV1Response.Result.newBuilder();

        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int picClassId = request.getClassid();
        int picColorId = request.getColorid();
        int pageIndex = request.getPage() == 0 ? 1 : request.getPage();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        if (seriesId == 0 && specId == 0 || pageIndex < 1 || size < 0) {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        List<CarPhotoViewItemMessage> list = null;
        if (seriesId != 0 && specId == 0) {
            if (picClassId != 0 && picColorId != 0)
                list = carPhotoBySeriesAndClassAndColor(seriesId, picClassId, picColorId,false);
            else if (picClassId != 0 && picColorId == 0)
                list = carPhotoBySeriesAndClass(seriesId, picClassId,false);
            else if (picColorId != 0 && picClassId == 0)
                list = carPhotoBySeriesAndColor(seriesId, picColorId,false);
            else
                list = carPhotoBySeries(seriesId,false);
        } else {
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            if (picClassId != 0 && picColorId != 0)
                list = carPhotoBySpecAndClassAndColor(seriesId, specId, picClassId, picColorId,false);
            else if (picClassId != 0 && picColorId == 0)
                list = carPhotoBySpecAndClass(seriesId, specId, picClassId,false);
            else if (picColorId != 0 && picClassId == 0)
                list = carPhotoBySpecAndColor(seriesId, specId, picColorId,false);
            else
                list = carPhotoBySpec(seriesId, specId,false);
        }
        int total = list.size();// 图片数量
        int length = pageIndex * size > total ? total : pageIndex * size;
        Map<Integer, SpecBaseInfo> specBaseInfoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(list)){
            List<Integer> specIds = list.stream().map(CarPhotoViewItemMessage::getSpecId).distinct().collect(Collectors.toList());
            specBaseInfoMap = commService.getSpecBaseInfo(specIds);
        }
        for (int i = (pageIndex - 1) * size; i < length; i++) {
            CarPhotoViewItemMessage item = list.get(i);
            specId = item.getSpecId();
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specId);
            GetPicPictureItemsByConditionV1Response.Result.PicItems.Builder spec = GetPicPictureItemsByConditionV1Response.Result.PicItems.newBuilder()
                    .setId(item.getPicId())
                    .setFilepath(StringUtils.isNotBlank(item.getPicFilePath())?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()):"")
                    .setIshd(item.getIsHD())
                    .setSpecid(specId)
                    .setSpecname(null != specBaseInfo ? specBaseInfo.getSpecName():"")
                    .setMinprice(null != specBaseInfo ? specBaseInfo.getSpecMinPrice():0)
                    .setMaxprice(null != specBaseInfo ? specBaseInfo.getSpecMinPrice():0)
                    .setSpecstate(String.valueOf(item.getSpecState()));

            result.addPicitems(spec);
        }
        result.setPageindex(pageIndex);
        result.setSize(size);
        result.setTotal(total);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    /**
     * 根据车系id,车型id,类型id,颜色id,页码及页大小,图片id获取图片信息
     * @param request
     * @return
     */
    @Override
    public GetPicPictureItemsByConditionResponse getPicPictureItemsByConditionV2(GetPicPictureItemsByConditionRequest request) {
        GetPicPictureItemsByConditionResponse.Builder builder = GetPicPictureItemsByConditionResponse.newBuilder();
        GetPicPictureItemsByConditionResponse.Result.Builder result = GetPicPictureItemsByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int picClassId = request.getTypeid();
        int picColorId = request.getColorid();
        int picId = request.getImageid();
        int innerColorId = request.getInnercolorid();
        int size = request.getPagesize() == 0 ? 60 : request.getPagesize();
        int pageindex = request.getPageindex() == 0 ? 1 : request.getPageindex();
        if ((seriesId == 0 && specId == 0) || request.getPagesize() > 1000) {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        HashMap<Integer, Integer> dic = null;
        List<CarPhotoViewItemMessage> list = null;
        if(seriesId != 0 && specId == 0){
            if(picClassId != 0 && picColorId != 0){
                list = carPhotoBySeriesAndClassAndColor(seriesId, picClassId, picColorId,true);
                if(picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId){
                    dic = carPhotoIndexBySeriesAndClassAndColor(seriesId, picClassId, picColorId,false);
                    if(dic.size() != list.size()){
                        dic = carPhotoIndexBySeriesAndClassAndColor(seriesId, picClassId, picColorId,true);
                    }
                }
            }else if(picClassId != 0 && innerColorId != 0){
                list = carPhotoBySeriesAndClassAndInnerColor(seriesId, picClassId, innerColorId,false);
                if(picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId){
                    dic = carPhotoIndexBySeriesAndClassAndInnerColor(seriesId, picClassId, innerColorId,false);
                    if(dic.size() != list.size()){
                        dic = carPhotoIndexBySeriesAndClassAndInnerColor(seriesId, picClassId, innerColorId,true);
                    }
                }
            }else if(picClassId != 0 && picColorId == 0 && innerColorId == 0){
                list = carPhotoBySeriesAndClass(seriesId, picClassId,true);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySeriesAndClass(seriesId, picClassId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeriesAndClass(seriesId, picClassId, true);
                    }
                }
            }else if (picColorId != 0 && picClassId == 0 && innerColorId == 0){
                list = carPhotoBySeriesAndColor(seriesId, picColorId,true);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySeriesAndColor(seriesId, picColorId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeriesAndColor(seriesId, picColorId, true);
                    }
                }
            }else if (innerColorId != 0 && picClassId == 0 && picColorId == 0){
                list = carPhotoBySeriesAndInnerColor(seriesId, innerColorId,false);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId){
                    dic = carPhotoIndexBySeriesAndInnerColor(seriesId, innerColorId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeriesAndInnerColor(seriesId, innerColorId, true);
                    }
                }
            } else {
                list = carPhotoBySeries(seriesId,true);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySeries(seriesId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeries(seriesId, true);
                    }
                }
            }
        }else{
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            if (picClassId != 0 && picColorId != 0) {
                list = carPhotoBySpecAndClassAndColor(seriesId, specId, picClassId, picColorId,true);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySpecAndClassAndColor(seriesId, specId, picClassId, picColorId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndClassAndColor(seriesId, specId, picClassId, picColorId, true);
                    }
                }
            } else if(picClassId != 0 && innerColorId != 0) {
                list = carPhotoBySpecAndClassAndInnerColor(seriesId, specId, picClassId, innerColorId,false);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySpecAndClassAndInnerColor(seriesId, specId, picClassId, innerColorId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndClassAndInnerColor(seriesId, specId, picClassId, innerColorId, true);
                    }
                }

            } else if (picClassId != 0 && picColorId == 0 && innerColorId == 0) {
                list = carPhotoBySpecAndClass(seriesId, specId, picClassId,true);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySpecAndClass(seriesId, specId, picClassId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndClass(seriesId, specId, picClassId, true);
                    }
                }
            } else if (picColorId != 0 && picClassId == 0) {
                list = carPhotoBySpecAndColor(seriesId, specId, picColorId,true);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySpecAndColor(seriesId, specId, picColorId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndColor(seriesId, specId, picColorId, true);
                    }
                }
            } else if (innerColorId != 0 && picClassId == 0) {
                list = carPhotoBySpecAndInnerColor(seriesId, specId, innerColorId,false);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySpecAndInnerColor(seriesId, specId, innerColorId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndInnerColor(seriesId, specId, innerColorId, true);
                    }
                }
            } else {
                list = carPhotoBySpec(seriesId, specId,true);
                if (picId > 0 && list.size() == 0 || list.size() != 0 && list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexBySpec(seriesId, specId, false);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpec(seriesId, specId, true);
                    }
                }
            }
        }
        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        if (dic != null && dic.containsKey(picId) && picId > 0) {
            index = dic.get(picId);
            if (index > 0)
            {
                pageindex = index / size + 1;
            }
        } else if (picId > 0 && dic != null && !dic.containsKey(picId)) {
            result.setPageindex(pageindex);
            result.setPagesize(size);
            result.setRowcount(0);
            result.setIndex(-1);
            builder.setResult(result);
            builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
            builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
            return  builder.build();
        }
        int length = pageindex * size > total ? total : pageindex * size;
        CarPhotoViewItemMessage item;
        for (int i = (pageindex - 1) * size; i < length; i++) {
            item = list.get(i);
            GetPicPictureItemsByConditionResponse.SpecList.Builder spec = GetPicPictureItemsByConditionResponse.SpecList.newBuilder()
                    .setId(item.getPicId())
                    .setImgurl(StringUtils.isNotBlank(item.getPicFilePath())?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()):"")
                    .setSpecid(item.getSpecId())
                    .setSpecname(StringUtils.isNotBlank(item.getSpecname())?item.getSpecname():"")
                    .setSeriesid(item.getSeriesId())
                    .setSeriesname(StringUtils.isNotBlank(item.getSeriesName())?item.getSeriesName():"")
                    .setTypeid(item.getPicClass())
                    .setTypename(StringUtils.isNotBlank(item.getTypename())?item.getTypename():"")
                    .setColorid(item.getPicColorId())
                    .setColorname(StringUtils.isNotBlank(item.getColorname())?item.getColorname():"")
                    .setInnercolorid(item.getInnerColorId())
                    .setInnercolorname(StringUtils.isNotBlank(item.getInnerColorName())?item.getInnerColorName():"")
                    .setWidth(item.getWidth())
                    .setHeight(item.getHeight())
                    .setDealerid(item.getDealerid())
                    .setSixtypicsortid(SixtyPic.get(item.getPointlocatinid(),0))
                    .setOptional(item.getOptional())
                    .setShowid(item.getPicClass() == 55 ? item.getShowId() : 0)
                    .setShowname(StringUtils.isNotBlank(item.getShowname())?item.getShowname():"");
            result.addList(spec);
        }
        result.setPageindex(pageindex);
        result.setPagesize(size);
        result.setRowcount(total);
        result.setIndex(index);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndClassAndColor(int seriesId, int classId, int colorId, boolean isRebuild) {
        List<CarPhotoViewItemMessage> list = carPhotoBySeriesAndClassAndColor(seriesId, classId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(list);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndClassAndColor(List<CarPhotoViewItemMessage> data,int seriesId, int classId, int colorId, boolean isRebuild) {
        List<CarPhotoViewItemMessage> list = carPhotoBySeriesAndClassAndColor(data, seriesId, classId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(list);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndClassAndInnerColor(int seriesId, int classId, int innerColorId, boolean isRebuild) {
        List<CarPhotoViewItemMessage> list = carPhotoBySeriesAndClassAndInnerColor(seriesId, classId, innerColorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(list);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndClassAndInnerColor(List<CarPhotoViewItemMessage> data,int seriesId, int classId, int innerColorId, boolean isRebuild) {
        List<CarPhotoViewItemMessage> list = carPhotoBySeriesAndClassAndInnerColor(data, seriesId, classId, innerColorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(list);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndClass(int seriesId, int classId, boolean isRebuild) {
        List<CarPhotoViewItemMessage> list = carPhotoBySeriesAndClass(seriesId, classId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(list);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndClass(List<CarPhotoViewItemMessage> data, int seriesId, int classId, boolean isRebuild) {
        List<CarPhotoViewItemMessage> list = carPhotoBySeriesAndClass(data, seriesId, classId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(list);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndColor(int seriesId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySeriesAndColor(seriesId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexBySeriesAndColor(List<CarPhotoViewItemMessage> data, int seriesId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySeriesAndColor(data, seriesId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySeriesAndInnerColor(int seriesId, int innerColorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySeriesAndInnerColor(seriesId, innerColorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySeriesAndInnerColor(List<CarPhotoViewItemMessage> data, int seriesId, int innerColorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySeriesAndInnerColor(data, seriesId, innerColorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySeries(int seriesId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySeries(seriesId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySeries(List<CarPhotoViewItemMessage> data, int seriesId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySeries(data, seriesId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndClassAndColor(int seriesId, int specId, int classId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndClassAndColor(seriesId, specId, classId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndClassAndColor(List<CarPhotoViewItemMessage> data, int seriesId, int specId, int classId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndClassAndColor(data, seriesId, specId, classId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndClassAndInnerColor(int seriesId, int specId, int classId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndClassAndInnerColor(seriesId, specId, classId, colorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndClassAndInnerColor(List<CarPhotoViewItemMessage> data,int seriesId, int specId, int classId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndClassAndInnerColor(data,seriesId, specId, classId, colorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndClass(int seriesId, int specId, int classId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndClass(seriesId, specId, classId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndClass(List<CarPhotoViewItemMessage> data,int seriesId, int specId, int classId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndClass(data, seriesId, specId, classId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndColor(int seriesId, int specId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndColor(seriesId, specId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndColor(List<CarPhotoViewItemMessage> data,int seriesId, int specId, int colorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndColor(data, seriesId, specId, colorId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndInnerColor(int seriesId, int specId, int innerColorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndInnerColor(seriesId, specId, innerColorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpecAndInnerColor(List<CarPhotoViewItemMessage> data,int seriesId, int specId, int innerColorId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpecAndInnerColor(data,seriesId, specId, innerColorId, false);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpec(int seriesId, int specId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpec(seriesId, specId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer,Integer> carPhotoIndexBySpec(List<CarPhotoViewItemMessage> data,int seriesId, int specId, boolean isRebuild){
        List<CarPhotoViewItemMessage> photoViewEntities = carPhotoBySpec(data, seriesId, specId, isRebuild);
        HashMap<Integer, Integer> hashMap = carPhotoIndexMap(photoViewEntities);
        return hashMap;
    }

    public HashMap<Integer, Integer> carPhotoIndexMap(List<CarPhotoViewItemMessage> list) {
        HashMap<Integer, Integer> dic = new HashMap<>();
        int picId = 0;
        for (int i = 0; i < list.size(); i++)
        {
            picId = list.get(i).getPicId();
            if (!dic.containsKey(picId))
                dic.put(picId, i);
        }
        return dic;
    }

    public List<CarPhotoViewItemMessage> getCarPhotoViewEntity(int seriesId){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = photosService.get(seriesId);
        if(CollectionUtils.isEmpty(carPhotoViewEntities)){
            carPhotoViewEntities = new ArrayList<>();
        }
        return carPhotoViewEntities;
    }


    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndClassAndColor(int seriesId, int classId, int colorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicClass() == classId
                                && item.getPicColorId() == colorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndClassAndColor(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int classId, int colorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicClass() == classId
                                && item.getPicColorId() == colorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndClass(int seriesId, int classId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicClass() == classId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndClass(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int classId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicClass() == classId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndClassAndInnerColor(int seriesId, int classId, int innerColorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicClass() == classId
                                && item.getInnerColorId() == innerColorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndClassAndInnerColor(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int classId, int innerColorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicClass() == classId
                                && item.getInnerColorId() == innerColorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndColor(int seriesId, int colorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicColorId() == colorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndColor(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int colorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getPicColorId() == colorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndInnerColor(int seriesId, int innerColorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getInnerColorId() == innerColorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeriesAndInnerColor(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int innerColorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getInnerColorId() == innerColorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeries(int seriesId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySeries(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClassAndColor(int seriesId, int specId, int classId, int colorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicClass() == classId
                                && item.getPicColorId() == colorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClassAndColor(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int specId, int classId, int colorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicClass() == classId
                                && item.getPicColorId() == colorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClassAndInnerColor(int seriesId, int specId, int classId, int innerColorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicClass() == classId
                                && item.getInnerColorId() == innerColorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClassAndInnerColor(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int specId, int classId, int innerColorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicClass() == classId
                                && item.getInnerColorId() == innerColorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClass(int seriesId, int specId, int classId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicClass() == classId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndClass( List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int specId, int classId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicClass() == classId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndColor(int seriesId, int specId, int colorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicColorId() == colorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndColor(List<CarPhotoViewItemMessage> carPhotoViewEntities, int seriesId, int specId, int colorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getPicColorId() == colorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndInnerColor(int seriesId, int specId, int innerColorId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getInnerColorId() == innerColorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpecAndInnerColor(List<CarPhotoViewItemMessage> carPhotoViewEntities,int seriesId, int specId, int innerColorId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && item.getInnerColorId() == innerColorId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpec(int seriesId, int specId, boolean hasClub){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpec(List<CarPhotoViewItemMessage> carPhotoViewEntities,int seriesId, int specId, boolean hasClub){
        return carPhotoViewEntities.stream().filter(
                        item -> item.getSpecId() == specId
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getClassOrder)
                        .thenComparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public GetPicClassItemsBySeriesIdResponse getPicClassItemsBySeriesId(GetPicClassItemsBySeriesIdRequest request) {
        GetPicClassItemsBySeriesIdResponse.Builder builder = GetPicClassItemsBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int colorId = request.getColorid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if (seriesId == 0 || state == SpecStateEnum.NONE) {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        GetPicClassItemsBySeriesIdResponse.Result.Builder result = GetPicClassItemsBySeriesIdResponse.Result.newBuilder();
        result.setSeriesid(seriesId);
        result.setColorid(colorId);
        List<SpecPicColorStatisticsEntity> list;
        if (colorId == 0){
            SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesId).join();
            boolean isCv = null != seriesInfo ? Level.isCVLevel(seriesInfo.getLevelId()):false;
            list = specPicClassStatisticsBaseService.get(seriesId, isCv).join();
            if(request.getFilterlessthenthreepicspec() == 0){
                switch (state) {
                    //未上市(0X0001)
                    case NO_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 0).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //即将上市(0X0002)
                    case WAIT_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 10).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_3:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() <= 10).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_12:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //停售(0X0010)
                    case STOP_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 40).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //全部(0X001F)
                    case SELL_31:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                }
            } else {
                switch (state) {
                    //未上市(0X0001)
                    case NO_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 0 && s.getPicNumber() > 2).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //即将上市(0X0002)
                    case WAIT_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 10 && s.getPicNumber() > 2).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_3:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() <= 10 && s.getPicNumber() > 2).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_12:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30 && s.getPicNumber() > 2).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //停售(0X0010)
                    case STOP_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 40 && s.getPicNumber() > 2).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //全部(0X001F)
                    case SELL_31:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getPicNumber() > 2).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                }
            }
        } else{
            list = seriesSpecPicColorStatistics.get(seriesId);
            if(request.getFilterlessthenthreepicspec() == 1){
                switch (state) {
                    //未上市(0X0001)
                    case NO_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 0 && s.getPicNumber() > 2 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //即将上市(0X0002)
                    case WAIT_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 10 && s.getPicNumber() > 2 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_3:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() <= 10 && s.getPicNumber() > 2 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_12:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30 && s.getPicNumber() > 2 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //停售(0X0010)
                    case STOP_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 40 && s.getPicNumber() > 2 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //全部(0X001F)
                    case NONE:
                    case SELL_31:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getPicNumber() > 2 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                }
            } else {
                switch (state) {
                    //未上市(0X0001)
                    case NO_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 0 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //即将上市(0X0002)
                    case WAIT_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 10 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_3:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() <= 10 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    case SELL_12:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //停售(0X0010)
                    case STOP_SELL:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getSpecState() == 40 && s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                    //全部(0X001F)
                    case NONE:
                    case SELL_31:
                        list = CollectionUtils.isEmpty(list) ? null :
                                list.stream().filter(s -> s.getColorId() == colorId).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                        break;
                }
            }
        }
        if (CollectionUtils.isEmpty(list)){
            builder.setResult(result);
            builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
            builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
            return builder.build();
        }
        LinkedHashMap<Integer, ArrayList<SpecPicColorStatisticsEntity>> linkedHashMap = list.stream().collect(Collectors.groupingBy(config ->
                config.getPicClass(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        List<Integer> picClassIds = list.stream().map(SpecPicColorStatisticsEntity::getPicClass).distinct().collect(Collectors.toList());
        Map<Integer, PicClassEntity> picClassMap = picClassBaseService.getList(picClassIds);

        for(Map.Entry<Integer, ArrayList<SpecPicColorStatisticsEntity>> specPicColorStaticsMap:linkedHashMap.entrySet()){
            AtomicInteger picNumber = new AtomicInteger(0);
            AtomicInteger clubPicNumber = new AtomicInteger(0);
            ArrayList<SpecPicColorStatisticsEntity> colorStatisticsEntities = specPicColorStaticsMap.getValue();
            colorStatisticsEntities.forEach(colorStatistics->{
                picNumber.addAndGet(colorStatistics.getPicNumber());
                clubPicNumber.addAndGet(colorStatistics.getClubPicNumber());
            });
            PicClassEntity classEntity = picClassMap.get(specPicColorStaticsMap.getKey());
            GetPicClassItemsBySeriesIdResponse.ClassItem item = GetPicClassItemsBySeriesIdResponse.ClassItem.newBuilder()
                    .setId(specPicColorStaticsMap.getKey())
                    .setName(null != classEntity && StringUtils.isNotBlank(classEntity.getName()) ? classEntity.getName() : "")
                    .setPiccount(picNumber.get())
                    .setClubpiccount(clubPicNumber.get())
                    .build();

            result.addClassitems(item);
        }
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    @Override
    public GetSpecColorListBySpecListResponse getSpecColorListBySpecList(GetSpecColorListBySpecListRequest request) {
        GetSpecColorListBySpecListResponse.Builder builder =  GetSpecColorListBySpecListResponse.newBuilder();
        GetSpecColorListBySpecListResponse.Result.Builder result =  GetSpecColorListBySpecListResponse.Result.newBuilder();
        String specListString =  request.getSpecIdlist();
        List<Integer> speclist = CommonFunction.getListFromStr(specListString);
        if(CollectionUtils.isEmpty(speclist)){
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        speclist = speclist.stream().sorted().collect(Collectors.toList());
        if(speclist.stream().allMatch(item -> item < 1000000)){
            List<SpecBaseInfo> list1 = specBaseService.getList(speclist);
            List<KeyValueDto<Integer, Integer>> list = list1.stream()
                    .flatMap(specBaseInfo -> CommonFunction.getListFromStr(specBaseInfo.getSc()).stream().map(specColorId -> new KeyValueDto<>(specBaseInfo.getId(), specColorId)))
                    .sorted(Comparator.comparingInt(KeyValueDto::getKey))
                    .collect(Collectors.toList());
            int currentSpecId = 0;
            int lastSpecId = 0;
            int colorId = 0;
            GetSpecColorListBySpecListResponse.SpecItem.Builder specItem =  GetSpecColorListBySpecListResponse.SpecItem.newBuilder();
            List<CarSpecColorEntity> colors = null;
            Map<Integer,CarSpecColorEntity> colorMap = new HashMap<>();
            for (int i = 0, len = list.size(); i < len; i++)
            {
                KeyValueDto<Integer,Integer> item = list.get(i);
                currentSpecId = item.getKey();
                if (currentSpecId != lastSpecId)
                {
                    if (lastSpecId != 0)
                    {
                        specItem.setSpecid(lastSpecId);
                        result.addSpecitems(specItem);
                    }
                    specItem = GetSpecColorListBySpecListResponse.SpecItem.newBuilder();
                    lastSpecId = currentSpecId;
                    colors = specColorService.get(currentSpecId).join();
                    if(null == colors){
                        colors = new ArrayList<>();
                    }
                    colorMap = colors.stream().collect(Collectors.toMap(CarSpecColorEntity::getCId,a -> a,(x,y)->x));
                }
                colorId = item.getValue();
                ColorBaseInfo colorBase = colorBaseService.getColor(colorId);
                if(colorBase==null)
                    continue;

                CarSpecColorEntity color = colorMap.get(colorId);
                GetSpecColorListBySpecListResponse.ColorItem.Builder colorItem =  GetSpecColorListBySpecListResponse.ColorItem.newBuilder()
                        .setId(colorId)
                        .setName(colorBase.getName())
                        .setValue(colorBase.getValue())
                        .setPrice(null != color ? color.getPrice() : 0)
                        .setRemark(null != color ? StringUtils.defaultString(color.getMark()) : "");
                specItem.addColoritems(colorItem);
                if (i == len - 1)
                {
                    specItem.setSpecid(lastSpecId);
                    result.addSpecitems(specItem);
                }
            }
            result.setTotal(result.getSpecitemsCount());
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetSpecColorListBySpecListResponse getSpecInnerColorListBySpecIdList(GetSpecColorListBySpecListRequest request) {
        GetSpecColorListBySpecListResponse.Builder builder =  GetSpecColorListBySpecListResponse.newBuilder();
        GetSpecColorListBySpecListResponse.Result.Builder result =  GetSpecColorListBySpecListResponse.Result.newBuilder();
        String specListString =  request.getSpecIdlist();
        List<Integer> speclist = CommonFunction.getListFromStr(specListString);
        if(CollectionUtils.isEmpty(speclist)){
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        speclist = speclist.stream().sorted().collect(Collectors.toList());
        if(speclist.stream().allMatch(item -> item < 1000000)){
            List<SpecBaseInfo> list1 = specBaseService.getList(speclist);
            List<KeyValueDto<Integer, Integer>> list = list1.stream()
                    .flatMap(specBaseInfo -> CommonFunction.getListFromStr(specBaseInfo.getSic()).stream().map(specInnerColorId -> new KeyValueDto<>(specBaseInfo.getId(), specInnerColorId)))
                    .sorted(Comparator.comparingInt(KeyValueDto::getKey))
                    .collect(Collectors.toList());
            int currentSpecId = 0;
            int lastSpecId = 0;
            int colorId = 0;
            GetSpecColorListBySpecListResponse.SpecItem.Builder specItem =  GetSpecColorListBySpecListResponse.SpecItem.newBuilder();
            List<CarSpecColorEntity> colors = null;
            Map<Integer,CarSpecColorEntity> colorMap = new HashMap<>();
            for (int i = 0, len = list.size(); i < len; i++)
            {
                KeyValueDto<Integer,Integer> item = list.get(i);
                currentSpecId = item.getKey();
                if (currentSpecId != lastSpecId)
                {
                    if (lastSpecId != 0)
                    {
                        specItem.setSpecid(lastSpecId);
                        result.addSpecitems(specItem);
                    }
                    specItem = GetSpecColorListBySpecListResponse.SpecItem.newBuilder();
                    lastSpecId = currentSpecId;
                    colors = specInnerColorService.get(currentSpecId).join();
                    if(null == colors){
                        colors = new ArrayList<>();
                    }
                    colorMap = colors.stream().collect(Collectors.toMap(CarSpecColorEntity::getCId,a -> a,(x,y)->x));
                }
                colorId = item.getValue();
                ColorBaseInfo colorBase = innerColorBaseService.getColor(colorId);
                if(colorBase==null){
                    continue;
                }
                CarSpecColorEntity color = colorMap.get(colorId);
                GetSpecColorListBySpecListResponse.ColorItem.Builder colorItem =  GetSpecColorListBySpecListResponse.ColorItem.newBuilder()
                        .setId(colorId)
                        .setName(colorBase.getName())
                        .setValue(colorBase.getValue())
                        .setPrice(null != color ? color.getPrice() : 0)
                        .setRemark(null != color ? StringUtils.defaultString(color.getMark()) : "");
                specItem.addColoritems(colorItem);
                if (i == len - 1)
                {
                    specItem.setSpecid(lastSpecId);
                    result.addSpecitems(specItem);
                }
            }
            result.setTotal(result.getSpecitemsCount());
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetPicClassItemsResponse getPicClassBySpecIdItems(GetPicClassItemsRequest request) {
        GetPicClassItemsResponse.Builder builder = GetPicClassItemsResponse.newBuilder();
        int specId = request.getSpecid();
        int innerColorId = request.getInnerColorId();
        if (specId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        GetPicClassItemsResponse.Result.Builder result = GetPicClassItemsResponse.Result.newBuilder();
        result.setSpecid(specId);
        result.setInnercolorid(innerColorId);
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        if (Objects.isNull(specBaseInfo)) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        int seriesId = specBaseInfo.getSeriesId();
        SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesId).join();
        Map<Integer, List<SpecPicColorStatisticsEntity>> statisticsEntitiesMap;
        List<SpecPicColorStatisticsEntity> statisticsEntities;
        if (innerColorId == 0) {
            boolean isCv = Level.isCVLevel(seriesInfo.getLevelId());
            statisticsEntities = specPicClassStatisticsBaseService.get(seriesId, isCv).join();
            statisticsEntitiesMap = statisticsEntities.stream().filter(e -> Objects.equals(e.getSpecId(), specId)).
                    collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getPicClass, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

        } else {
            statisticsEntities = seriesSpecPicInnerColorStatistics.get(seriesId);
            statisticsEntitiesMap = statisticsEntities.stream().filter(e -> e.getSpecId() == specId && e.getColorId() == innerColorId).
                    collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getPicClass, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        }
        Map<Integer, PicClassEntity> picClassMap = picClassBaseService.getList(new ArrayList<>(statisticsEntitiesMap.keySet()));
        for (Map.Entry<Integer, List<SpecPicColorStatisticsEntity>> integerListEntry : statisticsEntitiesMap.entrySet()) {
            Integer key = integerListEntry.getKey();
            String picName = picClassMap.containsKey(key) ? picClassMap.get(key).getName() : "";
            List<SpecPicColorStatisticsEntity> value = integerListEntry.getValue();
            result.addClassitems(GetPicClassItemsResponse.Result.ClassItems.newBuilder().
                    setId(key).
                    setName(picName).
                    setPiccount(value.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum()).
                    setClubpiccount(value.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum()).
                    build());
        }
        result.setOfficialpicisnew(specBaseInfo.getOpn());
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    /**
     * 根据车系id获取图片类别数量
     * @param request
     * @return
     */
    @Override
    public ApiResult<PicClassItem> getPicClassClassItemsBySeriesIdV2(GetPicClassClassItemsBySeriesIdRequest request) {
        SpecStateEnum stateEnum = Spec.getSpecState(request.getState());
        int seriesId = request.getSeriesid();
        int innerColorId = request.getInnercolorid();
        if(seriesId == 0 || stateEnum == SpecStateEnum.NONE){
            return new ApiResult<>(null,ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        //不满足
        if(!(stateEnum == SpecStateEnum.NO_SELL || stateEnum == SpecStateEnum.WAIT_SELL || stateEnum == SpecStateEnum.SELL_3 ||
                stateEnum == SpecStateEnum.SELL_12 || stateEnum == SpecStateEnum.STOP_SELL || stateEnum == SpecStateEnum.SELL_31)){
            PicClassItem picClassItem = new PicClassItem();
            picClassItem.setSeriesid(seriesId);
            picClassItem.setInnerColorid(innerColorId);
            picClassItem.setClassitems(new ArrayList<>());
            return new ApiResult<>(picClassItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }
        SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesId).join();
        List<SpecPicColorStatisticsEntity> entityList = null;
        if(innerColorId == 0){
            boolean isCv = null != seriesInfo ? Level.isCVLevel(seriesInfo.getLevelId()):false;
            entityList = getSpecPicClassStatistics(isCv,seriesId,stateEnum);
        }else{
            entityList = getSpecPicInnerColorStatistics(seriesId,innerColorId,stateEnum);
        }
        if(CollectionUtils.isEmpty(entityList)){
            PicClassItem picClassItem = new PicClassItem();
            picClassItem.setSeriesid(seriesId);
            picClassItem.setInnerColorid(innerColorId);
            picClassItem.setClassitems(new ArrayList<>());
            return new ApiResult<>(picClassItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }

        List<Integer> picClassIds = entityList.stream().map(SpecPicColorStatisticsEntity::getPicClass).distinct().collect(Collectors.toList());
        Map<Integer, PicClassEntity> picClassMap = picClassBaseService.getList(picClassIds);
        //分组
        Map<Integer, List<SpecPicColorStatisticsEntity>> picColorMap = entityList.stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getPicClass,
                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        List<PicClassItem.ClassItem> classItems = new ArrayList<>();
        for(Map.Entry<Integer, List<SpecPicColorStatisticsEntity>> colorMap:picColorMap.entrySet()){
            //classId
            int classId = colorMap.getKey();
            PicClassItem.ClassItem classItem = new PicClassItem.ClassItem();
            classItem.setId(classId);
            PicClassEntity picClassEntity = picClassMap.get(classId);
            classItem.setName(null != picClassEntity ? picClassEntity.getName() : "");
            //
            List<SpecPicColorStatisticsEntity> statisticsEntityList = colorMap.getValue();
            AtomicInteger picCount = new AtomicInteger(0);
            AtomicInteger clubPicCount = new AtomicInteger(0);
            //遍历
            statisticsEntityList.forEach(colorStatistics->{
                picCount.addAndGet(colorStatistics.getPicNumber());
                clubPicCount.addAndGet(colorStatistics.getClubPicNumber());
            });
            classItem.setPiccount(picCount.get());
            classItem.setClubpiccount(clubPicCount.get());
            classItems.add(classItem);
        }
        PicClassItem picClassItem = new PicClassItem();
        picClassItem.setSeriesid(seriesId);
        //官图七天之内有更新车系
        picClassItem.setOfficialpicisnew(seriesInfo.getOpin());
        picClassItem.setInnerColorid(innerColorId);
        picClassItem.setClassitems(classItems);
        return new ApiResult<>(picClassItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据年代款id获取对应类别前五张图
     * @param request
     * @return
     */
    @Override
    public ApiResult<PicYearItem> getClassPictureByYearId(GetClassPictureByYearIdRequest request) {
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if(seriesId == 0 || yearId == 0){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        List<CarPhotoViewItemMessage> photoViewItemMessages = carPhotoBySeries(seriesId, false).stream().
                filter(carPhotoViewItemMessage -> yearId == carPhotoViewItemMessage.getSyearId()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(photoViewItemMessages)){
            PicYearItem picYearItem = new PicYearItem();
            picYearItem.setTypeitems(new ArrayList<>());
            return new ApiResult<>(picYearItem,RETURN_MESSAGE_ENUM0);
        }
        PicYearItem picYearItem = new PicYearItem();
        Map<Integer, List<CarPhotoViewItemMessage>> carPhotoMap = photoViewItemMessages.stream().collect(Collectors.groupingBy(carPhotoViewEntity -> carPhotoViewEntity.getPicClass(),
                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        List<PicYearItem.TypeItem> typeItems = new ArrayList<>();
        for(Map.Entry<Integer, List<CarPhotoViewItemMessage>> carPhotoViewMap : carPhotoMap.entrySet()){
            List<CarPhotoViewItemMessage> viewEntities = carPhotoViewMap.getValue().stream().limit(5).collect(Collectors.toList());
            List<PicYearItem.PicItem> picItemList = new ArrayList<>();
            //图片信息
            for(CarPhotoViewItemMessage carPhotoView : viewEntities ){
                PicYearItem.PicItem picItem = new PicYearItem.PicItem();
                picItem.setSpecid(carPhotoView.getSpecId());
                picItem.setSpecname(carPhotoView.getSpecname());
                picItem.setId(carPhotoView.getPicId());
                picItem.setFilepath(ImageUtil.getFullImagePathWithoutReplace(carPhotoView.getPicFilePath()));
                picItemList.add(picItem);
            }
            //分类信息
            PicYearItem.TypeItem typeItem = new PicYearItem.TypeItem();
            typeItem.setTypeid(carPhotoViewMap.getKey());
            typeItem.setTypename(carPhotoViewMap.getValue().get(0).getTypename());
            typeItem.setPictotal(carPhotoViewMap.getValue().size());
            typeItem.setPicitems(picItemList);
            typeItems.add(typeItem);
        }
        //返回对象组装
        picYearItem.setYearid(yearId);
        picYearItem.setSeriesid(seriesId);
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        picYearItem.setSeriesname(null != seriesBaseInfo ? seriesBaseInfo.getName():"");
        picYearItem.setTypeitems(typeItems);
        return new ApiResult<>(picYearItem, RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据年代款id获取对应25图
     * @param request
     * @return
     */
    @Override
    public ApiResult<Pic25YearItem> getYear25PictureByYearId(GetYear25PictureByYearIdRequest request) {
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if(seriesId == 0 || yearId == 0){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        List<Pic25YearItem.PicItem> picItems = new ArrayList<>();
        //获取商用车25图
        if(null != seriesBaseInfo && Level.isCVLevel(seriesBaseInfo.getLevelId())){
            List<CarPhotoViewItemMessage> photoViewItemMessages = getCarPhotoViewEntity(seriesId).stream().filter(carPhotoView ->
                    carPhotoView.getClassOrder() <= 2 && carPhotoView.getSyearId() == yearId && carPhotoView.getIsClubPhoto() == 0).
                    sorted(Comparator.comparing(CarPhotoViewItemMessage::getSpecPicNumber,Comparator.reverseOrder())).collect(Collectors.toList());
            //classOrder == 1
            List<CarPhotoViewItemMessage> messageOneList = photoViewItemMessages.stream().filter(carPhotoView -> carPhotoView.getClassOrder() == 1 && carPhotoView.getIsTitle() == 1).limit(1).collect(Collectors.toList());
            //classOrder == 2
            List<CarPhotoViewItemMessage> messageTwoList = photoViewItemMessages.stream().filter(carPhotoView -> carPhotoView.getClassOrder() == 2).limit(4).collect(Collectors.toList());
            //合并
            messageOneList.addAll(messageTwoList);
            for(CarPhotoViewItemMessage carPhotoViewItemMessage : messageOneList){
                Pic25YearItem.PicItem picItem = new Pic25YearItem.PicItem();
                int picClassId = carPhotoViewItemMessage.getPicClass();
                picItem.setItemid(picClassId == 1 ? 1 : 0);
                picItem.setTypeid(picClassId);
                picItem.setItemname("");
                picItem.setPicid(carPhotoViewItemMessage.getPicId());
                picItem.setPicpath(ImageUtil.getFullImagePathWithoutReplace(carPhotoViewItemMessage.getPicFilePath()));
                picItem.setSpecid(carPhotoViewItemMessage.getSpecId());
                picItem.setSpecname(carPhotoViewItemMessage.getSpecname());
                picItems.add(picItem);
            }
        }else{//获取乘用车25图
            List<Car25PictureViewEntity> car25PictureViewEntities = year25PictureByYearIdBaseService.get(seriesId, yearId).join();
            if(!CollectionUtils.isEmpty(car25PictureViewEntities)){
                car25PictureViewEntities = car25PictureViewEntities.stream().sorted(
                        Comparator.comparing(Car25PictureViewEntity::getOrdercls).thenComparing(Car25PictureViewEntity::
                                getSpecId,Comparator.reverseOrder())).collect(Collectors.toList());
                int itemId = 0;
                for(Car25PictureViewEntity car25PictureViewEntity : car25PictureViewEntities){
                    int orderCls = car25PictureViewEntity.getOrdercls();
                    if(orderCls == itemId || !Spec.ORDER_CLS.contains(orderCls)){
                        continue;
                    }
                    itemId = orderCls;
                    Pic25YearItem.PicItem picItem = new Pic25YearItem.PicItem();
                    picItem.setItemid(itemId);
                    picItem.setTypeid(car25PictureViewEntity.getTopId());
                    picItem.setItemname(car25PictureViewEntity.getItemName());
                    picItem.setPicid(car25PictureViewEntity.getPicId());
                    picItem.setPicpath(ImageUtil.getFullImagePathWithoutReplace(car25PictureViewEntity.getPicPath()));
                    picItem.setSpecid(car25PictureViewEntity.getSpecId());
                    picItem.setSpecname(car25PictureViewEntity.getSpecName());
                    picItems.add(picItem);
                }
            }
        }
        Pic25YearItem pic25YearItem = new Pic25YearItem();
        pic25YearItem.setYearid(yearId);
        pic25YearItem.setSeriesid(seriesId);
        pic25YearItem.setSeriesname(null != seriesBaseInfo ? seriesBaseInfo.getName() : "");
        pic25YearItem.setTotal(picItems.size());
        pic25YearItem.setPicitems(picItems);
        return new ApiResult<>(pic25YearItem,RETURN_MESSAGE_ENUM0);
    }

    /**
     * 没有传内饰颜色id
     * @param seriesId
     * @param stateEnum
     * @return
     */
    public List<SpecPicColorStatisticsEntity> getSpecPicClassStatistics(boolean isCv,int seriesId,SpecStateEnum stateEnum){

        List<SpecPicColorStatisticsEntity> entityList = specPicClassStatisticsBaseService.get(seriesId, isCv).join();
        if(!CollectionUtils.isEmpty(entityList)){
            switch (stateEnum){
                case NO_SELL:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() == 0).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case WAIT_SELL:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() == 10).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case SELL_3:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() <= 10).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case SELL_12:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() >= 20 && specPicColorStatisticsEntity.getSpecState() <= 30).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case STOP_SELL:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() == 40).sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case SELL_31:
                    entityList = entityList.stream().
                            sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
            }
        }
        return entityList;
    }

    /**
     * 内饰颜色
     * @param seriesId
     * @param innerColorId
     * @param stateEnum
     * @return
     */
    public List<SpecPicColorStatisticsEntity> getSpecPicInnerColorStatistics(int seriesId,int innerColorId, SpecStateEnum stateEnum){
        List<SpecPicColorStatisticsEntity> entityList = seriesSpecPicInnerColorStatistics.get(seriesId);
        if(!CollectionUtils.isEmpty(entityList)){
            switch (stateEnum){
                case NO_SELL:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() == 0 && innerColorId == specPicColorStatisticsEntity.getColorId()).
                            sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case WAIT_SELL:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() == 10 && innerColorId == specPicColorStatisticsEntity.getColorId()).
                            sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case SELL_3:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() <= 10 && innerColorId == specPicColorStatisticsEntity.getColorId()).
                            sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case SELL_12:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() >= 20 && specPicColorStatisticsEntity.getSpecState() <= 30 && innerColorId == specPicColorStatisticsEntity.getColorId()).
                            sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case STOP_SELL:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity ->
                            specPicColorStatisticsEntity.getSpecState() == 40 && innerColorId == specPicColorStatisticsEntity.getColorId()).
                            sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).collect(Collectors.toList());
                    break;
                case SELL_31:
                    entityList = entityList.stream().filter(specPicColorStatisticsEntity -> innerColorId == specPicColorStatisticsEntity.getColorId()).
                            sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getClassOrder)).
                            collect(Collectors.toList());
                    break;
            }
        }
        return entityList;
    }

    /**
     * 根据车型id获取对应类别前五张图
     * @param request
     * @return
     */
    @Override
    public ApiResult<PicSpecItem> getSpecClassPictureBySpecId(SpecClassPictureBySpecIdRequest request) {
        int specId = request.getSpecid();
        if(specId == 0){
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        int seriesId = null != specBaseInfo?specBaseInfo.getSeriesId():0;
        List<CarPhotoViewItemMessage> carPhotoViewEntities = photosService.get(seriesId);
        PicSpecItem picSpecItem = new PicSpecItem();
        if(CollectionUtils.isEmpty(carPhotoViewEntities)){
            picSpecItem.setTypeitems(new ArrayList<>());
            return new ApiResult<>(picSpecItem, RETURN_MESSAGE_ENUM0);
        }
        carPhotoViewEntities = carPhotoBySpec(carPhotoViewEntities, specId, false);

        Map<Integer, List<CarPhotoViewItemMessage>> collect = carPhotoViewEntities.stream().collect(Collectors.groupingBy(carPhotoViewEntity -> carPhotoViewEntity.getPicClass(),
                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        List<PicSpecItem.TypeItem> typeItems = new ArrayList<>();
        for(Map.Entry<Integer, List<CarPhotoViewItemMessage>> carPhotoViewMap : collect.entrySet()){

            List<CarPhotoViewItemMessage> viewEntities = carPhotoViewMap.getValue().stream().limit(5).collect(Collectors.toList());
            List<PicSpecItem.PicItem> picItemList = new ArrayList<>();
            //图片信息
            for(CarPhotoViewItemMessage carPhotoView : viewEntities ){
                PicSpecItem.PicItem picItem = new PicSpecItem.PicItem(carPhotoView.getPicId(),ImageUtil.getFullImagePathWithoutReplace(carPhotoView.getPicFilePath()));
                picItemList.add(picItem);
            }
            //分类信息
            PicSpecItem.TypeItem typeItem = new PicSpecItem.TypeItem();
            typeItem.setTypeid(carPhotoViewMap.getKey());
            typeItem.setTypename(carPhotoViewMap.getValue().get(0).getTypename());
            typeItem.setPictotal(carPhotoViewMap.getValue().size());
            typeItem.setPicitems(picItemList);
            typeItems.add(typeItem);
        }
        //返回对象组装
        picSpecItem.setSpecid(specId);
        picSpecItem.setSpecname(null != specBaseInfo ? specBaseInfo.getSpecName():"");
        picSpecItem.setSeriesid(seriesId);
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        picSpecItem.setSeriesname(null != seriesBaseInfo ? seriesBaseInfo.getName():"");
        picSpecItem.setTypeitems(typeItems);
        return new ApiResult<>(picSpecItem, RETURN_MESSAGE_ENUM0);
    }

    public List<CarPhotoViewItemMessage> carPhotoBySpec(List<CarPhotoViewItemMessage> list, int specId, boolean hasClub) {
        List<CarPhotoViewItemMessage> result = list.stream().filter(
                item -> {
                    if (item.getSpecId() == (specId) &&
                            (hasClub || item.getIsClubPhoto() == (0))) {
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
        ).collect(Collectors.toList());
        return result;
    }

    @Override
    public GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByPicId(GetPicScanPictureItemsByPicIdRequest request) {
        GetPicScanPictureItemsByConditionResponse.Builder builder = GetPicScanPictureItemsByConditionResponse.newBuilder();
        GetPicScanPictureItemsByConditionResponse.Result.Builder result = GetPicScanPictureItemsByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int picColorId = request.getColorid();
        int picId = request.getPicid();
        if ((seriesId <= 0 && specId <= 0) || picColorId < 0 || picId <= 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        HashMap<Integer, Integer> dic = null;
        List<CarPhotoViewItemMessage> list = null;
        if (seriesId != 0 && specId == 0) {
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySeriesAndColor(carPhotoViewEntities, seriesId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeriesAndColor(carPhotoViewEntities, seriesId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySeries(carPhotoViewEntities, seriesId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeries(carPhotoViewEntities, seriesId, true);
                    }
                }
            }
        } else {
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySpecAndColor(carPhotoViewEntities, seriesId, specId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndColor(carPhotoViewEntities, seriesId, specId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySpec(carPhotoViewEntities, seriesId, specId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpec(carPhotoViewEntities, seriesId, specId, true);
                    }
                }
            }
        }

        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        if (dic != null && dic.containsKey(picId)) {
            index = dic.get(picId);
        } else {
            picId = list.size() > 0 ? list.get(0).getPicId() : 0;
        }

//        List<Integer> seriesIds = list.stream().map(x -> x.getSeriesId()).distinct().collect(Collectors.toList());
//        Map<Integer,SeriesInfo> seriesInfoMap = seriesInfoService.getMap(seriesIds, false, false);
        SeriesInfo seriesInfo = seriesInfoService.get(seriesId, false, false);
        CarPhotoViewItemMessage item;
        for (int i = index - 1; i <= index + 1; i++) {
            if (i < 0 || i > total - 1) {
                continue;
            }
            item = list.get(i);
            //SeriesInfo seriesInfo = CollectionUtils.isEmpty(seriesInfoMap) ? null : seriesInfoMap.get(item.getSeriesId());
            GetPicScanPictureItemsByConditionResponse.PicList.Builder spec = GetPicScanPictureItemsByConditionResponse.PicList.newBuilder()
                    .setId(item.getPicId())
                    .setTypeid(item.getPicClass())
                    .setColorid(item.getPicColorId())
                    .setFilepath(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()) : "")
                    .setIshd(item.getIsHD())
                    .setBrandid(seriesInfo != null ? seriesInfo.getBrandid() : 0)
                    .setBrandname(seriesInfo != null ? seriesInfo.getBrandname() : "")
                    .setFactoryid(seriesInfo != null ? seriesInfo.getFctid() : 0)
                    .setFactoryname(seriesInfo != null ? seriesInfo.getFctname() : "")
                    .setSeriesid(item.getSeriesId())
                    .setSeriesname(StringUtils.isNotBlank(item.getSeriesName()) ? item.getSeriesName() : "")
                    .setSpecid(item.getSpecId())
                    .setSpecname(StringUtils.isNotBlank(item.getSpecname()) ? item.getSpecname() : "");
            result.addPicitems(spec);
        }
        result.setTotal(total);
        result.setPicid(picId);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    @Override
    public GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByClass(GetPicScanPictureItemsByClassRequest request) {
        GetPicScanPictureItemsByConditionResponse.Builder builder = GetPicScanPictureItemsByConditionResponse.newBuilder();
        GetPicScanPictureItemsByConditionResponse.Result.Builder result = GetPicScanPictureItemsByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int picColorId = request.getColorid();
        int classId = request.getClassid();
        if ((seriesId <= 0 && specId <= 0) || picColorId < 0 || classId <= 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        HashMap<Integer, Integer> dic = null;
        List<CarPhotoViewItemMessage> list = null;
        if (seriesId != 0 && specId == 0) {
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySeriesAndColor(carPhotoViewEntities,seriesId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeriesAndColor(carPhotoViewEntities,seriesId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySeries(carPhotoViewEntities,seriesId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeries(carPhotoViewEntities,seriesId, true);
                    }
                }
            }
        } else {
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySpecAndColor(carPhotoViewEntities,seriesId, specId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndColor(carPhotoViewEntities,seriesId, specId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySpec(carPhotoViewEntities,seriesId, specId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpec(carPhotoViewEntities,seriesId, specId, true);
                    }
                }
            }
        }

        int picId = 0;
        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        if (dic != null && total > 0) {
            CarPhotoViewItemMessage first = list.stream().filter(x -> x.getPicClass() == classId).findFirst().orElse(null);
            picId = first != null ? first.getPicId() : list.get(0).getPicId();
            index = picId > 0 ? dic.get(picId) : 0;
        } else {
            picId = list.size() > 0 ? list.get(0).getPicId() : 0;
        }

        List<Integer> seriesIds = list.stream().map(x -> x.getSeriesId()).collect(Collectors.toList());
        Map<Integer,SeriesInfo> seriesInfoMap = seriesInfoService.getMap(seriesIds, false, false);

        CarPhotoViewItemMessage item;
        for (int i = index - 1; i <= index + 1; i++) {
            if (i < 0 || i > total - 1) {
                continue;
            }
            item = list.get(i);
            SeriesInfo seriesInfo = CollectionUtils.isEmpty(seriesInfoMap) ? null : seriesInfoMap.get(item.getSeriesId());
            GetPicScanPictureItemsByConditionResponse.PicList.Builder spec = GetPicScanPictureItemsByConditionResponse.PicList.newBuilder()
                    .setId(item.getPicId())
                    .setTypeid(item.getPicClass())
                    .setColorid(item.getPicColorId())
                    .setFilepath(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()) : "")
                    .setIshd(item.getIsHD())
                    .setBrandid(seriesInfo != null ? seriesInfo.getBrandid() : 0)
                    .setBrandname(seriesInfo != null ? seriesInfo.getBrandname() : "")
                    .setFactoryid(seriesInfo != null ? seriesInfo.getFctid() : 0)
                    .setFactoryname(seriesInfo != null ? seriesInfo.getFctname() : "")
                    .setSeriesid(item.getSeriesId())
                    .setSeriesname(StringUtils.isNotBlank(item.getSeriesName()) ? item.getSeriesName() : "")
                    .setSpecid(item.getSpecId())
                    .setSpecname(StringUtils.isNotBlank(item.getSpecname()) ? item.getSpecname() : "");
            result.addPicitems(spec);
        }
        result.setTotal(total);
        result.setPicid(picId);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    @Override
    public GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByColor(GetPicScanPictureItemsByColorRequest request) {
        GetPicScanPictureItemsByConditionResponse.Builder builder = GetPicScanPictureItemsByConditionResponse.newBuilder();
        GetPicScanPictureItemsByConditionResponse.Result.Builder result = GetPicScanPictureItemsByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int picColorId = request.getColorid();
        if ((seriesId <= 0 && specId <= 0) || picColorId < 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        List<CarPhotoViewItemMessage> list = null;
        if (seriesId != 0 && specId == 0) {
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySeriesAndColor(carPhotoViewEntities,seriesId, picColorId, false);
            } else {
                list = carPhotoBySeries(carPhotoViewEntities,seriesId, false);
            }
        } else {
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySpecAndColor(carPhotoViewEntities,seriesId, specId, picColorId, false);
            } else {
                list = carPhotoBySpec(carPhotoViewEntities,seriesId, specId, false);
            }
        }

        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        int picId = total > 0 ? list.get(0).getPicId() : 0;

        List<Integer> seriesIds = list.stream().map(x -> x.getSeriesId()).collect(Collectors.toList());
        Map<Integer,SeriesInfo> seriesInfoMap = seriesInfoService.getMap(seriesIds, false, false);

        CarPhotoViewItemMessage item;
        for (int i = index - 1; i <= index + 1; i++) {
            if (i < 0 || i > total - 1) {
                continue;
            }
            item = list.get(i);
            SeriesInfo seriesInfo = CollectionUtils.isEmpty(seriesInfoMap) ? null : seriesInfoMap.get(item.getSeriesId());
            GetPicScanPictureItemsByConditionResponse.PicList.Builder spec = GetPicScanPictureItemsByConditionResponse.PicList.newBuilder()
                    .setId(item.getPicId())
                    .setTypeid(item.getPicClass())
                    .setColorid(item.getPicColorId())
                    .setFilepath(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()) : "")
                    .setIshd(item.getIsHD())
                    .setBrandid(seriesInfo != null ? seriesInfo.getBrandid() : 0)
                    .setBrandname(seriesInfo != null ? seriesInfo.getBrandname() : "")
                    .setFactoryid(seriesInfo != null ? seriesInfo.getFctid() : 0)
                    .setFactoryname(seriesInfo != null ? seriesInfo.getFctname() : "")
                    .setSeriesid(item.getSeriesId())
                    .setSeriesname(StringUtils.isNotBlank(item.getSeriesName()) ? item.getSeriesName() : "")
                    .setSpecid(item.getSpecId())
                    .setSpecname(StringUtils.isNotBlank(item.getSpecname()) ? item.getSpecname() : "");
            result.addPicitems(spec);
        }
        result.setTotal(total);
        result.setPicid(picId);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    @Override
    public GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByInnerColor(GetPicScanPictureInnerItemsByInnerColorRequest request) {
        GetPicScanPictureInnerItemsByConditionResponse.Builder builder = GetPicScanPictureInnerItemsByConditionResponse.newBuilder();
        GetPicScanPictureInnerItemsByConditionResponse.Result.Builder result = GetPicScanPictureInnerItemsByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int innerColorId = request.getInnerColorid();
        if ((seriesId <= 0 && specId <= 0) || innerColorId < 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        List<CarPhotoViewItemMessage> list = null;
        if (seriesId != 0 && specId == 0) {
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (innerColorId != 0) {
                list = carPhotoBySeriesAndInnerColor(carPhotoViewEntities,seriesId, innerColorId, false);
            } else {
                list = carPhotoBySeries(carPhotoViewEntities,seriesId, false);
            }
        } else {
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (innerColorId != 0) {
                list = carPhotoBySpecAndInnerColor(carPhotoViewEntities,seriesId, specId, innerColorId, false);
            } else {
                list = carPhotoBySpec(carPhotoViewEntities,seriesId, specId, false);
            }
        }

        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        int picId = total > 0 ? list.get(0).getPicId() : 0;

        List<Integer> seriesIds = list.stream().map(x -> x.getSeriesId()).collect(Collectors.toList());
        Map<Integer,SeriesInfo> seriesInfoMap = seriesInfoService.getMap(seriesIds, false, false);

        CarPhotoViewItemMessage item;
        for (int i = index - 1; i <= index + 1; i++) {
            if (i < 0 || i > total - 1) {
                continue;
            }
            item = list.get(i);
            SeriesInfo seriesInfo = CollectionUtils.isEmpty(seriesInfoMap) ? null : seriesInfoMap.get(item.getSeriesId());
            GetPicScanPictureInnerItemsByConditionResponse.PicList.Builder spec = GetPicScanPictureInnerItemsByConditionResponse.PicList.newBuilder()
                    .setId(item.getPicId())
                    .setTypeid(item.getPicClass())
                    .setColorid(item.getPicColorId())
                    .setInnercolorid(item.getInnerColorId())
                    .setFilepath(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()) : "")
                    .setIshd(item.getIsHD())
                    .setBrandid(seriesInfo != null ? seriesInfo.getBrandid() : 0)
                    .setBrandname(seriesInfo != null ? seriesInfo.getBrandname() : "")
                    .setFactoryid(seriesInfo != null ? seriesInfo.getFctid() : 0)
                    .setFactoryname(seriesInfo != null ? seriesInfo.getFctname() : "")
                    .setSeriesid(item.getSeriesId())
                    .setSeriesname(StringUtils.isNotBlank(item.getSeriesName()) ? item.getSeriesName() : "")
                    .setSpecid(item.getSpecId())
                    .setSpecname(StringUtils.isNotBlank(item.getSpecname()) ? item.getSpecname() : "");
            result.addPicitems(spec);
        }
        result.setTotal(total);
        result.setPicid(picId);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    @Override
    public GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByPicId(GetPicScanPictureInnerItemsByPicIdRequest request) {
        GetPicScanPictureInnerItemsByConditionResponse.Builder builder = GetPicScanPictureInnerItemsByConditionResponse.newBuilder();
        GetPicScanPictureInnerItemsByConditionResponse.Result.Builder result = GetPicScanPictureInnerItemsByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int picColorId = request.getInnerColorid();
        int picId = request.getPicid();
        if ((seriesId <= 0 && specId <= 0) || picColorId < 0 || picId <= 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        HashMap<Integer, Integer> dic = null;
        List<CarPhotoViewItemMessage> list = null;
        if (seriesId != 0 && specId == 0) {
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySeriesAndInnerColor(carPhotoViewEntities,seriesId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeriesAndInnerColor(carPhotoViewEntities,seriesId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySeries(carPhotoViewEntities,seriesId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeries(carPhotoViewEntities,seriesId, true);
                    }
                }
            }
        } else {
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySpecAndInnerColor(carPhotoViewEntities,seriesId, specId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndInnerColor(carPhotoViewEntities,seriesId, specId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySpec(carPhotoViewEntities,seriesId, specId, false);
                if (list.size() == 0 || list.get(0).getPicId() != picId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpec(carPhotoViewEntities,seriesId, specId, true);
                    }
                }
            }
        }

        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        if (dic != null && dic.containsKey(picId)) {
            index = dic.get(picId);
        } else {
            picId = list.size() > 0 ? list.get(0).getPicId() : 0;
        }

        List<Integer> seriesIds = list.stream().map(x -> x.getSeriesId()).collect(Collectors.toList());
        Map<Integer,SeriesInfo> seriesInfoMap = seriesInfoService.getMap(seriesIds, false, false);

        CarPhotoViewItemMessage item;
        for (int i = index - 1; i <= index + 1; i++) {
            if (i < 0 || i > total - 1) {
                continue;
            }
            item = list.get(i);
            SeriesInfo seriesInfo = CollectionUtils.isEmpty(seriesInfoMap) ? null : seriesInfoMap.get(item.getSeriesId());
            GetPicScanPictureInnerItemsByConditionResponse.PicList.Builder spec = GetPicScanPictureInnerItemsByConditionResponse.PicList.newBuilder()
                    .setId(item.getPicId())
                    .setTypeid(item.getPicClass())
                    .setColorid(item.getPicColorId())
                    .setInnercolorid(item.getInnerColorId())
                    .setFilepath(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()) : "")
                    .setIshd(item.getIsHD())
                    .setBrandid(seriesInfo != null ? seriesInfo.getBrandid() : 0)
                    .setBrandname(seriesInfo != null ? seriesInfo.getBrandname() : "")
                    .setFactoryid(seriesInfo != null ? seriesInfo.getFctid() : 0)
                    .setFactoryname(seriesInfo != null ? seriesInfo.getFctname() : "")
                    .setSeriesid(item.getSeriesId())
                    .setSeriesname(StringUtils.isNotBlank(item.getSeriesName()) ? item.getSeriesName() : "")
                    .setSpecid(item.getSpecId())
                    .setSpecname(StringUtils.isNotBlank(item.getSpecname()) ? item.getSpecname() : "");
            result.addPicitems(spec);
        }
        result.setTotal(total);
        result.setPicid(picId);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }
    @Override
    public GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByClass(GetPicScanPictureInnerItemsByClassRequest request) {
        GetPicScanPictureInnerItemsByConditionResponse.Builder builder = GetPicScanPictureInnerItemsByConditionResponse.newBuilder();
        GetPicScanPictureInnerItemsByConditionResponse.Result.Builder result = GetPicScanPictureInnerItemsByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int picColorId = request.getInnerColorid();
        int classId = request.getClassid();
        if ((seriesId <= 0 && specId <= 0) || picColorId < 0 || classId <= 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        HashMap<Integer, Integer> dic = null;
        List<CarPhotoViewItemMessage> list = null;
        if (seriesId != 0 && specId == 0) {
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySeriesAndInnerColor(carPhotoViewEntities,seriesId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeriesAndInnerColor(carPhotoViewEntities,seriesId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySeries(carPhotoViewEntities,seriesId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySeries(carPhotoViewEntities,seriesId, true);
                    }
                }
            }
        } else {
            SpecBaseInfo specBase = specBaseService.get(specId).join();
            seriesId = null != specBase ? specBase.getSeriesId() : seriesId;
            List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
            if (picColorId != 0) {
                list = carPhotoBySpecAndInnerColor(carPhotoViewEntities,seriesId, specId, picColorId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpecAndInnerColor(carPhotoViewEntities,seriesId, specId, picColorId, true);
                    }
                }
            } else {
                list = carPhotoBySpec(carPhotoViewEntities,seriesId, specId, false);
                if (list.size() == 0 || list.get(0).getPicClass() != classId) {
                    dic = carPhotoIndexMap(list);
                    if (dic.size() != list.size()) {
                        dic = carPhotoIndexBySpec(carPhotoViewEntities,seriesId, specId, true);
                    }
                }
            }
        }

        int picId = 0;
        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        if (dic != null && total > 0) {
            CarPhotoViewItemMessage first = list.stream().filter(x -> x.getPicClass() == classId).findFirst().orElse(null);
            picId = first != null ? first.getPicId() : list.get(0).getPicId();
            index = picId > 0 ? dic.get(picId) : 0;
        } else {
            picId = list.size() > 0 ? list.get(0).getPicId() : 0;
        }

        List<Integer> seriesIds = list.stream().map(x -> x.getSeriesId()).collect(Collectors.toList());
        Map<Integer,SeriesInfo> seriesInfoMap = seriesInfoService.getMap(seriesIds, false, false);

        CarPhotoViewItemMessage item;
        for (int i = index - 1; i <= index + 1; i++) {
            if (i < 0 || i > total - 1) {
                continue;
            }
            item = list.get(i);
            SeriesInfo seriesInfo = CollectionUtils.isEmpty(seriesInfoMap) ? null : seriesInfoMap.get(item.getSeriesId());
            GetPicScanPictureInnerItemsByConditionResponse.PicList.Builder spec = GetPicScanPictureInnerItemsByConditionResponse.PicList.newBuilder()
                    .setId(item.getPicId())
                    .setTypeid(item.getPicClass())
                    .setColorid(item.getPicColorId())
                    .setInnercolorid(item.getInnerColorId())
                    .setFilepath(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathWithoutReplace(item.getPicFilePath()) : "")
                    .setIshd(item.getIsHD())
                    .setBrandid(seriesInfo != null ? seriesInfo.getBrandid() : 0)
                    .setBrandname(seriesInfo != null ? seriesInfo.getBrandname() : "")
                    .setFactoryid(seriesInfo != null ? seriesInfo.getFctid() : 0)
                    .setFactoryname(seriesInfo != null ? seriesInfo.getFctname() : "")
                    .setSeriesid(item.getSeriesId())
                    .setSeriesname(StringUtils.isNotBlank(item.getSeriesName()) ? item.getSeriesName() : "")
                    .setSpecid(item.getSpecId())
                    .setSpecname(StringUtils.isNotBlank(item.getSpecname()) ? item.getSpecname() : "");
            result.addPicitems(spec);
        }
        result.setTotal(total);
        result.setPicid(picId);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    @Override
    public GetSpecColorListBySpecIdResponse getSpecColorListBySpecId(GetSpecColorListBySpecIdRequest request) {
        GetSpecColorListBySpecIdResponse.Builder builder = GetSpecColorListBySpecIdResponse.newBuilder();
        int specId = request.getSpecid();
        if (specId == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        if (specId < 1000000) {
            List<SpecStateEntity> specStates = specListSameYearBaseService.get(specId).join();
            return getSpecColorListBySeriesId(specStates, false);
        }
        GetSpecColorListBySpecIdResponse.Result.Builder result = GetSpecColorListBySpecIdResponse.Result.newBuilder();
        result.addAllSpecitems(new ArrayList<>()).setTotal(0);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @Override
    public GetSpecColorListBySpecIdResponse getSpecInnerColorListBySpecId(GetSpecColorListBySpecIdRequest request) {
        GetSpecColorListBySpecIdResponse.Builder builder = GetSpecColorListBySpecIdResponse.newBuilder();
        int specId = request.getSpecid();
        if (specId == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        if (specId < 1000000) {
            List<SpecStateEntity> specStates = specListSameYearBaseService.get(specId).join();
            return getSpecColorListBySeriesId(specStates, true);
        }
        GetSpecColorListBySpecIdResponse.Result.Builder result = GetSpecColorListBySpecIdResponse.Result.newBuilder();
        result.addAllSpecitems(new ArrayList<>()).setTotal(0);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @Override
    public GetSpecColorListBySpecIdResponse getSpecColorListByYearId(GetSpecColorListByYearIdRequest request){
        GetSpecColorListBySpecIdResponse.Builder builder = GetSpecColorListBySpecIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if (seriesId == 0 || yearId == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(seriesBaseInfo != null){
            if (!Level.isCVLevel(seriesBaseInfo.getLevelId())) {
                List<SpecStateEntity> specStates = specListSameYearByYearService.get(seriesId, yearId, false).join();
                return getSpecColorListBySeriesId(specStates, false);
            }
        }
        GetSpecColorListBySpecIdResponse.Result.Builder result = GetSpecColorListBySpecIdResponse.Result.newBuilder();
        result.addAllSpecitems(new ArrayList<>()).setTotal(0);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @Override
    public GetSpecColorListBySpecIdResponse getSpecInnerColorListByYearId(GetSpecColorListByYearIdRequest request){
        GetSpecColorListBySpecIdResponse.Builder builder = GetSpecColorListBySpecIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if (seriesId == 0 || yearId == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(seriesBaseInfo != null){
            if (!Level.isCVLevel(seriesBaseInfo.getLevelId())) {
                List<SpecStateEntity> specStates = specListSameYearByYearService.get(seriesId, yearId, false).join();
                return getSpecColorListBySeriesId(specStates, true);
            }
        }
        GetSpecColorListBySpecIdResponse.Result.Builder result = GetSpecColorListBySpecIdResponse.Result.newBuilder();
        result.addAllSpecitems(new ArrayList<>()).setTotal(0);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }
    /**
     * 根据精选类别获取全部精选信息
     * @param request
     * @return
     */
    @Override
    public GetFeaturedInfoByTypeIdResponse getFeaturedInfoByTypeId(GetFeaturedInfoByTypeIdRequest request) {
        GetFeaturedInfoByTypeIdResponse.Builder builder = GetFeaturedInfoByTypeIdResponse.newBuilder();
        int typeId = CommonFunction.getStringToInt(request.getTypeid(), -1);
        int page = request.getPage() == 0 ? 1 : request.getPage();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        if(typeId < 0 || page < 0 || size < 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<FeaturedPictureEntity> featuredPictureEntities = featuredPictureBaseService.getAll();
        GetFeaturedInfoByTypeIdResponse.Result.Builder resultBuilder = GetFeaturedInfoByTypeIdResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(featuredPictureEntities)){
            if (typeId != 0 || page < 1 || size < 0) {
                featuredPictureEntities = featuredPictureEntities.stream().filter(featuredPictureEntity ->
                        featuredPictureEntity.getTypeId() == typeId).collect(Collectors.toList());
            }
            int startIndex = (page - 1) * size;
            int endIndex = startIndex + size > featuredPictureEntities.size() ? featuredPictureEntities.size() : startIndex + size;
            int specId = 0, picId = 0;
            List<Integer> specIds = new ArrayList<>();
            int index = startIndex;
            for (; index < endIndex; index++) {
                String link = featuredPictureEntities.get(index).getTl();
                String[] splitStr = StringUtils.split(link, "/.-");
                if (CommonFunction.PATTERN_1.matcher(link).find()) {
                    specIds.add(Integer.parseInt(splitStr[7]));
                } else if(CommonFunction.PATTERN_2.matcher(link).find()){
                    specIds.add(Integer.parseInt(splitStr[6]));
                }else if(CommonFunction.PATTERN_3.matcher(link).find()){
                    specIds.add(Integer.parseInt(splitStr[7]));
                }else if(CommonFunction.PATTERN_4.matcher(link).find()){
                    specIds.add(Integer.parseInt(splitStr[6]));
                }
            }

            Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIds);
            for (; startIndex < endIndex; startIndex++) {
                String link = featuredPictureEntities.get(startIndex).getTl();
                String[] splitStr = StringUtils.split(link, "/.-");
                if (CommonFunction.PATTERN_1.matcher(link).find()) {
                    specId = Integer.parseInt(splitStr[7]);
                    picId = Integer.parseInt(splitStr[9]);
                } else if(CommonFunction.PATTERN_2.matcher(link).find()){
                    specId = Integer.parseInt(splitStr[6]);
                    picId = Integer.parseInt(splitStr[8]);
                }else if(CommonFunction.PATTERN_3.matcher(link).find()){
                    specId = Integer.parseInt(splitStr[7]);
                    picId = Integer.parseInt(splitStr[9]);
                }else if(CommonFunction.PATTERN_4.matcher(link).find()){
                    specId = Integer.parseInt(splitStr[6]);
                    picId = Integer.parseInt(splitStr[9]);
                }
                GetFeaturedInfoByTypeIdResponse.FeaturedInfoItem.Builder featuredInfoItem = GetFeaturedInfoByTypeIdResponse.FeaturedInfoItem.newBuilder();
                featuredInfoItem.setId(featuredPictureEntities.get(startIndex).getId());
                featuredInfoItem.setTitle(featuredPictureEntities.get(startIndex).getLt());
                featuredInfoItem.setPicpath(ImageUtil.getFullImagePathWithoutReplace(featuredPictureEntities.get(startIndex).getIu()));
                featuredInfoItem.setPicid(picId);
                featuredInfoItem.setSpecid(specId);
                SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specId);
                featuredInfoItem.setSeriesid(null != specBaseInfo ? specBaseInfo.getSeriesId() : 0);
                featuredInfoItem.setPublishtime(LocalDateUtils.format(featuredPictureEntities.get(startIndex).getPt(), DATE_TIME_PATTERN_TWO));
                featuredInfoItem.setTypeid(featuredPictureEntities.get(startIndex).getTypeId());
                featuredInfoItem.setPcurl(featuredPictureEntities.get(startIndex).getTl());
                featuredInfoItem.setShorttitle(featuredPictureEntities.get(startIndex).getSt());
                featuredInfoItem.setMshorttitle(featuredPictureEntities.get(startIndex).getMst());
                resultBuilder.addFeatureditems(featuredInfoItem);
            }
        }
        resultBuilder.setPageindex(page)
                .setSize(size)
                .setTotal(featuredPictureEntities.size());

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }


    private GetSpecColorListBySpecIdResponse getSpecColorListBySeriesId(List<SpecStateEntity> specStates, boolean inner) {
        GetSpecColorListBySpecIdResponse.Builder builder = GetSpecColorListBySpecIdResponse.newBuilder();
        GetSpecColorListBySpecIdResponse.Result.Builder result = GetSpecColorListBySpecIdResponse.Result.newBuilder();
        builder.setReturnCode(0).setReturnMsg("成功");
        if(CollectionUtils.isEmpty(specStates)){
            result.addAllSpecitems(new ArrayList<>()).setTotal(0);
            return builder.setResult(result).build();
        }
        List<Integer> specIds = specStates.stream().filter(x -> x.getSpecState() >= 10).map(SpecStateEntity::getSpecId).sorted().collect(Collectors.toList());

        //获取color
        Map<Integer,List<CarSpecColorEntity>> carSpecColorMap = inner ? carSpecInnerColorBaseService.getMap(specIds) : carSpecColorBaseService.getMap(specIds);
        if(CollectionUtils.isEmpty(carSpecColorMap)){
            result.addAllSpecitems(new ArrayList<>()).setTotal(0);
            return builder.setResult(result).build();
        }

        Map<Integer, List<SpecPicColorStatisticsEntity>> specColorMap = new HashMap<>();
        for (Integer currentSpecId : specIds) {
            GetSpecColorListBySpecIdResponse.SpecItem.Builder specItem = GetSpecColorListBySpecIdResponse.SpecItem.newBuilder();
            List<GetSpecColorListBySpecIdResponse.ColorItem> colorList = new ArrayList<>();

            specItem.setSpecid(currentSpecId);
            List<CarSpecColorEntity> carSpecColors = carSpecColorMap.get(currentSpecId);
            if(CollectionUtils.isEmpty(carSpecColors)){
                continue;
            }
            for(CarSpecColorEntity item : carSpecColors){
                int colorId = item.getCId();
                int picNum = 0;
                int clubPicNum = 0;
                List<SpecPicColorStatisticsEntity> specColorEntities = specColorMap.get(item.getSsId());
                if (CollectionUtils.isEmpty(specColorEntities)) {
                    specColorEntities = inner ? seriesSpecPicInnerColorStatistics.get(item.getSsId()) : specPicColorStatisticsBaseService.get(item.getSsId()).join();
                    specColorMap.put(item.getSsId(), specColorEntities);
                }
                if (!CollectionUtils.isEmpty(specColorEntities)) {
                    specColorEntities = specColorEntities.stream()
                            .filter(s -> item.getSId() == s.getSpecId())
                            .filter(s -> item.getCId() == s.getColorId())
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(specColorEntities)) {
                        picNum = specColorEntities.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum();
                        clubPicNum = specColorEntities.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum();
                    }
                }
                ColorBaseInfo colorBase = inner ? innerColorBaseService.getColor(colorId) : colorBaseService.getColor(colorId);
                GetSpecColorListBySpecIdResponse.ColorItem.Builder colorItem = GetSpecColorListBySpecIdResponse.ColorItem.newBuilder()
                        .setId(colorId)
                        .setName(colorBase == null ? "" :colorBase.getName())
                        .setValue(colorBase == null ? "" : colorBase.getValue())
                        .setPicnum(picNum)
                        .setClubpicnum(clubPicNum)
                        .setPrice(item.getPrice())
                        .setRemark(item.getMark() != null ? item.getMark() : "");
                colorList.add(colorItem.build());
            }
            List<GetSpecColorListBySpecIdResponse.ColorItem> sortedColors = colorList.stream()
                    .sorted(Comparator.comparingInt(GetSpecColorListBySpecIdResponse.ColorItem::getPicnum).reversed())
                    .collect(Collectors.toList());
            specItem.addAllColoritems(sortedColors);
            result.addSpecitems(specItem);
            specItem.clear();
        }
        result.setTotal(result.getSpecitemsCount());
        builder.setResult(result);
        return builder.build();
    }

    @Override
    public GetPicClassItemByMoreSpecIdResponse getPicClassItemByMoreSpecId(GetPicClassItemByMoreSpecIdRequest request) {
        GetPicClassItemByMoreSpecIdResponse.Builder builder = GetPicClassItemByMoreSpecIdResponse.newBuilder();
        if (Objects.equals(request.getSpecid(), "")) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }

        List<String> specIdList = CommonFunction.getListFromStrContainBlank(request.getSpecid());
        if (specIdList.isEmpty()) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }

        if (specIdList.size() > 30) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM118.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM118.getReturnMsg())
                    .build();
        }
        GetPicClassItemByMoreSpecIdResponse.Result.Builder result = GetPicClassItemByMoreSpecIdResponse.Result.newBuilder();

        Map<Integer, Integer> specPicNumMap = new HashMap<>();
        List<Integer> specIntList = specIdList.stream().filter(StringUtils::isNotBlank)
                .filter(x -> !"0".equals(x)).map(Integer::valueOf).collect(Collectors.toList());
        List<SpecBaseInfo> specBaseInfoList = specBaseService.getList(specIntList);
        if (!CollectionUtils.isEmpty(specBaseInfoList)) {
            List<Integer> seriesIds = specBaseInfoList.stream().map(SpecBaseInfo::getSeriesId).distinct().collect(Collectors.toList());
            for (Integer seriesId : seriesIds) {
                SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesId).join();
                boolean isCv = null != seriesInfo && Level.isCVLevel(seriesInfo.getLevelId());
                List<SpecPicColorStatisticsEntity> piclist = specPicClassBaseBaseService.get(seriesId, isCv).join();
                if(!CollectionUtils.isEmpty(piclist)){
                    specPicNumMap.putAll(piclist.stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getSpecId
                            , Collectors.summingInt(SpecPicColorStatisticsEntity::getPicNumber))));
                }
            }
        }
        for (String specId : specIdList) {
            GetPicClassItemByMoreSpecIdResponse.Result.PicItemList.Builder picItem = GetPicClassItemByMoreSpecIdResponse.Result.PicItemList.newBuilder();
            picItem.setSpecid(specId);
            if (Objects.equals(specId, "") || Objects.equals(specId, "0")) {
                result.addPicitem(picItem.setPicnum("0"));
            } else if (!specPicNumMap.isEmpty() && specPicNumMap.containsKey(Integer.valueOf(specId))) {
                result.addPicitem(picItem.setPicnum(String.valueOf(specPicNumMap.get(Integer.valueOf(specId)))));
            }else{
                result.addPicitem(picItem.setPicnum("0"));
            }
        }

        builder.setResult(result)
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }
    @Override
    public PicColorItemsBySeriesIdResponse getPicColorItemsBySeriesId(PicColorItemsBySeriesIdRequest request){
        PicColorItemsBySeriesIdResponse.Builder builder =  PicColorItemsBySeriesIdResponse.newBuilder();
        PicColorItemsBySeriesIdResponse.Result.Builder result =  PicColorItemsBySeriesIdResponse.Result.newBuilder();
        int seriesId =  request.getSeriesid();
        int classId = request.getClassid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if (seriesId == 0 || state == SpecStateEnum.NONE)
        {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        List<SpecPicColorStatisticsEntity> list = seriesSpecPicColorStatistics.get(seriesId);
        if(!CollectionUtils.isEmpty(list)){
            if(classId == 0){
                boolean flag = false;
                switch (state) {
                    //未上市(0X0001)
                    case NO_SELL:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() == 0)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //即将上市(0X0002)
                    case WAIT_SELL:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() == 10)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //未售(0X0003)
                    case SELL_3:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                        break;
                    //在售(0X000C)
                    case SELL_12:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //停售(0X0010)
                    case STOP_SELL:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() == 40)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //全部(0X001F)
                    case SELL_31:
                        flag = true;
                        list = list.stream().sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                }
            }
            else {
                boolean flag = false;
                switch (state) {
                    //未上市(0X0001)
                    case NO_SELL:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() == 0 && s.getPicClass() == classId)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //即将上市(0X0002)
                    case WAIT_SELL:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() == 10 && s.getPicClass() == classId)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //未售(0X0003)
                    case SELL_3:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() <= 10 && s.getPicClass() == classId)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //在售(0X000C)
                    case SELL_12:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30 && s.getPicClass() == classId)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //停售(0X0010)
                    case STOP_SELL:
                        flag = true;
                        list = list.stream().filter(s -> s.getSpecState() == 40 && s.getPicClass() == classId)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                    //全部(0X001F)
                    case NONE:
                    case SELL_31:
                        flag = true;
                        list = list.stream().filter(s -> s.getPicClass() == classId)
                                .sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                        break;
                }
            }
            List<KeyValueDto<Integer,Integer>> countList = new ArrayList<>();
            Map<Integer, List<SpecPicColorStatisticsEntity>> map = list.stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getColorId));
            for (Map.Entry<Integer,List<SpecPicColorStatisticsEntity>> item:map.entrySet()) {
                countList.add(new KeyValueDto<Integer,Integer>(){
                    {
                        setKey(item.getKey());
                        setValue(item.getValue().stream().collect(Collectors.summingInt(SpecPicColorStatisticsEntity::getPicNumber)));
                    }
                });
            }
            countList = countList.stream().sorted(Comparator.comparing(KeyValueDto::getValue,Comparator.reverseOrder()))
                    .collect(Collectors.toList());
            for (KeyValueDto<Integer,Integer> kv:countList) {
                List<SpecPicColorStatisticsEntity> item = map.get(kv.getKey());
                PicColorItemsBySeriesIdResponse.Coloritem.Builder color =  PicColorItemsBySeriesIdResponse.Coloritem.newBuilder();
                ColorBaseInfo colorBase = colorBaseService.getColor(kv.getKey());
                color.setId(kv.getKey());
                color.setName(colorBase == null?"":colorBase.getName());
                color.setValue(colorBase == null?"":colorBase.getValue());
                color.setPiccount(item.stream().collect(Collectors.summingInt(SpecPicColorStatisticsEntity::getPicNumber)));
                color.setClubpiccount(item.stream().collect(Collectors.summingInt(SpecPicColorStatisticsEntity::getClubPicNumber)));
                color.setIsonsale(item.stream().anyMatch(s -> s.getSpecState() == 10 || s.getSpecState() == 20 || s.getSpecState() == 30)?1:0);
                List<Integer> specStateList = item.stream().map(i -> i.getSpecState()).distinct().sorted().collect(Collectors.toList());
                color.addAllSpecstatelist(specStateList);
                result.addColoritems(color);
            }
            List<PicColorItemsBySeriesIdResponse.Coloritem> sorted = result.getColoritemsList().stream().sorted(Comparator.comparing(PicColorItemsBySeriesIdResponse.Coloritem::getPiccount,Comparator.reverseOrder()))
                    .collect(Collectors.toList());
            result.clearColoritems();
            result.addAllColoritems(sorted);
        }
        result.setClassid(classId);
        result.setSeriesid(seriesId);
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return  builder.build();
    }

    @Override
    public Spec25PictureBySpecListResponse spec25PictureBySpecList(Spec25PictureBySpecListRequest request){
        Spec25PictureBySpecListResponse.Builder builder = Spec25PictureBySpecListResponse.newBuilder();
        Spec25PictureBySpecListResponse.Result.Builder result = Spec25PictureBySpecListResponse.Result.newBuilder();
        String specString = request.getSpeclist();
        int[] specIds = StringIntegerUtils.convertToInt32(specString,",",0);
        if(specIds.length == 0 || Arrays.stream(specIds).anyMatch(item -> item == 0) || specIds.length > 10){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        KeyValueDto<String,int[]>[] _items = new KeyValueDto[]
        {
                new KeyValueDto("外观",new int[]{1,2,3,4,33}),
                new KeyValueDto("外观细节类",new int[]{31,34,29,30,32}),
                new KeyValueDto("中控类",new int[]{10,11,12,6,7}),
                new KeyValueDto("车厢座椅",new int[]{25,26,27,28,8}),
                new KeyValueDto("其他细节类",new int[]{21,22,23,35,24})
        };
        List<KeyValueDto<Integer,String>> types = autoCacheService.getCar25PictureType();
        Arrays.stream(specIds).forEach(specid ->{
            List<Car25PictureViewEntity> dicSpec25pic = specid < 1000000 ? spec25PicBaseService.get(specid).join() : null;
            Map<Integer,List<Car25PictureViewEntity>> map = dicSpec25pic != null ? dicSpec25pic.stream().collect(Collectors.groupingBy(Car25PictureViewEntity::getId)) : null;
            Spec25PictureBySpecListResponse.SpecItem.Builder specItem = Spec25PictureBySpecListResponse.SpecItem.newBuilder();
            for (KeyValueDto<String,int[]> item:_items) {
                if(dicSpec25pic != null){
                    Spec25PictureBySpecListResponse.PicItemList.Builder specPic = Spec25PictureBySpecListResponse.PicItemList.newBuilder();
                    for (int sitem:item.getValue()) {
                        List<Car25PictureViewEntity> pics = map.get(sitem);
                        Spec25PictureBySpecListResponse.PicItem.Builder pic = Spec25PictureBySpecListResponse.PicItem.newBuilder();
                        Optional<KeyValueDto<Integer,String>> iname = types.stream().filter(v -> v.getKey() == sitem).findFirst();
                        if(pics != null && pics.size() > 0){
                            Car25PictureViewEntity p = pics.get(0);
                            pic.setItemid(sitem);
                            pic.setItemname(iname.isPresent()==true?iname.get().getValue():"");
                            pic.setTypeid(p.getTopId());
                            pic.setPicid(p.getPicId());
                            pic.setPicpath(ImageUtil.getFullImagePath(p.getPicPath()));
                            pic.setRemark(p.getRemark().replace("<br>"," "));
                        }
                        else {
                            pic.setItemid(sitem);
                            pic.setItemname(iname.isPresent()==true?iname.get().getValue():"");
                            pic.setTypeid(0);
                            pic.setPicid(0);
                            pic.setPicpath("");
                            pic.setRemark("");
                        }
                        specPic.addPicitems(pic);
                    }
                    specPic.setTypename(item.getKey());
                    specItem.addPicitems(specPic);
                }
            }
            specItem.setSpecname("");
            specItem.setBrandname("");
            specItem.setSeriesname("");
            SpecBaseInfo specBase = specBaseService.get(specid).join();
            if(specBase != null){
                specItem.setSpecname(specBase.getSpecName());
                specItem.setSeriesid(specBase.getSeriesId());
                SeriesBaseInfo seriesBase = seriesBaseService.get(specBase.getSeriesId()).join();
                if(seriesBase != null){
                    specItem.setSeriesname(seriesBase.getName());
                    specItem.setBrandid(seriesBase.getBrandId());
                    BrandBaseInfo brandBase = brandBaseService.get(seriesBase.getBrandId()).join();
                    specItem.setBrandname(brandBase == null?"":brandBase.getName());
                }
                specItem.setSpecprice(specBase.getSpecMaxPrice());
                specItem.setSpecstate(specBase.getSpecState());
            }
            specItem.setSpecid(specid);
            result.addSpecitems(specItem);
        });
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return  builder.build();
    }

    @Override
    public GetPicClassPictureItemsBySpecIdResponse getPicClassPictureItemsBySpecId (GetPicClassPictureItemsBySpecIdRequest request) {
        GetPicClassPictureItemsBySpecIdResponse.Builder builder = GetPicClassPictureItemsBySpecIdResponse.newBuilder();
        GetPicClassPictureItemsBySpecIdResponse.Result.Builder result = GetPicClassPictureItemsBySpecIdResponse.Result.newBuilder();
        int specId = request.getSpecid();
        int classId = request.getClassid();
        if (specId == 0 || classId == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        CompletableFuture<SpecBaseInfo> specBaseFuture = specBaseService.get(specId);
        CompletableFuture<PicClassEntity> picClassFuture = picClassBaseService.get(classId);
        CompletableFuture.allOf(specBaseFuture, picClassFuture).join();

        PicClassEntity picClassEntity = picClassFuture.join();
        result.setClassid(classId);
        if(picClassEntity != null){
            result.setClassname(picClassEntity.getName());
        }
        SpecBaseInfo specBaseInfo = specBaseFuture.join();
        if (specBaseInfo != null) {
            result.setSpecid(specId);
            result.setSpecname(specBaseInfo.getSpecName());
            result.setSeriesid(specBaseInfo.getSeriesId());

            CompletableFuture<SeriesBaseInfo> seriesBaseInfoFuture = seriesBaseService.get(specBaseInfo.getSeriesId());
            CompletableFuture<List<CarPhotoViewItemMessage>> carPhotoViewEntitiesFuture = CompletableFuture.supplyAsync(() -> photosService.get(specBaseInfo.getSeriesId()));
            CompletableFuture.allOf(seriesBaseInfoFuture, carPhotoViewEntitiesFuture).join();

            SeriesBaseInfo seriesBaseInfo = seriesBaseInfoFuture.join();
            if(seriesBaseInfo != null){
                result.setSeriesname(seriesBaseInfo.getName());
            }

            List<CarPhotoViewItemMessage> carPhotoViewEntities = carPhotoViewEntitiesFuture.join();
            if(!CollectionUtils.isEmpty(carPhotoViewEntities)){
                carPhotoViewEntities = carPhotoService.carPhotoBySpecAndClass(carPhotoViewEntities, specId, classId, false);
                List<GetPicClassPictureItemsBySpecIdResponse.Result.PicItemList> imgList = carPhotoViewEntities.stream()
                        .limit(5)
                        .map(carPhotoViewEntity -> GetPicClassPictureItemsBySpecIdResponse.Result.PicItemList.newBuilder()
                                .setId(carPhotoViewEntity.getPicId())
                                .setFilepath(ImageUtil.getFullImagePathWithoutReplace(carPhotoViewEntity.getPicFilePath()))
                                .build())
                        .collect(Collectors.toList());
                result.addAllPicitems(imgList);
                result.setPicnum(CollectionUtils.isEmpty(carPhotoViewEntities) ? 0 : carPhotoViewEntities.size());

            }
        }

        builder.setResult(result);
        builder.setReturnMsg("成功").setReturnCode(0);
        return  builder.build();
    }

    @Override
    public IndexSlidePicResponse indexSlidePic(IndexSlidePicRequest request){
        IndexSlidePicResponse.Builder builder = IndexSlidePicResponse.newBuilder();
        IndexSlidePicResponse.Result.Builder result = IndexSlidePicResponse.Result.newBuilder();
        List<ZixunCarpicEntity> small = autoCacheService.getZixunCarpicSmall();
        List<ZixunCarpicEntity> big = autoCacheService.getZixunCarpicBig();
        if(!CollectionUtils.isEmpty(big)){
            final Integer[] a = {1};
            big.forEach(x ->{
                IndexSlidePicResponse.PicItem.Builder item = IndexSlidePicResponse.PicItem.newBuilder();
                item.setTitle(x.getTitle());
                item.setImgurl(x.getImgurl());
                item.setLinkurl(x.getLinkurl());
                item.setSortid(a[0]);
                result.addBigpiclist(item);
                a[0]++;
            });
        }
        if(!CollectionUtils.isEmpty(small)){
            final Integer[] b = {1};
            small.forEach(x ->{
                IndexSlidePicResponse.PicItem.Builder item = IndexSlidePicResponse.PicItem.newBuilder();
                item.setTitle(x.getTitle());
                item.setImgurl(x.getImgurl());
                item.setLinkurl(x.getLinkurl());
                item.setSortid(b[0] % 4 < 4 && b[0] % 4 > 0 ? (b[0] / 4 + 1) : b[0] / 4);
                result.addSmallpiclist(item);
                b[0]++;
            });
        }
        builder.setResult(result);
        builder.setReturnMsg("成功").setReturnCode(0);
        return  builder.build();
    }

    @Override
    public GetPicPictureListByConditionResponse getPicPictureListByCondition(GetPicPictureListByConditionRequest request) {
        GetPicPictureListByConditionResponse.Builder builder = GetPicPictureListByConditionResponse.newBuilder();
        GetPicPictureListByConditionResponse.Result.Builder result = GetPicPictureListByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int classId = request.getClassid();
        int page = request.getPage() == 0 ? 1 : request.getPage();
        int size = request.getSize() == 0 ? 10 : request.getSize();
        int imageId = request.getImageid() == 0 ? 1 : request.getImageid();
        if(size > 15){
            size = 15;
        }
        if (seriesId == 0 || page < 1 || size < 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        HashMap<Integer, Integer> dic = null;
        List<CarPhotoViewItemMessage> list = null;
        List<CarPhotoViewItemMessage> carPhotoViewEntities = getCarPhotoViewEntity(seriesId);
        list = carPhotoBySeriesAndClass(carPhotoViewEntities, seriesId, classId, false);
        if (list.size() == 0 || list.get(0).getPicId() != imageId) {
            dic = carPhotoIndexMap(list);
            if (dic.size() != list.size()) {
                dic = carPhotoIndexBySeriesAndClass(carPhotoViewEntities,seriesId, classId, true);
            }
        }

        int total = list.size();// 图片数量
        int index = 0; //图片索引位置
        if (dic != null && dic.containsKey(imageId)) {
            index = dic.get(imageId);
        } else {
            imageId = list.size() > 0 ? list.get(0).getPicId() : 0;
        }

        List<Integer> specIds = list.stream().map(x -> x.getSpecId()).collect(Collectors.toList());
        Map<Integer,SpecBaseInfo> specInfoMap = specBaseService.getMap(specIds);

        CarPhotoViewItemMessage item;
        for (int i = index - size; i <= index + size; i++) {
            if (i < 0 || i > total - 1) {
                continue;
            }
            item = list.get(i);
            SpecBaseInfo specInfo = CollectionUtils.isEmpty(specInfoMap) ? null : specInfoMap.get(item.getSpecId());
            GetPicPictureListByConditionResponse.PicList.Builder spec = GetPicPictureListByConditionResponse.PicList.newBuilder()
                    .setId(item.getPicId())
                    .setSmallimg(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathByPrefix(item.getPicFilePath(),"640x480_") : "")
                    .setBigimg(StringUtils.isNotBlank(item.getPicFilePath()) ?
                            ImageUtil.getFullImagePathByPrefix(item.getPicFilePath(),"800x0_1_q87_") : "")
                    .setSpecid(item.getSpecId())
                    .setSpecname(specInfo != null ? specInfo.getSpecName() : "")
                    .setMinprice(specInfo != null ? specInfo.getSpecMinPrice() : 0)
                    .setMaxprice(specInfo != null ? specInfo.getSpecMaxPrice() : 0)
                    .setSpecstate(specInfo != null ? specInfo.getSpecState() : 0);
            result.addPicitems(spec);
        }
        result.setPageindex(page);
        result.setSize(size);
        result.setTotal(total);
        builder.setResult(result);
        builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode());
        return builder.build();
    }

    /**
     * 根据精选图片id获取对应图片列表
     * @param request
     * @return
     */
    @Override
    public GetFeaturedPhotoByFeaturedIdResponse getFeaturedPhotoByFeaturedId(GetFeaturedPhotoByFeaturedIdRequest request) {
        GetFeaturedPhotoByFeaturedIdResponse.Builder builder = GetFeaturedPhotoByFeaturedIdResponse.newBuilder();
        int featuredId = request.getFeaturedid();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        if (featuredId == 0 || size < 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        FeaturedPictureEntity featuredPictureEntity = featuredPictureBaseService.get(featuredId);

        int specId = 0, picId = 0, pageIndex = 1, total = 0;
        GetFeaturedPhotoByFeaturedIdResponse.Result.Builder resultBuilder = GetFeaturedPhotoByFeaturedIdResponse.Result.newBuilder();
        if(null != featuredPictureEntity){
            String link = featuredPictureEntity.getTl();
            String[] splitStr = StringUtils.split(link, "/.-");
            if (CommonFunction.PATTERN_1.matcher(link).find()) {
                specId = Integer.parseInt(splitStr[7]);
                picId = Integer.parseInt(splitStr[9]);
            } else if(CommonFunction.PATTERN_2.matcher(link).find()){
                specId = Integer.parseInt(splitStr[6]);
                picId = Integer.parseInt(splitStr[8]);
            }else if(CommonFunction.PATTERN_3.matcher(link).find()){
                specId = Integer.parseInt(splitStr[7]);
                picId = Integer.parseInt(splitStr[9]);
            }else if(CommonFunction.PATTERN_4.matcher(link).find()){
                specId = Integer.parseInt(splitStr[6]);
                picId = Integer.parseInt(splitStr[9]);
            }
            if (specId != 0 && picId != 0) {
                SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
                int seriesId = null != specBaseInfo ? specBaseInfo.getSeriesId() : 0;
                //下标
                Map<Integer,Integer> dic = new HashMap<>();
                List<CarPhotoViewItemMessage> photoViewItemMessages = carPhotoBySeries(seriesId, false);
                int tempPicId = 0;
                for (int i = 0; i < photoViewItemMessages.size(); i++) {
                    tempPicId = photoViewItemMessages.get(i).getPicId();
                    if (!dic.containsKey(tempPicId))
                        dic.put(tempPicId, i);
                }
                if(dic.containsKey(picId)){
                    total = photoViewItemMessages.size();
                    int picIndex = dic.get(picId);
                    picIndex = picIndex < 0 ? 0 : picIndex;
                    pageIndex = (picIndex + 1) % size != 0 ? (picIndex + 1) / size + 1 : (picIndex + 1) / size;
                    int startIndex = (pageIndex - 1) * size;
                    int endIndex = startIndex + size > photoViewItemMessages.size() ? photoViewItemMessages.size() : startIndex + size;
                    for (; startIndex < endIndex; startIndex++) {
                        GetFeaturedPhotoByFeaturedIdResponse.PicItem.Builder picItem = GetFeaturedPhotoByFeaturedIdResponse.PicItem.newBuilder();
                        int tempSpecId = photoViewItemMessages.get(startIndex).getSpecId();
                        picItem.setId(photoViewItemMessages.get(startIndex).getPicId());
                        picItem.setPicpath(ImageUtil.getFullImagePathWithoutReplace(photoViewItemMessages.get(startIndex).getPicFilePath()));
                        picItem.setSpecid(tempSpecId);
                        picItem.setSpecname(null != photoViewItemMessages.get(startIndex) && null != photoViewItemMessages.get(startIndex).getSpecname()
                                ? photoViewItemMessages.get(startIndex).getSpecname() : "");
                        picItem.setSeriesid(seriesId);
                        picItem.setSeriesname(null != photoViewItemMessages.get(startIndex) && null != photoViewItemMessages.get(startIndex).getSeriesName()
                                ? photoViewItemMessages.get(startIndex).getSeriesName() : "");
                        picItem.setIshd(photoViewItemMessages.get(startIndex).getIsHD());
                        resultBuilder.addPicitems(picItem);
                    }
                }
            }
        }
        resultBuilder.setPageindex(pageIndex);
        resultBuilder.setPicid(picId);
        resultBuilder.setSize(size);
        resultBuilder.setTotal(total);

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 获取全部精选图片分类
     * @param request
     * @return
     */
    @Override
    public GetFeaturedTypeResponse getFeaturedType(GetFeaturedTypeRequest request) {
        GetFeaturedTypeResponse.Builder builder = GetFeaturedTypeResponse.newBuilder();
        List<KeyValueDto<Integer, String>> featuredTypeAll = autoCacheService.getFeaturedTypeAll();
        GetFeaturedTypeResponse.Result.Builder resultBuilder = GetFeaturedTypeResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(featuredTypeAll)){
            for(KeyValueDto<Integer, String> keyValueDto : featuredTypeAll){
                GetFeaturedTypeResponse.FeaturedTypeItem.Builder featuredTypeItem = GetFeaturedTypeResponse.FeaturedTypeItem.newBuilder();
                featuredTypeItem.setId(keyValueDto.getKey());
                featuredTypeItem.setName(null != keyValueDto.getValue() ? keyValueDto.getValue() : "");
                resultBuilder.addTypeitems(featuredTypeItem);
            }
        }
        resultBuilder.setTotal(resultBuilder.getTypeitemsCount());
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 根据车系id及图片类别id获取图片类别数量及前五张图片
     * @param request
     * @return
     */
    @Override
    public GetPictureItemsBySeriesIdAndClassIdResponse getPictureItemsBySeriesIdAndClassId(GetPictureItemsBySeriesIdAndClassIdRequest request) {
        GetPictureItemsBySeriesIdAndClassIdResponse.Builder builder = GetPictureItemsBySeriesIdAndClassIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int classId = request.getClassid();
        if (seriesId == 0 || classId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<CarPhotoViewItemMessage> photoViewItemMessages = carPhotoBySeriesAndClass(seriesId, classId, false);
        List<CarPhotoViewItemMessage> photoViewItemMessageList = photoViewItemMessages.size() > 5 ? photoViewItemMessages.stream().limit(5).collect(Collectors.toList()) : photoViewItemMessages;
        GetPictureItemsBySeriesIdAndClassIdResponse.Result.Builder resultBuilder = GetPictureItemsBySeriesIdAndClassIdResponse.Result.newBuilder();
        for(CarPhotoViewItemMessage carPhotoViewItemMessage : photoViewItemMessageList){
            GetPictureItemsBySeriesIdAndClassIdResponse.PicItem.Builder picItem = GetPictureItemsBySeriesIdAndClassIdResponse.PicItem.newBuilder();
            picItem.setId(carPhotoViewItemMessage.getPicId());
            picItem.setFilepath(null != carPhotoViewItemMessage.getPicFilePath() ? ImageUtil.getFullImagePathWithoutReplace(carPhotoViewItemMessage.getPicFilePath()) : "");
            picItem.setSpecid(carPhotoViewItemMessage.getSpecId());
            picItem.setSpecname(null != carPhotoViewItemMessage.getSpecname() ? carPhotoViewItemMessage.getSpecname() : "");
            resultBuilder.addPicitems(picItem);
        }
        resultBuilder.setSeriesid(seriesId);
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        resultBuilder.setSeriesname(null != seriesBaseInfo && null != seriesBaseInfo.getName() ? seriesBaseInfo.getName() : "");
        resultBuilder.setClassid(classId);
        PicClassEntity picClassEntity = picClassBaseService.get(classId).join();
        resultBuilder.setClassname(null != picClassEntity && null != picClassEntity.getName() ? picClassEntity.getName() : "");
        resultBuilder.setPicnum(photoViewItemMessages.size());

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 获取定时发布的图片数据
     * @param request
     * @return
     */
    @Override
    public PictureDetailItemsByConditionResponse getPictureDetailItemsByConditionForPublish(PictureDetailItemsByConditionRequest request) {
        PictureDetailItemsByConditionResponse.Builder builder = PictureDetailItemsByConditionResponse.newBuilder();
        int userId = request.getUserid();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int classId = request.getClassid();
        int colorId = request.getColorid();
        int pageIndex = request.getPage() == 0 ? 1 : request.getPage();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        if (seriesId == 0 && specId == 0 || pageIndex < 1 || size < 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        PictureDetailItemsByConditionResponse.Result.Builder result = PictureDetailItemsByConditionResponse.Result.newBuilder();
        List<PicPublishEntity> publishEntities = null;
        if (userId > 0) {
            publishEntities = picClassMapper.getPicItemsPublishByUserId(userId);
        } else {
            publishEntities = picClassMapper.getAllPicItemsPublish();
        }
        if(!CollectionUtils.isEmpty(publishEntities)){
            if (seriesId != 0 && specId == 0){
                int finalSeriesId = seriesId;
                if(classId != 0 && colorId != 0){
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == finalSeriesId && picPublishEntity.getTypeId() == classId && picPublishEntity.getColorId() == colorId)
                            .sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }else if(classId != 0 && colorId == 0){
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == finalSeriesId && picPublishEntity.getTypeId() == classId)
                            .sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }else if(colorId != 0 && classId == 0){
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == finalSeriesId && picPublishEntity.getColorId() == colorId)
                            .sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }else{
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == finalSeriesId)
                            .sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }
            }else{
                SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
                int tmpSeriesId = null != specBaseInfo ? specBaseInfo.getSeriesId() : 0;
                if(classId != 0 && colorId != 0){
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == tmpSeriesId && picPublishEntity.getSpecId() == specId
                            && picPublishEntity.getTypeId() == classId && picPublishEntity.getColorId() == colorId).
                            sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }else if(classId != 0 && colorId == 0) {
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == tmpSeriesId && picPublishEntity.getSpecId() == specId
                                    && picPublishEntity.getTypeId() == classId).
                            sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }else if(colorId != 0 && classId == 0) {
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == tmpSeriesId && picPublishEntity.getSpecId() == specId
                                    && picPublishEntity.getColorId() == colorId).
                            sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }else{
                    publishEntities = publishEntities.stream().filter(picPublishEntity -> picPublishEntity.getSeriesId() == tmpSeriesId && picPublishEntity.getSpecId() == specId).
                            sorted(Comparator.comparing(PicPublishEntity::getPicId,Comparator.reverseOrder())).collect(Collectors.toList());
                }
            }
            int total = publishEntities.size();
            int length = pageIndex * size > total ? total : pageIndex * size;
            List<Integer> colorIds = publishEntities.stream().map(PicPublishEntity::getColorId).distinct().collect(Collectors.toList());
            Map<Integer, ColorBaseInfo> colorBaseInfoMap = colorBaseService.getColorMap(colorIds);
            for (int i = (pageIndex - 1) * size; i < length; i++) {
                PicPublishEntity publishEntity = publishEntities.get(i);
                PictureDetailItemsByConditionResponse.PicItem.Builder piItem = PictureDetailItemsByConditionResponse.PicItem.newBuilder();
                piItem.setId(publishEntity.getPicId());
                piItem.setTypeid(publishEntity.getTypeId());
                PicClassEntity picClassEntity = picClassBaseService.get(publishEntity.getTypeId()).join();
                piItem.setTypename(null != picClassEntity && null != picClassEntity.getName() ? picClassEntity.getName() : "");
                piItem.setColorid(publishEntity.getColorId());
                ColorBaseInfo colorBaseInfo = colorBaseInfoMap.get(publishEntity.getColorId());
                piItem.setColorname(null != colorBaseInfo && null != colorBaseInfo.getName() ? colorBaseInfo.getName() : "");
                piItem.setFilepath(null != publishEntity.getFilePath() ? ImageUtil.getFullImagePathWithoutReplace(publishEntity.getFilePath()) : "");
                piItem.setIshd(publishEntity.getIsHD());
                piItem.setSpecid(publishEntity.getSpecId());
                piItem.setSpecname(null != publishEntity.getSpecName() ? publishEntity.getSpecName() : "");
                piItem.setYearid(publishEntity.getSYearId());
                piItem.setYearname(publishEntity.getSYear() + "款");
                piItem.setSeriesid(publishEntity.getSeriesId());
                piItem.setSeriesname(null != publishEntity.getSeriesName() ? publishEntity.getSeriesName() : "");
                piItem.setFctid(publishEntity.getFctId());
                piItem.setFctname(null != publishEntity.getFctName() ? publishEntity.getFctName() : "");
                piItem.setBrandid(publishEntity.getBrandId());
                piItem.setBrandname(null != publishEntity.getBrandName() ? publishEntity.getBrandName() : "");
                piItem.setSpecstate(publishEntity.getSpecState());
                piItem.setPublishtime(null != publishEntity.getPublishTime() ?
                        Timestamp.newBuilder().setSeconds(publishEntity.getPublishTime().getTime()/1000).setNanos(0).build() :
                        Timestamp.newBuilder().setSeconds(0).setNanos(0).build());
                result.addPicitems(piItem);
            }
            result.setTotal(total);
        }
        result.setPageindex(pageIndex);
        result.setSize(size);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }
    /**
     * 获取按照车型分组的车型图片列表
     * @param request
     * @return
     */
    @Override
    public PicListGroupByConditionResponse picListGroupByCondition(PicListGroupByConditionRequest request) {
        PicListGroupByConditionResponse.Builder builder = PicListGroupByConditionResponse.newBuilder();
        //车系id
        int seriesId = request.getSeriesid();
        //颜色id
        int colorId = request.getColorid();
        //内饰颜色id
        int innerColorId = request.getInnercolorid();
        //车型列表第几页
        int pageIndex = request.getPageindex() <= 0 ? 1 : request.getPageindex();
        //一页数量
        int size = request.getPagesize() <= 0 ? 2 : request.getPagesize();
        //一页展示几个车型
        int gSize = request.getGpsize() <= 0 ? 12 : request.getGpsize();
        //分类id,1外观 12 细节 ----外观和细节合并值为112
        int classId = request.getTypeid();
        if (seriesId == 0 || classId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<CarPhotoViewItemMessage> messageList = getCarPhotoViewEntityByClassId(seriesId,classId,0);
        if(colorId > 0){
            messageList = getSeriesAndColor(messageList,colorId,true);
        }else if(innerColorId > 0){
            messageList = getSeriesAndInnerColor(messageList,innerColorId,false);
        }else{
            messageList = getSeries(messageList,classId,true);
        }

        //按照车展id 和 车型id进行分组
        LinkedHashMap<String, ArrayList<CarPhotoViewItemMessage>> groupMap = messageList.stream().collect(Collectors.groupingBy(carPhotoViewEntity -> String.valueOf(carPhotoViewEntity.getShowId()) + "-" +String.valueOf(carPhotoViewEntity.getSpecId()),
                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        int total = messageList.size();
        int specNum = groupMap.size();
        //当只有一个车型shi，获取所有的图片
        if(specNum > 1){
            //截取指定长度的数据进行遍历
            groupMap = groupMap.entrySet().stream().skip((pageIndex - 1) * size).limit(size)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        }
        PicListGroupByConditionResponse.Result.Builder result = PicListGroupByConditionResponse.Result.newBuilder();
        for(Map.Entry<String, ArrayList<CarPhotoViewItemMessage>> messageMap : groupMap.entrySet()){
            PicListGroupByConditionResponse.SpecItem.Builder specItem = PicListGroupByConditionResponse.SpecItem.newBuilder();
            ArrayList<CarPhotoViewItemMessage> list = messageMap.getValue();
            specItem.setSpecid(list.get(0).getSpecId());
            specItem.setShowid(list.get(0).getShowId());
            String specName = list.get(0).getSpecname();
            String showName = list.get(0).getShowname();
            specItem.setSpecname(null != specName ? specName : "");
            specItem.setShowname(null != showName ? showName : "");
            specItem.setSpecstate(list.get(0).getSpecState());
            specItem.setPictotal(list.size());
            int count = 0;
            for(CarPhotoViewItemMessage message : list){
                PicListGroupByConditionResponse.PicItem.Builder picItem = PicListGroupByConditionResponse.PicItem.newBuilder();
                picItem.setId(message.getPicId());
                picItem.setFilepath(null != message.getPicFilePath() ? ImageUtil.getFullImagePathWithoutReplace(message.getPicFilePath()) : "");
                picItem.setIshd(message.getIsHD());
                picItem.setTypeid(message.getPicClass());
                picItem.setTypename(null != message.getTypename() ? message.getTypename() : "");
                picItem.setColorid(message.getPicColorId());
                picItem.setColorname(null != message.getColorname() ? message.getColorname() : "");
                picItem.setInnercolorid(message.getInnerColorId());
                picItem.setInnercolorname(null != message.getInnerColorName() ? message.getInnerColorName() : "");
                picItem.setWidth(message.getWidth());
                picItem.setHeight(message.getHeight());
                picItem.setDealerid(message.getDealerid());
                picItem.setIswallpaper(message.getIsWallPaper());
                picItem.setOptional(message.getOptional());
                picItem.setSixtypicsortid(SixtyPic.get(message.getPointlocatinid(),0));
                specItem.addPicitems(picItem);
                count++;
                if(specNum > 1 && count >= gSize){
                    break;
                }
            }
            result.addSpecitems(specItem);
        }
        result.setPageindex(pageIndex);
        result.setSize(size);
        result.setTotal(total);
        result.setSpecnum(specNum);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }


    /**
     * 获取根据车型和其他条件的更多图片数据
     * @param request
     * @return
     */
    @Override
    public PicListMoreByConditionResponse picListMoreByCondition(PicListMoreByConditionRequest request) {
        PicListMoreByConditionResponse.Builder builder = PicListMoreByConditionResponse.newBuilder();
        //车型id
        int specId = request.getSpecid();
        //图片分类id
        int classId = request.getTypeid();
        //外观颜色id
        int colorId = request.getColorid();
        //内饰颜色id
        int innerColorId = request.getInnercolorid();
        //车展id,车展tab签需要传，因为有的车型在两个或者更多的车展都存在，
        int showId = request.getShowid();

        //需要判断是车系的展示还是车型页，因为车系展示不需要分页,车型页的数据需要分页
        int pageIndex = request.getPageindex() <= 0 ? 0 : request.getPageindex();
        int pageSize = request.getPagesize() <= 0 ? 0 : request.getPagesize();

        if (specId == 0 || classId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        int seriesId = null != specBaseInfo ? specBaseInfo.getSeriesId() : 0;
        List<CarPhotoViewItemMessage> messageList = getCarPhotoViewEntityByClassId(seriesId,classId,specId);
        //有车展id，过滤当前车展的
        if(showId > 0){
            messageList = messageList.stream().filter(carPhotoViewItemMessage -> carPhotoViewItemMessage.getShowId() == showId).collect(Collectors.toList());
        }
        if(colorId > 0){
            messageList = getSeriesAndColor(messageList,colorId,true);
        }else if(innerColorId > 0){
            messageList = getSeriesAndInnerColor(messageList,innerColorId,false);
        }else{
            messageList = getSeries(messageList,classId,true);
        }
        //总数提前获取
        int count = messageList.size();
        //需要判断是车系的展示还是车型页，因为车系展示不需要分页,车型页的数据需要分页
        if(pageIndex != 0 && pageSize != 0){
            messageList = messageList.stream().skip((pageIndex - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        }
        PicListMoreByConditionResponse.Result.Builder result = PicListMoreByConditionResponse.Result.newBuilder();
        for(CarPhotoViewItemMessage message : messageList){
            PicListMoreByConditionResponse.PicItem.Builder picItem = PicListMoreByConditionResponse.PicItem.newBuilder();
            picItem.setId(message.getPicId());
            picItem.setFilepath(null != message.getPicFilePath() ? ImageUtil.getFullImagePathWithoutReplace(message.getPicFilePath()) : "");
            picItem.setIshd(message.getIsHD());
            picItem.setTypeid(message.getPicClass());
            picItem.setTypename(null != message.getTypename() ? message.getTypename() : "");
            picItem.setColorid(message.getPicColorId());
            picItem.setColorname(null != message.getColorname() ? message.getColorname() : "");
            picItem.setInnercolorid(message.getInnerColorId());
            picItem.setInnercolorname(null != message.getInnerColorName() ? message.getInnerColorName() : "");
            picItem.setSpecid(message.getSpecId());
            picItem.setSpecname(null != message.getSpecname() ? message.getSpecname() : "");
            picItem.setShowid(message.getShowId());
            picItem.setShowname(null != message.getShowname() ? message.getShowname() : "");
            picItem.setWidth(message.getWidth());
            picItem.setHeight(message.getHeight());
            picItem.setSpecstate(message.getSpecState());
            picItem.setDealerid(message.getDealerid());
            picItem.setIswallpaper(message.getIsWallPaper());
            picItem.setOptional(message.getOptional());
            picItem.setSixtypicsortid(SixtyPic.get(message.getPointlocatinid(),0));
            result.addPicitems(picItem);
        }
        result.setPageindex(pageIndex);
        result.setSize(pageSize);
        //总数
        result.setTotal(count);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }

    /**
     * 分页获取车系页图片详情页数据
     * @param request
     * @return
     */
    @Override
    public PicListDetailByConditionResponse picListDetailByCondition(PicListDetailByConditionRequest request) {
        PicListDetailByConditionResponse.Builder builder = PicListDetailByConditionResponse.newBuilder();
        //车系id
        int seriesId = request.getSeriesid();
        //颜色id
        int colorId = request.getColorid();
        //内饰颜色id
        int innerColorId = request.getInnercolorid();
        //页码
        int pageIndex = request.getPageindex() <= 0 ? 1 : request.getPageindex();
        //一页数量
        int size = request.getPagesize() <= 0 ? 60 : request.getPagesize();
        //分类id,1外观 12 细节 ----外观和细节合并值为112
        int classId = request.getTypeid();
        if (seriesId == 0 || classId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<CarPhotoViewItemMessage> messageList = getCarPhotoViewEntityByClassId(seriesId,classId,0);
        if(colorId > 0){
            messageList = getSeriesAndColor(messageList,colorId,true);
        }else if(innerColorId > 0){
            messageList = getSeriesAndInnerColor(messageList,innerColorId,false);
        }else{
            messageList = getSeries(messageList,classId,true);
        }
        int total = messageList.size();// 图片数量
        int length = pageIndex * size > total ? total : pageIndex * size;
        PicListDetailByConditionResponse.Result.Builder result = PicListDetailByConditionResponse.Result.newBuilder();
        for (int i = (pageIndex - 1) * size; i < length; i++) {
            PicListDetailByConditionResponse.PicItem.Builder picItem = PicListDetailByConditionResponse.PicItem.newBuilder();
            CarPhotoViewItemMessage message = messageList.get(i);
            picItem.setId(message.getPicId());
            picItem.setFilepath(null != message.getPicFilePath() ? ImageUtil.getFullImagePathWithoutReplace(message.getPicFilePath()) : "");
            picItem.setIshd(message.getIsHD());
            picItem.setTypeid(message.getPicClass());
            picItem.setTypename(null != message.getTypename() ? message.getTypename() : "");
            picItem.setColorid(message.getPicColorId());
            picItem.setColorname(null != message.getColorname() ? message.getColorname() : "");
            picItem.setInnercolorid(message.getInnerColorId());
            picItem.setInnercolorname(null != message.getInnerColorName() ? message.getInnerColorName() : "");
            picItem.setSpecid(message.getSpecId());
            picItem.setSpecname(null != message.getSpecname() ? message.getSpecname() : "");
            picItem.setShowid(message.getShowId());
            picItem.setShowname(null != message.getShowname() ? message.getShowname() : "");
            picItem.setWidth(message.getWidth());
            picItem.setHeight(message.getHeight());
            picItem.setSpecstate(message.getSpecState());
            picItem.setDealerid(message.getDealerid());
            picItem.setIswallpaper(message.getIsWallPaper());
            picItem.setOptional(message.getOptional());
            picItem.setSixtypicsortid(SixtyPic.get(message.getPointlocatinid(),0));
            result.addPicitems(picItem);
        }
        result.setPageindex(pageIndex);
        result.setSize(size);
        result.setTotal(total);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }


    /**
     * 112 ----外观和细节的数据
     * @param seriesId
     * @return
     */
    public List<CarPhotoViewItemMessage> getCarPhotoViewEntityByClassId(int seriesId,int classId,int specId){
        List<CarPhotoViewItemMessage> carPhotoViewEntities = photosService.get(seriesId);
        if(CollectionUtils.isEmpty(carPhotoViewEntities)){
            carPhotoViewEntities = new ArrayList<>();
        }
        //外观和细节的数据
        if(classId == 112){
            carPhotoViewEntities = carPhotoViewEntities.stream().filter(carPhotoViewItemMessage -> carPhotoViewItemMessage.getPicClass() == 1
                    || carPhotoViewItemMessage.getPicClass() == 12).collect(Collectors.toList());
        }else{
            carPhotoViewEntities = carPhotoViewEntities.stream().filter(carPhotoViewItemMessage -> carPhotoViewItemMessage.getPicClass() == classId).collect(Collectors.toList());
        }
        //过滤车型
        if(specId > 0){
            carPhotoViewEntities = carPhotoViewEntities.stream().filter(carPhotoViewItemMessage -> carPhotoViewItemMessage.getSpecId() == specId).collect(Collectors.toList());
        }
        return carPhotoViewEntities;
    }


    public List<CarPhotoViewItemMessage> getSeriesAndColor(List<CarPhotoViewItemMessage> list, int colorId, boolean hasClub) {
        return list.stream().filter(
                        item -> item.getPicColorId() == colorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSyear,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getNewpicorder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicClass)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> getSeriesAndInnerColor(List<CarPhotoViewItemMessage> list, int innerColorId, boolean hasClub) {
        return list.stream().filter(
                        item -> item.getInnerColorId() == innerColorId
                                && item.getSpecPicNumber() > 2
                                && (hasClub || item.getIsClubPhoto() == 0))
                .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSyear,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                        .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                        .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                        .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                        .thenComparing(CarPhotoViewItemMessage::getNewpicorder,Comparator.reverseOrder())
                        .thenComparing(CarPhotoViewItemMessage::getPicClass)
                        .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CarPhotoViewItemMessage> getSeries(List<CarPhotoViewItemMessage> list,int classId, boolean hasClub) {
        //外观和细节合并112 细节12  外观颜色
        if(classId == 112 || classId == 12 || classId == 1){
            return list.stream().filter(
                            item -> item.getSpecPicNumber() > 2
                                    && (hasClub || item.getIsClubPhoto() == 0))
                    .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSyear,Comparator.reverseOrder())
                            .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                            .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                            .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                            .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                            .thenComparing(CarPhotoViewItemMessage::getNewpicorder,Comparator.reverseOrder())
                            .thenComparing(CarPhotoViewItemMessage::getPicColorId,Comparator.reverseOrder())
                            .thenComparing(CarPhotoViewItemMessage::getPicClass)
                            .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }else{
            //内饰颜色,需要按照颜色排序
            if(classId == 3 || classId == 10){
                return list.stream().filter(
                                item -> item.getSpecPicNumber() > 2
                                        && (hasClub || item.getIsClubPhoto() == 0))
                        .sorted(Comparator.comparing(CarPhotoViewItemMessage::getSyear,Comparator.reverseOrder())
                                .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                                .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                                .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                                .thenComparing(CarPhotoViewItemMessage::getNewpicorder,Comparator.reverseOrder())
                                .thenComparing(CarPhotoViewItemMessage::getInnerColorId,Comparator.reverseOrder())
                                .thenComparing(CarPhotoViewItemMessage::getPicId,Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            }else{//车展55 需要按照showId进行排序，其他的showId为0，不影响其他的排序
                return list.stream().filter(
                                item -> item.getSpecPicNumber() > 2
                                        && (hasClub || item.getIsClubPhoto() == 0))
                        .sorted(Comparator.comparing(CarPhotoViewItemMessage::getShowId,Comparator.reverseOrder())
                                .thenComparing(CarPhotoViewItemMessage::getSyear,Comparator.reverseOrder())
                                .thenComparing(CarPhotoViewItemMessage::getStateOrder)
                                .thenComparing(CarPhotoViewItemMessage::getIsclassic)
                                .thenComparing(CarPhotoViewItemMessage::getSourceTypeOrder)
                                .thenComparing(CarPhotoViewItemMessage::getDealerPicOrder)
                                .thenComparing(CarPhotoViewItemMessage::getNewpicorder, Comparator.reverseOrder())
                                .thenComparing(CarPhotoViewItemMessage::getPicId, Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            }
        }

    }


}
