package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.pic.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.PicClassMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.CarPhotoService;
import com.autohome.car.api.services.PictureService;
import com.autohome.car.api.services.SpecService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.ColorBaseInfo;
import com.autohome.car.api.services.basic.models.FactoryBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesSpecPicInnerColorStatistics;
import com.autohome.car.api.services.basic.specs.SpecParamService;
import com.autohome.car.api.services.impls.AutoCacheServiceImpl;
import com.autohome.car.api.services.models.CarPhotoView;
import com.autohome.car.api.services.models.CarPhotoViewPage;
import com.autohome.car.api.services.models.PicColorInfoResult;
import com.autohome.car.api.services.models.SpecParam;
import com.autohome.car.api.services.models.pic.Pic25YearItem;
import com.autohome.car.api.services.models.pic.PicSpecItem;
import com.autohome.car.api.services.models.pic.PicYearItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@DubboService
@RestController
public class PicServiceGrpcImpl extends DubboPicServiceTriple.PicServiceImplBase {

    @Autowired
    PictureService pictureService;

    @Autowired
    SpecService specService;

    @Autowired
    PicClassBaseService picClassBaseService;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    CarPhotoService carPhotoService;

    @Autowired
    SeriesSpecPicInnerColorStatistics seriesSpecPicInnerColorStatistics;

    @Autowired
    InnerColorBaseService innerColorBaseService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    FactoryBaseService factoryBaseService;

    @Autowired
    BrandBaseService brandBaseService;

    @Autowired
    PicClassMapper picClassMapper;

    @Autowired
    SpecParamService specParamService;

    @Autowired
    AutoCacheServiceImpl autoCacheServiceImpl;

    @Override
    @GetMapping("/v1/carpic/pic_allpictureitemsbycondition.ashx")
    public GetAllPictureItemsByConditionResponse getAllPictureItemsByCondition(GetAllPictureItemsByConditionRequest request) {
        if( (request.getSeriesid() <= 0 && request.getSpecid() <=0 ) ){
            return GetAllPictureItemsByConditionResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int classId = request.getClassid();
        int colorId = request.getColorid();
        int page = request.getPage() <=0 ? 1 : request.getPage();
        int size = request.getSize() <=0 ? 20 : request.getSize();

        page = page <=0 ? 1 : page;
        size = size <=0 ? 20 : size;


        CarPhotoViewPage picList = carPhotoService.carPhoto(seriesId,specId,classId,colorId,page,size);

        GetAllPictureItemsByConditionResponse.Builder builder = GetAllPictureItemsByConditionResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");

        GetAllPictureItemsByConditionResponse.Result result = MessageUtil.toMessage(picList,GetAllPictureItemsByConditionResponse.Result.class);
        builder.setResult(result);

        return builder.build();
    }


    @Override
    public GetClassItemsBySeriesIdResponse getClassItemsBySeriesId(GetClassItemsBySeriesIdRequest request) {

        GetClassItemsBySeriesIdResponse.Builder builder = GetClassItemsBySeriesIdResponse.newBuilder();
        builder.setSeriesId(1).setColorId(2);

        for (int i = 0; i < 2; i++) {

            GetClassItemsBySeriesIdResponse.ClassItem item = GetClassItemsBySeriesIdResponse.ClassItem.newBuilder()
                    .setId(i)
                    .setName("name" + i)
                    .setPicCount(i * 10)
                    .build();

            builder.addClassItems(item);
        }
        return builder.build();
    }

    @Override
    @GetMapping("/v1/carpic/picclass_classitemsbyspecid.ashx")
    public GetClassItemsBySpecIdResponse getClassItemsBySpecId(GetClassItemsBySpecIdRequest request) {
        return specService.getClassItemsBySpecId(request);
    }


    @Override
    @GetMapping("/v1/carpic/piccolor_coloritemsbyspecid.ashx")
    public GetPicColorInfoResponse getPicColorInfo(GetPicColorInfoRequest request) {
        if(request.getSpecid()<=0){
            return GetPicColorInfoResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        return convertPicColorInfoToResponse(pictureService.getPicColorInfo(request.getSpecid(), request.getClassid()));
    }

    @Override
    @GetMapping("/v1/carpic/piccolor_innercoloritemsbyspecid.ashx")
    public GetPicColorInfoResponse getInnerColorInfo(GetPicColorInfoRequest request) {
        if(request.getSpecid()<=0){
            return GetPicColorInfoResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        return convertPicColorInfoToResponse(pictureService.getInnerPicColorInfo(request.getSpecid(), request.getClassid()));
    }


    GetPicColorInfoResponse convertPicColorInfoToResponse(PicColorInfoResult baseResult){
        GetPicColorInfoResponse.Result result = GetPicColorInfoResponse.Result.newBuilder()
                .setClassid(baseResult.getClassid())
                .setSeriesid(baseResult.getSeriesid())
                .addAllColoritems(MessageUtil.toMessageList(baseResult.getColoritems(), GetPicColorInfoResponse.Result.Coloritem.class))
                .build();

        return GetPicColorInfoResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(result)
                .build();
    }

    @Override
    @GetMapping("/v1/carpic/series_25picturebyseriesid.ashx")
    public GetSeries25PictureBySeriesIdResponse getSeries25PictureBySeriesId(GetSeries25PictureBySeriesIdRequest request) {
        return pictureService.getSeries25PictureBySeriesId(request);
    }

    /**
     * 根据车型id获取对应类别前五张图
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/spec_classpicturebyspecId.ashx")
    @Override
    public SpecClassPictureBySpecIdResponse getSpecClassPictureBySpecId(SpecClassPictureBySpecIdRequest request) {
        ApiResult<PicSpecItem> apiResult = pictureService.getSpecClassPictureBySpecId(request);
        if(apiResult.getReturncode() != ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() || null == apiResult.getResult()){
            return SpecClassPictureBySpecIdResponse.newBuilder().
                    setReturnCode(apiResult.getReturncode()).
                    setReturnMsg(apiResult.getMessage()).build();
        }
        SpecClassPictureBySpecIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),SpecClassPictureBySpecIdResponse.Result.class);
        return SpecClassPictureBySpecIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }
    @Override
    @GetMapping("/v1/carprice/spec_colorlistbyspecidList.ashx")
    public GetSpecColorListBySpecListResponse getSpecColorListBySpecList(GetSpecColorListBySpecListRequest request) {
        return pictureService.getSpecColorListBySpecList(request);
    }

    /**
     * 二期 根据车型id获取同年代款颜色列表
     */
    @Override
    @GetMapping("/v1/carprice/Spec_InnerColorListBySpecIdList.ashx")
    public GetSpecColorListBySpecListResponse getSpecInnerColorListBySpecIdList(GetSpecColorListBySpecListRequest request) {
        return pictureService.getSpecInnerColorListBySpecIdList(request);
    }

    @Override
    @GetMapping("/v1/carpic/pic_pictureitemsbycondition.ashx")
    public GetPicPictureItemsByConditionV1Response getPicPictureItemsByCondition(GetPicPictureItemsByConditionV1Request request) {
        return pictureService.getPicPictureItemsByConditionV1(request);
    }

    @Override
    @GetMapping("/v1/carpic/picclass_classitemsbyseriesid.ashx")
    public GetPicClassItemsBySeriesIdResponse getPicClassItemsBySeriesId(GetPicClassItemsBySeriesIdRequest request) {
        return pictureService.getPicClassItemsBySeriesId(request);
    }

    @Override
    @GetMapping("/v1/carprice/spec_colorlistbyspecid.ashx")
    public GetSpecColorListBySpecIdResponse getSpecColorListBySpecId(GetSpecColorListBySpecIdRequest request) {
        return pictureService.getSpecColorListBySpecId(request);
    }

    @Override
    @GetMapping("/v1/carprice/spec_innercolorlistbyspecid.ashx")
    public GetSpecColorListBySpecIdResponse getSpecInnerColorListBySpecId(GetSpecColorListBySpecIdRequest request) {
        return pictureService.getSpecInnerColorListBySpecId(request);
    }

    @Override
    @GetMapping("/v1/carpic/PicClass_ClassItemByMoreSpecId.ashx")
    public GetPicClassItemByMoreSpecIdResponse getPicClassItemByMoreSpecId(GetPicClassItemByMoreSpecIdRequest request) {
        return pictureService.getPicClassItemByMoreSpecId(request);
    }

    @Override
    @GetMapping("/v1/carprice/spec_colorlistbyyearid.ashx")
    public GetSpecColorListBySpecIdResponse getSpecColorListByYearId(GetSpecColorListByYearIdRequest request) {
        return pictureService.getSpecColorListByYearId(request);
    }

    @Override
    @GetMapping("/v1/CarPrice/Spec_InnerColorListByYearId.ashx")
    public GetSpecColorListBySpecIdResponse getSpecInnerColorListByYearId(GetSpecColorListByYearIdRequest request) {
        return pictureService.getSpecInnerColorListByYearId(request);
    }
    @Override
    @GetMapping("/v1/carpic/piccolor_coloritemsbyseriesid.ashx")
    public PicColorItemsBySeriesIdResponse getPicColorItemsBySeriesId(PicColorItemsBySeriesIdRequest request) {
        return pictureService.getPicColorItemsBySeriesId(request);
    }

//    @Override
//    @GetMapping("/v1/carpic/pic_picturedetailItemsbycondition.ashx")
//    public PicPictureDetailItemsByConditionResponse picPictureDetailItemsByCondition(PicPictureDetailItemsByConditionRequest request){
//        return pictureService.picPictureDetailItemsByCondition(request);
//    }

    @Override
    @GetMapping("/v1/carpic/Spec_25PictureBySpecList.ashx")
    public Spec25PictureBySpecListResponse spec25PictureBySpecList(Spec25PictureBySpecListRequest request){
        return pictureService.spec25PictureBySpecList(request);
    }

    @Override
    @GetMapping("/v1/carpic/piccolor_innercoloritemsbyseriesid.ashx")
    public PicColorInnerColorItemsBySeriesIdResponse picColorInnerColorItemsBySeriesId(PicColorInnerColorItemsBySeriesIdRequest request) {
        PicColorInnerColorItemsBySeriesIdResponse.Builder builder = PicColorInnerColorItemsBySeriesIdResponse.newBuilder();
        if(request.getSeriesid()<=0 || StringUtils.isBlank( request.getState())){
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        int seriesId = request.getSeriesid();
        int classId = request.getClassid();
        PicColorInnerColorItemsBySeriesIdResponse.Result.Builder result = PicColorInnerColorItemsBySeriesIdResponse.Result.newBuilder();
        result.setClassid(request.getClassid());
        result.setSeriesid(request.getSeriesid());

        List<SpecPicColorStatisticsEntity> picColorsInfos = seriesSpecPicInnerColorStatistics.get(seriesId);
        if (CollectionUtils.isEmpty(picColorsInfos))
            return builder.setReturnCode(0).setReturnMsg("成功").setResult(result).build();

        if(request.getClassid()>0) {
            picColorsInfos = picColorsInfos.stream().filter(x -> x.getPicClass() == classId).collect(Collectors.toList());
        }

        switch (request.getState().toLowerCase()){
            case "0x0001": //returnState = SpecState.NoSell;
                picColorsInfos = picColorsInfos.stream().filter(x->x.getSpecState() == 0).collect(Collectors.toList());
                break;
            case "0x0002": //returnState = SpecState.WaitSell;
                picColorsInfos = picColorsInfos.stream().filter(x->x.getSpecState() == 10).collect(Collectors.toList());
                break;
            case "0x0010": //returnState = SpecState.StopSell;
                picColorsInfos = picColorsInfos.stream().filter(x->x.getSpecState() == 40).collect(Collectors.toList());
                break;
            case "0x0003": //returnState = SpecState.NoSell | SpecState.WaitSell;
                picColorsInfos = picColorsInfos.stream().filter(x->x.getSpecState() <= 10).collect(Collectors.toList());
                break;
            case "0x000c": //returnState = SpecState.Sell | SpecState.SellInStop;
                picColorsInfos = picColorsInfos.stream().filter(x->x.getSpecState() >= 20 && x.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case "0x001f": //returnState = SpecState.NoSell | SpecState.WaitSell | SpecState.Sell | SpecState.SellInStop | SpecState.StopSell;
            default:
                break;
        }

        if(picColorsInfos .size() == 0){
            return builder.setReturnCode(0).setReturnMsg("成功").setResult(result).build();
        }


        Map<Integer, List<SpecPicColorStatisticsEntity>> query = picColorsInfos.stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getColorId));
        Map<Integer, ColorBaseInfo> colorList = innerColorBaseService.getColorMap(query.keySet().stream().collect(Collectors.toList()));

        List<Map.Entry<Integer,List<SpecPicColorStatisticsEntity>>> list = new ArrayList<>(query.entrySet());
        //list.stream().sorted(Comparator.comparing(x->x.getValue().stream().mapToInt(y->y.getPicNumber()).sum(),Comparator.reverseOrder()));

        List< PicColorInnerColorItemsBySeriesIdResponse.Result.Coloritem> items = new ArrayList<>();
        for(Map.Entry<Integer,List<SpecPicColorStatisticsEntity>> entry: list){

            int k = entry.getKey();
            List<SpecPicColorStatisticsEntity> v= entry.getValue();
            ColorBaseInfo colorBaseInfo = colorList.get(k);
            items.add(
                    PicColorInnerColorItemsBySeriesIdResponse.Result.Coloritem.newBuilder()
                            .setId(k)
                            .setName(colorBaseInfo==null?"":colorBaseInfo.getName())
                            .setValue(colorBaseInfo==null?"":colorBaseInfo.getValue())
                            .setClubpiccount(v.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum())
                            .setPiccount(v.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum())
                            .setIsonsale(v.stream().anyMatch(x->x.getSpecState()==10 || x.getSpecState() == 20 || x.getSpecState() == 30)?1:0).build()
            );
        }

        items.sort(Comparator.comparing(PicColorInnerColorItemsBySeriesIdResponse.Result.Coloritem::getPiccount,Comparator.reverseOrder()).thenComparing(PicColorInnerColorItemsBySeriesIdResponse.Result.Coloritem::getId,Comparator.reverseOrder()));

        result.addAllColoritems(items);

        return builder.setReturnCode(0).setReturnMsg("成功").setResult(result).build();
    }

    @Override
    @GetMapping("/v1/carpic/pic_picturedetailItemsbycondition.ashx")
    public PicPicturedetailItemsbyconditionResponse picPicturedetailItemsbycondition(PicPicturedetailItemsbyconditionRequest request) {
        PicPicturedetailItemsbyconditionResponse.Builder builder = PicPicturedetailItemsbyconditionResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");

        if( (request.getSeriesid() <= 0 && request.getSpecid() <=0 )){
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int classId = request.getClassid();
        int colorId = request.getColorid();
        int page = request.getPage() <=0 ? 1 : request.getPage();
        int size = request.getSize() <=0 ? 20 : request.getSize();

        page = page <=0 ? 1 : page;
        size = size <=0 ? 20 : size;


        AtomicReference<FactoryBaseInfo> fctInfo = new AtomicReference<>();
        AtomicReference<SeriesBaseInfo> seriesInfo = new AtomicReference<>();

        CompletableFuture task = seriesBaseService.get(seriesId).thenAcceptAsync(seriesBaseInfo -> {
            seriesInfo.set(seriesBaseInfo);
            if(seriesBaseInfo!=null) {
                fctInfo.set(factoryBaseService.getFactory(seriesBaseInfo.getFactId()));
            }
        });

        CarPhotoViewPage picList = carPhotoService.carPhoto(seriesId,specId,classId,colorId,page,size);

        task.join();

        SeriesBaseInfo series = seriesInfo.get();
        FactoryBaseInfo fct = fctInfo.get();

        PicPicturedetailItemsbyconditionResponse.Result.Builder result = PicPicturedetailItemsbyconditionResponse.Result.newBuilder();
        result.setPageindex(picList.getPageindex());
        result.setSize(picList.getSize());
        result.setTotal(picList.getTotal());
        for (CarPhotoView picitem : picList.getPicitems()) {
            result.addPicitems(
                    PicPicturedetailItemsbyconditionResponse.Result.PicItem.newBuilder()
                            .setColorid(picitem.getColorid())
                            .setColorname(picitem.getColorname())
                            .setIshd(picitem.getIshd())
                            .setId(picitem.getId())
                            .setFilepath(picitem.getFilepath())
                            .setSpecid(picitem.getSpecid())
                            .setSpecname(picitem.getSpecname())
                            .setTypeid(picitem.getTypeid())
                            .setTypename(picitem.getTypename())
                            .setYearid(picitem.getYearId())
                            .setYearname(picitem.getYearName())
                            .setSeriesid(seriesId)
                            .setSeriesname(series==null?"":series.getName())
                            .setFctid(series==null?0:series.getFactId())
                            .setFctname(fct==null?"":fct.getName())
                            .setBrandid(series==null?0:series.getBrandId())
                            .setBrandname(series==null?"":series.getBrandName())
                            .setSpecstate(picitem.getSpecState())
            );
        }
        builder.setResult(result);

        return builder.build();
    }
    @Override
    @GetMapping("/v1/carpic/picclass_pictureitemsbyspecid.ashx")
    public GetPicClassPictureItemsBySpecIdResponse getPicClassPictureItemsBySpecId (GetPicClassPictureItemsBySpecIdRequest request){
        return pictureService.getPicClassPictureItemsBySpecId(request);
    }

    @Override
    @GetMapping("/v1/CarPic/Pic_ScanPictureItemsBypicId.ashx")
    public GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByPicId(GetPicScanPictureItemsByPicIdRequest request){
        return pictureService.getPicScanPictureItemsByPicId(request);
    }

    @Override
    @GetMapping("/v1/carpic/Pic_ScanPictureItemsByClassId.ashx")
    public GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByClass(GetPicScanPictureItemsByClassRequest request){
        return pictureService.getPicScanPictureItemsByClass(request);
    }

    @Override
    @GetMapping("/v1/CarPic/Pic_ScanPictureItemsBycolorId.ashx")
    public GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByColor(GetPicScanPictureItemsByColorRequest request){
        return pictureService.getPicScanPictureItemsByColor(request);
    }

    @Override
    @GetMapping("/v1/CarPic/Pic_ScanPictureItemsByinnercolorId.ashx")
    public GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByInnerColor(GetPicScanPictureInnerItemsByInnerColorRequest request){
        return pictureService.getPicScanPictureInnerItemsByInnerColor(request);
    }

    @Override
    @GetMapping("/v1/CarPic/Pic_ScanPictureInnerColorItemsByPicId.ashx")
    public GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByPicId(GetPicScanPictureInnerItemsByPicIdRequest request){
        return pictureService.getPicScanPictureInnerItemsByPicId(request);
    }

    @Override
    @GetMapping("/v1/carpic/Pic_ScanInnerColorPictureItemsByClassId.ashx")
    public GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByClass(GetPicScanPictureInnerItemsByClassRequest request){
        return pictureService.getPicScanPictureInnerItemsByClass(request);
    }

    /**
     * 根据年代款id获取对应类别前五张图
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/year_classpicturebyyearid.ashx")
    @Override
    public GetClassPictureByYearIdResponse getClassPictureByYearId(GetClassPictureByYearIdRequest request) {
        ApiResult<PicYearItem> apiResult = pictureService.getClassPictureByYearId(request);
        GetClassPictureByYearIdResponse.Builder builder = GetClassPictureByYearIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetClassPictureByYearIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetClassPictureByYearIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 根据年代款id获取对应25图
     * @param request
     * @return
     */
    @GetMapping("/v1/dealer/Year_25PictureByYearId.ashx")
    @Override
    public GetYear25PictureByYearIdResponse getYear25PictureByYearId(GetYear25PictureByYearIdRequest request) {
        ApiResult<Pic25YearItem> apiResult = pictureService.getYear25PictureByYearId(request);
        GetYear25PictureByYearIdResponse.Builder builder = GetYear25PictureByYearIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetYear25PictureByYearIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetYear25PictureByYearIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 根据精选类别获取全部精选信息
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/featured_infobytypeId.ashx")
    @Override
    public GetFeaturedInfoByTypeIdResponse getFeaturedInfoByTypeId(GetFeaturedInfoByTypeIdRequest request) {
        return pictureService.getFeaturedInfoByTypeId(request);
    }

    @GetMapping("/v1/CarPic/Pic_PictureListsByCondition.ashx")
    @Override
    public GetPicPictureListByConditionResponse getPicPictureListByCondition(GetPicPictureListByConditionRequest request) {
        return pictureService.getPicPictureListByCondition(request);
    }

    /**
     * 根据精选图片id获取对应图片列表
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/featured_photobyfeaturedid.ashx")
    @Override
    public GetFeaturedPhotoByFeaturedIdResponse getFeaturedPhotoByFeaturedId(GetFeaturedPhotoByFeaturedIdRequest request) {
        return pictureService.getFeaturedPhotoByFeaturedId(request);
    }

    /**
     * 获取全部精选图片分类
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/featured_type.ashx")
    @Override
    public GetFeaturedTypeResponse getFeaturedType(GetFeaturedTypeRequest request) {
        return pictureService.getFeaturedType(request);
    }

    /**
     * 根据车系id及图片类别id获取图片类别数量及前五张图片
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/picclass_pictureitemsbyseriesId.ashx")
    @Override
    public GetPictureItemsBySeriesIdAndClassIdResponse getPictureItemsBySeriesIdAndClassId(GetPictureItemsBySeriesIdAndClassIdRequest request) {
        return pictureService.getPictureItemsBySeriesIdAndClassId(request);
    }
    @GetMapping("/v1/edit/Pic_PictureItemsByCondition.ashx")
    @Override
    public EditPicPictureItemByConditionResponse editPicPictureItemByCondition(EditPicPictureItemByConditionRequest request){
        EditPicPictureItemByConditionResponse.Builder builder = EditPicPictureItemByConditionResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int classId = request.getClassid();
        int pageIndex = request.getPage() == 0 ? 1 : request.getPage();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        if((seriesId == 0 && specId == 0) || pageIndex < 1 || size < 0){
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        EditPicPictureItemByConditionResponse.Result.Builder result = EditPicPictureItemByConditionResponse.Result.newBuilder();
        int pageStart = pageIndex == 1 ? 1 : (pageIndex - 1) * size + 1;
        int pageEnd = pageIndex == 1 ? size : pageIndex * size;

        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        boolean isCV = seriesBaseInfo != null && Level.isCVLevel(seriesBaseInfo.getLevelId());
        int total = picClassMapper.getSeriesPicBySeriesIdPicClassCount(seriesId,specId,classId,isCV);

        List<SeriesPicClassEntity> seriesPicList = picClassMapper.getSeriesPicBySeriesIdPicClass(seriesId,specId,classId,pageStart,pageEnd,isCV);
        if(!CollectionUtils.isEmpty(seriesPicList)){
            List<Integer> picClassIds = seriesPicList.stream().map(SeriesPicClassEntity::getPicClass).collect(Collectors.toList());
            List<Integer> specIds = seriesPicList.stream().map(SeriesPicClassEntity::getSpecId).collect(Collectors.toList());

            Map<Integer, PicClassEntity> picMap = picClassBaseService.getList(picClassIds);
            Map<Integer, SpecParam> specMap = specParamService.getMap(specIds);
            for(SeriesPicClassEntity item : seriesPicList){
                EditPicPictureItemByConditionResponse.Result.PicItem.Builder row = EditPicPictureItemByConditionResponse.Result.PicItem.newBuilder();
                row.setId(item.getPicId());
                row.setTypeid(item.getPicClass());
                PicClassEntity picClass = picMap != null ? picMap.getOrDefault(item.getPicClass(), null) : null;
                row.setTypename(picClass != null ? picClass.getName() : "");
                row.setFilepath(ImageUtil.getFullImagePathWithoutReplace(item.getPicfilepath() != null ? item.getPicfilepath() : ""));
                row.setIshd(item.getIsHD());
                row.setSpecid(item.getSpecId());
                SpecParam spec = specMap != null ? specMap.getOrDefault(item.getSpecId(), null) : null;
                row.setSpecname(spec != null ? spec.getSpecname() : "");
                row.setYearid(item.getSyearId());
                row.setYearname("");
                row.setSeriesid(seriesId);
                row.setSeriesname(spec != null ? spec.getSeriesname() : "");
                row.setFctid(spec != null ? spec.getFctid() : 0);
                row.setFctname(spec != null ? spec.getFctname() : "");
                row.setBrandid(spec != null ? spec.getBrandid() : 0);
                row.setBrandname(spec != null ? spec.getBrandname() : "");
                row.setSpecstate(item.getSpecState());
                result.addPicitems(row);
            }
        }
        result.setTotal(total).setPageindex(pageIndex).setSize(size);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();

    }

    @GetMapping("/v1/labelpic/PicList_BySpecId.ashx")
    @Override
    public GetPicConfigRelationResponse getPicConfigRelation(GetPicConfigRelationRequest request){
        GetPicConfigRelationResponse.Builder builder = GetPicConfigRelationResponse.newBuilder();
        List<PicConfigRelationEntity> picRelation = autoCacheServiceImpl.getPicConfigRelation(request.getSpecid());
        List<PicPointLocationEntity> picPoint = autoCacheServiceImpl.getPicLocation(request.getSpecid());
        Map<Integer, List<GetPicConfigRelationResponse.ConfigItem>> picRelationMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(picRelation)){
            picRelationMap = picRelation.stream()
                    .collect(Collectors.groupingBy(PicConfigRelationEntity::getPicid,
                            Collectors.mapping(picConfig -> GetPicConfigRelationResponse.ConfigItem.newBuilder()
                                            .setUserid(picConfig.getUserid())
                                            .setItemid(picConfig.getItemid())
                                            .setSubitemid(picConfig.getSubitemid())
                                            .setPicid(picConfig.getPicid())
                                            .build(),
                                    Collectors.toList())));
        }
        if(!CollectionUtils.isEmpty(picPoint)){
            for(PicPointLocationEntity item : picPoint){
                List<GetPicConfigRelationResponse.ConfigItem> listPicConfig = new ArrayList<>();
                if(!CollectionUtils.isEmpty(picRelationMap) && picRelationMap.containsKey(item.getPicid())){
                    listPicConfig = picRelationMap.get(item.getPicid());
                }
                GetPicConfigRelationResponse.PicItem.Builder picItem = GetPicConfigRelationResponse.PicItem.newBuilder();
                picItem.addAllConfiglist(listPicConfig);
                picItem.setPicid(item.getPicid());
                picItem.setPicurl(ImageUtil.getFullImagePathWithoutReplace(item.getPicurl() != null ? item.getPicurl() : ""));
                picItem.setSixtypicid(item.getPointlocatinid());
                picItem.setSixtypiceditor(item.getPointlocationseteditor());
                builder.addResult(picItem);
            }
        }
        return builder.setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/v1/pointlocation/Series_PicList.ashx")
    @Override
    public GetSeriesPicPointLocationResponse getSeriesPicPointLocation(GetSeriesPicPointLocationRequest request){
        GetSeriesPicPointLocationResponse.Builder builder = GetSeriesPicPointLocationResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int locationId = request.getLocationid();
        int pageIndex = request.getPageindex() == 0 ? 1 : request.getPageindex();
        int pageSize = request.getPagesize() == 0 ? 10 : request.getPagesize();
        if(seriesId == 0 || locationId == 0){
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        GetSeriesPicPointLocationResponse.Result.Builder result = GetSeriesPicPointLocationResponse.Result.newBuilder();

        List<Car25LocationPicViewEntity> pic25List = autoCacheServiceImpl.get25PointLocation(seriesId, locationId);
        if(!CollectionUtils.isEmpty(pic25List)) {
            List<Integer> specIds = pic25List.stream().map(Car25LocationPicViewEntity::getSpecId).collect(Collectors.toList());
            Map<Integer, SpecBaseInfo> specBaseInfoMap = specBaseService.getMap(specIds);
            int totalBeforePaging = pic25List.stream()
                    .collect(Collectors.groupingBy(
                            dr -> {
                                int syear = dr.getSyear();
                                int picid = dr.getPicId();
                                int specid = dr.getSpecId();
                                String picurl = dr.getPicPath();
                                return syear + "_" + picid + "_" + specid + "_" + picurl;
                            }))
                    .size();
            Map<String, List<Car25LocationPicViewEntity>> pagedPicMap = pic25List.stream()
                    .collect(Collectors.groupingBy(
                            dr -> {
                                int syear = dr.getSyear();
                                int picid = dr.getPicId();
                                int specid = dr.getSpecId();
                                String picurl = dr.getPicPath();
                                return syear + "@-" + picid + "@-" + specid + "@-" + picurl;
                            }))
                    .entrySet().stream()
                    .sorted(
                            //线上可能bug，year排序被覆盖
//                            .comparingInt((Map.Entry<String, List<Car25LocationPicViewEntity>> entry) -> Integer.parseInt(entry.getKey().split("_")[0]))
//                                    .reversed()
                            Comparator.comparingInt((Map.Entry<String, List<Car25LocationPicViewEntity>> entry) -> Integer.parseInt(entry.getKey().split("@-")[1]))
                                    .reversed())
                    .skip((pageIndex - 1) * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> newValue,
                            LinkedHashMap::new));
            for (Map.Entry<String, List<Car25LocationPicViewEntity>> picEntry : pagedPicMap.entrySet()) {
                String[] keyParts = picEntry.getKey().split("@-");
                int syear = Integer.parseInt(keyParts[0]);
                int picid = Integer.parseInt(keyParts[1]);
                int specid = Integer.parseInt(keyParts[2]);
                String picurl = keyParts[3];
                String specName = specBaseInfoMap != null && specBaseInfoMap.containsKey(specid) ? specBaseInfoMap.get(specid).getSpecName() : ""; // Replace with your implementation

                List<GetSeriesPicPointLocationResponse.ValueItem> arrPointParamAndConfig = new ArrayList<>();
                for (Car25LocationPicViewEntity pic : picEntry.getValue()) {
                    if(pic.getDatatype() > 0){
                        String itemName = pic.getItemname();
                        String value = pic.getValu();
                        int itemId = pic.getItemid();
                        if (pic.getDatatype() == 1) {
                            if (Strings.isNotBlank(itemName) && Strings.isNotBlank(value)) {
                                arrPointParamAndConfig.add(GetSeriesPicPointLocationResponse.ValueItem.newBuilder()
                                        .setDatatype(pic.getDatatype())
                                        .setItemid(itemId)
                                        .setSubitemid(0)
                                        .setName(itemName)
                                        .setValue(value)
                                        .build());
                            }
                        }else if(pic.getDatatype() == 2){
                            int itemvalueId = pic.getItemValueId();
                            int subValue = Strings.isNotBlank(pic.getSubValue()) ? Integer.parseInt(pic.getSubValue()) : 0;
                            String subItemName = pic.getSubitemname();
                            int subItemId = pic.getSubItemId();
                            if (subValue == 1 && itemvalueId != 1) {
                                arrPointParamAndConfig.add(GetSeriesPicPointLocationResponse.ValueItem.newBuilder()
                                        .setDatatype(pic.getDatatype())
                                        .setItemid(itemId)
                                        .setSubitemid(subItemId)
                                        .setName(itemName)
                                        .setValue(subItemName)
                                        .build());
                            } else if (itemvalueId == 1) {
                                arrPointParamAndConfig.add(GetSeriesPicPointLocationResponse.ValueItem.newBuilder()
                                        .setDatatype(pic.getDatatype())
                                        .setItemid(itemId)
                                        .setSubitemid(0)
                                        .setName(itemName)
                                        .setValue("")
                                        .build());
                            }
                        }
                    }
                }
                result.addList(GetSeriesPicPointLocationResponse.PicItem.newBuilder()
                        .setPicid(picid)
                        .setPicurl(ImageUtil.getFullImagePathWithoutReplace(picurl))
                        .setSpecid(specid)
                        .setSpecname(specName)
                        .addAllValuelist(arrPointParamAndConfig)
                        .build());
            }
            result.setPagesize(pageSize).setRowcount(totalBeforePaging);
            builder.setResult(result);
        }
        return builder.setReturnCode(0).setReturnMsg("成功").build();
    }

    /**
     * 获取定时发布的图片数据
     * @param request
     * @return
     */
    @GetMapping("/v1/CarPic/Pic_PictureDetailItemsByConditionForPublish.ashx")
    @Override
    public PictureDetailItemsByConditionResponse getPictureDetailItemsByConditionForPublish(PictureDetailItemsByConditionRequest request) {
        return pictureService.getPictureDetailItemsByConditionForPublish(request);
    }

}