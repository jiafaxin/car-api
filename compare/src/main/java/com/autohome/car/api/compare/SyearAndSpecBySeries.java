package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SyearAndSpecBySeries {
    public static void main(String[] args) {
        SpringApplication.run(SyearAndSpecBySeries.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com";
        String[] states = new String[]{"0x0001","0x0002","0x0004","0x0008","0x0010","0x000c","0x001c","0x000f","0X001f"};
        int[] filterimages = new int[]{0,1};
        for (int i = 0; i <= 7300; i++) {
             for (String state:states) {
                for (int filterimage:filterimages) {
                    String url = "/v1/javascript/syearandspecbyseries.ashx?_appid=app&seriesid="+i+"&state="+state+"&isfilterspecimage="+filterimage;
                    new CompareJson().compareUrl(od.concat(url), nd.concat(url));
                }
            }
//            String url = "/v1/carpic/piccolor_coloritemsbyseriesid.ashx?_appid=app&seriesid="+i+"&state=0X001F";
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
    }
}
