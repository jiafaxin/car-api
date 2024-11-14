package com.autohome.car.api.services.impls;

import com.autohome.car.api.common.*;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AutoCacheServiceImpl implements AutoCacheService {

    final static String BaseKey = "autocache:service:v1:";

    @Autowired
    EhCache ehCache;

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    PicClassMapper picClassMapper;

    @Autowired
    BrandMapper brandMapper;

    @Resource
    private AutoTagMapper autoTagMapper;

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SeriesViewMapper seriesViewMapper;

    @Autowired
    CarSpecLevelMapper carSpecLevelMapper;

    @Autowired
    FactoryMapper factoryMapper;

    @Resource
    private VrSpecMapper vrSpecMapper;

    @Autowired
    SpecPriceStopSellViewMapper specPriceStopSellViewMapper;

    @Autowired
    SYearViewMapper sYearViewMapper;

    @Autowired
    SpecColorMapper specColorMapper;

    @Resource
    private ElectricSpecViewMapper electricSpecViewMapper;

    @Resource
    private FeaturedPictureMapper featuredPictureMapper;

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private CarManuePicMapper carManuePicMapper;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private CarManuePriceMapper carManuePriceMapper;

    @Resource
    private ShowMapper showMapper;

    @Resource
    private ZiXunCarPicVrMapper ziXunCarPicVrMapper;

    @Resource
    private SpecConfigMapper specConfigMapper;

    @Resource
    private ConfigTypeMapper configTypeMapper;

    @Resource
    private CrashTestSeriesMapper crashTestSeriesMapper;

    @Resource
    private SpecMapper specMapper;

    @Autowired
    Car25PictureViewMapper car25PictureViewMapper;

    @Autowired
    ZixunCarpicMapper zixunCarpicMapper;

    @AutoCache(expireIn = 30)
    @Override
    public List<SpecViewEntity>  getSpecItemsBySeries(int seriesId, boolean isCv){
        return specViewMapper.getSpecItemsBySeries(seriesId,isCv);
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<KeyValueDto<Integer,String>> getCar25PictureType(){
        return picClassMapper.getCar25PictureType();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<BrandSeriesStateBaseEntity> getAllSeriesBrands(){
        return brandMapper.getAllSeriesBrands();
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<BrandBaseEntity> getAllBrandName(){
        return brandMapper.getAllBrandInfo();
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<Integer> getSeriesHaveCrashInfo(int seriesId){
        return seriesMapper.getSeriesHaveCrashInfo(seriesId);
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<SpecViewEntity> getSpecBySeries(int seriesId,boolean isCV){
        return specViewMapper.getSpecBySeries(seriesId,isCV);
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<CrashTestDetailEntity> getCrashTestBySeriesId(int seriesId, int standardId){
        return seriesMapper.getCrashTestBySeriesId(seriesId,standardId);
    }

    @AutoCache(expireIn = 60)
    @Override
    public byte[] getAllSeriesList(){
        List<SeriesInfoAllEntity> allSeriesList = seriesMapper.getAllSeriesList();
        String json = JsonUtils.toString(allSeriesList);
        return GZIPUtils.compress(json.getBytes(StandardCharsets.UTF_8));
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<BrandViewEntity> getAllBrandItems(){
        return specViewMapper.getAllBrandItems();
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<BrandViewEntity> getAllElectricBrandItems(){
        return specViewMapper.getAllElectricBrandItems();
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<SeriesInfoEntity> getAllSeriesItems(){
        return seriesViewMapper.getAllSeriesItems();
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<FactoryInfoEntity> getAllFactoryInfos(){
        return  factoryMapper.getAllFactoryInfos();
    }

    @AutoCache(expireIn = 30)
    @Override
    public List<FactoryInfoEntity> getAllFactoryInfoSortedLetter(){
        return  factoryMapper.getAllFactoryInfoSortedLetter();
    }

    @Override
    @AutoCache(expireIn = 30)
    public List<Integer> getAllSeriesHaveCrashInfo(){
        return seriesMapper.getAllSeriesHaveCrashInfo();
    }
    @AutoCache(expireIn = 30)
    @Override
    public List<BFSInfoEntity> getBFSInfoByFctId(int fctId) {
        return seriesViewMapper.getBFSInfoByFctId(fctId);
    }


    @Override
    @AutoCache(expireIn = 60)
    public List<SpecPriceViewEntity> carSpecPriceBySeriesId(int seriesId){
        return specPriceStopSellViewMapper.carSpecPriceBySeriesId(seriesId);
    }

    @Override
    @AutoCache(expireIn = 60)
    public List<SpecPriceViewEntity> carSpecPriceSellBySeriesId(int seriesId){
        return specPriceStopSellViewMapper.carSpecPriceSellBySeriesId(seriesId);
    }

    @Override
    @AutoCache(expireIn = 60)
    public List<SpecPriceViewEntity> carSpecPriceWaitSellBySeriesId(int seriesId){
        return specPriceStopSellViewMapper.carSpecPriceWaitSellBySeriesId(seriesId);
    }

    @Override
    @AutoCache(expireIn = 60)
    public List<SYearViewEntity> getSYearViewByYearId(int yearId){
        return sYearViewMapper.getSYearViewByYearId(yearId);
    }

    @Override
    @AutoCache(expireIn = 60)
    public List<SpecYearColorEntity> getSpecColorPicByYearId(int yearId, boolean inner){
        return specColorMapper.getSpecColorPicByYearId(yearId, inner);
    }

    @Override
    @AutoCache(expireIn = 60)
    public List<SpecStateEntity> getSpecListBySeriesId(int seriesId, boolean isCv){
        return specViewMapper.getSpecListBySeriesId(seriesId, isCv);
    }

    @Override
    @AutoCache(expireIn = 60)
    public List<SeriesFctEntity> getSeriesByFctId(int fctId){
        return seriesViewMapper.getSeriesByFctId(fctId);
    }

    @Override
    @AutoCache(expireIn = 30)
    public int getSeriesHotCount(String seriesIspublic){
        return seriesViewMapper.getSeriesHotCount(seriesIspublic);
    }

    @Override
    @AutoCache(expireIn = 30)
    public List<SeriesHotEntity> getSeriesHot(int start,int end,String seriesIspublic){
        return seriesViewMapper.getSeriesHot(start,end,seriesIspublic);
    }

    @Override
    @AutoCache(expireIn = 30)
    public List<Integer> getSeries25Pic(int seriesid){
        return car25PictureViewMapper.getSeries25Pic(seriesid);
    }

    @Override
    @AutoCache(expireIn = 30)
    public List<ZixunCarpicEntity> getZixunCarpicBig(){
        return zixunCarpicMapper.getZixunCarpicBig();
    }

    @Override
    @AutoCache(expireIn = 30)
    public List<ZixunCarpicEntity> getZixunCarpicSmall(){
        return zixunCarpicMapper.getZixunCarpicSmall();
    }

    @Override
    @AutoCache(expireIn = 30)
    public List<Integer> getCrashTestSeriesList(){
        return seriesMapper.getCrashTestSeriesList();
    }


    @AutoCache(expireIn = 60)
    @Override
    public List<ElectricSpecViewEntity> getElectricInfoByBrandId(int brandId) {
        return electricSpecViewMapper.getElectricInfoByBrandId(brandId);
    }

    @AutoCache(expireIn = 60)
    @Override
    public List<SeriesStateEntity> getSeriesInfoByBrandIdAndState(String tableName, int brandId) {
        return seriesViewMapper.getSeriesInfoByBrandIdAndState(tableName,brandId);
    }
    @AutoCache(expireIn = 60)
    @Override
    public List<SeriesCountryEntity> getSeriesCountryAll() {
        return seriesViewMapper.getSeriesCountryAll();
    }
    @AutoCache(expireIn = 20,removeIn = 120)
    @Override
    public List<BrandInfoEntity> getBrandInfoAll() {
        return brandMapper.getBrandInfoAll();
    }

    @AutoCache(expireIn = 20,removeIn = 120)
    @Override
    public List<ConfigBaseEntity> getConfigItemAll() {
        return configMapper.getConfigItemAll();
    }

    @AutoCache(expireIn = 20,removeIn = 120)
    @Override
    public List<CarManueEntity> getCarManuePicBrandAll() {
        return carManuePicMapper.getCarManuePicBrandAll();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<ElectricSpecParamEntity> getEleSpecSpecViewById(int specId){
        return electricSpecViewMapper.getEleSpecSpecViewById(specId);
    }
    @AutoCache(expireIn = 20,removeIn = 120)
    @Override
    public List<KeyValueDto<Integer, String>> getFeaturedTypeAll() {
        return featuredPictureMapper.getFeaturedTypeAll();
    }
    @AutoCache(expireIn = 60)
    @Override
    public List<Integer> getSpecListByDate(Date startDate, Date endDate) {
        List<Integer> specListByDate = specViewMapper.getSpecListByDate(startDate, endDate);
        if(!CollectionUtils.isEmpty(specListByDate)){
            return specListByDate;
        }
        return null;
    }

    @AutoCache(expireIn = 60)
    @Override
    public KeyValueDto<Integer, String> getGroupByName(String brandName) {
        return groupMapper.getGroupByName(brandName);
    }

    @AutoCache(expireIn = 20,removeIn = 120)
    @Override
    public List<CarManueEntity> getAllCarManuePrice() {
        return carManuePriceMapper.getAllCarManuePrice();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<CarManueBaseEntity> getCarManuePriceByFirstLetter(String firstLetter) {
        return carManuePriceMapper.getCarManuePriceByFirstLetter(firstLetter);
    }

    @AutoCache(expireIn = 2*60)
    @Override
    public KeyValueDto<Integer,String> getSeriesIdBySeriesName(String seriesName) {
        return brandMapper.getSeriesIdBySeriesName(seriesName);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<GBrandEntity> getGBrandsAll() {
        return seriesMapper.getGBrandsAll();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<CrashSeriesEntity> getCrashTestData(int orderType, int standardId) {
        return crashTestSeriesMapper.getCrashTestData(orderType, standardId);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<CrashCnCapSeriesEntity> getCrashCnCapTestData() {
        return crashTestSeriesMapper.getCrashCnCapTestData();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<SeriesViewSimpInfo> getSeriesViewInfo() {
        return seriesViewMapper.getSeriesViewInfo();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<Integer> getAllValidSeriesIds() {
        return seriesMapper.getAllValidSeriesIds();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<BrandInfoEntity> getPavilionBrands(int showId, int pavilionId) {
        return brandMapper.getPavilionBrands(showId, pavilionId);
    }


    @AutoCache(expireIn = 120)
    @Override
    public List<ShowCarsViewEntity> getShowCarsInfoByPavilionId(int showId, int pavilionId) {
        return showMapper.getShowCarsInfoByPavilionId(showId, pavilionId);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<ShowCarsViewEntity> getEvCarShowInfoByShowId(int showId) {
        return showMapper.getEvCarShowInfoByShowId(showId);
    }
    @AutoCache(expireIn = 120)
    @Override
    public List<ShowCarsViewEntity> getShowCarsInfoByShowId(int showId) {
        return showMapper.getShowCarsInfoByShowId(showId);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<ShowCarsViewEntity> getEvShowCarsNewEnergyByShowId(int showId) {
        return showMapper.getEvShowCarsNewEnergyByShowId(showId);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<ZiXunCarPicVrEntity> getIndexSlideVr() {
        return ziXunCarPicVrMapper.getIndexSlideVr();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<ConfItemEntity> getSpecConfItem(int specId) {
        return specConfigMapper.getSpecConfItem(specId);
    }
    @AutoCache(expireIn = 120)
    @Override
    public List<ConfItemEntity> getCvSpecConfItem(int specId) {
        return specConfigMapper.getCvSpecConfItem(specId);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<KeyValueDto<Integer, String>> getAllConfigItem() {
        return configMapper.getAllConfigItem();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<KeyValueDto<Integer, String>> getAllConfigType() {
        return configTypeMapper.getAllConfigType();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<BrandPicListEntity> getPicBrandListAll(){
        return brandMapper.getPicBrandListAll();
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<BrandPicListEntity> getBrandShowByPavLetter(int showId, String pavList, String letter){
        return brandMapper.getBrandShowByPavLetter(showId, pavList, letter);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<PicPointLocationEntity> getPicLocation(int specId){
        return picClassMapper.getPicLocation(specId);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<PicConfigRelationEntity> getPicConfigRelation(int specId){
        return picClassMapper.getPicConfigRelation(specId);
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<Car25LocationPicViewEntity> get25PointLocation(int seriesid, int pointLocationId){
        return car25PictureViewMapper.get25PointLocation(seriesid, pointLocationId);
    }

    @AutoCache(expireIn = 60)
    @Override
    public List<Integer> getNewCarSpecIds(){
        return specMapper.getNewCarSpecIds();
    }

    @AutoCache(expireIn = 60)
    @Override
    public Map<Integer,Integer> getSpecJianShuiMap(List<Integer> specIds){
        Map<Integer, Integer> result = new HashMap<>();
        List<KeyValueDto<Integer,Integer>> jianshuiList = specMapper.getSpecJianShuiList(specIds);
        if(CollectionUtils.isEmpty(jianshuiList)){
            return result;
        }
        for (KeyValueDto<Integer, Integer> keyValue : jianshuiList) {
            if(keyValue == null){
                continue;
            }
            result.put(keyValue.getKey(), keyValue.getValue());
        }

        return result;
    }

    @AutoCache(expireIn = 120)
    @Override
    public List<KeyValueDto<Integer, Integer>> getSeriesByPageLevelId(int levelId, SpecStateEnum state, int start, int end){
        return seriesViewMapper.getSeriesByPageLevelId(levelId, state, start, end);
    }

    @AutoCache(expireIn = 120)
    @Override
    public int getSeriesCountByLevelId(int levelId, SpecStateEnum state){
        return seriesViewMapper.getSeriesCountByLevelId(levelId, state);
    }

    @AutoCache(expireIn = 30,removeIn = 120)
    @Override
    public List<KeyValueDto<Integer,String>> getShowCarsImg(int showId, int seriesId){
        return showMapper.getShowCarsImg(showId, seriesId);
    }

    @AutoCache(expireIn = 30,removeIn = 120)
    @Override
    public KeyValueDto<Integer,Integer> getFctGBrandsById(int seriesId){
        return seriesMapper.getFctGBrandsById(seriesId);
    }

    @Override
    @AutoCache(expireIn = 30)
    public List<Integer> getHotBrand(){
        return brandMapper.getHotBrand();
    }

    @Override
    @AutoCache(expireIn = 60)
    public List<BrandStateEntity> getBrandBaseAll() {
        return brandMapper.getBrandBaseAll();
    }

}
