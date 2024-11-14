package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.SpecConfigProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import java.util.List;

@Mapper
public interface SpecConfigMapper {

    @SelectProvider(value = SpecConfigProvider.class, method = "getSpecConfigs")
    List<SpecConfigEntity> getSpecConfigs(int specId);

    @SelectProvider(value = SpecConfigProvider.class,method = "getSpecConfigRelations")
    List<SpecConfigRelationEntity> getSpecConfigRelations(int specId);

    @SelectProvider(value = SpecConfigProvider.class,method = "getSpecConfigSubItems")
    List<SpecConfigSubItemEntity> getSpecConfigSubItems(int specId);

    @Select("SELECT DISTINCT SpecId, ItemId ,SubItemId,Price FROM ConfigSpecPrice WITH(NOLOCK) ORDER BY SpecId,ItemId,SubItemId")
    List<SpecConfigPriceEntity> getAllSpecConfigPrice();


    @Select("SELECT DISTINCT SpecId, ItemId ,SubItemId,Price FROM ConfigSpecPrice WITH(NOLOCK) WHERE SpecId = #{specId} ORDER BY SpecId,ItemId,SubItemId")
    List<SpecConfigPriceEntity> getSpecConfigPrice(int specId);


    @Select("WITH specconfig AS \n" +
            "(\n" +
            "\t  SELECT A.specid,A.ItemId,A.ItemValueId , B.SubItemId,B.Value AS SubValue FROM ConfigSpecRelation AS A WITH(NOLOCK) left JOIN ConfigSubItemValueRelation  AS B WITH(NOLOCK) ON A.ItemValueId = B.ItemValueId WHERE specid =#{specId} AND A.ItemValueId>-1\n" +
            "\t  UNION ALL \n" +
            "\t  SELECT A.SpecId,A.ItemId,-1 AS ItemValueId,A.SubItemId,A.SubValue FROM ConfigSubItemSpecRelation  AS A WITH(NOLOCK) WHERE SpecId = #{specId}\n" +
            ")\n" +
            "\n" +
            " SELECT A.TypeId, A.ID AS itemid,A.DisplayType,A.Name,B.SpecId,ISNULL(B.ItemValueId,0) AS ItemValueId,B.SubItemId,B.SubValue FROM  ConfigItem AS A WITH(NOLOCK)\n" +
            " LEFT JOIN specconfig AS B WITH(NOLOCK) ON A.Id = B.ItemId \n" +
            " LEFT JOIN ConfigSubItem AS C WITH(NOLOCK) ON B.SubItemId = C.Id AND B.ItemId = C.ItemId\n" +
            " WHERE  A.IsShow = 1 \n" +
            " ORDER BY A.TypeId,A.Sort,C.Sort")
    List<ConfItemEntity> getSpecConfItem(int specId);

    @Select("WITH specconfig AS \n" +
            "(\n" +
            "\t\tSELECT A.specid,A.ItemId,A.ItemValueId ,B.SubItemId,B.Value AS SubValue FROM Cv_ConfigSpecRelation AS A WITH(NOLOCK) left JOIN ConfigSubItemValueRelation  AS B WITH(NOLOCK) ON A.ItemValueId = B.ItemValueId WHERE specid =#{specId} AND A.ItemValueId>-1\n" +
            "\t\tUNION ALL \n" +
            "\t\tSELECT A.SpecId,A.ItemId,-1 AS ItemValueId,A.SubItemId,A.SubValue FROM CV_ConfigSubItemSpecRelation  AS A WITH(NOLOCK) WHERE SpecId = #{specId}\n" +
            ")\n" +
            "\n" +
            "\tSELECT A.TypeId, A.ID AS itemid,A.DisplayType,A.Name,B.SpecId,ISNULL(B.ItemValueId,0) AS ItemValueId,B.SubItemId,B.SubValue FROM  ConfigItem AS A WITH(NOLOCK)\n" +
            "\tLEFT JOIN specconfig AS B WITH(NOLOCK) ON A.Id = B.ItemId \n" +
            "\tLEFT JOIN ConfigSubItem AS C WITH(NOLOCK) ON B.SubItemId = C.Id AND B.ItemId = C.ItemId\n" +
            "\tWHERE  A.CVIsShow = 1 \n" +
            "\tORDER BY A.TypeId,A.Sort,C.Sort")
    List<ConfItemEntity> getCvSpecConfItem(int specId);

    @SelectProvider(value = SpecConfigProvider.class,method = "getSpecConfigSubItemValues")
    List<SpecConfigSubItemEntity> getSpecConfigSubItemValues(int specId);

}
