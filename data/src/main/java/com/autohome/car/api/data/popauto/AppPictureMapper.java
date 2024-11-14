package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.AppPictureEntity;
import com.autohome.car.api.data.popauto.providers.AppPictureProvider;
import com.autohome.car.api.data.popauto.providers.ParamSubItemProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface AppPictureMapper {

    @Select("with title as(select Top 50 a.id, a.brandid, a.SeriesId,a.SpecId,Title,PublishTime,looptype,BigImg,DisplayType,bigimgtype from AppNewPicture_Title a with(nolock) where PublishTime<=GETDATE()  order by PublishTime desc\n" +
            "                )\n" +
            "                select  a.id, a.brandid, a.SeriesId,a.SpecId,Title,PublishTime,looptype,BigImg,DisplayType,\n" +
            "                b.PicTypeId,b.picpath ,b.picid,a.bigimgtype from title a with(nolock) inner join AppNewPicture_Pic b with(nolock) on a.Id=b.titleid\n" +
            "                order by PublishTime desc")
    @AutoCache(expireIn = 10, removeIn = 60)
    List<AppPictureEntity> getAppNewPicture();

    @SelectProvider(value = AppPictureProvider.class,method = "getAppNewPictureByTime")
    List<AppPictureEntity> getAppNewPictureByTime(String editTime, int size);
}
