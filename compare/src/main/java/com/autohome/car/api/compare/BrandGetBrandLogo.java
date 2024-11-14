package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BrandGetBrandLogo {
    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        SpringApplication.run(BrandGetBrandLogo.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-http.thallo.corpautohome.com";
        for (int i = 0; i < 587; i++) {
            String url = "/v2/CarPrice/Brand_GetBrandLogo.ashx?_appid=app&brandid=" + i;
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
        }
        System.out.println("success:" + (System.currentTimeMillis() - s));
    }
}
