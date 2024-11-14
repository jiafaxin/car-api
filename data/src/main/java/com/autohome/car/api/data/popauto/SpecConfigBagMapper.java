package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.SpecConfigBagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SpecConfigBagMapper {

    @Select("select A.id, A.specid,A.bagId,B.name as BagName,B.price,B.descrip FROM ConfigBagSpecRelation  AS A WITH(NOLOCK) inner join ConfigBag  AS B WITH(NOLOCK) ON A.bagId=B.id ORDER BY B.price asc")
    List<SpecConfigBagEntity> getAllBags();

    @Select("select A.specid,A.bagId,B.name as BagName,B.price,B.descrip \n" +
            "FROM ConfigBagSpecRelation  AS A WITH(NOLOCK) \n" +
            "\t inner join ConfigBag  AS B WITH(NOLOCK) ON A.bagId=B.id  \n" +
            "WHERE A.specid = #{specId}\n" +
            "ORDER BY B.price asc")
    List<SpecConfigBagEntity> getBags(int specId);

}
