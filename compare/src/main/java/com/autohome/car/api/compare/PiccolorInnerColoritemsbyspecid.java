package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PiccolorInnerColoritemsbyspecid {
    public static void main(String[] args) {
        SpringApplication.run(PiccolorInnerColoritemsbyspecid.class, args);
        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-http.thallo.corpautohome.com";
        for (int i = 0; i <= 63265; i++) {
            String url = "/v1/carpic/piccolor_innercoloritemsbyspecid.ashx?_appid=app&specid=" + i;
            new CompareJson().exclude("root.result.electricrongliang","root.result.transmissionitems").compareUrl(od.concat(url), nd.concat(url));
            if(i%1000 == 0){
                System.out.println(i);
            }
        }

        for (int i = 1000000; i <= 1016180; i++) {
            String url = "/v1/carpic/piccolor_innercoloritemsbyspecid.ashx?_appid=app&specid=" + i;
            new CompareJson().exclude("root.result.electricrongliang","root.result.transmissionitems").compareUrl(od.concat(url), nd.concat(url));
            if(i%1000 == 0){
                System.out.println(i);
            }
        }

        System.out.println("=== success =================================");
    }
}
