package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class JFX_FIVE {

    public static void main(String[] args) {
        SpringApplication.run(JFX_TWO.class, args);
        String od = "https://car.api.autohome.com.cn";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com";
        //根据车系id获取图片类别数量
        List<String> state = Arrays.asList("0x0001", "0x0002", "0x0003", "0x0008",
                "0x000c","0x0010","0x001F");
        for (int i = 0; i < 7305; i++) {
            for(int j = 0;j<state.size();j++){
                String url = "/v2/carpic/picclass_classitemsbyseriesid.ashx?_appid=car&seriesid=" + i +"&state="+state.get(j);
                new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            }
            if(i%1000==0){
                System.out.println("===================当前执行到："+i+"行====================");
            }
        }
        System.out.println("================success======================");
        //根据车系id获取各状态下车型数量
//        for (int i = 0; i < 7305; i++) {
//            String url = "/v1/CarPrice/Spec_CountBySeriesId.ashx?_appid=car&seriesid=" + i ;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
//
//        //根据品牌集合获取品牌基本信息
//        for (int i = 0; i < 590; i++) {
//            String url =  "/v2/App/Brand_GetBrandInfoByIdList.ashx?_appid=car&brandid=" + i ;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%100==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
//
//        //根据品牌ID获取品牌model
//        for (int i = 0; i < 590; i++) {
//            String url =  "/v2/CarPrice/Brand_GetBrandById.ashx?_appid=car&brandid=" + i ;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%100==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
        //根据品牌获取品牌下车系名称等信息
//        for (int i = 0; i < 590; i++) {
//            String url =  "/v1/carprice/series_namebybrandid.ashx?_appid=car&brandid=" + i ;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%100==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
//
        //根据车系id获取多个配置信息
//        for (int i = 0; i < 7305; i++) {
//            String url =  "/v3/CarPrice/SpecificConfig_GetListBySeriesId.ashx?_appid=car&seriesid=" + i ;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
          //v2/Base/Fct_GetAllFcts.ashx
//        String url =  "/v2/Base/Fct_GetAllFcts.ashx?_appid=car";
//        new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//        System.out.println("================success======================");
    }
}
