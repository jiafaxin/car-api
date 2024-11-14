package com.autohome.car.api.compare.compare.url;

import com.autohome.car.api.compare.compare.enums.OperType;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.compare.service.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;



/**
 * /v1/carprice/factory_getallname.ashx      pass
 * /v1/App/AutoTag_SeriesTagBySeriesIds.ashx pass      创建 AutoTagServiceJob
        *
        /v1/javascript/syearbyseries.ashx pass 创建 SeriesSpecYearServiceJob
        /v1/shuyu/baike_linkforexplan.ashx pass
        /v2/carprice/Config_GetListBySpecList.ashx pass
        /v1/App/Electric_BrandList.ashx pass 创建 ElectricBrandServiceJob SpecViewBrandServiceJob
        /v2/CarPrice/Series_OnlyElectricList.ashx pass 创建 SeriesElectricServiceJob
        /v1/App/Spec_ParamListBySpecList.ashx pass
        /v1/carprice/brand_logobybrandlist.ashx pass
        */

@Component
public class ZffUrl implements Url {
    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public Map<String, Param> getUrl() {
        Map<String, Param> map = new HashMap<>();

        //八期测试
        map.put("/v1/www/Index_Slidevr.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/carprice/SeriesUpcoming.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/shuyu/baike_pagelist.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/shuyu/baike_Infobyid.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/ShuYu/baike_getSecondClassesByParentId.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/carshow/show_info.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/carshow/show_brandinfobypavilionid.ashx", Param.builder().callBack(new ShowBrandInfoByPavilionIdCallBack()).build());
        map.put("/v1/carshow/showpic_infobybrandlist.ashx", Param.builder().operType(OperType.NO_PARAM).slice(5).callBack(new ShowpicInfoByBrandListCallBack()).build());
        map.put("/v1/carshow/showpic_infobypavilionid.ashx", Param.builder().callBack(new ShowPicInfoByPavilionIdCallBack()).build());
        map.put("/v1/labelpic/ConfigList_BySpecId.ashx", Param.builder().operType(OperType.SPEC_ID).build());
        map.put("/Crash/CrashTest_SeriesRank.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new CrashTestSeriesCallBack()).build());
        map.put("/v1/carprice/spec_paramlistbyspecId.ashx", Param.builder().operType(OperType.SPEC_ID).build());

        //五期测试
        map.put("/v1/carprice/factory_getallname.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/shuyu/baike_linkforexplan.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v2/CarPrice/Series_OnlyElectricList.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/App/Electric_BrandList.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/App/AutoTag_SeriesTagBySeriesIds.ashx", Param.builder().field("seriesids").slice(3).operType(OperType.SERIES_ID_List).build());
        map.put("/v1/javascript/syearbyseries.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new SyearbyseriesCallBack()).build());
        map.put("/v1/carprice/brand_logobybrandlist.ashx", Param.builder().slice(3).operType(OperType.BRAND_ID_LIST).build());
        map.put("/v1/App/Spec_ParamListBySpecList.ashx", Param.builder().slice(3).field("speclist").operType(OperType.SPEC_ID_List).build());
        map.put("/v2/carprice/Config_GetListBySpecList.ashx", Param.builder().slice(3).field("speclist").operType(OperType.SPEC_ID_List).build());
       // map.put("/v1/carprice/spec_paramlistbyspeclist.ashx", Param.builder().slice(3).field("speclist").operType(OperType.SPEC_ID_List).build());
        //四期测试
        map.put("/v1/javascript/specbysyear.ashx", Param.builder().operType(OperType.YEAR_ID).callBack(new YearIdCallBack()).build());
        map.put("/v1/carprice/series_logobyseriesid.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v1/CarPrice/Spec_GetSpecNameBySpecId.ashx", Param.builder().operType(OperType.SPEC_ID).build());
        map.put("/v1/carprice/spec_logobyspecid.ashx", Param.builder().operType(OperType.SPEC_ID).build());
        map.put("/v2/carpic/picclass_classitemsbyspecid.ashx", Param.builder().operType(OperType.SPEC_ID).build());
        map.put("/v1/car/Config_ListBySpecId.ashx", Param.builder().operType(OperType.SPEC_ID).callBack(new BySpecIdCallBack()).build());
        map.put("/v1/carprice/spec_paramlistbyseriesid.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v2/car/Config_BagBySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        return map;
    }
}

