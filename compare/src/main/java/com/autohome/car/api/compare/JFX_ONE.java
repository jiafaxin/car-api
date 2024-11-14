package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JFX_ONE {

    public static void main(String[] args) {
        SpringApplication.run(JFX_ONE.class, args);

        //根据品牌id获取品牌代表图
//        String od = "https://car.api.autohome.com.cn/v1/carprice/brand_logobybrandid.ashx?_appid=dealer&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v1/carprice/brand_logobybrandid.ashx?_appid=dealer&";
//        for (int i = 0; i < 587; i++) {
//            String url = "brandid=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%100==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");

//
//        根据车系id获取电车的车型信息
//        String od = "https://car.api.autohome.com.cn/v1/App/Electric_SpecParamBySeriesId.ashx?_appid=app&";
//        String nd = "http://car-car-api.http.thallo.corpautohome.com/v1/App/Electric_SpecParamBySeriesId.ashx?_appid=app&";
//        for (int i = 0; i < 7305; i++) {
//            String url = "seriesid=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
        //根据多个车型id获取相关信息
//        String od = "https://car.api.autohome.com.cn/v1/carprice/spec_infobyspeclist.ashx?_appid=mall&";
//        String nd = "http://car-car-api.http.thallo.corpautohome.com/v1/carprice/spec_infobyspeclist.ashx?_appid=mall&";
//        for (int i = 1000000; i < 1016253; i++) {
//            String url = "speclist=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
        //根据车系id获取车型的参数信息
//        String od = "https://car.api.autohome.com.cn/v1/carprice/spec_parambyseriesId.ashx?state=0x001f&_appid=car&";
//        String nd = "http://car-car-api.http.thallo.corpautohome.com/v1/carprice/spec_parambyseriesId.ashx?state=0x001f&_appid=car&";
//        for (int i = 0; i < 7305; i++) {
//            String url = "seriesid=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
        //车系ids列表获取相关基本信息
        String od = "https://car.api.autohome.com.cn/v1/CarPrice/Series_BaseInfoBySeriesList.ashx?_appid=car&";
        String nd = "http://car-car-api-http.thallo.corpautohome.com/v1/CarPrice/Series_BaseInfoBySeriesList.ashx?_appid=car&";
        for (int i = 0; i < 7305; i++) {
            String url = "serieslist=" + i;
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i%1000==0){
                System.out.println("===================当前执行到："+i+"行====================");
            }
        }
        System.out.println("================success======================");

    }
}
