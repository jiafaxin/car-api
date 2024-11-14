package com.autohome.car.api.data.popauto.providers;

public class CarPhotoViewProvider {

    public String getAllSeriesPicture(){
        return getSeriesPicture(0);
    }

    public String getSeriesPicture(int seriesId){
        String sql = "SELECT SeriesId,syearId,state,specid,photoId, PhotoFilepath,RowIndex AS OrderIndex\n" +
                "FROM(  \n" +
                "   SELECT SeriesId,syearId,SpecState as state,SpecId,PicId AS PhotoId,PicFilePath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY SeriesId ORDER BY PhotoIsBrand DESC,SeriesOrders,IsTitle DESC,SpecId DESC,PicId DESC) AS RowIndex\n" +
                "   FROM CarPhotoView WITH(NOLOCK)\n" +
                "   WHERE PicClass in (1,53) AND IsClubPhoto<>1 %s \n" +
                ") AS A\n" +
                "WHERE RowIndex<=3";

        sql = String.format(sql,seriesId <= 0 ? "" : "AND SeriesId = #{seriesId}");

        return sql;
    }

    public String getAllSeriesPicture2(){
        String sql = "SELECT\tseriesId,SpecState as state, SyearId,specid,photoId,PhotoFilepath,OrderIndex\n" +
                "FROM(\t\n" +
                "\tSELECT B.parent as seriesId,B.SpecState,  B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,1 AS OrderIndex\n" +
                "\tFROM Car25PictureView AS A WITH(NOLOCK)\n" +
                "\tINNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
                "\tWHERE A.Ordercls=1 AND LEN(PicPath)>1\n" +
                "\tUNION ALL\n" +
                "\tSELECT B.parent as seriesId,B.SpecState, B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,2 AS OrderIndex\n" +
                "\tFROM Car25PictureView AS A WITH(NOLOCK)\n" +
                "\tINNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
                "\tWHERE A.Ordercls=2 AND LEN(PicPath)>1\n" +
                "\tUNION ALL\n" +
                "\tSELECT B.parent as seriesId,B.SpecState, B.spec_year AS SyearId,B.Id AS SpecId,A.PicId AS photoId,PicPath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY B.spec_year ORDER BY  A.SpecId DESC) RowIndex,3 AS OrderIndex\n" +
                "\tFROM Car25PictureView AS A WITH(NOLOCK)\n" +
                "\tINNER JOIN Spec_New AS B WITH(NOLOCK) ON A.specid=B.Id\n" +
                "\tWHERE A.Ordercls=4 AND LEN(PicPath)>1\n" +
                ") AS A WHERE\tRowIndex=1;";
        return sql;
    }

    public String getAllSeriesPicture3(){
        String sql = "SELECT SeriesId,SpecState as state,SyearId,specid,photoId, PhotoFilepath,RowIndex AS OrderIndex\n" +
                "FROM(\n" +
                "SELECT SeriesId,SpecState, SyearId,SpecId,PicId AS PhotoId,PicFilePath AS PhotoFilepath,ROW_NUMBER() OVER(PARTITION BY SyearId ORDER BY PhotoIsBrand DESC,SeriesOrders,IsTitle DESC,SpecId DESC,PicId DESC) AS RowIndex\n" +
                "FROM CarPhotoView WITH(NOLOCK)\n" +
                "WHERE PicClass in (1,53) AND IsClubPhoto<>1\n" +
                ") AS A WHERE RowIndex<=3;";
        return sql;
    }
}
