package com.autohome.car.api.data.popauto.providers;


import com.autohome.car.api.common.BaseConfig.Spec;

import java.util.Objects;

public class SpecViewProvider {

    final static String CvBaseSql = "" +
            "SELECT id\n" +
            "    ,SpecName \n" +
            "    ,seriesId\n" +
            "    ,minPrice as specMinPrice\n" +
            "    ,maxPrice as specMaxPrice\n" +
            "    ,TimeMarket as timeMarket\n" +
            "    ,REPLACE(speclogo,'~','') AS logo\n" +
            "    ,stopTime\n" +
            "    ,CASE WHEN SpecState <=30 AND ParamShow=0 THEN 0 ELSE 1 END AS IsSpecParamIsShow\n" +
            "    ,CASE WHEN IsPreferential=1 THEN 1 ELSE 0 END AS IsPreferential\n" +
            "    ,CASE WHEN specstate>=10 and specstate<=40 and IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS SpecTaxType\n" +
            "    ,CASE WHEN booked=1 THEN 1 ELSE 0 END AS IsBooked\n" +
            "    ,TimeMarket\n"+
            "    ,SpecState\n"+
            "    ,0 AS isclassic,editTime \n"+
            "    ,CASE WHEN SpecState = 20 and datediff(day,timemarket,GETDATE())<=30 THEN 1 ELSE 0 END AS IsNew\n" +
            "  FROM CV_Spec WITH(NOLOCK)\n";

    final static String BaseSql = "" +
            "SELECT id \n" +
            "    ,replace(replace(model,'''',''),';','') AS SpecName \n" +
            "    ,parent AS seriesId\n" +
            "    ,spec_price AS specMinPrice\n" +
            "    ,spec_price AS specMaxPrice\n" +
            "    ,TimeMarket as timeMarket\n" +
            "    ,REPLACE(REPLACE(Img,'~',''),'/l_','/') AS logo\n" +
            "    ,stopTime\n" +
            "    ,CASE WHEN SpecState <=30 AND isshow=0 THEN 0 ELSE 1 END AS IsSpecParamIsShow\n" +
            "    ,CASE WHEN IsPreferential=1 THEN 1 ELSE 0 END AS IsPreferential\n" +
            "    ,CASE WHEN specstate>=10 and specstate<=40 and IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS SpecTaxType\n" +
            "    ,CASE WHEN booked=1 THEN 1 ELSE 0 END AS IsBooked\n" +
            "    ,TimeMarket\n"+
            "    ,SpecState\n"+
            "    ,isclassic,editTime \n"+
            "    ,CASE WHEN SpecState = 20 and datediff(day,timemarket,GETDATE())<=30 THEN 1 ELSE 0 END AS IsNew\n" +
            "  FROM [spec_new] WITH(NOLOCK) \n";


    public String getBase(int specId) {
        if (Spec.isCvSpec(specId)) {
            return CvBaseSql.concat("WHERE id = #{specId}");
        } else {
            return BaseSql.concat("WHERE id = #{specId}");
        }
    }

    public String getBaseBySeriesId(int seriesId,boolean isCV) {
        if (isCV) {
            return CvBaseSql.concat("WHERE seriesId = #{seriesId}");
        } else {
            return BaseSql.concat("WHERE parent = #{seriesId}");
        }
    }

    public String getAllBase() {
        return BaseSql.concat(" UNION ALL \n").concat(CvBaseSql);
    }

    public String getAllSpecs() {
        return getSpecs(0);
    }


    public String getSpecs(int seriesId) {
        String sql = "SELECT  A.SpecId,ISNULL(A.SyearId,0) AS SyearId,ISNULL(A.SeriesId,0) AS SeriesId,A.SpecState,A.SpecPrice as minPrice,A.SpecStructureType,A.Leveled AS LevelId,\n" +
                "        A.SpecTransmissionType,A.SpecDisplacement,A.FlowMode,B.PicNumber ,A.SeriesIsShow\n" +
                "FROM SpecView AS A WITH(NOLOCK)\n" +
                "     INNER JOIN CarSpecPictureStatistics AS B WITH(NOLOCK) ON A.SpecId=B.SpecId \n";
        if (seriesId <= 0) {
            return sql;
        }
        return sql.concat("WHERE A.SeriesId = #{seriesId}");
    }

    public String getAllCvSpecs() {
        return getCvSpecs(0);
    }

    public String getCvSpecs(int seriesId) {
        String sql = "SELECT A.SpecId,ISNULL(A.SyearId,0) AS SyearId,ISNULL(A.SeriesId,0) AS SeriesId,A.SpecState,A.MinPrice,A.MaxPrice,CONVERT(VARCHAR(100),A.StructType) AS SpecStructureType,A.LevelId,\n" +
                "       CONVERT(VARCHAR(100),GearBoxType ) AS SpecTransmissionType,A.DeCapacity AS SpecDisplacement,A.FlowMode,B.PicNumber , ISNULL(A.seriesIsshow,0) as SeriesIsShow\n" +
                "FROM CV_SpecView AS A WITH(NOLOCK)\n" +
                "     INNER JOIN CarSpecPictureStatistics AS B WITH(NOLOCK) ON A.SpecId=B.SpecId ";
        if (seriesId <= 0) {
            return sql;
        }
        return sql.concat("WHERE A.SeriesId = #{seriesId}");
    }

    public String getAllSeriesState() {
        return "SELECT * FROM (".concat(getSeriesState(0)).concat("\nUNION ALL \n").concat(getCVSeriesState(0)).concat("\n) as T ORDER by [key],[value] ASC");
    }

    public String getSeriesState(int seriesId) {
        return String.format("SELECT DISTINCT SeriesId as [key],CASE SpecState WHEN 30 THEN 20 ELSE SpecState END AS [value] \n" +
                        "FROM SpecView AS A WITH(NOLOCK) \n" +
                        "%s\n",
                seriesId <= 0 ? "" : "WHERE A.seriesId =  #{seriesId} \n ORDER BY [value]"
        );
    }


    public String getCVSeriesState(int seriesId) {
        return String.format("SELECT DISTINCT SeriesId as  [key],CASE SpecState WHEN 30 THEN 20 ELSE SpecState END AS [value] \n" +
                        "FROM CV_SpecView AS A WITH(NOLOCK)\n" +
                        "%s\n",
                seriesId <= 0 ? "" : "WHERE A.seriesId = #{seriesId} \n ORDER BY [value] "
        );
    }

    public String getSpecInfoBySpecId(int specid){
        if(specid > 1000000){
            return "SELECT minprice,maxprice, Syearid,Syear,BrandFirstLetter,FctFirstLetter,SeriesFirstLetter,quality AS SpecQuality,SpecState,fueltype,DeCapacity AS displacement,pricedescription,fueltype as fuelTypeDetail FROM CV_SpecView WITH(NOLOCK) WHERE SpecId=#{specid}";
        }
        else {
            return "SELECT specPrice as minprice,specPrice as maxprice,Syearid,Syear,BrandFirstLetter,FctFirstLetter,SeriesFirstLetter,SpecQuality,SpecState,fueltype,specDisplacement AS displacement,pricedescription,fuelTypeDetail FROM SpecView WITH(NOLOCK) WHERE SpecId=#{specid}";
        }
    }

    public String getSpecBySeriesId(int seriesId,boolean isCV,String where){
        String sql = "SELECT SpecId,specPrice as minprice,specPrice as maxprice,SyearId,Syear,SpecDrivingMode,SpecState,FlowMode,SpecDisplacement,specEngineHP as SpecEnginePower,SpecOrdercls,SpecIsImage,isclassic,specStructureType,fueltype,fueltypedetail,specIsshow\n" +
                "                           ,endurancemileage,isNUll(specStructureSeat,0)seat\n" +
                "\t\t                   FROM  SpecView  WITH(NOLOCK) WHERE SeriesId = #{seriesId}  AND " + where;
        if(isCV){
            sql = "SELECT SpecId,MinPrice,MaxPrice,SyearId,Syear,DriveForm,SpecState,FlowMode,DeCapacity as SpecDisplacement,HorsePower as SpecEnginePower,Orders AS SpecOrdercls,structtype,fueltype,endurancemileage,seats as seat\n" +
                    "                                    FROM CV_SpecView WITH(NOLOCK)  WHERE SeriesId = #{seriesId} AND " + where;
        }
        return sql;
    }

    public String getAllSpecInfoBySpecId(){
        return "SELECT SpecId,minprice,maxprice, Syearid,Syear,BrandFirstLetter,FctFirstLetter,SeriesFirstLetter,quality AS SpecQuality,SpecState,fueltype,DeCapacity AS displacement,pricedescription,fueltype as fuelTypeDetail FROM CV_SpecView WITH(NOLOCK) WHERE SpecId>1000000\n" +
        "\t\tunion all\n" +
        "\t\tSELECT SpecId,specPrice as minprice,specPrice as maxprice,Syearid,Syear,BrandFirstLetter,FctFirstLetter,SeriesFirstLetter,SpecQuality,SpecState,fueltype,specDisplacement AS displacement,pricedescription,fuelTypeDetail FROM SpecView WITH(NOLOCK) WHERE SpecId <= 1000000";
    }

    public String getAllSpecBySeriesId(int seriesId,boolean isCV){
        String sql = "SELECT SpecId,specPrice as minprice,specPrice as maxprice,SyearId,Syear,specimg,SpecDrivingMode,SpecState,FlowMode,SpecDisplacement,specEngineHP as SpecEnginePower,SpecOrdercls,SpecIsImage,isclassic,specStructureType,fueltype,fueltypedetail,specIsshow,batteryCapacity,officialFastChargetime, officialSlowChargetime\n" +
                ",SpecPhotoNum as specPicNum,0 AS engineId,SpecEngine as engineName,SpecStructureDoor as doors,SpecOilOffical as officalOil,SpecWidth as width,SpecLength as length,SpecHeight as height,SpecWeight as weightkg,SpecQuality as quality,SeriesIsImport as seriesIsImport,specStructureSeat as seats " +
                ",endurancemileage,isNUll(specStructureSeat,0)seat\n" +
                " ,seriesId, specOilOffical AS officalOil, specQuality as quality,specIsImage,specSpeedupOffical as ssuo,specName " +
                ",CASE SpecState when 20 then 0 when 30 then 0.5 ELSE 1 END AS specOrder " +
                ",CASE SpecIsPublic WHEN 1 THEN 9999  ELSE Syear END AS AppointOrder,SpecIsPublic,SpecOrdercls " +
                "\t\t                   FROM  SpecView  WITH(NOLOCK) WHERE SeriesId = #{seriesId}";
        if(isCV){
            sql = "SELECT SpecId,MinPrice,MaxPrice,SyearId,Syear,img as specimg,DriveForm,SpecState,FlowMode,DeCapacity as SpecDisplacement,HorsePower as SpecEnginePower,Orders AS SpecOrdercls,structtype,fueltype,endurancemileage,seats as seat,batteryCapacity,officialFastChargetime, officialSlowChargetime\n" +
                    ",specIsshow ,SpecPicNum as specPicNum,EngineId as engineId,EngineName as engineName,Doors as doors,OfficalOil as officalOil,Width as width,[Length] as length,Height as height,Weightkg as weightkg,Quality as quality,SeriesIsImport as seriesIsImportNum,Seats as seats  " +
                    " ,seriesId, officalOil,quality, 0 as specIsImage ,0 as ssuo,specName" +
                    ", CASE SpecState when 20 then 0 when 30 then 0.5 ELSE 1 END AS specOrder " +
                    ", CASE SpecState WHEN 20 THEN 9999 WHEN 30 THEN 9999 ELSE syear END AS AppointOrder,CASE SpecState WHEN 0 THEN 0 WHEN 10 THEN 0 WHEN 20 THEN 1 WHEN 30 THEN 1 WHEN 40 THEN 2 END AS SpecIsPublic,Orders as SpecOrdercls " +
                    "FROM CV_SpecView WITH(NOLOCK)  WHERE SeriesId = #{seriesId}";
        }
        return sql;
    }

    public String getAllElectroTotalKW(){
        return " select distinct  SpecId as [key],valu as [value] from specconfig with(nolock)  where name = '电动机总功率(kW)' and valu <> '-' and len(valu)>0\n" +
                "\t\t\t  union all \n" +
                "\t\t\t  select distinct  SpecId as [key],itemvalue as [value] from CV_SpecParamView with(nolock) where name = '电动机总功率(kW)' and itemvalue <> '-' and len(itemvalue)>0\n";
    }

    public String getElectroTotalKW(int specid){
        return " select distinct  SpecId as [key],valu as [value] from specconfig with(nolock)  where name = '电动机总功率(kW)' and valu <> '-' and len(valu)>0 and SpecId = #{specid}\n" +
                "union all \n" +
                "select distinct  SpecId as [key],itemvalue as [value] from CV_SpecParamView with(nolock) where name = '电动机总功率(kW)' and itemvalue <> '-' and len(itemvalue)>0  and SpecId = #{specid}";
    }

    public String getCv(int specId){
        String sql =
                "SELECT   SpecId\n" +
                        "        ,MinPrice\n" +
                        "        ,MaxPrice\n" +
                        "        ,SpecPicNum\n" +
                        "        ,EngineId\n" +
                        "        ,EngineName\n" +
                        "        ,Doors\n" +
                        "        ,Seats\n" +
                        "        ,StructType\n" +
                        "        ,'' AS SpecStructureType \n" +
                        "        ,OfficalOil\n" +
                        "        ,Width\n" +
                        "        ,[Length]\n" +
                        "        ,Height\n" +
                        "        ,DriveForm\n" +
                        "        ,'' AS SpecDrivingMode\n" +
                        "        ,Quality\n" +
                        "        ,Weightkg\n" +
                        "        ,SpecState\n" +
                        "        ,FlowMode\n" +
                        "        ,DeCapacity as SpecDisplacement\n" +
                        "        ,HorsePower as SpecEnginePower\n" +
                        "        ,CASE SeriesIsImport WHEN 1 THEN '进口' ELSE '国产' END AS SeriesIsImport\n" +
                        "        ,FuelType\n" +
                        "        ,maxspeed as specMaxspeed\n" +
                        "        ,isnull(officialFastChargetime,0) officialFastChargetime\n" +
                        "        ,isnull(officialSlowChargetime,0) officialSlowChargetime\n" +
                        "        ,ISNULL( fastChargeBatteryPercentage,0) fastChargeBatteryPercentage\n" +
                        "        ,batteryCapacity\n" +
                        "        ,endurancemileage\n" +
                        "        ,convert(nvarchar(100), torque) as  torque\n" +
                        "        ,pricedescription\n" +
                        "        ,0 as ssuo\n" +
                        "        ,ElectricMotorGrossPower\n" +
                        "        ,ElectricMotorGrossTorque\n" +
                        "        ,'' AS engingKW\n" +
                        "        ,FuelType AS fuelTypeDetail\n" +
                        "FROM   CV_SpecView WITH(NOLOCK)  %s\n";

        return String.format(sql,specId<=0?"":" WHERE   SpecId = #{specId};");
    }

    public String get(int specId){
        String sql =
                "SELECT   SpecId\n" +
                        "        ,A.specPrice as MinPrice\n" +
                        "        ,A.specPrice as MaxPrice\n" +
                        "        ,A.SpecPhotoNum as SpecPicNum\n" +
                        "        ,0 AS EngineId\n" +
                        "        ,A.SpecEngine as EngineName\n" +
                        "        ,A.SpecStructureDoor as Doors\n" +
                        "        ,CONVERT(varchar(20), A.SpecStructureSeat) as Seats\n" +
                        "        ,0 AS StructType\n" +
                        "        ,A.SpecStructureType \n" +
                        "        ,A.SpecOilOffical as OfficalOil\n" +
                        "        ,A.SpecWidth as Width\n" +
                        "        ,A.SpecLength as [Length]\n" +
                        "        ,A.SpecHeight as Height\n" +
                        "        ,0 AS DriveForm\n" +
                        "        ,A.SpecDrivingMode\n" +
                        "        ,A.SpecQuality as Quality\n" +
                        "        ,A.SpecWeight as Weightkg\n" +
                        "        ,A.SpecState\n" +
                        "        ,A.FlowMode\n" +
                        "        ,A.SpecDisplacement\n" +
                        "        ,A.specEngineHP AS SpecEnginePower\n" +
                        "        ,SeriesIsImport\n" +
                        "        ,FuelType\n" +
                        "        ,specMaxspeed\n" +
                        "        ,isnull(officialFastChargetime,0) officialFastChargetime\n" +
                        "        ,isnull(officialSlowChargetime,0) officialSlowChargetime\n" +
                        "        ,ISNULL( fastChargeBatteryPercentage,0) fastChargeBatteryPercentage\n" +
                        "        ,batteryCapacity\n" +
                        "        ,endurancemileage\n" +
                        "        ,A.specEngineTorque as torque\n" +
                        "        ,pricedescription\n" +
                        "        ,specSpeedupOffical as ssuo\n" +
                        "        ,A.ElectricMotorGrossPower\n" +
                        "        ,A.ElectricMotorGrossTorque\n" +
                        "        ,A.specEnginePower AS engingKW\n" +
                        "        ,fuelTypeDetail\n" +
                        "FROM   SpecView AS A WITH(NOLOCK) %s\n";
        return String.format(sql,specId<=0?"":" WHERE SpecId = #{specId};");
    }

    public String getAll(){
        return get(0).concat("UNION ALL \n".concat(getCv(0)));
    }

    /**
     * 车型Logo
     */
    public String getAllSpecLogo(){
        return getSpecLogoBySpecId(null);
    }
    public String getSpecLogoBySpecId(Integer specId){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT Id as [key],REPLACE(REPLACE(Img,'~',''),'/l_','/') AS [value] FROM Spec_new WITH(NOLOCK) ");
        sb.append(" WHERE LEN(img)>0 ");
        if (Objects.nonNull(specId)) {
            sb.append(" and Id = #{specId}");
        }
        sb.append(" UNION ALL ");
        sb.append(" SELECT SpecId AS [key],REPLACE(Img,'~','') AS [value] FROM CV_SpecView WITH(NOLOCK) ");
        sb.append(" WHERE LEN(img)>0 ");
        if (Objects.nonNull(specId)) {
            sb.append(" and SpecId = #{specId}");
        }
        return sb.toString();
    }

    public String isAllHaveMaintains(){
        return "select  distinct  specid as [key],IsShow as [value] from [popautoMaintaining] .dbo.Spec_Mtn_Info  with(nolock)  where IsShow = 1";
    }

    public String isHaveMaintains(int specId){
        return isAllHaveMaintains().concat(" and SpecId = #{specId}");
    }

    public String getBrandEVSeriesList(int brandId) {
        String sql = "with specData as (\n" +
                "        select seriesid,fueltypedetail as fueltype from SpecView with(nolock)where brandid=#{brandId}\n" +
                "        union all \n" +
                "        select seriesid,fueltype  from cv_specview with(nolock)where brandid=#{brandId}\n" +
                "    ) \n" +
                "    select  A.brandId,A.brandFirstLetter,A.seriesId,A.SeriesState,case A.SeriesState \n" +
                "    when 10 then 1 when 20 then 1 when 30 then 1 when 40 then 2 when 0 then 2 end OrderSeriesState, A.seriesOrdercls,B.fueltype\n" +
                "    from SeriesView as A with(nolock) \n" +
                "    inner join specData as B with(nolock) on A.seriesId= B.seriesId \n" +
                "    left JOIN [Replication].dbo.dxp_CarBrandSeries_Ranks E WITH (NOLOCK) ON B.seriesid= E.series_id and E.isdelete=0 \n" +
                "    where A.seriesisnewenergy =1 and A.brandId = #{brandId}\n" +
                "    group by A.brandId,A.brandFirstLetter,\n" +
                "    A.seriesId,A.SeriesState,A.seriesOrdercls,B.fueltype,E.scores_ranks\n" +
                "    order by OrderSeriesState, E.scores_ranks";
        return sql;
    }

    public String getSpecInfoBySeriesId(int seriesId,boolean isCV){
        if(isCV){
            return "SELECT 0 specisimage, SpecId,SpecState,CASE WHEN SpecState>=20 AND SpecState<=30 THEN 0 ELSE 1 END AS SpecOrder ,syear FROM CV_SpecView WITH(NOLOCK)\n" +
                    "                            WHERE SeriesId=#{seriesId}  ORDER BY SpecOrder,Orders,SpecId DESC";
        }
        else {
            return "SELECT specisimage,SpecId,SpecState,SpecOrdercls,CASE WHEN SpecState>=20 AND SpecState<=30 THEN 0 ELSE 1 END AS SpecOrder,syear FROM SpecView WITH(NOLOCK) \n" +
                    "                            WHERE SeriesId=#{seriesId} ORDER BY SpecOrder,isclassic,SpecOrdercls,SpecId DESC";
        }
    }


    public String getSpecByYearId(int yearId) {
        String sql = "SELECT Row_Number() OVER(ORDER BY OrderBy DESC,specOrdercls ) AS rInd,SpecId as id,SeriesId,SyearId as yId,Syear,SpecState as state,specIsImage as sImage" +
                " FROM (\tSELECT SpecId,SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,specOrdercls,specIsImage,\n" +
                "  CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999  WHEN SpecState<=10 THEN 8888  ELSE  Syear END AS OrderBy\n" +
                " FROM SpecView WITH(NOLOCK) where syearId = #{yearId}\n" +
                " UNION\n" +
                " SELECT SpecId,SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,Orders AS specOrdercls,0 AS specIsImage,\n" +
                "             CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999  WHEN SpecState<=10 THEN 8888 ELSE  Syear END AS OrderBy\n" +
                " FROM CV_SpecView WITH(NOLOCK) where SyearId = #{yearId})  AS Temp ";
        return sql;
    }

    public String getSpecItemsBySeries(int seriesId,boolean isCV){
        if(isCV){
            return "SELECT SpecId,SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,Orders AS specOrdercls,0 AS specIsImage,\n" +
                    "                            CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999  WHEN SpecState<=10 THEN 8888 ELSE  Syear END AS OrderBy\n" +
                    "                            FROM CV_SpecView WITH(NOLOCK) where seriesid= #{seriesId} and SyearId>0";
        }
        else {
            return " SELECT SpecId,SeriesId,ISNULL(SyearId,0) AS SyearId,Syear,SpecState,specIsImage,specOrdercls,\n" +
                    "                                    CASE  WHEN SpecState>=20 AND SpecState<=30 THEN 9999  WHEN SpecState<=10 THEN 8888  ELSE  Syear END AS OrderBy\n" +
                    "                                    FROM SpecView WITH(NOLOCK)  where seriesid=#{seriesId} and SyearId>0";
        }
    }

    public String allSpecShowCount(int seriesState){
        String w = "";
        switch (seriesState){
            case 1:  //在售
                w = "specState >=20 and specState<=30";
                break;
            case 2:  //代售
                w = "specState = 10";
                break;
        }
        return String.format("SELECT T.parent AS [key],SUM(CONVERT(INT,T.isshow)) AS [value]\n" +
                "FROM( \n" +
                "\tSELECT DISTINCT parent,isshow  \n" +
                "\tFROM spec_new WITH (nolock)\n" +
                "\tWHERE  %s\n" +
                "\tUNION ALL \n" +
                "\tSELECT DISTINCT seriesid, SpecIsShow \n" +
                "\tfrom cv_specview with(nolock) \n" +
                "\twhere %s\n" +
                ") AS T\n" +
                "GROUP BY T.parent",w,w);
    }

    public String specShowCount(int seriesId,int seriesState){
        String w = "";
        switch (seriesState){
            case 1:  //在售
                w = "specState >=20 and specState<=30";
                break;
            case 2:  //代售
                w = "specState = 10";
                break;
        }
        return String.format("SELECT \n" +
                "(SELECT ISNULL(SUM(CONVERT(INT,isshow)),0) FROM spec_new WITH (nolock) WHERE %s AND parent = #{seriesId}) \n" +
                "+\n" +
                "(SELECT ISNULL(SUM(CONVERT(INT,SpecIsShow)),0) FROM cv_specview WITH (nolock) where %s AND seriesId = #{seriesId}) ",w);
    }

    public String getSpecListBySeriesYear(int seriesId,int yearId){
        String where = "";
        if(seriesId > 0){
            where = " AND seriesId=#{seriesId}";
        }
        return String.format("SELECT SpecId,SeriesId,SpecState,SpecIsImage,CASE WHEN SpecState>=20 AND SpecState<=30 THEN 0 ELSE 1 END AS SpecOrder,SpecOrdercls as Orders, isclassic AS OrderBy  \n" +
                "FROM SpecView WITH(NOLOCK) \n" +
                "WHERE  SyearId=#{yearId} %s \n" +
                "AND ((SpecState<=30 AND specIsshow=1) or (SpecState=40 AND specIsImage=0)) \n", where);
    }

    public String getCvSpecListBySeriesYear(int seriesId,int yearId){
        String where = "";
        if(seriesId > 0){
            where = " AND seriesId=#{seriesId}";
        }
        return String.format("SELECT SpecId,SpecState,0 SpecIsImage,CASE WHEN SpecState>=20 AND SpecState<=30 THEN 0 ELSE 1 END AS SpecOrder,Orders, 0 AS OrderBy  \n" +
                "FROM CV_SpecView WITH(NOLOCK) \n" +
                "WHERE  SyearId=#{yearId} %s \n" +
                "AND ((SpecState>=10 AND SpecState<=30 AND specIsshow=1) or SpecState=40) \n", where);
    }

    public String getSpecBySeries(int seriesId,boolean isCV){
        if(isCV){
            return "select  A.specId as specid,A.SpecState,A.maxPrice,A.minPrice ,C.id as syearid, C.alias_name as syearname  from CV_SpecView as  A with(nolock)                             \n" +
                    "                                        inner join spec_year as C with(nolock) on A.syearId = C.id \n" +
                    "                                        where A.SeriesId = #{seriesId}  and A.specstate>=20 and A.specstate<=30";
        }
        else {
            return "select specid,SpecState,specPrice as maxPrice,specPrice as minprice,syearId as syearid,syearName as syearname   from SpecView as A with(Nolock) where A.seriesId  = #{seriesId} and A.specstate>=20 and A.specstate<=30";
        }
    }

    public String getSpecListBySeriesId(int seriesId, boolean isCv){
        if(!isCv){
            return "SELECT SpecId,SeriesId,SpecState,SpecIsImage,CASE SpecState when 20 then 0 when 30 then 0.5 ELSE 1 END AS SpecOrder,SpecOrdercls as Orders, isclassic AS OrderBy  \n" +
                    "FROM SpecView WITH(NOLOCK) \n" +
                    "WHERE seriesId=#{seriesId}\n" +
                    "AND SpecState<=30 AND specIsshow=1 AND specIsImage=0 \n" +
                    "ORDER BY SpecOrder,OrderBy,Orders,SpecId DESC";
        }else{
            return "SELECT SpecId,SpecState,0 SpecIsImage,CASE SpecState when 20 then 0 when 30 then 0.5 ELSE 1 END AS SpecOrder,Orders \n" +
                    "FROM CV_SpecView WITH(NOLOCK) \n" +
                    "WHERE seriesId=#{seriesId} \n" +
                    "AND SpecState>=10 AND SpecState<=30 AND specIsshow=1 \n" +
                    "ORDER BY SpecOrder,Orders,SpecId DESC";
        }

    }
}
