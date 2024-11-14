package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class GetClassItemsBySpecId {
    public static void main(String[] args) {
        SpringApplication.run(GetClassItemsBySpecId.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-http.thallo.corpautohome.com";
//        String nd = "http://car-car-api-testyc3.thallo.corpautohome.com";
//        String nd = "http://car-car-api-test.thallo.corpautohome.com";
//        String nd = "http://car2.api.autohome.com.cn";
        int[] colors = new int[]{2975,256,3682,2930,3241,3105,422,1788,1263,3723,4978,2086,325,3151,2120,459,3518,212,1136,2856,456,2203,695,838,886,2850,1175,850,288,441,1968,2606,3589,123,2195,3423,2000,361,1783,1245,2916,2866,3280,2651,1532,49,289,2684,6831,4242};
        List<CompletableFuture> tasks = new ArrayList<>();
        for (int i = 1; i <= 63351; i++) {
            for (int color:colors) {
                int finalI = i;
                tasks.add(CompletableFuture.runAsync(()->{
                    String url = "/v1/carpic/picclass_classitemsbyspecid.ashx?_appid=app.iphone&colorid="+color+"&specid=" + finalI;
                    new CompareJson().compareUrl(od.concat(url), nd.concat(url));
                }));
                if(tasks.size()>=20){
                    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                    tasks = new ArrayList<>();
                }
            }
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
        for (int i = 1000001; i <= 1017000; i++) {
            for (int color:colors) {
                int finalI = i;
                tasks.add(CompletableFuture.runAsync(()->{
                    String url = "/v1/carpic/picclass_classitemsbyspecid.ashx?_appid=app.iphone&colorid="+color+"&specid=" + finalI;
                    new CompareJson().compareUrl(od.concat(url), nd.concat(url));
                }));
                if(tasks.size()>=20){
                    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                    tasks = new ArrayList<>();
                }
            }
            if(i % 100 == 0){
                System.out.println(i);
            }
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
    }
}
