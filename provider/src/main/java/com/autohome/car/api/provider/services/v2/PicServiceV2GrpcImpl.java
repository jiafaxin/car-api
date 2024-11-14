package com.autohome.car.api.provider.services.v2;

import autohome.rpc.car.car_api.v2.pic.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.HtmlUtils;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.data.popauto.AppPictureMapper;
import com.autohome.car.api.data.popauto.OptimizeSeriesColorMapper;
import com.autohome.car.api.data.popauto.entities.AppPictureEntity;
import com.autohome.car.api.data.popauto.entities.OptimizeSeriesColorEntity;
import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.PictureService;
import com.autohome.car.api.services.basic.ColorBaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SpecPicClassStatisticsBaseService;
import com.autohome.car.api.services.basic.models.ColorBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.models.pic.PicClassItem;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;
import static java.util.stream.Collectors.toSet;

@DubboService
@RestController
public class PicServiceV2GrpcImpl extends DubboPicServiceTriple.PicServiceImplBase {

    private static final Set<Integer> PICCLASS = Collections.unmodifiableSet(Stream.of(1, 10, 3, 12, 51, 53).collect(toSet()));

    @Autowired
    PictureService pictureService;

    @Autowired
    OptimizeSeriesColorMapper optimizeSeriesColorMapper;

    @Autowired
    ColorBaseService colorBaseService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    AppPictureMapper appPictureMapper;

    @Autowired
    SpecPicClassStatisticsBaseService specPicClassStatisticsBaseService;

    /**
     * 根据车系id,车型id,类型id,颜色id,页码及页大小,图片id获取图片信息
     * @param request
     * @return
     */
    @Override
    @GetMapping("/v2/app/Pic_PictureItemsByCondition.ashx")
    public GetPicPictureItemsByConditionResponse getPicPictureItemsByCondition(GetPicPictureItemsByConditionRequest request){
        return pictureService.getPicPictureItemsByConditionV2(request);
    }

    @Override
    @GetMapping("/v2/carpic/picclass_classitemsbyspecid.ashx")
    public GetPicClassItemsResponse getPicClassBySpecIdItems(GetPicClassItemsRequest request) {
        return pictureService.getPicClassBySpecIdItems(request);
    }

    /**
     * 根据车系id获取图片类别数量
     * @param request
     * @return
     */
    @GetMapping("/v2/carpic/picclass_classitemsbyseriesid.ashx")
    @Override
    public GetPicClassClassItemsBySeriesIdResponse getPicClassClassItemsBySeriesId(GetPicClassClassItemsBySeriesIdRequest request) {
        ApiResult<PicClassItem> apiResult = pictureService.getPicClassClassItemsBySeriesIdV2(request);
        GetPicClassClassItemsBySeriesIdResponse.Builder builder = GetPicClassClassItemsBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetPicClassClassItemsBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetPicClassClassItemsBySeriesIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    @GetMapping("/v2/App/Pic_ColorAllClassBySeriesList.ashx")
    @Override
    public GetPicColorClassListBySeriesResponse  getPicColorClassListBySeries(GetPicColorClassListBySeriesRequest request){
        GetPicColorClassListBySeriesResponse.Builder builder = GetPicColorClassListBySeriesResponse.newBuilder();
        GetPicColorClassListBySeriesResponse.Result.Builder result = GetPicColorClassListBySeriesResponse.Result.newBuilder();
        List<Integer> seriesIds = request.getSerieslistList();
        if(CollectionUtils.isEmpty(seriesIds) || seriesIds.size() > 30){
            return builder.setReturnCode(102).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<OptimizeSeriesColorEntity> list = optimizeSeriesColorMapper.getOptimizeSeriesColorEntity(seriesIds);
        Map<Integer,List<OptimizeSeriesColorEntity>> colorMap = list.stream()
                .sorted(Comparator.comparingInt(OptimizeSeriesColorEntity::getPiccount).reversed())
                .collect(Collectors.groupingBy(OptimizeSeriesColorEntity::getSeriesid));

        Map<Integer, SeriesBaseInfo> seriesMap = seriesBaseService.getMap(seriesIds);
        if(CollectionUtils.isEmpty(seriesMap)){
            return builder.setReturnCode(0).setReturnMsg("成功").build();
        }
        List<Integer> colorIds = list.stream().map(OptimizeSeriesColorEntity::getColorid).distinct().collect(Collectors.toList());
        Map<Integer, ColorBaseInfo> colorInfoMap = colorBaseService.getColorMap(colorIds);
        for(Integer seriesId :seriesIds){
            List<OptimizeSeriesColorEntity> subList = colorMap.get(seriesId);
            if(!CollectionUtils.isEmpty(subList)){
                GetPicColorClassListBySeriesResponse.Result.SeriesItem.Builder series = GetPicColorClassListBySeriesResponse.Result.SeriesItem.newBuilder();
                series.setSeriesid(seriesId);
                series.setSeriesname(seriesMap.get(seriesId) != null ? seriesMap.get(seriesId).getName() : "");
                for(OptimizeSeriesColorEntity item : subList){
                    GetPicColorClassListBySeriesResponse.Result.SeriesItem.ColorItem.Builder color = GetPicColorClassListBySeriesResponse.Result.SeriesItem.ColorItem.newBuilder();
                    color.setColorid(item.getColorid());
                    color.setColorname(colorInfoMap != null && colorInfoMap.get(item.getColorid()) != null ? colorInfoMap.get(item.getColorid()).getName() : "");
                    color.setColorvalue(colorInfoMap != null && colorInfoMap.get(item.getColorid()) != null ? colorInfoMap.get(item.getColorid()).getValue() : "");
                    color.setImg(ImageUtil.getFullImagePath(item.getPicfilepath() != null ? item.getPicfilepath() : ""));
                    color.setImgnum(item.getPiccount());
                    series.addColorlist(color);
                }
                result.addSeriesitems(series);
            }
        }
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/v2/App/Pic_RecommendThreePicAndBigPic.ashx")
    @Override
    public RecommendThreePicAndBigPicResponse recommendThreePicAndBigPic(RecommendThreePicAndBigPicRequest request){
        RecommendThreePicAndBigPicResponse.Builder builder = RecommendThreePicAndBigPicResponse.newBuilder().setReturnCode(0).setReturnMsg("成功");
        RecommendThreePicAndBigPicResponse.Result.Builder result = RecommendThreePicAndBigPicResponse.Result.newBuilder();

        String pattern = "yyyy-MM-dd HH:mm:ss"; // 参数日期时间格式
        String editTime = "";
        if(Strings.isNotBlank(request.getUtime())){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime dateTime = LocalDateTime.parse(request.getUtime(), formatter);
            editTime = dateTime.format(formatter);
        }

        List<AppPictureEntity> list =  appPictureMapper.getAppNewPictureByTime(editTime, request.getSize());
        if(CollectionUtils.isEmpty(list)){
            return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }

        Map<AppPictureEntity, List<AppPictureEntity>> orderedData = new LinkedHashMap<>();
        for(AppPictureEntity dr : list){
            if(orderedData.containsKey(dr.groupByEdit())){
                orderedData.get(dr.groupByEdit()).add(dr);
            }else{
                List<AppPictureEntity> value = new ArrayList<>();
                value.add(dr);
                orderedData.put(dr.groupByEdit(), value);
            }
        }

        Map<Integer,List<SpecPicColorStatisticsEntity>> specPicMap = new HashMap<>();
        List<Integer> seriesIds = orderedData.keySet().stream()
                .map(AppPictureEntity::getSeriesId)
                .collect(Collectors.toList());
        List<SeriesBaseInfo> seriesList = seriesBaseService.getList(seriesIds);
        if(!CollectionUtils.isEmpty(seriesList)){
            List<Pair<Integer, Boolean>> seriesParam = seriesList.stream()
                    .map(entity -> Pair.of(entity.getId(), Level.isCVLevel(entity.getLevelId())))
                    .collect(Collectors.toList());
            specPicMap = specPicClassStatisticsBaseService.getSpecMap(seriesParam);
        }

        for (Map.Entry<AppPictureEntity, List<AppPictureEntity>> entry : orderedData.entrySet()) {
            AppPictureEntity k = entry.getKey();
            List<AppPictureEntity> v = entry.getValue();
            List<SpecPicColorStatisticsEntity> picList = !CollectionUtils.isEmpty(specPicMap) ? specPicMap.getOrDefault(k.getSpecId(), new ArrayList<>()) : new ArrayList<>();
            int picNum = picList.stream().filter(x -> PICCLASS.contains(x.getPicClass()))
                    .mapToInt(SpecPicColorStatisticsEntity::getPicNumber)
                    .sum();
            RecommendThreePicAndBigPicResponse.TopicItem.Builder item = RecommendThreePicAndBigPicResponse.TopicItem.newBuilder();
            item.setBizType(6);
            item.setCarBrandId(k.getBrandId());
            item.setCmsSeriesId(k.getSeriesId());
            item.setCmsSpecId(k.getSpecId());
            item.setReplyCount(picNum);
            item.setCmsContentClass(k.getBigimgtype());
            item.setTitle(k.getTitle().contains("&#") ? HtmlUtils.decode(k.getTitle()) : k.getTitle());
            item.setTitleid(k.getId());
            item.setLooptype(k.getLooptype());
            item.setImgUrl(k.getBigImg());
            item.setPublishTime(new SimpleDateFormat("yyyy/M/d H:mm:ss").format(k.getPublishTime()));
            item.setEdittime(new SimpleDateFormat("yyyy/M/d H:mm:ss").format(k.getEdittime()));
            item.setDisplaytype(k.getDisplayType());
            item.setNlpTagsChoose2("实拍/汽车展示,实拍/到店实拍");
            for (AppPictureEntity vitem : v) {
                item.addPicitems(RecommendThreePicAndBigPicResponse.PicList.newBuilder()
                                .setBizId(vitem.getPicId())
                                .setPicpath(vitem.getPicpath().replace("/m_","/"))
                                .setPictype(vitem.getPicTypeId())
                );
            }
            result.addTopiclist(item);
        }
        return builder.setResult(result).build();
    }

    /**
     * 获取按照车型分组的车型图片列表
     * @param request
     * @return
     */
    @GetMapping("/v2/pic/picListGroupByCondition")
    @Override
    public PicListGroupByConditionResponse picListGroupByCondition(PicListGroupByConditionRequest request) {
        return pictureService.picListGroupByCondition(request);
    }

    /**
     * 获取根据车型和其他条件的更多图片数据,不分页
     * @param request
     * @return
     */
    @GetMapping("/v2/pic/picListMoreByCondition")
    @Override
    public PicListMoreByConditionResponse picListMoreByCondition(PicListMoreByConditionRequest request) {
        return pictureService.picListMoreByCondition(request);
    }

    /**
     * 分页获取车系页图片详情页数据
     * @param request
     * @return
     */
    @GetMapping("/v2/pic/picListDetailByCondition")
    @Override
    public PicListDetailByConditionResponse picListDetailByCondition(PicListDetailByConditionRequest request) {
        return pictureService.picListDetailByCondition(request);
    }

}