package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GetSpecInnerColorBySpecId {
    public static void main(String[] args) {
        SpringApplication.run(GetSpecInnerColorBySpecId.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com";
        for (int i = 0; i <= 63265; i++) {
            String url = "/v1/carprice/spec_innercolorbyspecid.ashx?_appid=car&specid=" + i;
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
        }

        for (int i = 1000000; i <= 1016180; i++) {
            String url = "/v1/carprice/spec_innercolorbyspecid.ashx?_appid=car&specid=" + i;
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
        }

    }
}
