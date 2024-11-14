package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpecInnercolorlistbyseriesid {
    public static void main(String[] args) {
        SpringApplication.run(SpecInnercolorlistbyseriesid.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com";
        for (int i = 0; i <= 8000; i++) {
            String url = "/v1/carprice/spec_innercolorlistbyseriesid.ashx?_appid=app&seriesid=" + i;
            new CompareJson().exclude().compareUrl(od.concat(url), nd.concat(url));
            if(i%1000 == 0){
                System.out.println(i);
            }
        }

        System.out.println("=== success =================================");
    }
}
