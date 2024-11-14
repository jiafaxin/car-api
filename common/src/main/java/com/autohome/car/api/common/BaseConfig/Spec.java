package com.autohome.car.api.common.BaseConfig;

import com.autohome.car.api.common.SpecStateEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Spec {
    public static boolean isCvSpec(int specId) {
        return specId > 1000000;
    }

    public static int[] getSpecList(String specList) {
        String[] str = specList.split(",");
        int[] intSpec = new int[str.length];
        for (int i = 0; i < str.length; i++) {
            intSpec[i] = Integer.parseInt(str[i]);
        }
        return intSpec;
    }


    public static SpecStateEnum getSpecState(String stateCode) {
        stateCode = stateCode.toLowerCase();
        SpecStateEnum returnState = SpecStateEnum.NONE;
        switch (stateCode) {
            case "0x0001":
                returnState = SpecStateEnum.NO_SELL;
                break;
            case "0x0002":
                returnState = SpecStateEnum.WAIT_SELL;
                break;
            case "0x0004":
                returnState = SpecStateEnum.SELL;
                break;
            case "0x0008":
                returnState = SpecStateEnum.SELL_IN_STOP;
                break;
            case "0x0010":
                returnState = SpecStateEnum.STOP_SELL;
                break;
            case "0x0003":
                returnState = SpecStateEnum.SELL_3;
                break;
            case "0x000c":
                returnState = SpecStateEnum.SELL_12;
                break;
            case "0x000e":
                returnState = SpecStateEnum.SELL_14;
                break;
            case "0x001c":
                returnState = SpecStateEnum.SELL_28;
                break;
            case "0x000f":
                returnState = SpecStateEnum.SELL_15;
                break;
            case "0x001e":
                returnState = SpecStateEnum.SELL_30;
                break;
            case "0x001f":
                returnState = SpecStateEnum.SELL_31;
                break;
        }
        return returnState;
    }


    public static String carBodyStruct(int structId) {
        String result = "";
        switch (structId) {
            case 1:
                result = "两厢";
                break;
            case 2:
                result = "三厢";
                break;
            case 3:
                result = "掀背";
                break;
            case 4:
                result = "旅行版";
                break;
            case 5:
                result = "硬顶敞篷车";
                break;
            case 6:
                result = "软顶敞篷车";
                break;
            case 7:
                result = "硬顶跑车";
                break;
            case 8:
                result = "客车";
                break;
            case 9:
                result = "货车";
                break;
            case 10:
                result = "皮卡";
                break;
            case 11:
                result = "MPV";
                break;
            case 12:
                result = "SUV";
                break;
            case 13:
                result = "三厢跨界车";
                break;
            case 14:
                result = "两厢跨界车";
                break;
            case 15:
                result = "旅行跨界车";
                break;
            case 16:
                result = "SUV跨界车";
                break;
            case 1000:
                result = "跨界车";
                break;
        }
        return result;
    }

    public static String DriveMode(int modeId) {
        String result = "";
        switch (modeId) {
            case 1:
                result = "前置前驱";
                break;
            case 2:
                result = "前置后驱";
                break;
            case 3:
                result = "前置四驱";
                break;
            case 4:
                result = "中置后驱";
                break;
            case 5:
                result = "中置四驱";
                break;
            case 6:
                result = "后置后驱";
                break;
            case 7:
                result = "后置四驱";
                break;
            case 8:
                result = "双电机四驱";
                break;
            case 9:
                result = "电子适时四驱";
                break;
            case 10:
                result = "四电机四驱";
                break;
            case 11:
                result = "三电机四驱";
                break;
            case 12:
                result = "双电机后驱";
                break;
        }

        return result;
    }

    public static Integer DriveModeByString(String modeString) {
        int result = 0;
        if(StringUtils.isBlank(modeString)){
            return result;
        }
        switch (modeString) {
            case "前置前驱":
                result = 1;
                break;
            case "前置后驱":
                result = 2;
                break;
            case "前置四驱":
                result = 3;
                break;
            case "中置后驱":
                result = 4;
                break;
            case "中置四驱":
                result = 5;
                break;
            case "后置后驱":
                result = 6;
                break;
            case "后置四驱":
                result = 7;
                break;
            case "双电机四驱":
                result = 8;
                break;
            case "电子适时四驱":
                result = 9;
                break;
            case "四电机四驱":
                result = 10;
                break;
            case "三电机四驱":
                result = 11;
                break;
            case "双电机后驱":
                result = 12;
                break;
        }
        return result;
    }

    public static String AdmissionMethod(int amId) {
        String result = "";
        switch (amId) {
            case 1:
                result = "自然吸气";
                break;
            case 2:
                result = "涡轮增压";
                break;
            case 3:
                result = "机械增压";
                break;
            case 4:
                result = "机械+涡轮增压";
                break;
            case 5:
                result = "双涡轮增压";
                break;
            case 6:
                result = "三涡轮增压";
                break;
            case 7:
                result = "四涡轮增压";
                break;
            case 8:
                result = "双机械增压";
                break;
            case 9:
                result = "涡轮增压+电动增压";
                break;
        }

        return result;
    }

    public static String carGearbox(String gearbox) {
        if (gearbox == null || gearbox.length() == 0) {
            return "";
        } else if (gearbox.contains("手动")) {
            return "手动";
        } else {
            return "自动";
        }
    }

    public static List<Integer> arrNewEnergyFueltype = Arrays.asList(4, 5, 6, 7);

    /// <summary>
    /// 新能源基本参数项，只有新能源车型才显示，非新能源车型隐藏这些参数:
    /// "快充电量百分比", "工信部纯电续驶里程(km)", "电池充电时间", "充电桩价格", "实测快充时间(小时)", "实测慢充时间(小时)", "快充时间(小时)", "慢充时间(小时)", "实测续航里程(km)"
    /// </summary>
    public static List<String> listNewEnergyParam = Arrays.asList("快充电量百分比", "纯电续航里程(km)", "NEDC纯电续航里程(km)", "WLTC纯电续航里程(km)", "CLTC纯电续航里程(km)", "EPA纯电续航里程(km)", "工信部纯电续航里程(km)", "电池充电时间", "充电桩价格", "实测快充时间(小时)", "实测慢充时间(小时)", "快充时间(小时)", "慢充时间(小时)", "实测续航里程(km)", "电能当量燃料消耗量(L/100km)");

    /// <summary>
    /// 纯电动车型不显示:
    /// "充电桩价格", "系统综合功率(kW)", "系统综合扭矩(N·m)"
    /// </summary>
    public static List<String> listNotDisPlayOfPEVCarParam = Arrays.asList("油箱容积(L)", "NEDC综合油耗(L/100km)", "WLTC综合油耗(L/100km)", "四驱形式", "充电桩价格", "发动机", "变速箱", "系统综合功率(kW)", "系统综合扭矩(N·m)", "工信部综合油耗(L/100km)", "实测油耗(L/100km)", "环保标准");

    /**
     * 动态展示参数项
     */
    public static final List<Integer> DYNAMIC_DISPLAY_PARAM_ITEMS = Arrays.asList(7, 8, 9, 12, 34, 61, 65, 66, 67, 68, 69, 77, 78, 89, 95, 98, 99, 107, 109, 113, 117, 118, 119, 122, 125, 124, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 141, 142, 143, 144, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156);

    /**
     * 2023年5月24 汽油电驱换到特殊燃料类型里 运营需求
     * 油车燃料类型，不展示电动机分类
     */
    public static final List<Integer> OIL_FUEL_TYPE_LIST = Arrays.asList(1, 2, 8, 9, 10, 11,13,14,15);

    /**
     * 特殊燃料类型：3油电混合，5插电混合动力，12 汽油电驱
     */
    public static final List<Integer> FUEL_TYPE_FILTER_IDS = Arrays.asList(3, 5, 12);

    /**
     * 新能源的燃料形式
     * 4纯电 5 插电，6增程 7氢
     */
    public static final List<Integer> ARR_NEW_ENERGY_FUEL_TYPE = Arrays.asList(4, 5, 6, 7);
    //实测参数项 需要展示实测详情链接判断用
    //109	实测0-100km/h加速(s)
    //89	实测100-0km/h制动(m)
    //113	实测油耗(L/100km)
    //107	实测续航里程(km)
    public static final List<Integer> REAL_TEST_ITEMS = Arrays.asList(89, 109, 113, 107);

    public static final List<String> PARAM_TYPE = Arrays.asList("车系名称", "车型名称", "车型图片");



    public static Map<String, String> DicConfig_Group = new LinkedHashMap<String, String>() {{
        put("被动安全", "安全配置");
        put("主动安全", "安全配置");
        put("驾驶操控", "操控配置");
        put("四驱/越野", "操控配置");
        put("驾驶硬件", "智能/辅助驾驶");
        put("驾驶功能", "智能/辅助驾驶");
        put("车外灯光", "外部配置");
        put("外观/防盗", "外部配置");
        put("天窗/玻璃", "外部配置");
        put("外后视镜", "外部配置");
        put("屏幕/系统", "内部配置");
        put("智能化配置", "内部配置");
        put("方向盘/内后视镜", "内部配置");
        put("车内充电", "内部配置");
        put("座椅配置", "内部配置");
        put("音响/车内灯光", "内部配置");
        put("空调/冰箱", "内部配置");
    }};

    public static List<Integer> ORDER_CLS = Arrays.asList(1, 11, 13, 14, 15);

}
