package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class GetSpecDetailBySeriesId {
    public static void main(String[] args) {
        SpringApplication.run(GetSpecDetailBySeriesId.class, args);

        List<CompletableFuture> tasks = new ArrayList<>();
        for (int i = 0; i <= 7300; i++) {
            String[] states = new String[]{"0x0001","0x0002","0x0004","0x0008","0x0010","0x000c","0x001c","0x000f","0X001f"};
            for (String state:states) {
                String url = "/v2/carprice/spec_detailbyseriesId.ashx?_appid=car&state=" + state + "&seriesid=" + i;
                tasks.add(new CompareJson().exclude(
                                "root.result.specitems[*].transmission",
                                "root.result.specitems[*].gearbox"
                                , "root.result.specitems[*].emissionstandards")
                        .compareUrlAsyncCommon(url));
            }

            if(tasks.size()>30){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }

            if(i % 100 == 0){
                System.out.println(i);
            }
        }
        System.out.println("success");
    }

}
