package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.ZiXunCarPicVrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ZiXunCarPicVrMapper {

    @Select("select Top 4 title,imgUrl,linkUrl from zixun_carpic_vr with(Nolock) where publishstate = 10 order by sortid")
    List<ZiXunCarPicVrEntity> getIndexSlideVr();
}
