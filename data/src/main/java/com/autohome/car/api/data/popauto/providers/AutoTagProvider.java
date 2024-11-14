package com.autohome.car.api.data.popauto.providers;

import java.util.List;
import java.util.stream.Collectors;

public class AutoTagProvider {

    public String autoTagCarList(List<Integer> tagIds,int minPrice,int maxPrice,int orderid){
        String sql = "select B.SeriesId, B.SpecId,B.FctMaxPrice,B.FctMinPrice,B.SeriesFctMaxPrice,B.SeriesFctMinPrice,B.Seat, B.DeliveryCapacity,Horsepower,place,GearBox,StructId,DriveForm,fuelType,FlowMode,DriveType,specOrdercls from Optimize_AutoTag_Spec as B with(Nolock) where  B.SpecState = 20 ";
        String strwhere = "";
        if (tagIds!=null && tagIds.size() > 0) {
            for (int i = 0; i < tagIds.size(); i++) {
                if(i>=2) break;
                strwhere  += " AND t"+tagIds.get(i)+"="+tagIds.get(i)+"";
            }
        }
        else {
            sql = "select B.SeriesId, B.SpecId,B.FctMaxPrice,B.FctMinPrice,B.SeriesFctMaxPrice,B.SeriesFctMinPrice,B.Seat, B.DeliveryCapacity,Horsepower,place,GearBox,StructId,DriveForm,fuelType,FlowMode,DriveType ,specOrdercls from SpecPriceSellView as B with(Nolock)  where SpecState = 20 ";
        }
        if (minPrice > 0 && maxPrice > 0) {
            strwhere += " and B.FctMinPrice>=#{minPrice} and B.FctMaxPrice<=#{maxPrice}";
        }
        else if(minPrice>0&&maxPrice==0) {
            strwhere += " and B.FctMinPrice>=#{minPrice}";
        }
        else if(maxPrice>0&&minPrice==0) {
            strwhere += " and B.FctMaxPrice<=#{maxPrice} ";
        }
        sql = sql + strwhere;
        if (orderid == 0) {
            sql += " order by B.id ";
        }
        else if (orderid == 1) {
            sql += " order by B.SeriesFctMinPrice";
        }
        else if (orderid == 2) {
            sql += " order by B.SeriesFctMaxPrice desc";
        }
        return sql;
    }


    public String autoTagCarListAutoHome(List<Integer> tagIds,List<Integer> levels,List<Integer> country,int minPrice,int maxPrice,int orderId){
        String sql = "select id ,SeriesId,\n" +
                "        SpecId,FctMinPrice,\n" +
                "                FctMaxPrice,SeriesFctMaxPrice,\n" +
                "                SeriesFctMinPrice,\n" +
                "                DeliveryCapacity,Horsepower,place,\n" +
                "                GearBox,StructId,DriveForm,fuelType,FlowMode,DriveType,electricType,electricKW,isclassic\n" +
                "        FROM [Optimize_AutoTag_Spec] as B with(Nolock)\n" +
                "        where 1=1 ";

        StringBuilder sb = new StringBuilder();
        tagIds.stream().limit(2).forEach(x->{
            sb.append(String.format(" and t%s=%s",x,x));
        });

        if (levels.size() > 0){
            sb.append(" and levelid in("+String.join(",",levels.stream().map(x->Integer.toString(x)).collect(Collectors.toList()))+") ");
        }
        if (country.size() > 0){
            sb.append(" and Country in("+String.join(",",country.stream().map(x->Integer.toString(x)).collect(Collectors.toList()))+") ");
        }

        if (minPrice > 0 && maxPrice > 0)
        {
            sb.append(" and B.FctMinPrice>=#{minPrice} and B.FctMaxPrice<=#{maxPrice}");
        }
        else if (minPrice > 0 && maxPrice == 0) {
            sb.append(" and B.FctMinPrice>=#{minPrice} ");
        }
        else if (maxPrice > 0 && minPrice == 0) {
            sb.append(" and B.FctMaxPrice<=#{maxPrice} ");
        }

        sql = sql +sb;

        if (orderId == 0) //关注度
        {
            sql += " order by B.id ";
        }
        else if (orderId == 1) //价格升序
        {
            sql += " order by B.SeriesFctMinPrice ";
        }
        else if (orderId == 2)//价格降序
        {
            sql += " order by B.SeriesFctMaxPrice desc";
        }
        else
        {
            sql += " order by B.id ";
        }
        return sql;
    }
}
