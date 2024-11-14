package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.PicClassProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface PicClassMapper {
    @Select("SELECT  Id,Name FROM car_spec_photo_struct WITH(NOLOCK) WHERE visible=1 AND isShow=1 AND IsTop=0\n" +
            "      UNION ALL\n" +
            "      SELECT 200 AS  Id, '网友实拍' AS Name")
    List<PicClassEntity> getPicClassList();

    @Select("SELECT A.id,A.SpecId,A.PicPath,A.PicId,A.Ordercls,A.TopId \n" +
            "                                        FROM  Car25PictureView  AS A WITH(NOLOCK)\n" +
            "                                        INNER JOIN  Brands AS B WITH(NOLOCK) ON A.Specid=B.delegate25SpecId\n" +
            "                                        WHERE B.id=#{seriesid} AND  A.PicId  > 0 \n" +
            "                                        ORDER  BY A.Ordercls    \n" +
            "                                   -- car.api.autohome.com.cn\\v1\\carpic\\Series_25PictureBySeriesId.ashx")
    List<PicInfoEntity> Get25PicInfo(int seriesid);

    @Select("SELECT  Id as [key],Name as [value] FROM car_speec_photo_subtype WITH(NOLOCK) WHERE visible=1")
    List<KeyValueDto<Integer,String>> getCar25PictureType();

    /**
     * 车系白底车图
     * @return
     */
    @Select("SELECT SeriesId,FilePath\n" +
            "                            FROM(\tSELECT\tB.Parent AS SeriesId,A.FilePath,ROW_NUMBER() OVER(PARTITION BY B.Parent ORDER BY A.Id DESC) AS PartIndex\n" +
            "\t\t                            FROM\tcar_spec_photo AS A WITH(NOLOCK)\n" +
            "\t\t                            INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=B.Id\n" +
            "\t\t                            WHERE A.TypeId=52 AND A.IsDelete=0\n" +
            "\t\t                            UNION ALL\n" +
            "\t\t                            SELECT\tB.SeriesId,A.PhotoPath AS FilePath,ROW_NUMBER() OVER(PARTITION BY B.SeriesId ORDER BY A.PhotoId DESC) AS PartIndex\n" +
            "                                    FROM \tCV_Photo AS A WITH(NOLOCK) inner join CV_SpecView as B on A.specId = B.specId\n" +
            "                                    WHERE A.TypeClassId=52) AS A\n" +
            "                            WHERE PartIndex=1;   --car.api.autohome.com.cn/app_code/Invoke/carpic/PictureView.cs;")
    List<KeyValueDto<Integer,String>> getSeriesType25Picture();

    @SelectProvider(value = PicClassProvider.class,method = "getSeriesPicBySeriesIdPicClass")
    List<SeriesPicClassEntity> getSeriesPicBySeriesIdPicClass(int seriesId, int specId, int classId, int pageStart, int pageEnd, boolean isCV);

    @SelectProvider(value = PicClassProvider.class,method = "getSeriesPicBySeriesIdPicClassCount")
    int getSeriesPicBySeriesIdPicClassCount(int seriesId,int specId,int classId,boolean isCV);

    @Select("SELECT specid, id AS picid,filepath AS picurl, ISNULL(pointlocatinid,0) AS pointlocatinid , ISNULL(pointlocationseteditor,0) pointlocationseteditor FROM car_spec_photo  AS A WITH(NOLOCK)  WHERE specid =#{specId} and isdelete=0  ORDER BY ID DESC;")
    List<PicPointLocationEntity> getPicLocation(int specId);

    @Select("SELECT userid,itemid,subitemid,picid,specid FROM ConfigItem_RelationSpecPic WITH(NOLOCK) WHERE specid= #{specId}")
    List<PicConfigRelationEntity> getPicConfigRelation(int specId);

    @Select("select A.id as PicId,A.specid,A.typeid,A.filepath,A.isHD,B.seriesId,isnull(C.ColorId,0) as ColorId,B.syearId,B.syear,B.SpecState,D.PublishTime,B.specName,B.seriesName," +
            "B.fctId,B.fctName,B.brandId,B.brandName from \n" +
            "(select A.id,A.specid,A.typeid,A.filepath,A.isHD from car_spec_photo as A with(nolock) where IsDelete=3 and note= #{userId}) as A\n" +
            "inner join SpecView as B with(nolock) on A.specid=B.specId\n" +
            "left join Car_Photo_Color as C with(nolock) on A.id=C.PhotoId\n" +
            "inner join PhotoPublish as D with(nolock) on A.id=D.PicId ORDER BY  A.id DESC")
    List<PicPublishEntity> getPicItemsPublishByUserId(int userId);

    @Select("select A.id as PicId,A.specid,A.typeid,A.filepath,A.isHD,B.seriesId,isnull(C.ColorId,0) as ColorId,B.syearId,B.syear,B.SpecState,D.PublishTime,B.specName,B.seriesName," +
            "B.fctId,B.fctName,B.brandId,B.brandName from \n" +
            "(select A.id,A.specid,A.typeid,A.filepath,A.isHD from car_spec_photo as A with(nolock) where IsDelete=3) as A\n" +
            "inner join SpecView as B with(nolock) on A.specid=B.specId\n" +
            "left join Car_Photo_Color as C with(nolock) on A.id=C.PhotoId\n" +
            "inner join PhotoPublish as D with(nolock) on A.id=D.PicId ORDER BY  A.id DESC")
    List<PicPublishEntity> getAllPicItemsPublish();
}
