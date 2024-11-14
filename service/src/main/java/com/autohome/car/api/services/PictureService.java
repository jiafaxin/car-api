package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.javascript.IndexSlidePicRequest;
import autohome.rpc.car.car_api.v1.javascript.IndexSlidePicResponse;
import autohome.rpc.car.car_api.v1.pic.*;
import autohome.rpc.car.car_api.v2.pic.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.PicColorInfoResult;
import com.autohome.car.api.services.models.pic.Pic25YearItem;
import com.autohome.car.api.services.models.pic.PicClassItem;
import com.autohome.car.api.services.models.pic.PicSpecItem;
import com.autohome.car.api.services.models.pic.PicYearItem;


public interface PictureService {

    /**
     * 根据车型id,图片类别id获取颜色图片数量
     * @param specId
     * @param classId
     * @return
     */
    PicColorInfoResult getPicColorInfo(int specId, int classId);

    PicColorInfoResult getInnerPicColorInfo(int specId,int classId);

    GetSeries25PictureBySeriesIdResponse getSeries25PictureBySeriesId(GetSeries25PictureBySeriesIdRequest request);

    /**
     * 根据车型id获取对应类别前五张图
     * @param request
     * @return
     */
    ApiResult<PicSpecItem> getSpecClassPictureBySpecId(SpecClassPictureBySpecIdRequest request);

    GetPicPictureItemsByConditionV1Response getPicPictureItemsByConditionV1(GetPicPictureItemsByConditionV1Request request);

    GetPicPictureItemsByConditionResponse getPicPictureItemsByConditionV2(GetPicPictureItemsByConditionRequest request);

    GetPicClassItemsBySeriesIdResponse getPicClassItemsBySeriesId(GetPicClassItemsBySeriesIdRequest request);

    GetSpecColorListBySpecListResponse getSpecColorListBySpecList(GetSpecColorListBySpecListRequest request);

    GetSpecColorListBySpecListResponse getSpecInnerColorListBySpecIdList(GetSpecColorListBySpecListRequest request);

    GetPicClassItemsResponse getPicClassBySpecIdItems(GetPicClassItemsRequest request);

    GetSpecColorListBySpecIdResponse getSpecColorListBySpecId(GetSpecColorListBySpecIdRequest request);

    GetSpecColorListBySpecIdResponse getSpecInnerColorListBySpecId(GetSpecColorListBySpecIdRequest request);

    GetPicClassItemByMoreSpecIdResponse getPicClassItemByMoreSpecId(GetPicClassItemByMoreSpecIdRequest request);
    PicColorItemsBySeriesIdResponse getPicColorItemsBySeriesId(PicColorItemsBySeriesIdRequest request);

    Spec25PictureBySpecListResponse spec25PictureBySpecList(Spec25PictureBySpecListRequest request);

    GetPicClassPictureItemsBySpecIdResponse getPicClassPictureItemsBySpecId (GetPicClassPictureItemsBySpecIdRequest request);

    GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByPicId(GetPicScanPictureItemsByPicIdRequest request);

    GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByClass(GetPicScanPictureItemsByClassRequest request);

    GetPicScanPictureItemsByConditionResponse getPicScanPictureItemsByColor(GetPicScanPictureItemsByColorRequest request);

    GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByInnerColor(GetPicScanPictureInnerItemsByInnerColorRequest request);

    GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByPicId(GetPicScanPictureInnerItemsByPicIdRequest request);

    GetPicScanPictureInnerItemsByConditionResponse getPicScanPictureInnerItemsByClass(GetPicScanPictureInnerItemsByClassRequest request);

    ApiResult<PicClassItem> getPicClassClassItemsBySeriesIdV2(GetPicClassClassItemsBySeriesIdRequest request);

    /**
     * 根据年代款id获取对应类别前五张图
     * @param request
     * @return
     */
    ApiResult<PicYearItem> getClassPictureByYearId(GetClassPictureByYearIdRequest request);

    /**
     * 根据年代款id获取对应25图
     * @param request
     * @return
     */
    ApiResult<Pic25YearItem> getYear25PictureByYearId(GetYear25PictureByYearIdRequest request);

    GetSpecColorListBySpecIdResponse getSpecColorListByYearId(GetSpecColorListByYearIdRequest request);

    GetSpecColorListBySpecIdResponse getSpecInnerColorListByYearId(GetSpecColorListByYearIdRequest request);

    /**
     * 根据精选类别获取全部精选信息
     * @param request
     * @return
     */
    GetFeaturedInfoByTypeIdResponse getFeaturedInfoByTypeId(GetFeaturedInfoByTypeIdRequest request);

    GetPicPictureListByConditionResponse getPicPictureListByCondition(GetPicPictureListByConditionRequest request);

    /**
     * 根据精选图片id获取对应图片列表
     * @param request
     * @return
     */
    GetFeaturedPhotoByFeaturedIdResponse getFeaturedPhotoByFeaturedId(GetFeaturedPhotoByFeaturedIdRequest request);

    /**
     * 获取全部精选图片分类
     * @param request
     * @return
     */
    GetFeaturedTypeResponse getFeaturedType(GetFeaturedTypeRequest request);

    /**
     * 根据车系id及图片类别id获取图片类别数量及前五张图片
     * @param request
     * @return
     */
    GetPictureItemsBySeriesIdAndClassIdResponse getPictureItemsBySeriesIdAndClassId(GetPictureItemsBySeriesIdAndClassIdRequest request);

    IndexSlidePicResponse indexSlidePic(IndexSlidePicRequest request);
    /**
     * 获取定时发布的图片数据
     * @param request
     * @return
     */
    PictureDetailItemsByConditionResponse getPictureDetailItemsByConditionForPublish(PictureDetailItemsByConditionRequest request);

    /**
     * 获取按照车型分组的车型图片列表
     * @param request
     * @return
     */
    PicListGroupByConditionResponse picListGroupByCondition(PicListGroupByConditionRequest request);

    /**
     * 获取根据车型和其他条件的更多图片数据
     * @param request
     * @return
     */
    PicListMoreByConditionResponse picListMoreByCondition(PicListMoreByConditionRequest request);


    /**
     * 分页获取车系页图片详情页数据
     * @param request
     * @return
     */
    PicListDetailByConditionResponse picListDetailByCondition(PicListDetailByConditionRequest request);
}
