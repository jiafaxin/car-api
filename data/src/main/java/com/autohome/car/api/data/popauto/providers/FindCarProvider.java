package com.autohome.car.api.data.popauto.providers;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindCarProvider {
    public String findSeries(
              int minPrice
            , int maxPrice
            , java.util.List<java.lang.Integer> levelIds
            , int brandId
            , int gearBox
            , Double minDeliveryCapacity
            , Double maxDeliveryCapacity
            , List<java.lang.Integer> structId
            , int country
            , List<java.lang.Integer> configids
            , int place
            , int fuelType
            , int seat
            , int driveType
    ) {
//            int minPrice = (int)params.get("minPrice");
//            int maxPrice =  (int)params.get("");
//            List<Integer> levelIds = (List<Integer>)params.get("");
//            int brandId = (int)params.get("brandId");
//            int gearBox = (int)params.get("gearBox");
//            Double minDeliveryCapacity = (Double)params.get("minDeliveryCapacity");
//            Double maxDeliveryCapacity = (Double)params.get("maxDeliveryCapacity");
//            List<Integer> structId = (List<Integer>)params.get("structId");
//            int country = (int)params.get("country");
//            List<Integer> configids = (List<Integer>)params.get("configids");
//            int place = (int)params.get("place");
//            int fuelType = (int)params.get("fuelType");
//            int seat = (int)params.get("seat");
//            int driveType = (int)params.get("driveType");

        StringBuilder where = new StringBuilder();
        if(minPrice >0 || minPrice >0){
            if(minPrice == 0){
                where.append(" and (([FctMaxPrice] > #{minPrice} and [FctMaxPrice] <= #{maxPrice} ) or ([FctMinPrice] > #{minPrice} and [FctMinPrice] <= #{maxPrice} )) ");
            }else{
                where.append(" and (([FctMaxPrice] >= #{minPrice} and [FctMaxPrice] <= #{maxPrice} ) or ([FctMinPrice] >= #{minPrice} and [FctMinPrice] <= #{maxPrice} ))");
            }
        }
        if(levelIds!=null && levelIds.size() > 0){
            if(levelIds.size()==1){
                int levelId = levelIds.get(0);
                if(levelId == 14 || levelId == 15){
                    where.append(" and LevelId between 14 and 15 ");
                }else{
                    where.append(" and LevelId=").append(levelId).append(" ");
                }
            }else{
                where.append(String.format(" and LevelId in(%s)",String.join(",",levelIds.stream().map(x->Integer.toString(x)).collect(Collectors.toList()))));
            }
        }
        if(brandId>0){
            where.append(" and brandid=#{brandId}");
        }
        if(gearBox > 0){
            where.append(" and GearBox=#{gearBox}");
        }
        if(minDeliveryCapacity>0 || maxDeliveryCapacity>0){
            where.append(" and DeliveryCapacity >= #{minDeliveryCapacity} and DeliveryCapacity <= #{maxDeliveryCapacity} ");
        }
        if(structId!=null && structId.size()>0){
            if(structId.size() == 1){
                if(structId.get(0)>0) {
                    where.append(" and StructId=#{structId}");
                }
            }else{
                where.append(" and StructId >=5 and StructId<=6");
            }
        }
        if(country>0){
            where.append(" and Country=#{country}");
        }
        if(configids!=null&&configids.size()>0){
            for (Integer configid : configids) {
                where.append(" and c" + configid + "=" + configid);
            }
        }
        if(place>0){
            where.append(" and place=#{place} ");
        }
        if(fuelType>0){
            if(fuelType==5){
                where.append(" and fuelType>=5");
            }else{
                where.append(" and fuelType=#{fuelType}");
            }
        }
        if(seat>0){
            where.append(" and Seat=#{seat} ");
        }
        if(driveType>0){
            where.append(" and driveType= #{driveType}");
        }
        String whereStr = where.toString();

        String sql = "with specInfo as (\n" +
                "\tselect  SeriesId,FctMaxPrice,FctMinPrice, LevelId,DeliveryCapacity,StructId,GearBox,place,Country,fuelType,BrandId,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10, DriveType,Seat from SpecPriceSellView  with(nolock) \n" +
                "\tunion all \n" +
                "\tselect   SeriesId,FctMaxPrice,FctMinPrice,LevelId,DeliveryCapacity,StructId,GearBox,place,Country,fuelType,BrandId,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,DriveType,Seat from SpecPriceWaitSellView  with(nolock)\n" +
                "\tunion all \n" +
                "\tselect   SeriesId, FctMaxPrice,FctMinPrice,LevelId,DeliveryCapacity,StructId,GearBox,place,Country,fuelType,BrandId,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,DriveType,Seat from SpecPriceStopSellView  with(nolock)\n" +
                ")\n" +
                "select distinct  SeriesId \n" +
                "from specInfo ";

        if(StringUtils.isBlank(whereStr)){
            return sql + " where 1<>1 ";
        }else {
            return sql + " where 1=1 " + whereStr;
        }
    }
}
