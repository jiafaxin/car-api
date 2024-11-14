package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.BrandProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface BrandMapper {

    @SelectProvider(value = BrandProvider.class,method = "getBrandInfo")
    BrandBaseEntity getBrandInfo(int brandId);

    @SelectProvider(value = BrandProvider.class,method = "getAllBrandInfo")
    List<BrandBaseEntity> getAllBrandInfo();

    @Select("select DISTINCT id from [group] with(nolock)")
    List<Integer> getAllBrandIds();

    @Select("SELECT BrandId,FirstLetter,BrandCount,MAX(SeriesOrdercls) AS orders\n" +
            "FROM CarManuePrice WITH (NOLOCK)\n" +
            "GROUP BY BrandId,BrandCount,FirstLetter\n" +
            "ORDER BY FirstLetter,max(SeriesOrdercls);")
    @AutoCache(removeIn = 60,expireIn = 10)
    List<BrandPriceMenuEntity> getBrandPriceMenus();

    @SelectProvider(value = BrandProvider.class,method = "getAllSeriesBrands")
    List<BrandSeriesStateBaseEntity> getAllSeriesBrands();

    @Select("SELECT TOP 20 BrandId FROM [Replication].[dbo].dxp_Brand_Order WITH (NOLOCK) ORDER BY Ordernum ASC")
    List<Integer> getHotBrand();

    @Select("with brand as (\n" +
            "                                        select distinct  A.BrandId,A.FirstLetter from CarManuePrice as A  with(Nolock) \n" +
            "                                        ) \n" +
            "                                        select A.BrandId,A.FirstLetter, ISNULL(B.ordernum,1000) as ordernum  from brand as A  with(Nolock) \n" +
            "                                        left join [replication].dbo.dxp_Brand_Order as B with(nolock) on A.BrandId = B.Brandid order by FirstLetter, ordernum")
    List<BrandInfoEntity> getBrandInfoAll();


    @Select("SELECT A.brandId,C.firstLetter  \n" +
            "                        FROM [replication].dbo.Pavilion_Brand AS A WITH(NOLOCK)\n" +
            "                        JOIN [replication].dbo.Pavilion  AS B WITH(NOLOCK) ON A.PavilionId = B.ID\n" +
            "                        JOIN [popauto].[dbo].[Group] AS C WITH(NOLOCK) ON A.BrandId = C.Id\n" +
            "                        WHERE B.ParentId=#{showId} AND A.IsDel=0 AND B.IsDel=0 AND B.ID = #{pavilionId}\n" +
            "                        ORDER BY C.FirstLetter,A.ID DESC")
    List<BrandInfoEntity> getPavilionBrands(@Param("showId") int showId, @Param("pavilionId") int pavilionId);

    @Select("SELECT Id as [key] ,name as [value] FROM Brands WITH(NOLOCK) WHERE Name = #{seriesName}")
    KeyValueDto<Integer,String> getSeriesIdBySeriesName(String seriesName);

    @Select("WITH BrandList AS \n" +
            "  (  \n" +
            "      SELECT DISTINCT BrandId,BrandName, FirstLetter,BrandCount FROM CarManuePic WITH (NOLOCK)\n" +
            "   ) \n" +
            "   SELECT * FROM ( \n" +
            "      SELECT A.BrandId,A.BrandName,A.FirstLetter,A.BrandCount,ISNULL(B.ordernum,10000) AS BrandOrder,REPLACE(C.img,'~','') as img  FROM BrandList AS A WITH(NOLOCK) \n" +
            "      LEFT JOIN [Replication].dbo.dxp_Brand_order AS B WITH(NOLOCK) ON A.BrandId =B.Brandid \n" +
            "      LEFT JOIN [group] AS C WITH(NOLOCK) ON A.BrandId = C.id \n" +
            "    ) AS T \n" +
            "   ORDER BY FirstLetter,BrandOrder,BrandName;")
    List<BrandPicListEntity> getPicBrandListAll();

    @SelectProvider(value = BrandProvider.class,method = "getBrandShowByPavLetter")
    List<BrandPicListEntity> getBrandShowByPavLetter(int showId, String pavList, String letter);

    @Select("SELECT id as [key],name AS [value] FROM exposition.dbo.Gbrands with(nolock) where isdel=0")
    List<KeyValueDto<Integer,String>> getGBrandAll();


    @Select("SELECT ROW_NUMBER() OVER(ORDER BY BFirstLetter,ordernum,brandId) AS RankIndex,BrandId,BFirstLetter,IsCV,SpecState,specIsImage,ordernum\n" +
            "                                        FROM (SELECT  spec.brandId,brandFirstLetter AS BFirstLetter,spec.SpecState,spec.IsCV,specIsImage,ISNULL(C.ordernum,100000) as ordernum\n" +
            "\t                                              FROM(SELECT BrandId,SpecState,1 AS IsCV,specIsImage,brandFirstLetter FROM SpecView WITH(NOLOCK)\n" +
            "\t\t\t                                                UNION\n" +
            "\t\t\t                                                SELECT BrandId,SpecState,2 AS IsCV,0 AS specIsImage ,brandFirstLetter FROM CV_SpecView WITH(NOLOCK)) AS spec \n" +
            "\t                                              left JOIN [Replication].dbo.dxp_Brand_order AS C WITH(NOLOCK) ON spec.brandId = C.Brandid\n" +
            "\t                                              ) AS Temp")
    List<BrandStateEntity> getBrandBaseAll();
}
