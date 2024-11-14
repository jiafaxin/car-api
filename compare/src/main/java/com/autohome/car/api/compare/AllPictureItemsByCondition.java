package com.autohome.car.api.compare;

import com.autohome.car.api.common.HttpClient;
import com.autohome.car.api.compare.tools.CompareJson;
import org.apache.dubbo.common.utils.IOUtils;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootApplication
public class AllPictureItemsByCondition {


    static final String od = "http://car.api.autohome.com.cn";
    static final String nd = "http://car-car-api-http.thallo.corpautohome.com";

    public static void main(String[] args) {
        SpringApplication.run(AllPictureItemsByCondition.class, args);

        List<String> cs = conditions();
        List<CompletableFuture> tasks = new ArrayList<>();
        int count = 0;

        for (String c : cs) {
            count++;
            if(count%1000==0){
                System.out.println("count:"+count);
            }

            String[] sls = c.split(",");
            int specid = Integer.parseInt(sls[0]);
            int seriesId = Integer.parseInt(sls[1]);
            int classId = Integer.parseInt(sls[2]);
            int colorId = Integer.parseInt(sls[3]);

            tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&seriesid=" + seriesId));
            tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&seriesid=" + seriesId + "&classid="+classId));
            if(colorId>0){
                tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&seriesid=" + seriesId+"&colorid="+colorId));
                tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&seriesid=" + seriesId + "&classid="+classId+"&colorid="+colorId));
            }

            tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&specid=" + specid));
            tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&specid=" + specid + "&classid="+classId));
            if(colorId>0){
                tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&specid=" + specid+"&colorid="+colorId));
                tasks.add(c("/v1/carpic/pic_allpictureitemsbycondition.ashx?_appid=app&specid=" + specid + "&classid="+classId+"&colorid="+colorId));
            }


            if(tasks.size()>=48) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }

        }

        System.out.println("=== success =================================");
    }

    public static CompletableFuture c(String url){
        return new CompareJson().exclude(
                "root.result.electricrongliang",
                "root.result.transmissionitems"
        ).compareUrlAsync(od.concat(url), nd.concat(url));
    }

    public static List<String> conditions(){
        InputStream in = new AllPictureItemsByCondition().getClass().getClassLoader().getResourceAsStream("piccondition.txt");
        try {
            String strs = IOUtils.read(in,"utf-8");
            return Arrays.stream(strs.split("\r\n")).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
