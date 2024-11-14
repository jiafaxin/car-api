package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FactoryByBrand {
    public static void main(String[] args) {
        SpringApplication.run(FactoryByBrand.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com";
        String[] states = new String[]{"0x0001","0x0002","0x0004","0x0008","0x0010","0x000c","0x001c","0x000f","0X001f"};
        String[] typeids = new String[]{"0","1"};
        String[] filterimages = new String[]{"0","1"};
        for (int i = 0; i <= 600; i++) {
//            for (String state:states) {
//                for (String typeid:typeids) {
//                    for (String filterimage:filterimages) {
//                        String url = "/v1/javascript/factorybybrand.ashx?_appid=car&state="+state+"&typeid="+typeid+"&brandid=" + i+"&IsFilterSpecImage="+filterimage;
//                        new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//                    }
//                }
//            }
            String url = "/v1/javascript/factorybybrand.ashx?_appid=car&state=0X001F&typeid=0&brandid=" + i+"&IsFilterSpecImage=0";
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
    }
}
