package com.autohome.car.api.data.popauto.providers;

public class PicClassProvider {

    public String getSeriesPicBySeriesIdPicClass(int seriesId,int specId,int classId,int pageStart,int pageEnd,boolean isCV) {
        String sqlStr = "";
        String strSqlWhere = "";
        if(!isCV){
            sqlStr += "SElECT * FROM　(\n" +
                    "      SELECT PicId,SpecId,picfilepath,PicClass,IsHD,syearId,Syear,SpecState,ROW_NUMBER() OVER(ORDER BY PicId DESC) AS RN FROM (\t\n" +
                    "      SELECT A.id AS picid,A.specid,A.typeid AS picclass,filepath AS PicFilePath,A.isHD,B.spec_year AS syearid,B.syear,B.SpecState FROM car_spec_photo AS A WITH(NOLOCK) INNER JOIN spec_new AS B WITH(NOLOCK) ON A.specid=B.id \n" +
                    "      WHERE A.IsDelete=0 AND A.Owner>=2  %s )\n" +
                    "      AS T ) AS TT \n" +
                    "  WHERE RN BETWEEN #{pageStart} AND #{pageEnd};";
            if (seriesId > 0) {
                strSqlWhere += " And B.parent= #{seriesId} ";
            }
            if (specId > 0) {
                strSqlWhere += " And A.specId= #{specId} ";
            }
            if (classId > 0) {
                strSqlWhere += " And A.typeid= #{classId} ";
            }
        }else{
            sqlStr += "SElECT * FROM　(\n" +
                    "     SELECT PicId,SpecId,picfilepath,PicClass,IsHD,syearId,Syear,SpecState,ROW_NUMBER() OVER(ORDER BY PicId DESC) AS RN FROM (\t\n" +
                    "     SELECT A.PhotoId AS picid,A.specid,A.TypeClassId AS picclass,A.PhotoPath AS PicFilePath,A.isHD,C.syearId AS syearid,0 as syear,C.SpecState FROM CV_Photo AS A WITH(NOLOCK) INNER JOIN CV_SpecView as C with(nolock) on A.specid = C.specId \n" +
                    "      WHERE 1=1 %s )\n" +
                    "     AS T ) AS TT \n" +
                    " WHERE RN  BETWEEN #{pageStart} AND #{pageEnd} ";
            if (seriesId > 0) {
                strSqlWhere += " And C.seriesId= #{seriesId} ";
            }
            if (specId > 0) {
                strSqlWhere += " And A.specId= #{specId} ";
            }
            if (classId > 0) {
                strSqlWhere += " And A.TypeClassId= #{classId} ";
            }
        }
        return String.format(sqlStr, strSqlWhere);
    }

    public String getSeriesPicBySeriesIdPicClassCount(int seriesId,int specId,int classId,boolean isCV) {
        String sqlStr = "";
        String strSqlWhere = "";
        if(!isCV){
            sqlStr += "SELECT COUNT(*) AS num FROM car_spec_photo AS A WITH(NOLOCK) INNER JOIN spec_new AS B WITH(NOLOCK) ON A.specid=B.id \n" +
                    "  WHERE A.IsDelete=0 AND A.Owner>=2  %s ";
            if (seriesId > 0) {
                strSqlWhere += " And B.parent= #{seriesId} ";
            }
            if (specId > 0) {
                strSqlWhere += " And A.specId= #{specId} ";
            }
            if (classId > 0) {
                strSqlWhere += " And A.typeid= #{classId} ";
            }
        }else{
            sqlStr += "SELECT COUNT(*) AS num FROM CV_Photo AS A WITH(NOLOCK) " +
                    "INNER JOIN CV_SpecView as C with(nolock) on A.specid = C.specId WHERE 1=1  %s ";
            if (seriesId > 0) {
                strSqlWhere += " And C.seriesId= #{seriesId} ";
            }
            if (specId > 0) {
                strSqlWhere += " And A.specId= #{specId} ";
            }
            if (classId > 0) {
                strSqlWhere += " And A.TypeClassId= #{classId} ";
            }
        }
        return String.format(sqlStr, strSqlWhere);
    }
}
