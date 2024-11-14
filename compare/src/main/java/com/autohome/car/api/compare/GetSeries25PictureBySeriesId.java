package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GetSeries25PictureBySeriesId {
    public static void main(String[] args) {
        SpringApplication.run(GetSeries25PictureBySeriesId.class, args);
        String od = "http://car.api.autohome.com.cn";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com";
//        String nd = "http://car-car-api-testyc3.thallo.corpautohome.com";
        String nd = "http://car2.api.autohome.com.cn";
        for (int i = 0; i <= 7300; i++) {
            String url = "/v1/carpic/series_25picturebyseriesid.ashx?_appid=car&seriesid=" + i;
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
    }
}
