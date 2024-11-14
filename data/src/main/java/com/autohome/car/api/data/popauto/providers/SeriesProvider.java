package com.autohome.car.api.data.popauto.providers;

public class SeriesProvider {

    public String getAllSeriesView(){
        return "SELECT seriesid\n" +
                "\t,seriesName\n" +
                "\t,levelId\n" +
                "\t,brandId\n" +
                "\t,brandName\n" +
                "\t,fctId\n" +
                "\t,fctName\n" +
                "\t,seriesCreateTime\n" +
                "\t,SeriesState\n" +
                "\t,seriesImg\n" +
                "\t,seriesPriceMin\n" +
                "\t,seriesPriceMax\n" +
                "\t,seriesisnewenergy\n" +
                "\t,pricedescription\n" +
                "\t,seriesplace \n" +
                "\t,BrandFirstLetter\n" +
                "\t,FctFirstLetter\n" +
                "\t,SeriesFirstLetter\n" +
                "\t,containelectriccar\n" +
                "\t,seriesSpecNum\n" +
                "\t,newSeriesOrderCls\n" +
                "\t,seriesIsPublic\n" +
                "\t,seriesPhotoNum\n" +
                "\t,seriesRank\n" +
                "\t,levelName\n" +
                "\t,seriesnewrank\n" +
                "\t,seriesConfigFilePath\n" +
                "FROM seriesview WITH(NOLOCK) \n";
    }


    public String getSeriesView(int seriesId){
        return getAllSeriesView().concat("WHERE seriesid =#{seriesId}");
    }


    public String getBase(int seriesId){
        return getAllBase().concat(" WHERE id = #{seriesId}");
    }

    /**
     * 还是车系表
     * @return
     */
    public String getAllBase(){
        return "SELECT id,name\n" +
                "\t  ,newFctid AS BrandId\n" +
                "\t  ,m AS FactId\n" +
                "\t  ,jb AS LevelId\n" +
                "\t  ,img AS logo\n" +
                "\t  ,url AS url\n" +
                "\t  ,newenergy_SeriesId as rId\n" +
                "\t  ,nobgcolorpicurl AS noBgLogo\n" +
                "\t  ,Place AS place\n" +
                "\t  ,isnull(englishName,'') as eName\n" +
                "\t  ,FirstLetter as fl\n" +
                "\t  ,EditTime\n" +
                "  FROM [Brands] WITH(NOLOCK)\n";
    }

    public String getAllElectricSeriesList() {
        return "select A.id,B.seriesName as name,B.SeriesState as state " +
                " from Brands  as A with(nolock) inner join SeriesView as B with(nolock)  on A.id=B.seriesId " +
                " where A.IsNewenergy=1  and B.seriesIsshow=1";
    }

}
