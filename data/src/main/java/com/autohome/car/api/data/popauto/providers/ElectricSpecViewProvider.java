package com.autohome.car.api.data.popauto.providers;

import org.apache.dubbo.common.utils.StringUtils;

public class ElectricSpecViewProvider {
    public String seriesSearchSeriesByPriceLevelKMFueltype(int topnum,String level,int fueltype,int minPrice,int maxPrice,int minKm,int maxKm){
        String where = "";
        if (level != "" && level .equals("suv")  && where.equals("")) {
            where += "   A.levelId in(16,17,18,19,20)";
        }
        if (fueltype > 0 && where.equals("")) {
            where += "   A.fuelType=#{fuelType}";
        }
        if (minPrice >= 0 && maxPrice > 0 && where.equals("")){
            where += " (A.FctMinPrice >=#{minPrice} and A.FctMaxPrice<#{maxPrice})";
        }

        if (minKm >= 0 && maxKm > 0 && where.equals("")){
            where += " (A.mileage >=#{minKm} and A.mileage<#{maxKm})";
        }

        if(StringUtils.isNotBlank(where)){
            where = " and " + where;
        }

        String sql = "select top (#{topnum}) seriesId as [key],seriesRank as [value] from " +
                "( select distinct A.seriesId,B.seriesRank from Electric_SpecView as A  with(nolock) " +
                "inner join SeriesView as B with(nolock) on A.seriesId =B.seriesId " +
                "where (A.SpecState>=20 AND A.SpecState<=30) and  A.specid<1000000  " + where + " " +
                ") as t order by seriesRank desc";

        return sql;
    }

    public String getSeriesRand_ElectricZengCheng(int type){
        switch (type){
            case 0:
            return "SELECT  seriesid ,seriesrank,zhengchezhibao,Max(dianchileixing)dianchileixing,mileage,chongdianshijian,officialFastChargetime,officialSlowChargetime\n" +
                    "                            FROM Electric_SpecView as A WITH(NOLOCK) \n" +
                    "                           \n" +
                    "                            WHERE (SpecState>=20 AND SpecState<=30) and fueltype=1 and specid<1000000\n" +
                    "                            group by  seriesid ,seriesrank,zhengchezhibao,mileage,chongdianshijian,officialFastChargetime,officialSlowChargetime\n" +
                    "                            order by seriesrank desc";
            case 1:
                return "SELECT  seriesid ,seriesrank,zhengchezhibao,Max(dianchileixing)dianchileixing,mileage,chongdianshijian,officialFastChargetime,officialSlowChargetime\n" +
                        "                            FROM Electric_SpecView as A WITH(NOLOCK) \n" +
                        "                           \n" +
                        "                            WHERE (SpecState>=20 AND SpecState<=30) and fueltype=2 and specid<1000000\n" +
                        "                            group by  seriesid ,seriesrank,zhengchezhibao,mileage,chongdianshijian,officialFastChargetime,officialSlowChargetime\n" +
                        "                            order by seriesrank desc";
            default:
                return "SELECT  seriesid ,seriesrank,zhengchezhibao,Max(dianchileixing)dianchileixing,mileage,chongdianshijian,officialFastChargetime,officialSlowChargetime\n" +
                        "                            FROM Electric_SpecView as A WITH(NOLOCK) \n" +
                        "                           \n" +
                        "                            WHERE (SpecState>=20 AND SpecState<=30) and fueltype=4 and specid<1000000\n" +
                        "                            group by  seriesid ,seriesrank,zhengchezhibao,mileage,chongdianshijian,officialFastChargetime,officialSlowChargetime\n" +
                        "                            order by seriesrank desc";

        }
    }
}
