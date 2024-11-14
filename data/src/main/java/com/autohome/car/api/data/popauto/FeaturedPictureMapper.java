package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.FeaturedPictureEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeaturedPictureMapper {

    @Select("SELECT Id,ShortTitle as st,LongTitle as lt,TypeId,PublishTime as pt,ThreadLink as tl,ImageUrl as iu,MShortTitle as mst\n" +
            "                                    FROM   FeaturedPicture WITH(NOLOCK)\n" +
            "                                    ORDER BY PublishTime DESC")
    List<FeaturedPictureEntity> getFeaturedPictureAll();

    @Select("SELECT Id as [key],TypeName as [value] FROM FeaturedPictureType WITH(NOLOCK) ORDER BY Ordercls")
    List<KeyValueDto<Integer,String>> getFeaturedTypeAll();


}
