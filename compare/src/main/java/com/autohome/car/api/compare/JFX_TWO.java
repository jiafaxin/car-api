package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JFX_TWO {
    public static void main(String[] args) {
        SpringApplication.run(JFX_TWO.class, args);
//        //根据车系获取年代款列表
//        String od = "https://car.api.autohome.com.cn/v1/www/Year_GetYearItemsBySeriesId.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v1/www/Year_GetYearItemsBySeriesId.ashx?";
//        for (int i = 0; i < 7305; i++) {
//            String url = "seriesid=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
        //根据车型id获取对应类别前五张图
//        String od = "https://car.api.autohome.com.cn/v1/carpic/spec_classpicturebyspecId.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v1/carpic/spec_classpicturebyspecId.ashx?_appid=car&";
//        for (int i = 46000; i < 63506; i++) {
//            String url = "specid=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
        //根据多个车型id获取多个配置信息0-63506 1000000-1016253
//        String od = "https://car.api.autohome.com.cn/v3/CarPrice/SpecificConfig_GetListBySpecList.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v3/CarPrice/SpecificConfig_GetListBySpecList.ashx?";
//
//        List<CompletableFuture> task = new ArrayList<>();
//
//        int i = 0;
//
//        for (Integer specId : ScnCompare.SpecIds()) {
//            i++;
//            if(i%1000 == 0){
//                System.out.println("success : " + i);
//            }
//            String url = "speclist=" + specId;
//            task.add(new CompareJson().compareUrlAsync(od.concat(url), nd.concat(url)));
//            if(task.size()>30){
//                CompletableFuture.allOf(task.toArray(new CompletableFuture[task.size()]));
//            }
//        }
//
//        System.out.println("================success======================");

        //车型车系有否有保养
//        String od = "https://car.api.autohome.com.cn/mtn/IsHaveMaintain.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/mtn/IsHaveMaintain.ashx?";
//
//        List<CompletableFuture> task = new ArrayList<>();
//        int i = 0;
//        for (Integer specId : ScnCompare.SpecIds()) {
//            i++;
//            if(i%1000 == 0){
//                System.out.println("spec : " + i);
//            }
//
//            String url = "specid=" + specId;
//            task.add(new CompareJson().compareUrlAsync(od.concat(url), nd.concat(url)));
//            if(task.size()>30){
//                CompletableFuture.allOf(task.toArray(new CompletableFuture[task.size()]));
//            }
//        }
//
//        i = 0;
//        for (Integer seriesid : ScnCompare.seriesIds()) {
//            i++;
//            if(i%1000 == 0){
//                System.out.println("series : " + i);
//            }
//
//            String url = "seriesid=" + seriesid;
//            task.add(new CompareJson().compareUrlAsync(od.concat(url), nd.concat(url)));
//            if(task.size()>30){
//                CompletableFuture.allOf(task.toArray(new CompletableFuture[task.size()]));
//            }
//        }
//
//        System.out.println("================success======================");
        //根据品牌获取关联厂商及车系信息
//        String od = "https://car.api.autohome.com.cn/v1/carprice/brand_correlateinfobybrandid.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v1/carprice/brand_correlateinfobybrandid.ashx?";
//        for (int i = 0; i < 590; i++) {
//            String url = "brandid=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%100==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
        //根据品牌ID获取品牌名称
//        String od = "https://car.api.autohome.com.cn/v2/CarPrice/Brand_GetBrandNameById.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v2/CarPrice/Brand_GetBrandNameById.ashx?";
//        for (int i = 0; i < 590; i++) {
//            String url = "brandid=" + i;
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%100==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");

        //根据车系id,车型id,类型id,颜色id,页码及页大小,图片id获取图片信息
//        String od = "https://car.api.autohome.com.cn/v2/app/Pic_PictureItemsByCondition.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v2/app/Pic_PictureItemsByCondition.ashx?";
//        List<Integer> picClassId = Arrays.asList(55, 54, 53, 51, 15, 14, 13, 12, 10, 3, 1);
////        picColorId 1-11248
////         innerColorId 1-3601
////        specid  0-63506 1000000-1016253
////        seriesId 0-7305
//        for (int i = 1; i < 7305; i++) {
//            String url = "seriesid=" + i + "&typeid="+0 + "&innercolorid=" + 0 +"&pageindex=1&pagesize=100";
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===============第1个当前seriesid：" + i + "行==========================");
//            }
//        }
//        System.out.println("================success1======================");
//
//        for (int i = 50000; i < 64000; i++) {
//            String url = "specid=" + i + "&typeid="+ 0 + "&innercolorid=" + 0 +"&pageindex=1&pagesize=100";
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("======当前时间："+ LocalDateUtils.getTodayNow() +"  specid：" + i + "行=============");
//            }
//        }
//        System.out.println("================success======================");

        //根据车系id,车型id,类型id,颜色id,页码及页大小获取图片信息
//        String od = "https://car.api.autohome.com.cn/v1/carpic/pic_pictureitemsbycondition.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v1/carpic/pic_pictureitemsbycondition.ashx?";
//        List<Integer> picClassId = Arrays.asList(55, 54, 53, 51, 15, 14, 13, 12, 10, 3, 1);
        //picColorId 1-11248
        // innerColorId 1-3601
        //specid  0-63506 1000000-1016253
        //seriesId 0-7305
//        for (int i = 0; i < 7305; i++) {
//            String url = "seriesid=" + i + "&classid="+0+"&colorid=0&page=1&size=100";
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前seriesId执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
//        for (int i = 58000; i < 60000; i++) {
//            String url = "specid=" + i + "&classid="+0+"&colorid=2975&page=1&size=100";
//            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            if(i%1000==0){
//                System.out.println("===================当前specId执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");
////
//        List<String> state = Arrays.asList("0X0001", "0X0002", "0x0003", "0x000c", "0x0010", "0x001f");
////        //据车系id、颜色id获取图片类别数量
//        String od = "https://car.api.autohome.com.cn/v1/carpic/picclass_classitemsbyseriesid.ashx?_appid=car&";
//        String nd = "http://car-car-api-http.thallo.corpautohome.com/v1/carpic/picclass_classitemsbyseriesid.ashx?";
//
//        List<CompletableFuture> tasks = new ArrayList<>();
//
//
//        for (int i = 0; i < 7305; i++) {
//            for(int j = 0;j<state.size();j++){
//                String url = "seriesid=" + i + "&state="+state.get(j)+"&filterlessthenthreepicspec=1";
//                tasks.add(new CompareJson().compareUrlAsync(od.concat(url), nd.concat(url)));
//            }
//            if(tasks.size()>20){
//                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
//            }
//            if(i%1000==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");

//        for (int i = 0; i < 7305; i++) {
//            for(int j = 0;j<state.size();j++){
//                for(int z = 0;z<2;z++){
//                    String url = "seriesid=" + i +"&colorid=256&state="+state.get(j)+"&filterlessthenthreepicspec="+z;
//                    new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//                }
//            }
//            if(i%1000==0){
//                System.out.println("===================当前seriesid:" +i+"====================");
//            }
//        }
//        System.out.println("================success======================");

//        String od = "https://car.api.autohome.com.cn/v1/App/Series_MenuByBrandIdNew.ashx?_appid=car&";
//        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com/v1/App/Series_MenuByBrandIdNew.ashx?";
//        List<String> state = Arrays.asList("0X0001", "0X0002", "0x0004", "0x0008",
//                "0x000c","0X0010","0X000F","0X001C","0X001F");
//        for (int i = 0; i < 590; i++) {
//            for(int j = 0;j<state.size();j++){
//                String url = "brandid=" + i +"&state="+state.get(j);
//                new CompareJson().compareUrl(od.concat(url), nd.concat(url));
//            }
//            if(i%100==0){
//                System.out.println("===================当前执行到："+i+"行====================");
//            }
//        }
//        System.out.println("================success======================");


        String od = "https://car.api.autohome.com.cn/v1/App/Series_SeriesInfoBySeriesList.ashx?_appid=car&";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com/v1/App/Series_SeriesInfoBySeriesList.ashx?";
        for (int i = 0; i < 7305; i++) {
            String url = "seriesids=" + i ;
            new CompareJson().compareUrl(od.concat(url), nd.concat(url));
            if(i%1000==0){
                System.out.println("===================当前执行到："+i+"行====================");
            }
        }
        System.out.println("================success======================");
    }
}
