package com.autohome.car.api.data.popauto.providers;

public class SpecColorProvider {

    public String getAllSeriesColor(){
        return getSeriesColor(0);
    }

    public String getSeriesColor(int seriesId) {

        String sql = "SELECT SeriesId,ColorId,ColorName,ColorValue,SUM(ISNULL(SpecColorPicNumber,0)) AS PicNum,SUM(ISNULL(ClubPicNum,0)) AS ClubPicNum,SpecState\n" +
                "FROM(\n" +
                "   SELECT D.SeriesId,A.ColorId,C.ColorName,C.ColorValue,B.SpecColorPicNumber,E.ClubPicNum,CASE D.SpecState WHEN 30 THEN 20 ELSE D.SpecState END AS SpecState\n" +
                "     FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
                "         LEFT JOIN (SELECT SpecId,ColorId,SUM(PicNumber)  AS SpecColorPicNumber FROM CarSpecPicColorStatistics WITH(NOLOCK) where picclass<200 GROUP BY SpecId,ColorId) AS B ON A.SpecId=B.SpecId AND A.ColorId=B.ColorId\n" +
                "         INNER JOIN Car_Fct_Color AS C WITH(NOLOCK) ON A.ColorId=C.Id\n" +
                "         INNER JOIN SpecView AS D WITH(NOLOCK)  ON A.SpecId=D.SpecId\n" +
                "         LEFT JOIN (\n" +
                "           SELECT SpecId,PicColorId,COUNT(1) AS ClubPicNum \n" +
                "             FROM CarPhotoView WITH(NOLOCK) \n" +
                "            WHERE IsClubPhoto=1 AND PicColorId>0 \n" +
                "         GROUP BY SpecId,PicColorId\n" +
                "         ) AS E ON A.ColorId=E.PicColorId AND A.SpecId=E.SpecId\n" +
                "     %s\n" +
                ") AS Temp\n" +
                "GROUP BY SeriesId,ColorId,ColorName,ColorValue,SpecState";

        sql = String.format(sql, seriesId <= 0 ? "" : "WHERE A.SeriesId = #{seriesId}");
        return sql;
    }

    public String getAllSeriesInnerSpecColor(int seriesId){
        return getSeriesInnerSpecColor(0);
    }

    public String getSeriesInnerSpecColor(int seriesId) {
        return String.format(
                "SELECT SeriesId,ColorId,ColorName,ColorValue,SUM(ISNULL(SpecColorPicNumber,0)) AS PicNum,SUM(ISNULL(ClubPicNum,0)) AS ClubPicNum,SpecState\n" +
                        "FROM(   \n" +
                        "    SELECT D.SeriesId,A.ColorId,C.ColorName,C.ColorValue,B.SpecColorPicNumber,E.ClubPicNum,CASE D.SpecState WHEN 30 THEN 20 ELSE D.SpecState END AS SpecState\n" +
                        "   FROM innerSpecColor AS A WITH(NOLOCK)\n" +
                        "       LEFT JOIN (\n" +
                        "         SELECT SpecId,ColorId,SUM(PicNumber)  AS SpecColorPicNumber \n" +
                        "         FROM CarSpecInnerColorStatistics WITH(NOLOCK) \n" +
                        "         where picclass<200 GROUP BY SpecId,ColorId\n" +
                        "       ) AS B ON A.SpecId=B.SpecId AND A.ColorId=B.ColorId\n" +
                        "       INNER JOIN InnerFctColor AS C WITH(NOLOCK) ON A.ColorId=C.Id\n" +
                        "       INNER JOIN SpecView AS D WITH(NOLOCK)  ON A.SpecId=D.SpecId\n" +
                        "       LEFT JOIN (\n" +
                        "         SELECT SpecId,PicColorId,COUNT(1) AS ClubPicNum \n" +
                        "         FROM CarPhotoView WITH(NOLOCK) \n" +
                        "         WHERE IsClubPhoto=1 AND PicColorId>0 \n" +
                        "         GROUP BY SpecId,PicColorId\n" +
                        "       ) AS E ON A.ColorId=E.PicColorId AND A.SpecId=E.SpecId\n" +
                        "    %s\n" +
                        ") AS Temp\n" +
                        "GROUP BY SeriesId,ColorId,ColorName,ColorValue,SpecState",
                seriesId <= 0 ? "" : "WHERE A.SeriesId = #{seriesId}"
        );
    }

    public String GetSpecPicColorStatisticsBySeriesId(int seriesId){
        if(seriesId <= 3080){
            return String.format("WITH ClubPicInfo\n" +
                    "                                        AS( SELECT SpecId,PicColorId,PicClass,COUNT(PicId) AS ClubPicNum\n" +
                    "                                            FROM CarPhotoView WITH(NOLOCK)\n" +
                    "                                            WHERE SeriesId=@SeriesId and PicColorId>0 and IsClubPhoto=1\n" +
                    "                                            GROUP BY SpecId,PicColorId,PicClass)\n" +
                    "                                        SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,ISNULL(C.ClubPicNum,0) AS ClubPicNumber,\n" +
                    "\t\t                                        CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder  \n" +
                    "                                        FROM CarSpecPicColorStatistics AS A WITH(NOLOCK)\n" +
                    "                                        INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
                    "                                        LEFT  JOIN ClubPicInfo AS C WITH(NOLOCK) ON A.SpecId=C.specId AND A.ColorId=C.PicColorId AND A.PicClass=C.PicClass\n" +
                    "                                        WHERE B.SeriesId=@SeriesId AND A.PicClass<200\n" +
                    "                                        ORDER BY ClassOrder;\n" +
                    "                                --car.api.autohome.com.cn/app_code/Invoke/carpic/PictureView.cs;");
        }
        else {
            return String.format("SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,0 AS ClubPicNumber,\t\t\t\t\n" +
                    "\t\t                                        CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder  \t\t\n" +
                    "                                        FROM CarSpecPicColorStatistics AS A WITH(NOLOCK)\t\t\t\t\n" +
                    "                                        INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\t\t\t\t\n" +
                    "                                        WHERE B.SeriesId=@SeriesId AND A.PicClass<200\t\t\t\t\n" +
                    "                                        ORDER BY ClassOrder;\t  --car.api.autohome.com.cn/app_code/Invoke/carpic/PictureView.cs;");
        }
    }

    public String getSpecColorPicNumBySeriesCV(int seriesId, boolean iscv){
        if (!iscv)
        {
            return  "WITH SpecPicClassNum  AS (\n" +
                    "                                    SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber\n" +
                    "                                    FROM CarSpecPicClassStatistics AS A WITH(NOLOCK) inner join SpecView AS B WITH(NOLOCK) ON A.SpecId = B.specId where A.SeriesId = #{seriesId} and SpecState<= 30\n" +
                    "                                     UNION ALL\n" +
                    "                                    SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber\n" +
                    "                                    FROM CarStopSpecPicClassStatistics AS A WITH(NOLOCK) inner join  SpecView AS B WITH(NOLOCK) ON A.SpecId = B.specId  where A.SeriesId = #{seriesId} and SpecState=40\n" +
                    "\t\t\t                        ) \n" +
                    "                        SELECT A.SpecId,A.SpecYear,A.CarState,B.PicNumber,B.PicClass FROM  CarSpecPictureStatistics as A WITH(NOLOCK)\n" +
                    "                            INNER JOIN  SpecPicClassNum as B on A.SpecId = B.specId\n" +
                    "                        WHERE A.PicNumber >= 3 AND A.SeriesId = #{seriesId} ";
        }
        else
        {
            return "WITH SpecPicClassNum  AS (\n" +
                    "\t\t\t                        SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber\n" +
                    "\t\t                            FROM CarSpecPicClassStatistics AS A WITH(NOLOCK) inner join CV_SpecView AS B WITH(NOLOCK) ON A.SpecId = B.specId where A.SeriesId=#{seriesId} and SpecState<=30\n" +
                    "\t\t\t                            UNION ALL\n" +
                    "\t\t                            SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber\n" +
                    "\t\t                            FROM CarStopSpecPicClassStatistics AS A WITH(NOLOCK) inner join  CV_SpecView AS B WITH(NOLOCK) ON A.SpecId = B.specId  where A.SeriesId =#{seriesId} and SpecState=40\n" +
                    "\t\t\t                        ) \n" +
                    "                        SELECT A.SpecId,A.SpecYear,A.CarState,B.PicNumber,B.PicClass FROM  CarSpecPictureStatistics as A WITH(NOLOCK) \n" +
                    "                         INNER JOIN  SpecPicClassNum as B on A.SpecId = B.specId\n" +
                    "                        WHERE A.PicNumber>=3 AND A.SeriesId = #{seriesId}";
        }
    }

    public String getSpecColorPicByYearId(int yearId, boolean inner){
        if(!inner){
            return "SELECT SyearId,SpecState,ColorId,ColorName,ColorValue,SUM(ISNULL(ClubPicNum,0)) AS ClubPicNum,SUM(ISNULL(SpecColorPicNumber,0)) AS PicNum\n" +
                    "     FROM (\n" +
                    "     SELECT D.SyearId,CASE D.SpecState WHEN 30 THEN 20 ELSE D.SpecState END AS SpecState,A.ColorId,C.ColorName,C.ColorValue,B.SpecColorPicNumber,E.ClubPicNum\n" +
                    "     FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
                    "     LEFT JOIN (SELECT SpecId,ColorId,SUM(PicNumber)  AS SpecColorPicNumber FROM CarSpecPicColorStatistics WITH(NOLOCK) where picclass<200 GROUP BY SpecId,ColorId) AS B ON A.SpecId=B.SpecId AND A.ColorId=B.ColorId\n" +
                    "     INNER JOIN Car_Fct_Color AS C WITH(NOLOCK) ON A.ColorId=C.Id\n" +
                    "     INNER JOIN SpecView AS D WITH(NOLOCK)  ON A.SpecId=D.SpecId\n" +
                    "     LEFT JOIN (SELECT SpecId,PicColorId,COUNT(1) AS ClubPicNum FROM CarPhotoView WITH(NOLOCK) WHERE IsClubPhoto=1 AND PicColorId>0 GROUP BY SpecId,PicColorId) AS E ON A.ColorId=E.PicColorId AND A.SpecId=E.SpecId\n" +
                    "     ) AS A\n" +
                    "\t\twhere syearid=#{yearId}\n" +
                    "GROUP BY SyearId,SpecState,ColorId,ColorName,ColorValue";
        }else{
            return "SELECT SyearId,SpecState,ColorId,ColorName,ColorValue,SUM(ISNULL(ClubPicNum,0)) AS ClubPicNum,SUM(ISNULL(SpecColorPicNumber,0)) AS PicNum\n" +
                    "     FROM (\n" +
                    "     SELECT D.SyearId,CASE D.SpecState WHEN 30 THEN 20 ELSE D.SpecState END AS SpecState,A.ColorId,C.ColorName,C.ColorValue,B.SpecColorPicNumber,E.ClubPicNum\n" +
                    "     FROM innerSpecColor AS A WITH(NOLOCK)\n" +
                    "     LEFT JOIN (SELECT SpecId,ColorId,SUM(PicNumber)  AS SpecColorPicNumber FROM CarSpecInnerColorStatistics WITH(NOLOCK) where picclass<200 GROUP BY SpecId,ColorId) AS B ON A.SpecId=B.SpecId AND A.ColorId=B.ColorId\n" +
                    "     INNER JOIN InnerFctColor AS C WITH(NOLOCK) ON A.ColorId=C.Id\n" +
                    "     INNER JOIN SpecView AS D WITH(NOLOCK)  ON A.SpecId=D.SpecId\n" +
                    "     LEFT JOIN (SELECT SpecId,PicColorId,COUNT(1) AS ClubPicNum FROM CarPhotoView WITH(NOLOCK) WHERE IsClubPhoto=1 AND PicColorId>0 GROUP BY SpecId,PicColorId) AS E ON A.ColorId=E.PicColorId AND A.SpecId=E.SpecId\n" +
                    "     ) AS A\n" +
                    "\t\twhere syearid=#{yearId}\n" +
                    " GROUP BY SyearId,SpecState,ColorId,ColorName,ColorValue";
        }
    }

}
