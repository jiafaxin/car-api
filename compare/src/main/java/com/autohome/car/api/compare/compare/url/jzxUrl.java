package com.autohome.car.api.compare.compare.url;

import com.autohome.car.api.compare.compare.enums.OperType;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.compare.service.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class jzxUrl implements Url {
    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public Map<String, Param> getUrl() {
        Map<String, Param> map = new HashMap<>();
        //四期
        map.put("/v3/CarPrice/Config_GetListBySpecId.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new ConfigListBySpecIdCallBack()).build());
        map.put("/v1/carprice/spec_colorlistbyspecid.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new SpecColorListBySpecIdCallBack()).build());
        map.put("/v2/car/Config_BagOfYearBySpecId.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new ConfigBagByYearSpecIdCallBack()).build());
        map.put("/v1/carprice/spec_innercolorlistbyspecid.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new SpecInnerColorListBySpecIdCallBack()).build());
        map.put("/v1/App/Series_SeriesListByBrandIds.ashx", Param.builder().operType(OperType.BRAND_ID).callBack(new SeriesListByBrandsCallBack()).build());
        map.put("/v1/carpic/PicClass_ClassItemByMoreSpecId.ashx", Param.builder().operType(OperType.SPEC_ID_List).callBack(new PicClassItemByMorSpecIdCallBack()).build());

        //五期
        map.put("/v1/CarPrice/Spec_CountBySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecCountBySeriesIdCallBack()).build());
        //map.put("/v1/carprice/series_getallname.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new SeriesAllNameCallBack()).build());
        map.put("/v1/carpic/picclass_pictureitemsbyspecid.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new PicClassPictureBySpecIdCallBack()).build());
        map.put("/v2/javascript/brand.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new JSBrandsCallBack()).build());
        map.put("/v1/carprice/spec_detailbyspeclist.ashx", Param.builder().operType(OperType.SPEC_ID_List).callBack(new SpecDetailBySpeclistCallBack()).build());
        map.put("/v1/CarPic/Pic_ScanPictureItemsBypicId.ashx", Param.builder().operType(OperType.COLOR_LIST).callBack(new PicScanColorByClassCallBack()).build());
        map.put("/v1/carpic/Pic_ScanPictureItemsByClassId.ashx", Param.builder().operType(OperType.COLOR_LIST).callBack(new PicScanColorByClassCallBack()).build());
        map.put("/v1/CarPic/Pic_ScanPictureItemsBycolorId.ashx", Param.builder().operType(OperType.COLOR_LIST).callBack(new PicScanColorByClassCallBack()).build());
        map.put("/v1/CarPic/Pic_ScanPictureItemsByinnercolorId.ashx", Param.builder().operType(OperType.COLOR_LIST).callBack(new PicScanInnerColorByClassCallBack()).build());
        map.put("/v1/CarPic/Pic_ScanPictureInnerColorItemsByPicId.ashx", Param.builder().operType(OperType.COLOR_LIST).callBack(new PicScanInnerColorByClassCallBack()).build());
        map.put("/v1/carpic/Pic_ScanInnerColorPictureItemsByClassId.ashx", Param.builder().operType(OperType.COLOR_LIST).callBack(new PicScanInnerColorByClassCallBack()).build());

        //六期
        map.put("/v1/carprice/spec_colorlistbyyearid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v1/CarPrice/Spec_InnerColorListByYearId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v2/car/Config_BagBySeriesIdYearId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v1/car/spec_paramlistbyyearid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v1/Unify/Spec_ListByYearId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecListByYearIdCallBack()).build());
        map.put("/v1/carprice/spec_paramlistbyyearid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v3/carprice/Config_GetListByYearId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v1/carprice/year_infobyyearid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v1/carprice/year_colorbyyearid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());
        map.put("/v1/carprice/year_innercolorbyyearid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecColorByYearIdCallBack()).build());

        //七期
        map.put("/v1/carprice/series_parambyspecId.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new DefaultCallBack()).build());
        map.put("/v2/carprice/Config_GetListBySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new DefaultCallBack()).build());
        map.put("/v2/carprice/Config_GetPriceListBySpecList.ashx", Param.builder().slice(3).operType(OperType.SPEC_ID_List).field("speclist").callBack(new DefaultCallBack()).build());
        map.put("/v1/carprice/spec_paramsinglebyspecIditemid.ashx", Param.builder().operType(OperType.SPEC_ID_List).callBack(new ConfigListBySpecIdAndItemCallBack()).build());
        map.put("/v1/App/Spec_SpeclistBySeriesIds.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SpecConfigBySeriesIdCallBack()).build());
        map.put("/v2/CarPrice/Spec_BaseInfoBySpecList.ashx", Param.builder().slice(3).operType(OperType.SPEC_ID_List).field("speclist").callBack(new DefaultCallBack()).build());
        map.put("/v1/App/Spec_InfoBySpecIds.ashx", Param.builder().slice(3).operType(OperType.SPEC_ID_List).field("speclist").callBack(new DefaultCallBack()).build());
        map.put("/v1/car/spec_paramlistbyseriesid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new DefaultCallBack()).build());
        map.put("/v1/carprice/series_namebyfctid.ashx", Param.builder().operType(OperType.FCT_ID).callBack(new DefaultCallBack()).build());
        map.put("/NewEnergy/Dingzhi_BrandElectric.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new JSBrandsCallBack()).build());
        map.put("/v1/CarPic/Pic_PictureListsByCondition.ashx", Param.builder().operType(OperType.COLOR_LIST).callBack(new PiPictureByConditionCallBack()).build());
        map.put("/NewEnergy/Dingzhi_SpecMainParam.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new DefaultCallBack()).build());

        //八期
        map.put("/v1/edit/Pic_PictureItemsByCondition.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new EditPicItemsByPageCallBack()).build());
        map.put("/v1/labelpic/PicList_BySpecId.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new DefaultCallBack()).build());
        map.put("/v1/Mweb/Pic_Brand_List.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new DefaultCallBack()).build());
        map.put("/v1/pointlocation/Series_PicList.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SeriesLocationCallBack()).build());
        map.put("/v1/Unify/Spec_ListBySeriesId.ashx", Param.builder().exclude(new String[]{"root.result.timer"}).operType(OperType.SERIES_ID).callBack(new DefaultCallBack()).build());
        map.put("/v2/App/Pic_ColorAllClassBySeriesList.ashx", Param.builder().operType(OperType.SERIES_ID_List).field("serieslist").slice(10).callBack(new DefaultCallBack()).build());
        map.put("/v2/CarPrice/Fct_GetFctById.ashx", Param.builder().operType(OperType.FCT_ID).callBack(new DefaultCallBack()).build());
        map.put("/v1/carprice/series_infowithpagebylevelid.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new SeriesLevelByPageCallBack()).build());
        map.put("/v1/carshow/show_brandinfobypavilionlistfirstletter.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new ShowBrandByPavilionIdLetterCallBack()).build());
        map.put("/v1/CarShow/show_seriespictureitemsbycondition.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new SeriesShowMaxPicByPageCallBack()).build());
        map.put("/v1/duibi/Param_ListBySpecList.ashx", Param.builder().operType(OperType.SERIES_ID_List).field("speclist").slice(3).callBack(new DefaultCallBack()).build());

        //九期
        String[] s = {"root.result.configtypeitems[*].configitems[*].pnid"};
        map.put("/v1/car/config_listbyyearid.ashx", Param.builder().exclude(s).operType(OperType.YEAR_ID).callBack(new ConfigListByYearCallBack()).build());
        map.put("/v1/Rec/Spec_AllSpecInfo.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new AllSpecInfoByStateCallBack()).build());
        map.put("/v1/App/Electric_SeriesList.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new DefaultCallBack()).build());
        map.put("/v1/Mweb/Brand_RankList.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new JSBrandsCallBack()).build());
        map.put("/v2/Base/Fct_GetAllFcts.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new DefaultCallBack()).build());
        map.put("/v1/carprice/series_getallprice.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new DefaultCallBack()).build());
        map.put("/v1/carprice/series_info.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new AllSpecInfoByStateCallBack()).build());
        map.put("/v2/carprice/Series_AllBaseInfo.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new AllSpecInfoByStateCallBack()).build());
        map.put("/v1/javascript/factory.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new AllSpecInfoByStateCallBack()).build());
        map.put("/v1/carprice/spec_getallname.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new AllSpecInfoByStateCallBack()).build());
        map.put("/NewEnergy/Series_SeriesRankByMileage.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new SeriesRankByFuelCallBack()).build());
        map.put("/NewEnergy/Dingzhi_SNew/SeriesAndSpecNum.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new SolrSeriesNumCallBack()).build());
        map.put("/NewEnergy/Dingzhi_SNew/SeriesResult.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new SolrSeriesNumCallBack()).build());
        map.put("/NewEnergy/Dingzhi_SNew/SpecsResult.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new SolrSeriesNumCallBack()).build());
        map.put("/v2/App/Pic_RecommendThreePicAndBigPic.ashx", Param.builder().operType(OperType.NO_PARAM).callBack(new PicThreeBigPicCallBack()).build());
        map.put("/v1/car/Www_LevelFindCar.ashx", Param.builder().exclude(new String[]{"root.result.timer"}).operType(OperType.NO_PARAM).callBack(new FindcarWWWCallBack()).build());
        map.put("/v1/pingan/SyearAndSpecBySeries.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new PingAnSyearAndSpecBySeriesCallBack()).build());


        return map;
    }
}
