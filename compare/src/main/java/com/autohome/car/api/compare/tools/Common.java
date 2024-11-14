package com.autohome.car.api.compare.tools;

import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.compare.ScnCompare;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.FactoryBaseEntity;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Deprecated
public class Common {
    /**
     * .net 线上环境
     */
    public static final String OLD_URL = "http://car.api.autohome.com.cn";

    /**
     * 预发环境url
     */
    public static final String NEW_URL = "http://car-car-api-compare-test-http.thallo.corpautohome.com";


    public static List<String> stateList = Arrays.asList("0X0001", "0X0002", "0X0004", "0X0008", "0X0010", "0X000C", "0X000F", "0X001C", "0X001F");

    public static List<String> yearState = Arrays.asList("0x0001","0x0002","0x0004","0x0008", "0x0010", "0x0003","0x000c","0x000e","0x001c","0x000f","0x001e");


    @Resource
    private SpecMapper specMapper;
    @Resource
    private SpecViewMapper specViewMapper;

    public static List<Integer> getAllSeriesIds(int start) {
        return ScnCompare.seriesIds();
//        List<Integer> seriesIds = new ArrayList<>(6853);
//        for (int i = start; i <= ALL_SERIES_IDS_COUNT; i++) {
//            seriesIds.add(i);
//        }
//        return seriesIds;
    }
    @Resource
    private SeriesMapper seriesMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private FactoryMapper factoryMapper;

    @Resource
    private SeriesViewMapper seriesViewMapper;




    public static List<Integer> getAllSpecIds() {
        return ScnCompare.SpecIds();
    }

    public List<Integer> getAllFctIds() {
        List<FactoryBaseEntity> allFactoryNames = factoryMapper.getAllFactoryNames();
        return allFactoryNames.stream().map(FactoryBaseEntity::getId).collect(Collectors.toList());
    }

    public List<Integer> getAllLevelId() {
        return seriesViewMapper.getAllLevelIdFromSeriesView();
    }


    public List<Integer> getAllBrandIds() {
        return brandMapper.getAllBrandIds();
    }

    public void compare(String path, String state, int start, String param, String[] exclude, int slice) {
        if (StringUtils.isNoneBlank(state)) {
            path = path.concat("&state=" + state);
        }
        List<Integer> ids = getAllSeriesIds(start);
        Collections.shuffle(ids);
        List<List<Integer>> lists = ToolUtils.splitList(ids, slice);

        System.out.println("总共有：" + ids.size());
        int i = 0;
        List<CompletableFuture> tasks = new ArrayList<>();

        for (List<Integer> list : lists) {
            String join = Joiner.on(",").join(list);
            String url = path + "&" + param + "=" + join;
            tasks.add(new CompareJson().exclude(exclude).compareUrlAsyncCommon(url));
            if(tasks.size()>=30){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }

    }

    public void compareSpec(String path, String param, String[] exclude, int slice) {
        List<Integer> ids = getAllSpecIds();
        System.out.println("总共有：" + ids.size());
        List<List<Integer>> lists = ToolUtils.splitList(ids, slice);
        Collections.shuffle(lists);
        int i = 0;
        List<CompletableFuture> tasks = new ArrayList<>();
        for (List<Integer> list : lists) {
            String join = Joiner.on(",").join(list);
            String url = path + "&" + param + "=" + join;
            tasks.add(new CompareJson().exclude(exclude).compareUrlAsyncCommon(url));
            if(tasks.size()>30){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }

    }

    public void compareSpecList(String path, String param, String[] exclude) {
        List<Integer> ids = getAllSpecIds();
        Collections.shuffle(ids);
        List<List<Integer>> lists = ToolUtils.splitList(ids, 2);
        System.out.println("总共有：" + lists.size());
        int i = 0;
        List<CompletableFuture> tasks = new ArrayList<>();

        for (List<Integer> list : lists) {
            String join = Joiner.on(",").join(list);
            String url = path + "&" + param + "=" + join;
            tasks.add(new CompareJson().exclude(exclude).compareUrlAsyncCommon(url)) ;
            if(tasks.size()>20){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }
    }

    public void compareBrand(String path, String param, String[] exclude) {
        List<Integer> ids = getAllBrandIds();
        System.out.println("总共有：" + ids.size());
        int i = 0;
        for (Integer brandId : ids) {
            String url = path + "&" + param + "=" + brandId;
            new CompareJson().exclude(exclude).compareUrlAsyncCommon(url);
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }

    }

    public void compareNewSeries(String path, String state, int start, String param, String[] exclude, int slice) {
        if (StringUtils.isNoneBlank(state)) {
            path = path.concat("&state=" + state);
        }
        List<Integer> ids = getAllSeriesIds(start);
        System.out.println("总共有：" + ids.size());
        int i = 0;
        for (Integer seriesId : ids) {
            String url = path + "&" + param + "=" + seriesId;
            new CompareJson().exclude(exclude).compareUrl(OLD_URL.concat(url), NEW_URL.concat(url));
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }

    }

    public void compareSpecNew(String path, String param, String[] exclude) {
        List<Integer> ids = getAllSpecIds();
        System.out.println("总共有：" + ids.size());
        int i = 0;
        List<CompletableFuture> tasks = new ArrayList<>();

        for (Integer id : ids) {
            String url = path + "&" + param + "=" + id +"&disptype=1";
            tasks.add(new CompareJson().exclude(exclude).compareUrlAsyncCommon(url)) ;
            if(tasks.size()>20){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }

    }

    public void compareFct(String path, String param, String[] exclude) {
        List<Integer> ids = getAllFctIds();
        System.out.println("总共有：" + ids.size());
        int i = 0;
        for (Integer fctId : ids) {
            String url = path + "&" + param + "=" + fctId;
            new CompareJson().exclude(exclude).compareUrl(OLD_URL.concat(url), NEW_URL.concat(url));
            i++;
            if (i % 100 == 0) {
                System.out.println(i);
            }
        }

    }
    public void compareLevelIds(String path, String param, String[] exclude) {
        List<Integer> ids = getAllLevelId();
        System.out.println("总共有：" + ids.size());
        int i = 0;
        for (Integer levelId : ids) {
            String url = path + "&" + param + "=" + levelId;
            new CompareJson().exclude(exclude).compareUrl(OLD_URL.concat(url), NEW_URL.concat(url));
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }
    }




    public void compareYearId(String path, String param, String[] exclude, String state) {
        List<Integer> specYearIds = specViewMapper.getAllSpecYearIds();
        System.out.println("总共有：" + specYearIds.size());
        int i = 0;
        for (Integer yearId : specYearIds) {
            String url = path + "&" + param + "=" + yearId + "&state=" + state;
            new CompareJson().exclude(exclude).compareUrl(OLD_URL.concat(url), NEW_URL.concat(url));
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }
    }
}



