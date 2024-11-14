package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class GetSpec25PictureBySpecId {
    public static void main(String[] args) {
        SpringApplication.run(GetSpec25PictureBySpecId.class, args);
        List<CompletableFuture> tasks = new ArrayList<>();
        for (int i = 0; i <= 63265; i++) {
            String url = "/v1/carpic/spec_25picturebyspecid.ashx?_appid=dealer&specid=" + i;
            tasks.add(new CompareJson().compareUrlAsyncCommon(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
            if(tasks.size()>30){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
        }

        for (int i = 1000000; i <= 1016180; i++) {
            String url = "/v1/carpic/spec_25picturebyspecid.ashx?_appid=dealer&specid=" + i;
            tasks.add(new CompareJson().compareUrlAsyncCommon(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
            if(tasks.size()>30){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
    }
}
