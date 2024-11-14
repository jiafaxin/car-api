package com.autohome.car.api.services;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.SpecStateEnum;
import com.autohome.car.api.data.popauto.entities.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AutoCacheService {

    List<SpecViewEntity>  getSpecItemsBySeries(int seriesId, boolean isCv);

    List<KeyValueDto<Integer,String>> getCar25PictureType();

    List<BrandBaseEntity> getAllBrandName();

    List<Integer> getSeriesHaveCrashInfo(int seriesId);

    List<SpecViewEntity> getSpecBySeries(int seriesId,boolean isCV);

    List<CrashTestDetailEntity> getCrashTestBySeriesId(int seriesId, int standardId);

    byte[] getAllSeriesList();

    List<BrandViewEntity> getAllBrandItems();

    List<BrandViewEntity> getAllElectricBrandItems();

    List<SeriesInfoEntity> getAllSeriesItems();

    List<BrandSeriesStateBaseEntity> getAllSeriesBrands();

    List<FactoryInfoEntity> getAllFactoryInfos();

     List<FactoryInfoEntity> getAllFactoryInfoSortedLetter();

     List<Integer> getAllSeriesHaveCrashInfo();

    List<BFSInfoEntity> getBFSInfoByFctId(int fctId);

    List<SpecPriceViewEntity> carSpecPriceBySeriesId(int seriesId);

    List<SpecPriceViewEntity> carSpecPriceWaitSellBySeriesId(int seriesId);

    List<SpecPriceViewEntity> carSpecPriceSellBySeriesId(int seriesId);

    List<SYearViewEntity> getSYearViewByYearId(int yearId);

    List<SpecYearColorEntity> getSpecColorPicByYearId(int yearId, boolean inner);

     List<SpecStateEntity> getSpecListBySeriesId(int seriesId, boolean isCv);

    List<SeriesFctEntity> getSeriesByFctId(int fctId);


    List<ElectricSpecViewEntity> getElectricInfoByBrandId(int brandId);

    List<SeriesStateEntity> getSeriesInfoByBrandIdAndState(String tableName,int brandId);

    List<SeriesCountryEntity> getSeriesCountryAll();

    List<BrandInfoEntity> getBrandInfoAll();

    List<ConfigBaseEntity> getConfigItemAll();

    List<CarManueEntity> getCarManuePicBrandAll();

    List<ElectricSpecParamEntity> getEleSpecSpecViewById(int specId);

    List<SeriesViewSimpInfo> getSeriesViewInfo();

    List<Integer> getAllValidSeriesIds();

    List<BrandInfoEntity> getPavilionBrands(int showId, int pavilionId);

    List<ShowCarsViewEntity> getShowCarsInfoByPavilionId(int showId, int pavilionId);

    List<ShowCarsViewEntity> getEvCarShowInfoByShowId(int showId);

    List<ShowCarsViewEntity> getShowCarsInfoByShowId(int showId);

    List<ShowCarsViewEntity> getEvShowCarsNewEnergyByShowId(int showId);

    List<ZiXunCarPicVrEntity> getIndexSlideVr();

    List<ConfItemEntity> getSpecConfItem(int specId);
    List<ConfItemEntity> getCvSpecConfItem(int specId);

    List<KeyValueDto<Integer,String>> getAllConfigItem();

    List<KeyValueDto<Integer,String>> getAllConfigType();

    List<KeyValueDto<Integer,String>> getFeaturedTypeAll();

    List<Integer> getSpecListByDate(Date startDate, Date endDate);

    KeyValueDto<Integer,String> getGroupByName(String brandName);

    List<CarManueEntity> getAllCarManuePrice();

    List<CarManueBaseEntity> getCarManuePriceByFirstLetter(String firstLetter);

    KeyValueDto<Integer,String> getSeriesIdBySeriesName(String seriesName);

    List<GBrandEntity> getGBrandsAll();

    List<BrandPicListEntity> getPicBrandListAll();

    List<BrandPicListEntity> getBrandShowByPavLetter(int showId, String pavList, String letter);

    List<PicPointLocationEntity> getPicLocation(int specId);

    List<PicConfigRelationEntity> getPicConfigRelation(int specId);

    List<Car25LocationPicViewEntity> get25PointLocation(int seriesid, int pointLocationId);

    List<Integer> getNewCarSpecIds();

    Map<Integer,Integer> getSpecJianShuiMap(List<Integer> specIds);

    List<KeyValueDto<Integer, Integer>> getSeriesByPageLevelId(int levelId, SpecStateEnum state, int start, int end);

    int getSeriesCountByLevelId(int levelId, SpecStateEnum state);

    List<KeyValueDto<Integer,String>> getShowCarsImg(int showId, int seriesId);

    KeyValueDto<Integer,Integer> getFctGBrandsById(int seriesId);


    List<CrashSeriesEntity> getCrashTestData(int orderType, int standardId);

    List<CrashCnCapSeriesEntity> getCrashCnCapTestData();
    int getSeriesHotCount(String seriesIspublic);

    List<SeriesHotEntity> getSeriesHot(int start,int end,String seriesIspublic);

    List<Integer> getSeries25Pic(int seriesid);

    List<ZixunCarpicEntity> getZixunCarpicBig();

    List<ZixunCarpicEntity> getZixunCarpicSmall();

    List<Integer> getCrashTestSeriesList();

    List<Integer> getHotBrand();

    List<BrandStateEntity> getBrandBaseAll();

}
