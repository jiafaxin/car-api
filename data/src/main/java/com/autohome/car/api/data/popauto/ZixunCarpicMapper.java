package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.ZixunCarpicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ZixunCarpicMapper {

    @Select("select Top 4  title,imgurl,linkurl from zixun_carpic_big with(Nolock) where publishstate = 10  order by sortid")
    List<ZixunCarpicEntity> getZixunCarpicBig();

    @Select("select Top 16 title,imgurl,linkurl from zixun_carpic_small with(Nolock) where publishstate = 10 order by sortid")
    List<ZixunCarpicEntity> getZixunCarpicSmall();

}
