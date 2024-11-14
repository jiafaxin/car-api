package com.autohome.car.api.compare;


import com.autohome.car.api.compare.tools.Common;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;

/**
 * 三期对比测试
 * zff
 */
@SpringBootApplication
public class ThreeCompareTest {
    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(ThreeCompareTest.class, args);
        Common common = ac.getBean(Common.class);
        startCompare(common);
    }

    public static void startCompare(Common common) {
        while (true) {
            Scanner myObj = new Scanner(System.in);  // 创建Scanner对象
            System.out.println("请输入要测试的url:");
            String url = myObj.nextLine();
            System.out.println("开始对比：" + url);
            switch (url) {
                case "/v1/carprice/fct_logobyfctid.ashx":  //pass
                case "/v1/CarPrice/Fct_GetFctNameByFctId.ashx": //pass
                    fctInfoByFctId(common, url);
                    break;
                case "/v1/CarPrice/Spec_InnerColorBySpecId.ashx":
                    specInnerColorBySpecId(common, url);
                    break;
                case "/v1/carprice/series_11infobylevelid.ashx": //pass
                    series11InfoByLevelId(common, url);
                    break;
                case "/v1/car/Spec_ListOfBookedBySeries.ashx":
                case "/v2/car/Config_BagBySeriesId.ashx":
                case "/v1/carprice/series_colorbyseriesid.ashx":  //排序有问题
                    bySeriesId(common, url);
                    break;
            }
            System.out.println("对比结束：" + url);
        }
    }

    private static void fctInfoByFctId(Common common, String url) {
        String[] exclude = {};
        common.compareFct(String.format("%s%s", url, "?_appid=app"), "fctid", exclude);
    }
    private static void bySeriesId(Common common, String url) {
        String[] exclude = {};
        common.compare(String.format("%s%s", url, "?_appid=app"), "", 1, "seriesid", exclude, 1);
    }
    private static void series11InfoByLevelId(Common common, String url) {
        String[] exclude = {};
        common.compareLevelIds(String.format("%s%s", url, "?_appid=app"), "levelid", exclude);
    }
    private static void specInnerColorBySpecId(Common common, String url) {
        String[] exclude = {};
        common.compareSpec(String.format("%s%s", url, "?_appid=app"), "specid", exclude, 10);
    }

}
