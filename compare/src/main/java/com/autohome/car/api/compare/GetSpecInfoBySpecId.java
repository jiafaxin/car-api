package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class GetSpecInfoBySpecId {
    public static void main(String[] args) {
        SpringApplication.run(GetSpecInfoBySpecId.class, args);
        String od = "http://car.api.autohome.com.cn";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com";
//        String nd = "http://10.248.168.3:8080";
        String nd = "http://car2.api.autohome.com.cn";
        List<CompletableFuture> tasks = new ArrayList<>();
        for (int i = 0; i <= 63351; i++) {
            int finalI = i;
            tasks.add(CompletableFuture.runAsync(()->{
                String url = "/v1/carprice/spec_infobyspecid.ashx?_appid=app&specid=" + finalI;
                new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            }));
            if(tasks.size()>=20){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
        for (int i = 1000001; i <= 1017000; i++) {

            int finalI = i;
            tasks.add(CompletableFuture.runAsync(()->{
                String url = "/v1/carprice/spec_infobyspecid.ashx?_appid=app&specid=" + finalI;
                new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            }));

            if(tasks.size()>=20){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

    }
}
