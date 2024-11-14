package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class SeriesParamBySeriesIdV1 {
    public static void main(String[] args) {
        SpringApplication.run(SeriesParamBySeriesIdV1.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-http.thallo.corpautohome.com";

        List<CompletableFuture> tasks = new ArrayList<>();

        for (int i = 0; i <= 6853; i++) {
            int finalI = i;
            tasks.add(CompletableFuture.runAsync(()->{
                String url = "/v1/carprice/series_parambyseriesid.ashx?_appid=app&seriesid=" + finalI;
                new CompareJson().exclude("root.result.electricrongliang","root.result.transmissionitems").compareUrl(od.concat(url), nd.concat(url));
            }));
            if(tasks.size()%20==0){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }

        }
        System.out.println("=== success =================================");
    }
}
