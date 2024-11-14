package com.autohome.car.api.compare.compare.url;

import com.autohome.car.api.compare.compare.enums.OperType;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.compare.service.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class jwwUrl implements Url {
    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public Map<String, Param> getUrl() {
        Map<String, Param> map = new HashMap<>();//YearParamByYearIdCallBack
        //三期
        map.put("/v1/carprice/spec_colorlistbyspecidList.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new SpecColorListBySpecIdListCallBack()).build());
        map.put("/v1/carprice/Spec_InnerColorListBySpecIdList.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new SpecInnerColorListBySpecIdListCallBack()).build());

        //四期
        map.put("/v1/javascript/factorybybrand.ashx", Param.builder().operType(OperType.BRAND_ID).callBack(new FactoryByBrandCallBack()).build());
        map.put("/v1/carpic/piccolor_coloritemsbyseriesid.ashx", Param.builder().operType(OperType.BRAND_ID).callBack(new PicColorItemsBySeriesIdCallBack()).build());
        map.put("/v1/carprice/series_innercolorbyseriesid.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v1/javascript/syearandspecbyseries.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SyearAndSpecBySeriesCallBack()).build());
//        map.put("/v1/carprice/year_parambyyearId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new YearParamByYearIdCallBack()).build());
        map.put("/v1/carpic/year_25picturebyyearid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new Year25PictureByYearIdCallBack()).build());

        //五期
        map.put("/Crash/SeriesHaveCrashInfo.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v1/carprice/brand_infobyseriesid.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v2/Base/Spec_GetSpecBySeries.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v1/CarPrice/Spec_BaseInfbySpecList.ashx", Param.builder().operType(OperType.SPEC_ID_List).callBack(new SpecBaseInfbySpecListCallBack()).build());
        map.put("/v1/carprice/brand_getallname.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/Crash/CrashTest_BySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new CrashTestBySeriesIdCallBack()).build());
        map.put("/v1/carpic/Spec_25PictureBySpecList.ashx", Param.builder().operType(OperType.SPEC_ID_List).callBack(new Spec25PictureBySpecListCallBack()).build());
        map.put("/v1/javascript/seriesbybrand.ashx", Param.builder().operType(OperType.BRAND_ID).callBack(new SeriesByBrandCallBack()).build());
        map.put("/v1/javascript/factoryandseriesbybrand.ashx", Param.builder().operType(OperType.BRAND_ID).callBack(new FactoryAndSeriesByBrandCallBack()).build());
        map.put("/v1/javascript/seriesbyfactory.ashx", Param.builder().operType(OperType.BRAND_ID).callBack(new SeriesByFactoryCallBack()).build());


        map.put("/v1/pointlocation/Series_25PointToVR.ashx", Param.builder().operType(OperType.SERIES_ID).build());

        return map;
    }
}
