package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.FactoryBaseEntity;
import com.autohome.car.api.data.popauto.entities.FactoryInfoEntity;
import com.autohome.car.api.data.popauto.entities.FctEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FactoryMapper {
    @Select("SELECT id as id, name as name,url as url,img as logo,firstLetter,isimport,createTime,editTime  FROM [Manufactory] WITH(NOLOCK);")
    List<FactoryBaseEntity> getAllFactoryNames();

    @Select("SELECT id FROM [Manufactory] WITH(NOLOCK);")
    List<Integer> getAllFactoryIds();

    @Select("SELECT Row_Number() OVER(ORDER BY BrandId,isimport,FactoryName) AS RankIndex,BrandId,FactoryId,FactoryName,FFirstLetter,IsCV,SpecState,specIsImage\n" +
            "                                            FROM (SELECT BrandId,factory.id AS FactoryId,factory.name AS FactoryName,factory.FirstLetter AS FFirstLetter,factory.isimport,\n" +
            "                                                                    spec.SpecState,spec.IsCV,specIsImage\n" +
            "\t                                                  FROM(SELECT BrandId,FctId,SpecState,1 AS IsCV,specIsImage FROM SpecView WITH(NOLOCK)\n" +
            "\t\t\t                                                    UNION \n" +
            "\t\t\t                                                    SELECT BrandId,FctId,SpecState,2 AS IsCV,0 AS specIsImage FROM CV_SpecView WITH(NOLOCK)) AS spec \n" +
            "\t                                                  INNER JOIN Manufactory factory WITH(NOLOCK) ON spec.FctId = factory.id) AS Temp")
    List<FactoryInfoEntity> getAllFactoryInfos();

    @Select("SELECT Row_Number() OVER(ORDER BY FFirstLetter,isimport,FactoryName) AS RankIndex,FactoryId,FactoryName,FFirstLetter,IsCV,SpecState,specIsImage\n" +
            "                                        FROM (SELECT factory.id AS FactoryId,factory.name AS FactoryName,factory.FirstLetter AS FFirstLetter,factory.isimport,\n" +
            "                                                                spec.SpecState,spec.IsCV,specIsImage\n" +
            "\t                                              FROM(SELECT FctId,SpecState,1 AS IsCV,specIsImage FROM SpecView WITH(NOLOCK)\n" +
            "\t\t\t                                                UNION \n" +
            "\t\t\t                                                SELECT FctId,SpecState,2 AS IsCV,0 AS specIsImage FROM CV_SpecView WITH(NOLOCK)) AS spec \n" +
            "\t                                              INNER JOIN Manufactory factory WITH(NOLOCK) ON spec.FctId = factory.id) AS Temp")
    List<FactoryInfoEntity> getAllFactoryInfoSortedLetter();

    @Select("select * from (\n" +
            "SELECT Row_Number() OVER(ORDER BY BrandId,isimport,FactoryName) AS RankIndex,BrandId,FactoryId,FFirstLetter,IsCV,SpecState,specIsImage\n" +
            "                                            FROM (SELECT BrandId,factory.id AS FactoryId,factory.name AS FactoryName,factory.FirstLetter AS FFirstLetter,factory.isimport,\n" +
            "                                                                    spec.SpecState,spec.IsCV,specIsImage\n" +
            "\t                                                  FROM(SELECT BrandId,FctId,SpecState,1 AS IsCV,specIsImage FROM SpecView WITH(NOLOCK)\n" +
            "\t\t\t                                                    UNION \n" +
            "\t\t\t                                                    SELECT BrandId,FctId,SpecState,2 AS IsCV,0 AS specIsImage FROM CV_SpecView WITH(NOLOCK)) AS spec \n" +
            "\t                                                  INNER JOIN Manufactory factory WITH(NOLOCK) ON spec.FctId = factory.id) AS Temp\n" +
            ") as a where a.brandid = #{brandid}")
    List<FactoryInfoEntity> getFactoryInfos(int brandid);


    @Select(" SELECT [fctId] as [key] ,[brandId] as [value]  FROM [popauto].[dbo].[FctView] WITH(NOLOCK)")
    List<KeyValueDto<Integer, Integer>> getAllFactoryBrands();


    @Select("SELECT Id AS FctId, FirstLetter,name AS FctName \n" +
            "                                    FROM popauto.dbo.manufactory AS A WITH(NOLOCK)\n" +
            "                                    WHERE EXISTS(SELECT 1 FROM exposition.dbo.Show_Cars WITH(NOLOCK) WHERE A.id=Show_Cars.Brandid)\n" +
            "                                    ORDER BY A.isimport, A.FirstLetter")
    @AutoCache(expireIn = 30, removeIn = 60)
    List<FctEntity> getShowFcts();
}
