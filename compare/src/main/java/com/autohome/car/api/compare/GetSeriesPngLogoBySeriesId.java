package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GetSeriesPngLogoBySeriesId {
    public static void main(String[] args) {
        SpringApplication.run(GetSeriesPngLogoBySeriesId.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com";
        for (int i = 0; i <= 7300; i++) {
            String url = "/v2/CarPic/Series_PngLogoBySeriesId.ashx?_appid=dealer&serieslist=" + i;
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
    }
}
