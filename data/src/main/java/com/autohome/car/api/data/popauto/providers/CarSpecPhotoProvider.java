package com.autohome.car.api.data.popauto.providers;

public class CarSpecPhotoProvider {

    public String getAllSeriesTypePhoto() {
        return "SELECT SeriesId as [key],FilePath as [value]\n" +
                "FROM(\tSELECT\tB.Parent AS SeriesId,A.FilePath,ROW_NUMBER() OVER(PARTITION BY B.Parent ORDER BY A.Id DESC) AS PartIndex\n" +
                "\t\tFROM\tcar_spec_photo AS A WITH(NOLOCK)\n" +
                "\t\tINNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=B.Id\n" +
                "\t\tWHERE A.TypeId=52 AND A.IsDelete=0\n" +
                "\t\tUNION ALL\n" +
                "\t\tSELECT\tB.SeriesId,A.PhotoPath AS FilePath,ROW_NUMBER() OVER(PARTITION BY B.SeriesId ORDER BY A.PhotoId DESC) AS PartIndex\n" +
                "        FROM \tCV_Photo AS A WITH(NOLOCK) inner join CV_SpecView as B on A.specId = B.specId\n" +
                "        WHERE A.TypeClassId=52) AS A\n" +
                "WHERE PartIndex=1";
    }

    public String getSeriesTypePhotoBySeriesId(Integer seriesId) {
        return getAllSeriesTypePhoto().concat(" AND SeriesId = #{seriesId}");
    }

}
