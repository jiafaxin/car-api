package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.AutoTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.AutoTagCarEntity;
import com.autohome.car.api.data.popauto.entities.AutoTagEntity;
import com.autohome.car.api.data.popauto.providers.AutoTagProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface AutoTagMapper {

    @Select("SELECT id,name FROM autotag_baseinfo WITH(NOLOCK) WHERE isdel=0 AND isopen =1")
    List<AutoTagEntity> getAutoTagName();

    @Select("WITH series AS \n" +
            "(\n" +
            "\tSELECT specId,seriesId FROM SpecView WITH(NOLOCK)\n" +
            "\tUNION ALL\n" +
            "\tSELECT specId,seriesId FROM CV_SpecView WITH(NOLOCK)\n" +
            ")\n" +
            "SELECT DISTINCT A.TagId FROM autotag_spec as A WITH(NOLOCK)\n" +
            "INNER JOIN series AS S WITH(NOLOCK) ON A.SpecId = S.specId\n" +
            "INNER JOIN autotag_baseinfo as B WITH(NOLOCK) ON A.TagId =B.id\n" +
            "INNER JOIN autotag_platform AS C WITH(NOLOCK) ON B.id=C.TagId \n" +
            "WHERE C.Steamid = 1 AND B.isdel=0 AND B.isopen=1 and S.seriesId = #{seriesId}")
    List<Integer> getSeriesTagsBySeriesId(int seriesId);

    @Select("WITH series AS \n" +
            "(\n" +
            "\tSELECT specId,seriesId FROM SpecView WITH(NOLOCK)\n" +
            "\tUNION ALL\n" +
            "\tSELECT specId,seriesId FROM CV_SpecView WITH(NOLOCK)\n" +
            ")\n" +
            "SELECT DISTINCT S.seriesId as [key],A.TagId as [value] FROM autotag_spec as A WITH(NOLOCK)\n" +
            "INNER JOIN series AS S WITH(NOLOCK) ON A.SpecId = S.specId\n" +
            "INNER JOIN autotag_baseinfo as B WITH(NOLOCK) ON A.TagId =B.id\n" +
            "INNER JOIN autotag_platform AS C WITH(NOLOCK) ON B.id=C.TagId \n" +
            "WHERE C.Steamid = 1 AND B.isdel=0 AND B.isopen=1")
    List<KeyValueDto<Integer, Integer>> getSeriesTags();

    @Select("select top(#{top}) id,name from autotag_baseinfo with(nolock) where isdel=0 and isopen =1 order by orders")
    @AutoCache(expireIn = 10,removeIn = 60)
    List<AutoTagEntity> getTagList(int top);

    @SelectProvider(value = AutoTagProvider.class,method = "autoTagCarListAutoHome")
    List<AutoTagCarEntity> autoTagCarListAutoHome(List<Integer> tagIds, List<Integer> levels, List<Integer> country, int minPrice, int maxPrice, int orderId);

    @SelectProvider(value = AutoTagProvider.class,method = "autoTagCarList")
    List<AutoTagCarEntity> autoTagCarList(List<Integer> tagIds,int minPrice,int maxPrice,int orderid);

}
