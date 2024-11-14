package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.Car25LocationPicViewEntity;
import com.autohome.car.api.data.popauto.entities.Car25PictureViewEntity;
import com.autohome.car.api.data.popauto.providers.SpecPhotoProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface Car25PictureViewMapper {


    @Select("SELECT A.Id,A.SpecId,A.PicPath,A.PicId,A.Ordercls,A.TopId\n" +
            "                                        FROM  Car25PictureView  AS A WITH(NOLOCK)\n" +
            "                                        INNER JOIN  Spec_new AS B ON A.specid=B.Id\n" +
            "                                        WHERE B.Spec_year=#{yearid} AND B.parent=#{seriesid} AND A.PicId>0\n" +
            "                                        ORDER BY A.Ordercls,A.SpecId")
    List<Car25PictureViewEntity> getYear25PictureByYearId(int seriesid, int yearid);

    @Select("SELECT B.syear, A.id  AS point25pic,A.SpecId,A.PicId,A.PicPath,\n" +
            "   ISNULL(C.datatype,0) as datatype,C.TypeId,isnull(C.itemid,0) as itemid,C.DisplayType,C.itemname,C.valu,C.ItemValueId,C.SubItemId,C.subitemname,C.SubValue \n" +
            "   FROM Car25PictureView AS A WITH(NOLOCK) INNER JOIN  specview AS B WITH(NOLOCK) ON A.specid = B.specid  \n" +
            "   LEFT JOIN  Car25PictureView_PointLocationInfo AS C WITH(NOLOCK) ON A.SpecId = C.SpecId AND A.id=C.pointlocation\n" +
            "    WHERE B.seriesid=#{seriesid} and A.id=#{pointLocationId} and A.PicId>0 and B.SpecState>=20 and B.SpecState<=30 " +
            "   Order by itemid;")
    List<Car25LocationPicViewEntity> get25PointLocation(int seriesid, int pointLocationId);


    @Select("SELECT B.id FROM Brands AS A WITH(NOLOCK) inner join Car25PictureView AS B  WITH(NOLOCK)   ON A.delegate25SpecId = B.SpecId\tWHERE A.id=#{seriesid}  AND PicId>0")
    List<Integer> getSeries25Pic(int seriesid);

}
