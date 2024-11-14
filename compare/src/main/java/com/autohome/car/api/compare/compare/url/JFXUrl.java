package com.autohome.car.api.compare.compare.url;

import com.autohome.car.api.compare.compare.enums.OperType;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.compare.service.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JFXUrl implements Url{
    @Override
    public Map<String, Param> getUrl() {
        Map<String, Param> map = new HashMap<>();
        //五期
        map.put("/v2/carpic/picclass_classitemsbyseriesid.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new PicClassItemsBySeriesIdStateInnerColorCallBack()).build());
        map.put("/v1/CarPrice/Spec_CountBySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v2/App/Brand_GetBrandInfoByIdList.ashx", Param.builder().operType(OperType.BRAND_ID_LIST).slice(2).build());
        map.put("/v2/CarPrice/Brand_GetBrandById.ashx", Param.builder().operType(OperType.BRAND_ID).build());
        map.put("/v1/carprice/series_namebybrandid.ashx", Param.builder().operType(OperType.BRAND_ID).build());
        map.put("/v3/CarPrice/SpecificConfig_GetListBySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v2/Base/Fct_GetAllFcts.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        //九期
        map.put("/v1/carshow/showpic_infobylevellist.ashx", Param.builder().callBack(new ShowPicInfoByLevelListCallBack()).operType(OperType.SHOW_ID).build());
        //carddl 迁移
        map.put("/v2/Base/Brand_GetAllBrands.ashx", Param.builder().operType(OperType.ID).build());
        map.put("/v1/javascript/brand.ashx", Param.builder().operType(OperType.STATE).callBack(new BrandCallBack()).build());
        map.put("/v2/Base/Series_GetAllSeries.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/carprice/series_getallname.ashx", Param.builder().operType(OperType.NO_PARAM).build());
        map.put("/v1/App/Spec_SpeclistBySeriesIds.ashx", Param.builder().operType(OperType.SERIES_ID_List).callBack(new SpecConfigBySeriesIdCallBack()).build());

        String[] s = {"root.result.configtypeitems[*].configitems[*].pnid"};
        map.put("/v1/car/config_listofyearbyspecid.ashx", Param.builder().operType(OperType.SPEC_ID).exclude(s).callBack(new ConfigListOfYearBySpecIdCallBack()).build());
        map.put("/v1/car/config_listbyseriesId.ashx", Param.builder().operType(OperType.SERIES_ID).exclude(s).callBack(new ConfigListBySeriesIdCallBack()).build());

        String[] exclude = {"root.result.paramtypeitems[*].paramitems[*].pnid"};
        map.put("/v1/car/spec_paramlistbyspecId.ashx", Param.builder().exclude(exclude).operType(OperType.SPEC_ID).build());
        map.put("/v1/car/spec_paramlistbyseriesid.ashx", Param.builder().exclude(exclude).operType(OperType.SERIES_ID).build());


        map.put("/v1/carshow/showpic_infobyshowidserieid.ashx", Param.builder().callBack(new ShowPicInfoByShowIdSerieIdCallBack()).operType(OperType.NO_PARAM).hasState(false).slice(0).urlStrategy(1).build());
        map.put("/v1/carshow/show_seriesbyfctid.ashx", Param.builder().operType(OperType.FCT_ID).build());

        return map;
    }

    @Override
    public boolean isSupport() {
        return true;
    }
}
