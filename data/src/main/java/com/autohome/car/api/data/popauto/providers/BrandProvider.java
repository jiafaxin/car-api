package com.autohome.car.api.data.popauto.providers;

import java.util.Objects;

public class BrandProvider {

    public String getAllBrandInfo(){
        return "select id,name,img as logo,url,country,CountryId,FirstLetter, createTime,EditTime from [group] with(nolock)";
    }

    public String getBrandInfo(int seriesId){
        return getAllBrandInfo().concat(" WHERE id = #{brandId}");
    }

    public String getAllSeriesBrandsSql(){
        return "SELECT RankIndex, BrandId,BrandName, BFirstLetter,logo, IsCV, SpecState, SpecIsImage\n" +
                "FROM (\n" +
                "  SELECT ROW_NUMBER() OVER (ORDER BY BFirstLetter, BrandName) AS RankIndex, BrandId,BrandName, BFirstLetter,logo, IsCV, SpecState, specIsImage\n" +
                "  FROM (\n" +
                "    SELECT brand.id AS BrandId, brand.name AS BrandName, brand.FirstLetter AS BFirstLetter,brand.img as logo, spec.SpecState, spec.IsCV, spec.specIsImage\n" +
                "    FROM (\n" +
                "      SELECT BrandId, SpecState, 1 AS IsCV, specIsImage FROM SpecView WITH (NOLOCK)\n" +
                "      UNION \n" +
                "      SELECT BrandId, SpecState, 2 AS IsCV, 0 AS specIsImage FROM CV_SpecView WITH (NOLOCK)\n" +
                "    ) AS spec\n" +
                "    INNER JOIN [group] brand WITH (NOLOCK) ON spec.BrandId = brand.id\n" +
                "  ) AS Temp\n" +
                ") AS Temp2\n";
    }

    public  String getAllSeriesBrands(){
        return getAllSeriesBrandsSql().concat(" ORDER BY RankIndex ASC");
    }

    public String getBrandShowByPavLetter(int showId, String pavList, String letter){
        String sql = "  SELECT DISTINCT  A.BrandId,C.FirstLetter,C.Name as BrandName,REPLACE(C.img,'~','') as img\n" +
                "              FROM  [replication].dbo.Pavilion_Brand AS A WITH (NOLOCK) \n" +
                "              JOIN  [replication].dbo.Pavilion AS B WITH (NOLOCK) ON A.PavilionId=B.ID  \n" +
                "              JOIN  [popauto].[dbo].[group] AS C WITH (NOLOCK) ON C.Id=A.BrandId \n" +
                "              WHERE  B.ParentId=#{showId} AND A.IsDel=0 AND B.IsDel=0 %s \n" +
                "              ORDER BY C.FirstLetter ,C.Name;";
        String where = "";
        if(!Objects.equals(pavList, "")){
            where += " AND B.ID IN (" + pavList + ")";
        }
        if(!Objects.equals(letter, "")){
            where += " AND C.FirstLetter=#{letter}";
        }
        return String.format(sql, where);
    }
}
