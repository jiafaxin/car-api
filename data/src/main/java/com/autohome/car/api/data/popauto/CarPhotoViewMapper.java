package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.CarPhotoTestRowEntity;
import com.autohome.car.api.data.popauto.entities.CarPhotoViewEntity;
import com.autohome.car.api.data.popauto.entities.SeriesPictureEntity;
import com.autohome.car.api.data.popauto.providers.CarPhotoViewProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface CarPhotoViewMapper {

    /**
     * 状态排序 未售+在售靠前 停售靠后
     * 类别排序 (id:1),中控方向盘(id:10),车厢座椅(id:3),其他细节(id:12),评测(id:13),改装(id:51),图解(id:14),活动(id:15) ,官图（id:53）
     * 20200218 类别排序改为： 外观(id:1),中控方向盘(id:10),车厢座椅(id:3),其他细节(id:12), 影·致(id:54),官图（id:53),评测(id:13),重要特点(id:14),改装(id:51),活动(id:15) ,
     * sourceTypeOrder 图片来源排序 为把经销商图片审核通过的图片排到编辑的图片后面，0 编辑上传、5 是经销商推图、10 是网友传图。（推送缺25图车型给经销商，经销商推图）
     *
     * @param seriesId
     */
    @Select("with newpic as (\n" +
            "select * from (\n" +
            "select specid,picid as newPicOrder,\n" +
            "ROW_NUMBER()over(partition by specid order by picid desc) as rn \n" +
            "FROM CarPhotoView AS A WITH(NOLOCK) where A.SeriesId= #{seriesId}\n" +
            ") as T where rn=1\n" +
            ")\n" +
            "SELECT A.Id,  A.SpecId,A.SeriesId,A.PicClass,A.PicFilePath,A.PicId,A.PicColorId,A.IsHD,A.isTitle,A.SpecState,A.SyearId,A.Syear,A.SpecPicNumber,InnerColorId,\n" +
            "CASE WHEN A.SpecState<=30 THEN 0 ELSE 1 END AS StateOrder,case IsClubPhoto when 3 then 0 when 2  then 0 else IsClubPhoto end as IsClubPhoto,\n" +
            "CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder,A.isclassic,A.dealerPicOrder,\n" +
            "CASE IsClubPhoto when 2 then 5 when 1 then 10 when 3 then 0 else IsClubPhoto end as sourceTypeOrder ,isnull(SpecPicUploadTimeOrder,0) as SpecPicUploadTimeOrder ,width,height,dealerid,B.newPicOrder\n" +
            ",isnull(pointlocatinid,0) as pointlocatinid,isnull(IsWallPaper,0) as IsWallPaper,isnull(optional,0) as optional, isnull(showId,0) as showId\n" +
            "FROM CarPhotoView AS A WITH(NOLOCK)  inner join newpic as B on A.SpecId = B.SpecId\n" +
            "WHERE A.SeriesId= #{seriesId}")
    List<CarPhotoViewEntity> getPhotoViewBySeries(int seriesId);

    /**
     * 获取车系下前三张图片
     * @return
     */
    @SelectProvider(value = CarPhotoViewProvider.class,method = "getAllSeriesPicture")
    List<SeriesPictureEntity> getAllSeriesPicture();

    @SelectProvider(value = CarPhotoViewProvider.class,method = "getAllSeriesPicture2")
    List<SeriesPictureEntity> getAllSeriesPicture2();


    @SelectProvider(value = CarPhotoViewProvider.class,method = "getAllSeriesPicture3")
    List<SeriesPictureEntity> getAllSeriesPicture3();


    @SelectProvider(value = CarPhotoViewProvider.class,method = "getSeriesPicture")
    List<SeriesPictureEntity> getSeriesPicture(int seriesId);


    @Select("SELECT A.SeriesId as [key],CONVERT(BIGINT,MAX(A.RV)) as [value]\n" +
            "FROM (SELECT TOP 100000 SeriesId,RV FROM [CarPhotoView] A WITH(NOLOCK) ORDER BY RV DESC) AS A\n" +
            "WHERE A.RV > #{minVersion}\n" +
            "GROUP BY A.SeriesId")
    List<KeyValueDto<Integer, Long>> getUpdateSeriesIds(Long minVersion);

    @Select("SELECT SpecId,SeriesId,PicClass,PicFilePath,PicId,PicColorId,IsHD,isTitle,SpecState,SyearId,Syear,SpecPicNumber,InnerColorId,isnull(showId,0) as showId,StateOrder,\n" +
            "                                IsClubPhoto,ClassOrder,isclassic,dealerPicOrder,sourceTypeOrder,SpecPicUploadTimeOrder,rn from (\n" +
            "\t                            SELECT  A.SpecId,A.SeriesId,A.PicClass,A.PicFilePath,A.PicId,A.PicColorId,A.IsHD,A.isTitle,A.SpecState,A.SyearId,A.Syear,A.SpecPicNumber,InnerColorId,showId,\n" +
            "\t                            CASE WHEN A.SpecState<=30 THEN 0 ELSE 1 END AS StateOrder,case IsClubPhoto when 3 then 0  when 2  then 0 else IsClubPhoto end as IsClubPhoto,\n" +
            "\t                            CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder,A.isclassic,A.dealerPicOrder,\n" +
            "\t                            CASE IsClubPhoto when 2 then 5 when 1 then 10  when 3 then 0 else IsClubPhoto end as sourceTypeOrder ,isnull(SpecPicUploadTimeOrder,0) as SpecPicUploadTimeOrder,row_number()over(partition by picclass order by picid desc) as rn\n" +
            "\t                            FROM CarPhotoView AS A WITH(NOLOCK)\n" +
            "                            WHERE A.SeriesId=#{seriesId} ) as T where rn <=10")
    List<CarPhotoViewEntity> getPhotoViewClassPicTop10BySeriesId(int seriesId);



    @Select("SELECT DISTINCT specid  FROM (\n" +
            " select specid,dtime,row_number()OVER(PARTITION BY specid ORDER BY picid DESC) AS RN  FROM CarPhotoView WITH(NOLOCK)  WHERE picclass=53 and DATEDIFF(DAY,dtime,getdate())<=7\n" +
            " ) AS T WHERE rn =1")
    List<Integer> getAllSpecOfficialPicIsNew();

    @Select("SELECT SyearId,specid,photoId, PhotoFilepath,RowIndex AS OrderIndex\n" +
            "                                        FROM(SELECT SyearId,SpecId,PicId AS PhotoId,PicFilePath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY SyearId ORDER BY SeriesOrders,SpecId DESC,PicId DESC) AS RowIndex\n" +
            "\t                                            FROM CarPhotoView WITH(NOLOCK)\n" +
            "                                        WHERE PicClass in (1,53) AND PhotoIsBrand=1 AND IsClubPhoto<>1) AS A\n" +
            "                                        WHERE RowIndex<=3\n" +
            "\n" +
            "                                        SELECT\tSyearId,specid,photoId,PhotoFilepath,OrderIndex\n" +
            "                                        FROM(\tSELECT B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,1 AS OrderIndex\n" +
            "\t\t                                        FROM Car25PictureView AS A WITH(NOLOCK)\n" +
            "\t\t                                        INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
            "\t\t                                        WHERE A.Ordercls=1 AND LEN(PicPath)>1\n" +
            "\t\t                                        UNION ALL\n" +
            "\t\t                                        SELECT B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,2 AS OrderIndex\n" +
            "\t\t                                        FROM Car25PictureView AS A WITH(NOLOCK)\n" +
            "\t\t                                        INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
            "\t\t                                        WHERE A.Ordercls=2 AND LEN(PicPath)>1\n" +
            "\t\t                                        UNION ALL\n" +
            "\t\t                                        SELECT B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,3 AS OrderIndex\n" +
            "\t\t                                        FROM Car25PictureView AS A WITH(NOLOCK)\n" +
            "\t\t                                        INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
            "\t\t                                        WHERE A.Ordercls=4 AND LEN(PicPath)>1) AS A\n" +
            "                                        WHERE\tRowIndex=1;\n" +
            "\n" +
            "                                        SELECT SyearId,specid,photoId, PhotoFilepath,RowIndex AS OrderIndex\n" +
            "                                        FROM(SELECT SyearId,SpecId,PicId AS PhotoId,PicFilePath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY SyearId ORDER BY PhotoIsBrand DESC,SeriesOrders,IsTitle DESC,SpecId DESC,PicId DESC) AS RowIndex\n" +
            "\t                                            FROM CarPhotoView WITH(NOLOCK)\n" +
            "                                        WHERE PicClass in (1,53) AND IsClubPhoto<>1) AS A\n" +
            "                                        WHERE RowIndex<=3;")
    List<SeriesPictureEntity> getAllSyearPicture();

    @Select("SELECT SyearId,specid,photoId, PhotoFilepath,RowIndex AS OrderIndex\n" +
            "                                        FROM(SELECT SyearId,SpecId,PicId AS PhotoId,PicFilePath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY SyearId ORDER BY SeriesOrders,SpecId DESC,PicId DESC) AS RowIndex\n" +
            "\t                                            FROM CarPhotoView WITH(NOLOCK)\n" +
            "                                        WHERE PicClass in (1,53) AND PhotoIsBrand=1 AND IsClubPhoto<>1) AS A\n" +
            "                                        WHERE RowIndex<=3")
    List<SeriesPictureEntity> getAllSyearPicture1();

    @Select("SELECT\tSyearId,specid,photoId,PhotoFilepath,OrderIndex\n" +
            "                                        FROM(\tSELECT B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,1 AS OrderIndex\n" +
            "\t\t                                        FROM Car25PictureView AS A WITH(NOLOCK)\n" +
            "\t\t                                        INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
            "\t\t                                        WHERE A.Ordercls=1 AND LEN(PicPath)>1\n" +
            "\t\t                                        UNION ALL\n" +
            "\t\t                                        SELECT B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,2 AS OrderIndex\n" +
            "\t\t                                        FROM Car25PictureView AS A WITH(NOLOCK)\n" +
            "\t\t                                        INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
            "\t\t                                        WHERE A.Ordercls=2 AND LEN(PicPath)>1\n" +
            "\t\t                                        UNION ALL\n" +
            "\t\t                                        SELECT B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,3 AS OrderIndex\n" +
            "\t\t                                        FROM Car25PictureView AS A WITH(NOLOCK)\n" +
            "\t\t                                        INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
            "\t\t                                        WHERE A.Ordercls=4 AND LEN(PicPath)>1) AS A\n" +
            "                                        WHERE\tRowIndex=1")
    List<SeriesPictureEntity> getAllSyearPicture2();

    @Select("SELECT SyearId,specid,photoId, PhotoFilepath,RowIndex AS OrderIndex\n" +
            "                                        FROM(SELECT SyearId,SpecId,PicId AS PhotoId,PicFilePath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY SyearId ORDER BY PhotoIsBrand DESC,SeriesOrders,IsTitle DESC,SpecId DESC,PicId DESC) AS RowIndex\n" +
            "\t                                            FROM CarPhotoView WITH(NOLOCK)\n" +
            "                                        WHERE PicClass in (1,53) AND IsClubPhoto<>1) AS A\n" +
            "                                        WHERE RowIndex<=3")
    List<SeriesPictureEntity> getAllSyearPicture3();


    @Select("SELECT TOP 100000 \n" +
            "\tA.id,\n" +
            "\tA.SpecId,\n" +
            "\tA.SeriesId,\n" +
            "\tA.PicClass,\n" +
            "\tA.PicId,\n" +
            "\tA.PicColorId,\n" +
            "\tA.SpecState,\n" +
            "\tA.SyearId,\n" +
            "\tA.InnerColorId \n" +
            "FROM\n" +
            "\tCarPhotoView AS A WITH ( NOLOCK ) \n" +
            "WHERE\n" +
            "\tpiccolorid IN (2975,256,3682,2930,3241,3105,422,1788,1263,4978,3723,2086,325,3151,2120,459,3518,212,1136,2856) \n" +
            "\tAND picclass IN (1, 3, 10, 12, 13, 14, 50, 51) \n" +
            "ORDER BY\n" +
            "\tNEWID( );")
    List<CarPhotoTestRowEntity> getRound5000SeriesSpecColorPicRows();

    @Select("SELECT TOP 5000 \n" +
            "\tA.id,\n" +
            "\tA.SpecId,\n" +
            "\tA.SeriesId,\n" +
            "\tA.PicClass,\n" +
            "\tA.PicId,\n" +
            "\tA.PicColorId,\n" +
            "\tA.SpecState,\n" +
            "\tA.SyearId,\n" +
            "\tA.InnerColorId \n" +
            "FROM\n" +
            "\tCarPhotoView AS A WITH ( NOLOCK ) \n" +
            "WHERE\n" +
            "\tinnerColorId IN (553,558,19,358,209,7,27,576,466,15,782,574,35,297,147,614,425,418,802,424,446) \n" +
            "\tAND picclass IN (1, 3, 10, 12, 13, 14, 50, 51) \n" +
            "ORDER BY\n" +
            "\tNEWID( );")
    List<CarPhotoTestRowEntity> getRound5000SeriesSpecInnerColorPicRows();
}
