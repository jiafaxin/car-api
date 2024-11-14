package com.autohome.car.api.compare.compare.url;

import com.autohome.car.api.compare.AllPictureItemsByCondition;
import com.autohome.car.api.compare.ScnCompare;
import com.autohome.car.api.compare.compare.enums.OperType;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.compare.service.BySpecIdCallBack;
import com.autohome.car.api.compare.compare.service.CallBack;
import com.autohome.car.api.compare.compare.service.YearIdCallBack;
import com.autohome.car.api.compare.tools.CompareJson;
import javafx.concurrent.Task;
import org.apache.dubbo.common.utils.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class ScnUrl implements Url {
    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public Map<String, Param> getUrl() {
        Map<String, Param> map = new HashMap<>();
        map.put("/v1/duibi/Config_DistinctListBySpecList.ashx", Param.builder().operType(OperType.SPEC_ID_List).slice(2).build());
        map.put("/v3/CarPrice/Config_GetListBySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v1/car/Series_ParamBySeriesId.ashx", Param.builder().operType(OperType.SERIES_ID).build());
        map.put("/v1/carprice/year_parambyyearId.ashx", Param.builder().operType(OperType.SERIES_ID).callBack(new CallBack() {
            @Override
            public void call(Param paramContext, IdsService idsService, String path) {
                List<String> conditions = conditions();
                List<CompletableFuture> tasks = new ArrayList<>();
                List<String> states = Arrays.asList("0x0001","0x0002","0x0010","0x000c");
                for (String condition : conditions) {
                    String seriesId = condition.split(",")[0];
                    String yearId = condition.split(",")[1];
                    for (String state : states) {
                        tasks.add( new CompareJson().compareUrlAsyncCommon("/v1/carprice/year_parambyyearId.ashx?_appid=app&seriesid="+seriesId+"&yearid="+yearId+"&state="+state,"beta"));
                    }
                    if(tasks.size()>20){
                        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                        tasks = new ArrayList<>();
                    }
                }
            }
        }).build());
        map.put("/v1/carprice/spec_detailbyyearId.ashx", Param.builder().callBack((Param paramContext, IdsService idsService, String path)->{
            List<String> conditions = conditions();
            List<CompletableFuture> tasks = new ArrayList<>();
            for (String condition : conditions) {
                String seriesId = condition.split(",")[0];
                String yearId = condition.split(",")[1];
                tasks.add( new CompareJson().compareUrlAsyncCommon("/v1/carprice/spec_detailbyyearId.ashx?_appid=app&seriesid="+seriesId+"&yearid="+yearId+"&state=0X0010","beta"));
                if(tasks.size()>20){
                    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                    tasks = new ArrayList<>();
                }
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        }).build());


        map.put("/v1/carpic/piccolor_innercoloritemsbyseriesid.ashx", Param.builder().callBack((Param paramContext, IdsService idsService, String path)->{
            List<String> conditions = conditions();
            List<CompletableFuture> tasks = new ArrayList<>();
            for (Integer seriesId : ScnCompare.seriesIds()) {
                tasks.add( new CompareJson().compareUrlAsyncCommon("/v1/carpic/piccolor_innercoloritemsbyseriesid.ashx?_appid=app&seriesid="+seriesId+"&state=0X001F","yzpro"));
                if(tasks.size()>20){
                    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                    tasks = new ArrayList<>();
                }
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        }).build());




        map.put("/v2/CarPrice/Spec_BaseInfoBySpecIds.ashx", Param.builder().callBack((Param paramContext, IdsService idsService, String path)->{
            List<CompletableFuture> tasks = new ArrayList<>();
            int[] classIds = {1, 3, 10, 12, 13, 14, 50, 51};
            int i = 0;
            for (Integer specId : ScnCompare.SpecIds()) {
                for (int classId : classIds) {
                    String url = "/v2/CarPrice/Spec_BaseInfoBySpecIds.ashx?_appid=app&specids=" + specId + "&classid=" + classId;
                    tasks.add(new CompareJson().exclude().compareUrlAsyncCommon(url, "yzpro"));
                    if(tasks.size()>30){
                        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                        tasks = new ArrayList<>();
                    }
                }
                if(i++%1000==0){
                    System.out.println("now:"+i);
                }
            }
        }).build());

        return map;
    }


    public static List<String> conditions() {
        InputStream in = new AllPictureItemsByCondition().getClass().getClassLoader().getResourceAsStream("seriesandyear.txt");
        try {
            String strs = IOUtils.read(in, "utf-8");
            return Arrays.stream(strs.split("\r\n")).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
