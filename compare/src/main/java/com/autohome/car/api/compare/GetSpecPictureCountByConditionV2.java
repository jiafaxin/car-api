package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class GetSpecPictureCountByConditionV2 {
    public static void main(String[] args) {
        SpringApplication.run(GetSpecPictureCountByConditionV2.class, args);
        List<CompletableFuture> tasks = new ArrayList<>();
        //纯车系，跑完没问题
        for (int i = 0; i <= 7300; i++) {
            String url = "/v2/CarPic/Spec_PictureCountByCondition.ashx?_appid=app.iphone&seriesid="+i+"&innerColorid=0&classid=0";
            tasks.add(new CompareJson().compareUrlAsyncCommon(url));
            if(i % 1000 == 0){
                System.out.println(i);
            }
            if(tasks.size()>30){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
        }
        //选了前20的颜色
        int[] colors = new int[]{2975,256,3682,2930,3241,3105,422,1788,1263,4978,3723,2086,325,3151,2120,459,3518,212,1136,2856};
        int[] classIds = new int[]{1,3,10,12,13,14,50,51};

        for (int i = 0; i <= 7300; i++) {
            for (int classId:classIds) {
                String url = "/v2/CarPic/Spec_PictureCountByCondition.ashx?_appid=app.iphone&seriesid="+i+"&innerColorid=0&classid="+classId;
                tasks.add(new CompareJson().compareUrlAsyncCommon(url));
            }
            if(i % 1000 == 0){
                System.out.println(i);
            }
            if(tasks.size()>30){
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
        }

        for (int i = 400; i <= 7300; i++) {
            for (int color:colors) {
                for (int classId:classIds) {
                    String url = "/v1/CarPic/Spec_PictureCountByCondition.ashx?_appid=app.iphone&seriesid="+i+"&innerColorid="+color+"&classid="+classId;
                    tasks.add(new CompareJson().compareUrlAsyncCommon(url));
                }
            }
            if(i % 1000 == 0){
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
