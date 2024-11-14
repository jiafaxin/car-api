package com.autohome.car.api.services.basic.solr;

import autohome.rpc.car.car_api.v1.new_energy.DingzhiSeriesResultResponse;
import autohome.rpc.car.car_api.v1.new_energy.DingzhiSpecResultResponse;
import com.alibaba.fastjson2.JSON;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.properties.SearchSeriesProperties;
import com.autohome.car.api.data.popauto.querys.SearchParams;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.SeriesConfig;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 车系查找相关方法
 */
@Service
public class SearchSeriesService {
    private static final String[] FIELDSARRAY = {"SeriesId","id"};
    private static final String[] SERIESFIELDS = {"SeriesId","BrandId","LevelId","id","fueltypedetail"};
    private static final String[] SPECFIELDS = {"id","fueltypedetail","SpecState","electricKW","endurancemileage","syearid","syear","FctMinPrice","FctMaxPrice"};
    private static Logger logger = LoggerFactory.getLogger(SearchSeriesService.class);

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrSearchFilterService solrSearchFilterService;

    @Autowired
    private SeriesConfigService seriesConfigService;

    @Autowired
    private SpecBaseService specBaseService;

    @Resource
    SearchSeriesProperties searchSeriesProperties;

    @Resource
    private SeriesBaseService seriesBaseService;
    /**
     * 车系查找
     * @param sp
     * @return
     */
    public Pair<Integer, Integer> getSeriesSpecNum(SearchParams sp) {
        String ukid = UUID.randomUUID().toString();
        long methodStart = System.currentTimeMillis();

        List<SimpleQuery> fqs =solrSearchFilterService.getFilterQueryByCondition(sp);

        Query query = new SimpleQuery("*:*");
        for (SimpleQuery fq : fqs) {
            query.addFilterQuery(fq);
        }

        if(sp.getSorttype() != null){
            query.addSort(solrSearchFilterService.getSortByCondition(sp));
        }

        ((SimpleQuery) query).addProjectionOnFields(FIELDSARRAY);

        SolrPageRequest pageRequest = new SolrPageRequest(0, 99999);
        query.setPageRequest(pageRequest);
        //按照车系分组
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.setTotalCount(true);
        //其实是不限制
        groupOptions.setLimit(10000);
        groupOptions.addGroupByField("SeriesId");
        query.setGroupOptions(groupOptions);

        GroupResult<searchSeriesResult> gr = null;
        try {
            gr = solrTemplate.queryForGroupPage(searchSeriesProperties.getCorename(), query, searchSeriesResult.class).getGroupResult("SeriesId");
        } catch (Exception e) {
            System.out.println(e);
            logger.error("请求异常-" + JSON.toJSONString(sp) + ":", e);
            return new ImmutablePair<>(0, 0);
        }

        long timeUsed = System.currentTimeMillis() - methodStart;
        if (timeUsed >= 1000) {
            logger.error(ukid + "|search-cars接口请求超时: " + timeUsed);
        }

        int groupMatches = gr.getMatches();
        int groupsCount = (int)gr.getGroupEntries().getTotalElements();

        return new ImmutablePair<>(groupMatches, groupsCount);
    }

    /**
     * 车系查找
     * @param sp
     * @return
     */
    public DingzhiSeriesResultResponse getSeriesResult(SearchParams sp) {
        DingzhiSeriesResultResponse.Builder builder = DingzhiSeriesResultResponse.newBuilder();
        DingzhiSeriesResultResponse.Result.Builder result = DingzhiSeriesResultResponse.Result.newBuilder();

        Query query = new SimpleQuery("*:*");
        List<SimpleQuery> fqs = solrSearchFilterService.getFilterQueryByCondition(sp);
        for (SimpleQuery fq : fqs) {
            query.addFilterQuery(fq);
        }

        //排序
        query.addSort(solrSearchFilterService.getSortByCondition(sp));

        //字段
        ((SimpleQuery) query).addProjectionOnFields(SERIESFIELDS);

        //分页
        SolrPageRequest pageRequest = new SolrPageRequest(0, 99999);
        query.setPageRequest(pageRequest);

        Page<searchSeriesResult> page = solrTemplate.query(searchSeriesProperties.getCorename(), query, searchSeriesResult.class);
        List<searchSeriesResult> specInfos = page.getContent();
        if (CollectionUtils.isEmpty(specInfos)) {
            return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }

        int totalpage = 0;
        int seriesNum = (int)specInfos.stream().map(searchSeriesResult::getSeriesId).distinct().count();
        if (seriesNum > 0) {
            totalpage = (int) Math.ceil((double) seriesNum / sp.getPagesize());
        }

        List<searchSeriesResult> listS = ToolUtils.deepCopyList(specInfos);
        List<Map<String, Integer>> seriesItems = new ArrayList<>();
        for(searchSeriesResult row : listS){
            Map<String, Integer> k = new HashMap<String, Integer>() {
                {
                    put("SeriesId", row.getSeriesId());
                    put("BrandId", row.getBrandId());
                    put("LevelId", row.getLevelId());
                }
            };
            if(!seriesItems.contains(k)){
                seriesItems.add(k);
            }
        }
        Map<Integer, Set<Integer>> groupedFuelIds = listS.stream()
                .collect(Collectors.groupingBy(
                        searchSeriesResult::getSeriesId,
                        Collectors.mapping(searchSeriesResult::getFueltypedetail, Collectors.toSet())
                ));
        Map<Integer, Long> groupedCountById = listS.stream()
                .collect(Collectors.groupingBy(
                        searchSeriesResult::getSeriesId,
                        Collectors.counting()
                ));

        int skip = sp.getPageindex() > 1 ? sp.getPageindex() - 1 : 0;
        int startIndex = skip * sp.getPagesize();
        int endIndex = Math.min(seriesItems.size(), (skip + 1) * sp.getPagesize());
        //起始页大于总数量
        if(startIndex >= seriesItems.size()){
            result.setTotlepage(totalpage).setTotalcount(seriesNum);
            return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }
        List<Map<String, Integer>> sublist = seriesItems.subList(startIndex, endIndex);

        List<Integer> seriesIds = sublist.stream().map(x -> x.get("SeriesId")).collect(Collectors.toList());
        Map<Integer, SeriesConfig> seriesMap = seriesConfigService.getMap(seriesIds);

        for (Map<String, Integer> item : sublist) {
            int seriesId = item.get("SeriesId");
            SeriesConfig series = seriesMap != null && seriesMap.containsKey(seriesId) ? seriesMap.get(seriesId) : null;
            if(series == null){
                continue;
            }
            DingzhiSeriesResultResponse.Result.SeriesItem.Builder itemBuilder = DingzhiSeriesResultResponse.Result.SeriesItem.newBuilder();
            itemBuilder.setSeriesid(seriesId);
            itemBuilder.setSeriesname(series.getName());
            itemBuilder.setBrandid(series.getBrandid());
            itemBuilder.setBrandname(series.getBrandname());
            itemBuilder.setLeveid(series.getLevelid());
            itemBuilder.setLevelname(series.getLevelname());
            itemBuilder.setSeriespricmin(series.getTempMinPrice());
            itemBuilder.setSeriespricmax(series.getTempMaxPrice());
            itemBuilder.setSeriespicurl(series.getPnglogo());
            itemBuilder.addAllEndurancemileage(series.getElectricmotormileage());
            itemBuilder.setCarcount(Math.toIntExact(groupedCountById.get(seriesId)));

            Set<Integer> fuelList = groupedFuelIds.get(seriesId);
            for(Integer fuelId : fuelList){
                DingzhiSeriesResultResponse.Result.SeriesItem.FuelClass.Builder fuelCls = DingzhiSeriesResultResponse.Result.SeriesItem.FuelClass.newBuilder();
                fuelCls.setId(fuelId);
                fuelCls.setName(CommonFunction.carFuel(fuelId));
                itemBuilder.addFueltype(fuelCls);
            }
            result.addSeriesitmes(itemBuilder);
        }

        result.setTotlepage(totalpage).setTotalcount(seriesNum);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    /**
     * 车型查找-新车品牌车系列表页搜索功能
     * @param sp
     * @return
     */
    public DingzhiSpecResultResponse getSpecResult(SearchParams sp) {
        DingzhiSpecResultResponse.Builder builder = DingzhiSpecResultResponse.newBuilder();
        DingzhiSpecResultResponse.Result.Builder result = DingzhiSpecResultResponse.Result.newBuilder();
        Query query = new SimpleQuery("*:*");
        List<SimpleQuery> fqs =solrSearchFilterService.getFilterQueryByCondition(sp);
        for (SimpleQuery fq : fqs) {
            query.addFilterQuery(fq);
        }

        //排序
        query.addSort(solrSearchFilterService.getSortByCondition(sp));

        //字段
        ((SimpleQuery) query).addProjectionOnFields(SPECFIELDS);

        //分页
        SolrPageRequest pageRequest = new SolrPageRequest(0, 99999);
        query.setPageRequest(pageRequest);
        //车系赋值
        int tempSeriesId = CommonFunction.getStringToInt(sp.getSeriesid(), 0);
        result.setSeriesid(tempSeriesId);
        SeriesBaseInfo series = seriesBaseService.get(tempSeriesId).join();
        if(series != null){
            result.setSeriesname(null != series.getName() ? series.getName() : "");
            result.setSeriespricmax(series.getSeriesPriceMax());
            result.setSeriespricmin(series.getSeriesPriceMin());
            result.setSeriespicurl(null != series.getNoBgLogo() ? ImageUtil.getFullImagePath(series.getNoBgLogo()) : "");
        }
        Page<searchSeriesResult> page = solrTemplate.query(searchSeriesProperties.getCorename(), query, searchSeriesResult.class);
        List<searchSeriesResult> specInfos = page.getContent();
        if (CollectionUtils.isEmpty(specInfos)) {
            return builder.setReturnCode(0).setResult(result).setReturnMsg("成功").build();
        }

        List<Integer> specIds = specInfos.stream().map(searchSeriesResult::getSpecId).collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> specMap = specBaseService.getMap(specIds);
        for (searchSeriesResult item : specInfos) {
            DingzhiSpecResultResponse.Result.SpecItem.Builder itemBuilder = DingzhiSpecResultResponse.Result.SpecItem.newBuilder();
            itemBuilder.setSpecid(item.getSpecId());
            itemBuilder.setSpecname(specMap != null && specMap.containsKey(item.getSpecId()) ? specMap.get(item.getSpecId()).getSpecName() : "");
            itemBuilder.setEnergytypeid(item.getFueltypedetail());
            itemBuilder.setEnergytypename(CommonFunction.carFuel(item.getFueltypedetail()));
            itemBuilder.setSpecstate(item.getSpecState());
            itemBuilder.setElectrickw(item.getElectricKW());
            itemBuilder.setMileage(item.getEndurancemileage());
            itemBuilder.setYearid(item.getSyearid());
            itemBuilder.setYearname(String.valueOf(item.getSyear()));
            itemBuilder.setFctmaxprice(item.getFctMaxPrice());
            itemBuilder.setFctminprice(item.getFctMinPrice());
            result.addSpeclist(itemBuilder);
        }
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }
}
