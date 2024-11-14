package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.NewEnergyBrandEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NewEnergyBrandMapper {

    @Select("WITH NewEnergyBrand as \n" +
            "( SELECT * FROM (\n" +
            "    SELECT *,ROW_NUMBER()OVER(PARTITION BY brandid ORDER BY havesale DESC) AS rn  FROM (\n" +
            "    SELECT  A.brandid,A.brandName,A.brandFirstLetter,A.brandImg,case A.seriesstate when 20 then 1 when 30 then 1 else 0 end as havesale from  SeriesView as A  WITH (NOLOCK)  WHERE A.seriesisnewenergy = 1 AND A.seriesIsshow=1\n" +
            "    ) AS T ) AS TT\n" +
            "    WHERE rn =1\n" +
            ") \n" +
            "SELECT A.brandId as bId,A.brandName as bn,A.brandFirstLetter as bfl,A.brandImg as bi,A.havesale as hs, C.country FROM  NewEnergyBrand  AS A WITH (NOLOCK) \n" +
            "LEFT JOIN [Replication].dbo.dxp_CarBrandSeries_Ranks B WITH(NOLOCK) ON A.brandId = B.brand_id AND B.typeid=1 AND B.isdelete=0\n" +
            "LEFT JOIN [group] as C with(nolock) on C.id = A.brandId\t\n" +
            "ORDER BY A.brandFirstLetter,B.scores_ranks;")
    List<NewEnergyBrandEntity> getAllList();

}
