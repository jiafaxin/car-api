package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.providers.FindCarProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface FindCarMapper {


    @SelectProvider(value = FindCarProvider.class,method = "findSeries")
    List<Integer> findSeries(int minPrice
            , int maxPrice
            , List<Integer> levelIds
            , int brandId
            , int gearBox
            , Double minDeliveryCapacity
            , Double maxDeliveryCapacity
            , List<Integer> structId
            , int country
            , List<Integer> configids
            , int place
            , int fuelType
            , int seat
            , int driveType
    );
}
