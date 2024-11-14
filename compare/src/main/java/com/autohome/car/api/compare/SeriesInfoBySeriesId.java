package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.Common;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;


/**
 * 二期接口对比
 */
@SpringBootApplication
public class SeriesInfoBySeriesId {

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(SeriesInfoBySeriesId.class, args);
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
                case "/v2/carpic/series_photowhitelogobyseriesid.ashx":
                    series_photowhitelogobyseriesid(common);
                    break;
                case "/v1/carprice/spec_detailbyseriesId.ashx":
                    spec_detailbyseriesId(common);
                    break;
                case "/v2/carprice/series_parambyserieslist.ashx":
                    testSeriesParamBySeriesList(common);
                    break;
                case "/v1/carprice/spec_logobyspeclist.ashx":
                    spec_logobyspeclist(common);
                    break;
                case "/v1/carprice/series_logobyserieslist.ashx":
                    series_logobyserieslist(common);
                    break;
                case "/v1/carprice/spec_colorlistbyspecidList.ashx":
                    spec_colorlistbyspecidList(common);
                    break;
                case "/v1/carprice/Spec_InnerColorListBySpecIdList.ashx":
                    spec_InnerColorListBySpecIdList(common);
                    break;
                case "/v1/App/Electric_ParamBySeriesId.ashx":
                    electric_ParamBySeriesId(common);
                    break;
                case "/v1/carprice/spec_infobyseriesId.ashx":
                    testSpec_infobyseriesId(common);
                    break;
                case "/v1/carprice/series_parambyserieslist.ashx":
                    v1Series_parambyserieslist(common);
                    break;
                case "/v1/carprice/spec_colorbyspecid.ashx":
                    spec_colorbyspecid(common);
                    break;
                case "/v1/App/Electric_SeriesListByBrandId.ashx":
                    electric_SeriesListByBrandId(common);
                    break;
                case "/v1/carprice/spec_paramlistbyspeclist.ashx":
                    spec_paramlistbyspeclist(common);
                    break;
            }
            System.out.println("对比结束：" + url);
        }
    }

    public static void testSpec_infobyseriesId(Common common) {
        String path = "/v1/carprice/spec_infobyseriesId.ashx?_appid=app";
        String[] exclude = {};

        for (String state : Common.stateList) {
            System.out.println(state + ": 对比开始");
            common.compare(path, state, 1, "seriesid", exclude, 1);
            System.out.println(state + ": 对比结束");
        }
    }

    public static void series_photowhitelogobyseriesid(Common common) {
        String[] exclude = {};
        String path = "/v2/carpic/series_photowhitelogobyseriesid.ashx?_appid=app";
        common.compare(path, "", 1, "serieslist", exclude, 1);
    }

    public static void series_logobyserieslist(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/series_logobyserieslist.ashx";
        common.compare(path, "", 1, "serieslist", exclude, 1);
    }

    public static void spec_detailbyseriesId(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/spec_detailbyseriesId.ashx?_appid=app";
        for (String state : Common.stateList) {
            System.out.println(state + ": 对比开始");
            common.compare(path, state, 1, "seriesid", exclude, 1);
            System.out.println(state + ": 对比结束");
        }
    }

    public static void spec_logobyspeclist(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/spec_logobyspeclist.ashx?_appid=app";
        common.compareSpecList(path, "speclist", exclude);
    }


    public static void testSeriesParamBySeriesList(Common common) {
        String[] exclude = {"root.result[0].minprice", "root.result[0].currentstateminoilwear", "root.result[0].paramisshow", "root.result[0].transmissionitems[1]", "root.result[0].transmissionitems[2]", "root.result[0].transmissionitems[3]"};
        String path = "/v2/carprice/series_parambyserieslist.ashx?_appid=app";
        common.compare(path, "", 1, "serieslist", exclude, 1);
    }

    public static void v1Series_parambyserieslist(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/series_parambyserieslist.ashx?_appid=app";
        common.compare(path, "", 1, "serieslist", exclude, 1);
    }


    public static void electric_ParamBySeriesId(Common common) {
        String[] exclude = {};
        String path = "/v1/App/Electric_ParamBySeriesId.ashx?_appid=app";
        common.compare(path, "", 1, "seriesid", exclude, 1);
    }

    public static void spec_paramlistbyspeclist(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/spec_paramlistbyspeclist.ashx?_appid=app";
        common.compareSpec(path, "speclist", exclude, 2);
    }

    public static void spec_colorbyspecid(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/spec_colorbyspecid.ashx?_appid=app";
        common.compareSpec(path, "specid", exclude, 1);
    }


    public static void electric_SeriesListByBrandId(Common common) {
        String[] exclude = {};
        String path = "/v1/App/Electric_SeriesListByBrandId.ashx?_appid=app";
        common.compareBrand(path, "brandId", exclude);
    }

    /**
     * /v1/carprice/spec_colorlistbyspecidList.ashx
     */
    public static void spec_colorlistbyspecidList(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/spec_colorlistbyspecidList.ashx?_appid=app";
        common.compareSpecList(path, "specIdlist", exclude);
    }


    public static void spec_InnerColorListBySpecIdList(Common common) {
        String[] exclude = {};
        String path = "/v1/carprice/Spec_InnerColorListBySpecIdList.ashx?_appid=app";
        common.compareSpecList(path, "specIdlist", exclude);
    }

    public static void series_parambyserieslist(Common common) {
        String[] exclude = {};
        String path = "/v2/carprice/series_parambyserieslist.ashx?_appid=app";
        common.compare(path, "", 1, "serieslist", exclude, 10);
    }
}
