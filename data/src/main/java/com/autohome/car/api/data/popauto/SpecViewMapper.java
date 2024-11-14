package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.SpecViewProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Date;
import java.util.List;
    
@Mapper
public interface SpecViewMapper {

    /**
     * 获取车系下最高最低油耗
     *
     * @return
     */
    @Select("select SeriesId,ISNULL(MAX(CONVERT(float ,OfficialOilWear)),0) AS MaxFuelConsumption,ISNULL(MIN(CONVERT(float ,OfficialOilWear)),0) MinFuelConsumption \n" +
            "from (\n" +
            "    select seriesid,\n" +
            "    case when specOilOffical>0 and SpecOilWearWLTC>0 then SpecOilWearWLTC\n" +
            "         when specOilOffical>0 then specOilOffical\n" +
            "         when SpecOilWearWLTC>0 then SpecOilWearWLTC else 0 end AS OfficialOilWear  \n" +
            "    from specview with(nolock) \n" +
            ") as T where OfficialOilWear>0\n" +
            "GROUP BY SeriesId\n" +
            "UNION ALL\n" +
            "select SeriesId,MAX(CONVERT(float ,OfficialOilWear)) AS MaxFuelConsumption,MIN(CONVERT(float ,OfficialOilWear)) MinFuelConsumption from (\n" +
            "select seriesid, OfficalOil AS OfficialOilWear FROM CV_SpecView with(nolock) where OfficalOil >0) as T GROUP  by seriesid;")
    List<SeriesFuelConsumptionEntity> getAllSeriesFuelConsumption();

    @Select("SELECT ISNULL(MAX(OfficialOilWear),0) AS MaxFuelConsumption,ISNULL(MIN(OfficialOilWear),0) AS MinFuelConsumption\n" +
            "FROM(\n" +
            "    SELECT CASE WHEN specOilOffical > 0 AND ISNULL(SpecOilWearWLTC,0) <= 0 THEN specOilOffical ELSE SpecOilWearWLTC END AS OfficialOilWear\n" +
            "    FROM(\n" +
            "        SELECT specOilOffical AS specOilOffical,SpecOilWearWLTC AS SpecOilWearWLTC \n" +
            "          FROM specview WITH(NOLOCK)\n" +
            "         WHERE seriesid = #{seriesId}\n" +
            "    ) AS T\n" +
            ")AS T\n" +
            "WHERE OfficialOilWear > 0")
    SeriesFuelConsumptionEntity getSeriesFuelConsumption(int seriesId);

    @Select("SELECT #{seriesId} as seriesId\n" +
            "\t  ,ISNULL(MAX(OfficalOil),0) AS MaxFuelConsumption\n" +
            "\t  ,ISNULL(MIN(OfficalOil),0) AS MinFuelConsumption\n" +
            "  FROM CV_SpecView WITH(NOLOCK) \n" +
            " WHERE OfficalOil >0 AND seriesId = #{seriesId}")
    SeriesFuelConsumptionEntity getCVSeriesFuelConsumption(int seriesId);


    /**
     * 获取车系和年代款纯电动信息,车系和年代款下纯电动的数量
     *
     * @return
     */
    @Select("select distinct  seriesId,specid,SpecState,syearId, case fueltypedetail when 4 then 1 else 0 end as pureelectric  \n" +
            "from SpecView  with(nolock)  \n" +
            "where specIsshow = 1\n" +
            "union all  \n" +
            "select distinct seriesid ,specid ,specState ,syearId\n" +
            "   ,case when  (select top 1 itemvalue from CV_SpecParamView with(nolock) where item = '基本参数' and name = '能源类型'  )='纯电动' then 1 else 0 end as pureelectric\n" +
            "from CV_SpecView as A with(nolock)  \n" +
            "where A.SpecIsShow=1;")
    List<SeriesElectricEntity> getAllSeriesElectricBase();

    @Select("select distinct seriesId,specid,SpecState,syearId, case fueltypedetail when 4 then 1 else 0 end as pureelectric  \n" +
            "from SpecView  with(nolock)  \n" +
            "where specIsshow = 1 AND seriesid = #{seriesId}")
    List<SeriesElectricEntity> getSeriesElectricBase(int seriesId);


    @Select("select distinct  seriesid,specid,specState,syearId,\n" +
            "case when  (select top 1 itemvalue from CV_SpecParamView with(nolock) where item = '基本参数' and name = '能源类型'  )='纯电动' then 1 else 0 end as pureelectric\n" +
            "from CV_SpecView as A with(nolock)  \n" +
            "where A.SpecIsShow=1 AND seriesid =  #{seriesId};")
    List<SeriesElectricEntity> getCVSeriesElectricBase(int seriesId);

    @Select("select syearId,seriesId,specId, '-' as ChargeTime, \n" +
            "    ISNULL((select top 1  isnull(valu,0) as kv from specconfig where  specconfig . specId =A.specId and item='电动机' and name = '电动机总功率(kW)'  ), '-') as ElectricKW,\n" +
            "    ISNULL((select top 1  isnull(valu,0) as kv from specconfig where  specconfig . specId =A.specId and  name = '电池能量(kWh)'  ), '-') as ElectricRONGLIANG,\n" +
            "    ISNULL((select top 1  isnull(valu,0) as kv from specconfig where  specconfig . specId =A.specId and item='电动机' and  name in ( 'NEDC纯电续航里程(km)' ,'WLTP纯电续航里程(km)','CLTC纯电续航里程(km)','EPA纯电续航里程(km)' ) and len(specconfig.valu)>0 and specconfig.valu<> '-' and specconfig.valu<> '0'), '-') as ElectricMotorMileage,                    \n" +
            "    SpecState\n" +
            "from SpecView as A with(Nolock)  where A.fueltypedetail in (4,5,6)\n" +
            "union all \n" +
            "select   A.syearid ,  A.SeriesId,A.SpecId,  \n" +
            "    ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '电池充电时间'  ),'-') as ChargeTime, \n" +
            "    ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '电动机总功率(kW)'),'-') as ElectricKW,\n" +
            "    ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '电池容量(kWh)'  ),'-') as ElectricRONGLIANG,\n" +
            "    ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '工信部续航里程(km)'),'-') as ElectricMotorMileage,\n" +
            "    specState\n" +
            "from CV_SpecView  as A with(nolock) where A.fueltype  in (4,7);")
    List<ElectricParamEntity> getAllElectricParam();

    @Select("select syearId,seriesId,specId, '-' as ChargeTime,SpecState, \n" +
            "ISNULL((select top 1  isnull(valu,0) as kv from specconfig where  specconfig . specId =A.specId and item='电动机' and name = '电动机总功率(kW)'  ), '-') as ElectricKW,\n" +
            "ISNULL((select top 1  isnull(valu,0) as kv from specconfig where  specconfig . specId =A.specId and  name = '电池能量(kWh)'  ), '-') as ElectricRONGLIANG,\n" +
            "ISNULL((select top 1  isnull(valu,0) as kv from specconfig where  specconfig . specId =A.specId and item='电动机' and  name in ( 'NEDC纯电续航里程(km)' ,'WLTP纯电续航里程(km)','CLTC纯电续航里程(km)','EPA纯电续航里程(km)' ) and len(specconfig.valu)>0 and specconfig.valu<> '-' and specconfig.valu<> '0'), '-') as ElectricMotorMileage\n" +
            "\n" +
            "from SpecView as A with(Nolock)  \n" +
            "where A.fueltypedetail in (4,5,6) AND seriesId = #{seriesId}")
    List<ElectricParamEntity> getElectricParam(int seriesId);

    @Select("select A.syearid ,  A.SeriesId,A.SpecId,  \n" +
            "ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '电池充电时间'  ),'-') as ChargeTime, \n" +
            "ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '电动机总功率(kW)'),'-') as ElectricKW,\n" +
            "ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '电池容量(kWh)'  ),'-') as ElectricRONGLIANG,\n" +
            "ISNULL((select top 1  isnull(ItemValue,0) as chargetime from CV_SpecParamView where  CV_SpecParamView . specId =A.specId and item='电动机' and name = '工信部续航里程(km)'),'-') as ElectricMotorMileage,\n" +
            "specState\n" +
            "from CV_SpecView  as A with(nolock) where A.fueltype  in (4,7)  AND seriesId = #{seriesId}")
    List<ElectricParamEntity> getCVElectricParam(int seriesId);


    @SelectProvider(value = SpecViewProvider.class, method = "getAllSpecs")
    List<SpecViewAndPicEntity> getAllSpecs();

    @SelectProvider(value = SpecViewProvider.class, method = "getSpecs")
    List<SpecViewAndPicEntity> getSpecs(int seriesId);

    @SelectProvider(value = SpecViewProvider.class, method = "getAllCvSpecs")
    List<SpecViewAndPicEntity> getAllCvSpecs();

    @SelectProvider(value = SpecViewProvider.class, method = "getCvSpecs")
    List<SpecViewAndPicEntity> getCvSpecs(int seriesId);

    @Select("SELECT SpecId,SeriesId,SpecState,SpecIsImage,CASE SpecId WHEN #{specId} THEN 0 ELSE 1 END OrderBy,CASE WHEN SpecState>=20 AND SpecState<=30 THEN 0 ELSE 1 END AS SpecOrder \n" +
            "FROM SpecView WITH(NOLOCK) \n" +
            "WHERE  SyearId=(SELECT syearId FROM SpecView WITH(NOLOCK)  WHERE specId=#{specId} ) \n" +
            "AND ((SpecState<=30 AND specIsshow=1) or (SpecState=40 AND specIsImage=0)) \n" +
            " ORDER BY OrderBy,SpecOrder,specOrdercls,specid DESC;")
    List<SpecStateEntity> getSpecListBySpecId(int specId);

    @Select("SELECT SpecId,SeriesId,SpecState,0 SpecIsImage,CASE SpecId WHEN #{specId} THEN 0 ELSE 1 END OrderBy,CASE WHEN SpecState>=20 AND SpecState<=30 THEN 0 ELSE 1 END AS SpecOrder \n" +
            "FROM CV_SpecView WITH(NOLOCK) \n" +
            "WHERE  SyearId=(SELECT syearId FROM CV_SpecView WITH(NOLOCK)  WHERE specId=#{specId} ) \n" +
            "AND ((SpecState>=10 AND SpecState<=30 AND specIsshow=1) or SpecState=40) \n" +
            " ORDER BY OrderBy,SpecOrder,Orders,specid DESC;")
    List<SpecStateEntity> getCvSpecListBySpecId(int specId);

    @SelectProvider(value = SpecViewProvider.class, method = "getSpecListBySeriesYear")
    List<SpecStateEntity> getSpecListByYear(int seriesId, int yearId);

    @SelectProvider(value = SpecViewProvider.class, method = "getCvSpecListBySeriesYear")
    List<SpecStateEntity> getCvSpecListByYear(int seriesId, int yearId);

    @SelectProvider(value = SpecViewProvider.class, method = "getSpecListBySeriesId")
    List<SpecStateEntity> getSpecListBySeriesId(int seriesId, boolean isCv);

    @SelectProvider(value = SpecViewProvider.class,method = "getCv")
    SpecViewEntity getCv(int specId);

    @SelectProvider(value = SpecViewProvider.class,method = "get")
    SpecViewEntity get(int specId);

    @SelectProvider(value = SpecViewProvider.class,method = "getAll")
    List<SpecViewEntity> getAll();

    @SelectProvider(value = SpecViewProvider.class,method = "getBase")
    SpecBaseEntity getBase(int specId);

    @SelectProvider(value = SpecViewProvider.class, method = "getBaseBySeriesId")
    List<SpecBaseEntity> getBaseBySeriesId(int seriesId,boolean isCV);

    @SelectProvider(value = SpecViewProvider.class, method = "getAllBase")
    List<SpecBaseEntity> getAllBase();

    @Select("SELECT SpecId as specId,specIsImage,specOrdercls,  Syearid as sYearId,Syear as sYear,BrandFirstLetter as brandFirstLetter,FctFirstLetter as fctFirstLetter, seriesFirstLetter as seriesFirstLetter,specQuality as specQuality,SpecState as specState " +
            ",brandId, specDrivingMode ,0 as driveForm,CONVERT(VARCHAR, specStructureSeat) as seats \n" +
            " FROM SpecView WITH(NOLOCK) \n" +
            " UNION ALL\n" +
            " SELECT SpecId as specId,0 AS specIsImage,Orders AS specOrdercls,Syearid as sYearId,Syear as sYear, BrandFirstLetter as brandFirstLetter,FctFirstLetter as fctFirstLetter, seriesFirstLetter as seriesFirstLetter,quality as specQuality,SpecState as specState" +
            ",brandId, '' as specDrivingMode ,driveForm,seats \n" +
            " FROM CV_SpecView WITH(NOLOCK)")
    List<SpecCVViewEntity> getSpecViewAll();

    @Select("SELECT SpecId as specId,specIsImage,specOrdercls,  Syearid as sYearId,Syear as sYear,BrandFirstLetter as brandFirstLetter,FctFirstLetter as fctFirstLetter, seriesFirstLetter as seriesFirstLetter,specQuality as specQuality,SpecState as specState" +
            ",brandId, specDrivingMode ,0 as driveForm,CONVERT(VARCHAR, specStructureSeat) as seats \n" +
            " FROM SpecView WITH(NOLOCK) where SpecId = #{specId} \n" +
            " UNION ALL\n" +
            " SELECT SpecId as specId,0 AS specIsImage,Orders AS specOrdercls,Syearid as sYearId,Syear as sYear, BrandFirstLetter as brandFirstLetter,FctFirstLetter as fctFirstLetter, seriesFirstLetter as seriesFirstLetter,quality as specQuality,SpecState as specState" +
            ",brandId, '' as specDrivingMode ,driveForm,seats \n" +
            " FROM CV_SpecView WITH(NOLOCK) where SpecId = #{specId} ")
    SpecCVViewEntity getSpecViewBySpecId(int specId);


    @SelectProvider(value = SpecViewProvider.class, method = "getAllSeriesState")
    List<KeyValueDto<Integer, Integer>> getAllSeriesState();

    @SelectProvider(value = SpecViewProvider.class, method = "getSeriesState")
    List<KeyValueDto<Integer, Integer>> getSeriesState(int seriesId);


    @SelectProvider(value = SpecViewProvider.class, method = "getCVSeriesState")
    List<KeyValueDto<Integer, Integer>> getCVSeriesState(int seriesId);


    @Select("" +
            "select parent as SeriesId  from spec_new  with(nolock)  where  SpecState<=30 and isshow = 1 \n" +
            "UNION ALL \n" +
            "select distinct A.SeriesId from  CV_SpecView as A with(nolock) where A.SpecState >= 10 and A.SpecState<=30   and A.SpecIsShow = 1")
    List<Integer> getAllSeriesParamIsShow();

    @Select("select Count(1) as tc  from spec_new  with(nolock)  where SpecState<=30 and isshow = 1 AND parent = #{seriesId}")
    int getSeriesParamIsShow(int seriesId);

    @Select("select Count(1) as tc  from  CV_SpecView as A with(nolock) where A.SpecState >= 10 and A.SpecState<=30  and A.SpecIsShow = 1 AND SeriesId = #{seriesId}")
    int getCVSeriesParamIsShow(int seriesId);

    @Select("SELECT count(*)  FROM spec_new  WITH(NOLOCK)  WHERE parent = #{seriesId} and IsImageSpec = 0 AND isshow = 1")
    int getSeriesParamNewIsShowBySeriesId(int seriesId);

    @Select("select count(*) from  CV_Spec as A with(nolock) where A.SeriesId = #{seriesId} and A.ParamShow = 1")
    int getCvSeriesParamNewIsShowBySeriesId(int seriesId);


    @Select("select distinct parent as SeriesId  from spec_new  with(nolock)  where  IsImageSpec = 0 and isshow = 1 \n" +
            "UNION ALL \n" +
            "select distinct A.SeriesId from  CV_Spec as A with(nolock) where  A.ParamShow = 1")
    List<Integer> getAllSeriesParamNewIsShow();

    @Select("select COUNT(1) from( \n" +
            "            select  COUNT(*) as specSum,  \n" +
            "            SUM(case isimagespec when 1 then 1 else 0  end)  as imgspecnum, \n" +
            "            SUM(case SpecState when 40 then 1 else 0 end ) as stopSpecNum\n" +
            "            from spec_new with(nolock)\n" +
            "            where parent = #{seriesId}\n" +
            ")as TT where  specSum = imgspecnum and specSum = stopSpecNum ;")
    int isSeriesIsImgSpec(int seriesId);

    @Select("select distinct parent from( \n" +
            "            select parent\n" +
            "                  ,COUNT(*) as specSum\n" +
            "                  ,SUM(case isimagespec when 1 then 1 else 0  end)  as imgspecnum\n" +
            "                  ,SUM(case SpecState when 40 then 1 else 0 end ) as stopSpecNum\n" +
            "             from spec_new with(nolock)\n" +
            "         GROUP BY parent\n" +
            ")as TT where  specSum = imgspecnum and specSum = stopSpecNum ;")
    List<Integer> getALLSeriesIsImgSpec();

    @Select("WITH SPECIDS AS(\n" +
            "\tSELECT specId FROM SpecView WITH(NOLOCK) WHERE seriesId = #{seriesId}\n" +
            ")\n" +
            "SELECT COUNT(1) FROM [popautoMaintaining].dbo.Spec_Mtn_Info A WITH(NOLOCK) INNER JOIN SPECIDS B ON A.SpecId = B.specId where A.IsShow = 1")
    int existmaintain(int seriesId);


    @Select("with spec as (\n" +
            "select distinct SpecId  from [popautoMaintaining] .dbo.Spec_Mtn_Info  with(nolock)  where IsShow = 1\n" +
            ") \n" +
            "select  distinct seriesId as Name from spec as A with(nolock) inner join SpecView as B with(nolock) on A.SpecId = B.specId ")
    List<Integer> existmaintainSeriesIds();

    @Select("SELECT top 1 parent FROM spec_new  WITH(NOLOCK)  WHERE SpecState=10 AND booked = 1 AND parent = #{seriesId}")
    Integer seriesContainBookedSpec(int seriesId);


    @Select("SELECT parent FROM spec_new  WITH(NOLOCK)  WHERE SpecState=10 AND booked = 1")
    List<Integer> seriesAllContainBookedSpec();

    @SelectProvider(value = SpecViewProvider.class,method = "allSpecShowCount")
    List<KeyValueDto<Integer,Integer>> allSpecShowCount(int seriesState);

    @Select("SELECT \n" +
            "(SELECT ISNULL(SUM(CONVERT(INT,isshow)),0) FROM spec_new WITH (nolock) WHERE specstate >= 20 AND specstate<= 30 AND parent = #{seriesId}) \n" +
            "+\n" +
            "(SELECT ISNULL(SUM(CONVERT(INT,SpecIsShow)),0) FROM cv_specview WITH (nolock) where specState >=20 and specState<=30 AND seriesId = #{seriesId}) ")
    Integer specShowCount(int seriesId,int seriesState);


    @SelectProvider(value = SpecViewProvider.class,method = "getSpecInfoBySpecId")
    SpecViewEntity getSpecInfoBySpecId(int specid);

    @SelectProvider(value = SpecViewProvider.class,method = "getSpecBySeriesId")
    List<SpecViewEntity> getSpecBySeriesId(int seriesId,boolean isCV,String where);

    @SelectProvider(value = SpecViewProvider.class,method = "getAllSpecBySeriesId")
    List<SpecViewEntity> getAllSpecBySeriesId(int seriesId,boolean isCV);

    @SelectProvider(value = SpecViewProvider.class,method = "getAllSpecInfoBySpecId")
    List<SpecViewEntity> getAllSpecInfoBySpecId();

    @SelectProvider(value = SpecViewProvider.class, method = "getAllElectroTotalKW")
    List<KeyValueDto<Integer, String>> getAllElectroTotalKW();

    @SelectProvider(value = SpecViewProvider.class, method = "getElectroTotalKW")
    KeyValueDto<Integer, String> getElectroTotalKW(int specid);

    /**
     * 车型Logo
     */
    @SelectProvider(value = SpecViewProvider.class, method = "getAllSpecLogo")
    List<KeyValueDto<Integer, String>> getAllSpecLogo();
    @SelectProvider(value = SpecViewProvider.class, method = "getSpecLogoBySpecId")
    KeyValueDto<Integer, String> getSpecLogoBySpecId(Integer specId);

    @SelectProvider(value = SpecViewProvider.class,method = "isHaveMaintains")
    KeyValueDto<Integer, Integer> isHaveMaintains(int specId);

    @SelectProvider(value = SpecViewProvider.class,method = "isAllHaveMaintains")
    List<KeyValueDto<Integer, Integer>> isAllHaveMaintains();

    @SelectProvider(value = SpecViewProvider.class,method = "getSpecInfoBySeriesId")
    List<SpecViewEntity> getSpecInfoBySeriesId(int seriesId,boolean isCV);

    @Select("SELECT distinct ISNULL(SyearId,0) as yearId FROM SpecView WITH(NOLOCK) UNION SELECT distinct ISNULL(SyearId,0) AS SyearId FROM CV_SpecView WITH(NOLOCK)")
    List<Integer> getAllSpecYearIds();

    @SelectProvider(value = SpecViewProvider.class,method = "getSpecByYearId")
    List<SpecYearEntity> getSpecByYearId(int yearId);
    @SelectProvider(value = SpecViewProvider.class,method = "getSpecItemsBySeries")
    List<SpecViewEntity> getSpecItemsBySeries(int seriesId,boolean isCV);

    @Select("SELECT  A.SpecId,ISNULL(A.SyearId,0) AS SyearId,ISNULL(A.SeriesId,0) AS SeriesId,A.SpecState,A.SpecPrice,A.SpecStructureType,A.Leveled AS LevelId,\n" +
            "A.SpecTransmissionType,A.SpecDisplacement,A.FlowMode,B.PicNumber ,A.SeriesIsShow\n" +
            "FROM SpecView AS A WITH(NOLOCK)\n" +
            "INNER JOIN CarSpecPictureStatistics AS B WITH(NOLOCK) ON A.SpecId=B.SpecId ")
    List<SpecViewAndPicEntity> getAllSpecYearStateList();

    @Select("SELECT  A.SpecId,ISNULL(A.SyearId,0) AS SyearId,ISNULL(A.SeriesId,0) AS SeriesId,A.SpecState,A.MinPrice,A.MaxPrice,A.StructType AS SpecStructureType,A.LevelId,\n" +
            "GearBoxType AS SpecTransmissionType,A.DeCapacity AS SpecDisplacement,A.FlowMode,B.PicNumber , ISNULL(A.seriesIsshow,0) as SeriesIsShow\n" +
            "FROM CV_SpecView AS A WITH(NOLOCK)\n" +
            "INNER JOIN CarSpecPictureStatistics AS B WITH(NOLOCK) ON A.SpecId=B.SpecId")
    List<SpecViewAndPicEntity> getAllCVSpecYearStateList();

    @Select("select distinct  seriesId,specid,SpecState,syearId, case fueltypedetail when 4 then 1 else 0 end as pureelectric  from SpecView  with(nolock)  where specIsshow = 1\n" +
            "union all \n" +
            "select distinct  seriesid,specid,specState,syearId,\n" +
            "case when  (select top 1 itemvalue from CV_SpecParamView with(nolock) where item = '基本参数' and name = '能源类型'  )='纯电动' then 1 else 0 end as pureelectric\n" +
            "from CV_SpecView as A with(nolock)  where A.SpecIsShow=1")
    List<ElectricParamEntity> getAllSeriesYearStateElectricBase();

    @Select("select SeriesId as [key],SyearId as [value] from SpecView WITH(NOLOCK) group by SeriesId,SyearId order by SeriesId")
    List<KeyValueDto<Integer,Integer>> getAllSeriesYearIds();


    @Select("SELECT Row_Number() OVER(ORDER BY SeriesId,OrderBy DESC,Syear DESC) AS rInd,SeriesId as id,SyearId as yId,Syear,SpecState as state,specIsImage as sImage\n" +
            "  FROM (\tSELECT SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,\n" +
            "  CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999 ELSE  SpecState END AS OrderBy ,specIsImage\n" +
            "  FROM SpecView WITH(NOLOCK)\n" +
            "  UNION\n" +
            "  SELECT SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,\n" +
            "  CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999 ELSE  SpecState END AS OrderBy ,0 AS specIsImage\n" +
            "  FROM CV_SpecView WITH(NOLOCK)) AS Temp\n" +
            "  WHERE SyearId>0 and seriesid = #{seriesId} order by rInd")
    List<SpecYearEntity> getSYearBySeriesId(int seriesId);

    @Select("SELECT Row_Number() OVER(ORDER BY SeriesId,OrderBy DESC,specOrdercls ) AS rInd,SpecId,SeriesId as id,SyearId as yId,Syear,SpecState as state,specIsImage as sImage\n" +
            "   FROM (\tSELECT SpecId,SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,specOrdercls,specIsImage,\n" +
            "   CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999  WHEN SpecState<=10 THEN 8888  ELSE  Syear END AS OrderBy\n" +
            "   FROM SpecView WITH(NOLOCK)\n" +
            "   UNION\n" +
            "   SELECT SpecId,SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,Orders AS specOrdercls,0 AS specIsImage,\n" +
            "   CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999  WHEN SpecState<=10 THEN 8888 ELSE  Syear END AS OrderBy\n" +
            "   FROM CV_SpecView WITH(NOLOCK)) AS Temp\n" +
            "   WHERE SyearId>0 and seriesid = #{seriesId} order by Syear DESC,rInd ASC")
    List<SpecSeriesYearEntity> getSpecYearBySeriesIdOrdYear(int seriesId);

    @Select("select  brandId,fueltypedetail, case specIspublic when 1 then 1 else 0 end as specIspublic ,isnull(endurancemileage,0)as endurancemileage, isnull(officialFastChargetime,0) as officialFastChargetime from SpecView with(nolock) \n" +
            " where fueltypedetail >=4 and fueltypedetail<=7 and specIsshow = 1\n" +
            " union \n" +
            " select  brandId,fueltype as fueltypedetail  ,case specState when 20  then 1 when 30 then 1 else 0 end as specispublic,isnull(endurancemileage,0)as endurancemileage, isnull(officialFastChargetime,0) as officialFastChargetime from CV_SpecView with(nolock) \n" +
            " where fueltype >=4 and fueltype<=7 and SpecIsShow  =1 ")
    List<SpecViewBrandEntity> getAllSpecViewBrand();

    @SelectProvider(value = SpecViewProvider.class,method = "getSpecBySeries")
    List<SpecViewEntity> getSpecBySeries(int seriesId,boolean isCV);

    @Select("SELECT Row_Number() OVER(ORDER BY BFirstLetter,BrandName) AS RankIndex,BrandId,BFirstLetter,IsCV,SpecState,specIsImage\n" +
            "                                        FROM (SELECT brand.id AS BrandId,brand.name AS BrandName,brand.FirstLetter AS BFirstLetter,spec.SpecState,spec.IsCV,specIsImage\n" +
            "\t                                              FROM(SELECT BrandId,SpecState,1 AS IsCV,specIsImage FROM SpecView WITH(NOLOCK)\n" +
            "\t\t\t                                                UNION\n" +
            "\t\t\t                                                SELECT BrandId,SpecState,2 AS IsCV,0 AS specIsImage FROM CV_SpecView WITH(NOLOCK)) AS spec \n" +
            "\t                                              INNER JOIN [group] brand WITH(NOLOCK) ON spec.brandid = brand.id) AS Temp")
    List<BrandViewEntity> getAllBrandItems();

    @Select("SELECT Row_Number() OVER(ORDER BY BFirstLetter,BrandName) AS RankIndex,BrandId,BFirstLetter,IsCV,SpecState,specIsImage\n" +
            "                            FROM (\n" +
            "\t                            SELECT brand.id AS BrandId,brand.name AS BrandName,brand.FirstLetter AS BFirstLetter,spec.SpecState,spec.IsCV,specIsImage\n" +
            "                            FROM(\n" +
            "\t                            SELECT BrandId,SpecState,1 AS IsCV,specIsImage \n" +
            "\t                            FROM SpecView  WITH(NOLOCK) where fueltype=4\n" +
            "\t                            ) AS spec \n" +
            "                            INNER JOIN [group] brand WITH(NOLOCK) ON spec.brandid = brand.id) AS Temp")
    List<BrandViewEntity> getAllElectricBrandItems();

    @Select("SELECT seriesId,seriesPlace,specId,SpecName, specimg,specPrice as minprice,\n" +
            "specPrice as maxprice,fueltype,SpecState,leveled as levelId,levelName,seriesName\n" +
            "FROM specview as A WITH(NOLOCK) \n" +
            "WHERE specIsImage = 0\n" +
            "UNION ALL\n" +
            "SELECT seriesId, case seriesPlace when 1 then '自主' when 2 then '合资' when 3 then '进口' else  '' end as seriesPlace ,\n" +
            "specId,SpecName,A.Img as specimg,MinPrice,MaxPrice,fueltype,SpecState,levelId ,B.name as levelName, C.name as seriesName\n" +
            "FROM CV_SpecView as A WITH(NOLOCK)\n" +
            "LEFT JOIN car_spec_jb as B ON A.levelId = B.Id\n" +
            "LEFT JOIN [brands] as C ON A.seriesId = C.Id ")
    List<SpecSearchEntity> getAllSpecBaseInfo();

    @Select("select series_id as [key],id as [value] from spec_year")
    List<KeyValueDto<Integer, Integer>> getSpecYear();

    @Select("select seriesId as [key] ,replace(REPLACE(img,'~',''),'/l_','/') as [value] from (select seriesid,img from( select parent as seriesid,img,ROW_NUMBER() OVER(PARTITION BY parent ORDER BY id DESC) AS RN from spec_new with(nolock) where SpecState = 40 and parent = #{seriesId} and img  != '~/image/gp_default.gif')as T where T.RN=1\n" +
            "      union all select seriesid as id,img name from ( select seriesid,Img,ROW_NUMBER() over(PARTITION BY seriesid order by specid desc) as RN from CV_SpecView with(nolock) where specState = 40 and seriesId= #{seriesId} )as CV where CV.RN=1) as t")
    KeyValueDto<Integer,String> getSeriesStopLogoBySeriesId(int seriesId);

    @Select("select seriesId as [key] ,replace(REPLACE(img,'~',''),'/l_','/') as [value] from (select seriesid,img from( select parent as seriesid,img,ROW_NUMBER() OVER(PARTITION BY parent ORDER BY id DESC) AS RN from spec_new with(nolock) where SpecState = 40 and img  != '~/image/gp_default.gif')as T where T.RN=1\n" +
            "      union all select seriesid as id,img name from ( select seriesid,Img,ROW_NUMBER() over(PARTITION BY seriesid order by specid desc) as RN from CV_SpecView with(nolock) where specState = 40)as CV where CV.RN=1) as t")
    List<KeyValueDto<Integer,String>> getSeriesStopLogoAll();

    @Select("SELECT DISTINCT specid FROM (\n" +
            "                            SELECT DISTINCT specid FROM car_spec_photo WITH(NOLOCK) WHERE pointlocatinid>0 AND  dtime>= #{startDate} AND dtime< #{endDate}\n" +
            "                            UNION ALL\n" +
            "                            SELECT DISTINCT specid FROM ConfigItem_RelationSpecPic WITH(NOLOCK) WHERE  created_stime>= #{startDate} AND created_stime< #{endDate}\n" +
            "                            UNION ALL\n" +
            "                            SELECT DISTINCT specid FROM car_spec_photo WITH(NOLOCK) WHERE IsDelete = 1 AND  dtime>= #{startDate} AND dtime< #{endDate}\n" +
            "                            UNION ALL\n" +
            "                            select distinct sourcespecid AS specid FROM log_picchangespec  WITH(NOLOCK) WHERE Created_Stime >= #{startDate} AND Created_Stime< #{endDate}\n" +
            "                            UNION ALL\n" +
            "                            SELECT DISTINCT targetspecid AS specid FROM log_picchangespec  WITH(NOLOCK) WHERE Created_Stime >= #{startDate} AND Created_Stime< #{endDate}\n" +
            "                            ) AS T ")
    List<Integer> getSpecListByDate(Date startDate,Date endDate);

    @Select("     select SeriesId as name from ( select parent as seriesid,ROW_NUMBER() over (partition by parent order by TimeMarket desc) as RN from spec_new with(nolock) where SpecState = 20 and DATEDIFF(DAY,TimeMarket, GETDATE())\n" +
            "      <=30 ) as TT where RN=1 \n" +
            "      union all \n" +
            "      select SeriesId as name from (select B.SeriesId, ROW_NUMBER() over( partition by B.seriesid order by TimeMarket desc) as RN from CV_Spec as A with(nolock) \n" +
            "      inner join CV_SpecView as B with(nolock) on A.id = B.specId where A.SpecState = 20 and DATEDIFF(DAY,TimeMarket, GETDATE())<=30) as TT where RN=1")
    @AutoCache(expireIn = 30)
    List<Integer> getNewSeriesList();

    @AutoCache(expireIn = 120)
    @Select("select distinct  case　fueltypedetail when 4 then 1 when 5 then 2 when 6 then 3 end  as fueltype,endurancemileage as licheng ,seriesId from SpecView with(nolock) where fueltypedetail >=4 and fueltypedetail<=6")
    List<ElectricSpecBaseEntity> getElectricSpecBaseAll();

    @AutoCache(expireIn = 120)
    @Select("SELECT sps.SeriesId,sps.SpecId,t.taxType FROM SpecPriceSellView sps with(nolock) left join (select id,CASE WHEN specstate>=10 and specstate<=40 and IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS taxType from spec_new\n" +
            "UNION ALL\n" +
            "SELECT id,CASE WHEN specstate>=10 and specstate<=40 and IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS taxType from CV_Spec) t on t.id = sps.specid \n" +
            " WHERE sps.FuelType=4\n" +
            " UNION ALL\n" +
            "SELECT sps.SeriesId,sps.SpecId,t.taxType FROM SpecPriceWaitSellView sps with(nolock) left join (select id,CASE WHEN specstate>=10 and specstate<=40 and IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS taxType from spec_new\n" +
            "UNION ALL\n" +
            "SELECT id,CASE WHEN specstate>=10 and specstate<=40 and IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS taxType from CV_Spec) t on t.id = sps.specid \n" +
            " WHERE sps.FuelType=4")
    List<SpecSellEntity> getElectricSellAll();
}
