package com.autohome.car.api.services.common;

import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.SpecStateEnum;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class CommonFunction {
    public static String carFuel(int fuelId)
    {
        String strResult = "";
        switch (fuelId)
        {
            case 1:
                strResult = "汽油";
                break;
            case 2:
                strResult = "柴油";
                break;
            case 3:
                strResult = "油电混合";
                break;
            case 4:
                strResult = "纯电动";
                break;
            case 5:
                strResult = "插电式混合动力";
                break;
            case 6:
                strResult = "增程式";
                break;
            case 7:
                strResult = "氢燃料";
                break;
            case 8:
                strResult = "汽油+48V轻混系统";
                break;
            case 9:
                strResult = "汽油+24V轻混系统";
                break;
            case 10:
                strResult = "汽油+90V轻混系统";
                break;
            case 11:
                strResult = "汽油+天然气";
                break;
            case 12:
                strResult = "汽油电驱";
                break;
            case 13:
                strResult = "柴油+48V轻混系统";
                break;
            case 14:
                strResult = "CNG";
                break;
            case 15:
                strResult = "甲醇混动";
                break;
            case 16:
                strResult = "氢燃料电池";
                break;
        }
        return strResult;
    }

    /**
     * 电车燃料类型
     * @param fuelType
     * @return
     */
    public static  String electricFuelTypeName(int fuelType){
        String fuelTypeName = "";
        switch (fuelType) {
            case 1 :
                fuelTypeName = "纯电";
                break;
            case 2 :
                fuelTypeName = "插电式混合动力";
                break;
            case 4 :
                fuelTypeName = "增程式";
                break;
        }
        return fuelTypeName;
    }

    public static String carBodyStruct(int structId)
    {
        String result = "";
        switch (structId)
        {
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

    public static String driveMode(int modeId)
    {
        String result = "";
        switch (modeId)
        {
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

    public static String DriveType(int modeId)
    {
        String result = "";
        switch (modeId)
        {
            case 1:
                result = "前驱";
                break;
            case 2:
                result = "后驱";
                break;
            case 3:
                result = "四驱";
                break;
        }

        return result;
    }

    public static String admissionMehtod(int amId)
    {
        String result = "";
        switch (amId)
        {
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

    public static String GetItemName(int itemId)
    {
        String retStr = "";
        switch (itemId)
        {
            case 1:
                retStr = "基本参数";
                break;
            case 2:
                retStr = "车身";
                break;
            case 6:
                retStr = "发动机";
                break;
            case 4:
                retStr = "底盘转向";
                break;
            case 3:
                retStr = "变速箱";
                break;
            case 5:
                retStr = "车轮制动";
                break;

        }
        return retStr;
    }

    public static String GetTransmissionType(int transmissionTypeId)
    {
        String result = "";
        switch (transmissionTypeId)
        {
            case 1:
                result = "手动";
                break;
            case 2:
                result = "自动";
                break;
            case 3:
                result = "序列";
                break;
            case 4:
                result = "无级";
                break;
            case 5:
                result = "湿式双离合";
                break;
            case 6:
                result = "AMT";
                break;
            case 7:
                result = "固定齿比";
                break;
            case 8:
                result = "ISR";
                break;
            case 9:
                result = "手自一体";
                break;
            case 10:
                result = "干式双离合";
                break;
            case 11:
                result = "双离合";
                break;
        }

        return result;
    }

    public static final String VR_SERIES_APP_ROOT_URL = "https://pano.autohome.com.cn/";

    public static final String VR_SERIES_IMAGE_ROOT_URL = "https://pano.autoimg.cn/pub/";

    public static final Pattern PATTERN_1 = Pattern.compile("[http|https]://car.autohome.com.cn/photo/series/(\\d+)/(\\d+)/(\\d+)(?:.html|.html#pvareaid=|.html?pvareaid=)", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_2 = Pattern.compile("[http|https]://car.autohome.com.cn/photo/(\\d+)/(\\d+)/(\\d+)(?:.html|.html#pvareaid=|.html?pvareaid=)", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_3 = Pattern.compile("[http|https]://car.autohome.com.cn/photo/series-(\\d+)-(\\d+)-(\\d+)-(\\d+)(?:.html|.html#pvareaid=|.html?pvareaid=)", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_4 = Pattern.compile("[http|https]://car.autohome.com.cn/photo/(\\d+)-(\\d+)-(\\d+)-(\\d+)(?:.html|.html#pvareaid=|.html?pvareaid=)", Pattern.CASE_INSENSITIVE);


    public static final Pattern DATE_PATTERN = Pattern.compile("^((((19|20)\\d{2})-(0?(1|[3-9])|1[012])-(0?[1-9]|[12]\\d|30))" +
            "|(((19|20)\\d{2})-(0?[13578]|1[02])-31)" +
            "|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))" +
            "|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))$");

    public static int getStringToInt(String value ,int defaultValue){
        int valueNew = defaultValue;
        try {
            valueNew = Integer.parseInt(value);
        }catch (Exception e){
            log.error("字段转化异常！当前字段的值为:{}",value);
        }
        return valueNew;
    }

    public static double getStringToDouble(String value ,double defaultValue){
        double valueNew = defaultValue;
        try {
            valueNew = Double.parseDouble(value);
        }catch (Exception e){
            log.error("double字段转化异常！当前字段的值为:{}",value);
        }
        return valueNew;
    }

    /**
     * 是否是商用车
     * @param levelId
     * @return
     */
    public static boolean isCv(int levelId){
        int[] levels = new int[]{11,12,13,14,25};
        if(Arrays.stream(levels).anyMatch(v -> v == levelId)==true){
            return true;
        }
        return false;
    }

    public static int transmissionOrderBy(String _transmission,boolean isCV)
    {
        if(isCV){
            return Integer.parseInt(_transmission);
        }

        int returnInt = 0;
        switch (_transmission)
        {
            case "手动变速箱(MT)": returnInt = 1; break;
            case "自动变速箱(AT)": returnInt = 2; break;
            case "序列变速箱": returnInt = 3; break;
            case "无级变速箱(CVT)": returnInt = 4; break;
            case "双离合变速箱(DCT)": returnInt = 5; break;
            case "机械式自动变速箱(AMT)": returnInt = 6; break;
            case "固定齿比变速箱": returnInt = 7; break;
            case "ISR变速箱": returnInt = 8; break;
            case "电子无级变速箱(E-CVT)": returnInt = 4; break;
            case "手自一体变速箱(AT)": returnInt = 9; break;
        }
        return returnInt;
    }

    public static KeyValueDto<Boolean,List<SpecViewEntity>>  filterSpecViewList(SpecStateEnum state, List<SpecViewEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        boolean flag = false;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售(0x000E)
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        KeyValueDto<Boolean,List<SpecViewEntity>> ret = new KeyValueDto<>();
        ret.setKey(flag);
        ret.setValue(list);
        return ret;
    }

    public static List<SeriesBaseInfo> filterSeriesViewList(SpecStateEnum state, List<SeriesBaseInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                list = list.stream().filter(s -> s.getSeriesState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                list = list.stream().filter(s -> s.getSeriesState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                list = list.stream().filter(s -> s.getSeriesState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                list = list.stream().filter(s -> s.getSeriesState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                list = list.stream().filter(s -> s.getSeriesIsPublic() == 2).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                list = list.stream().filter(s -> s.getSeriesIsPublic() == 1).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                list = list.stream().filter(s -> s.getSeriesIsPublic() <= 1).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                list = list.stream().filter(s -> s.getSeriesIsPublic() >= 1).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                break;
        }
        return list;
    }

    public static List<SpecViewEntity> sort(SpecStateEnum state, List<SpecViewEntity> list, boolean isCV) {
        List<SpecViewEntity> resultList;
        if (state == SpecStateEnum.SELL_31) {
            if (isCV) {
                resultList = list.stream().sorted(Comparator.comparingInt(SpecViewEntity::getSpecState).thenComparing(SpecViewEntity::getSpecId)).collect(Collectors.toList());
            } else {
                resultList = list.stream().sorted(Comparator.comparingInt(SpecViewEntity::getSpecId)).collect(Collectors.toList());
            }
        } else {
            resultList = list.stream().sorted(Comparator.comparingInt(SpecViewEntity::getSpecState).thenComparing(SpecViewEntity::getSpecId)).collect(Collectors.toList());
        }
        return resultList;
    }

    /**
     *
     * @param str 例 ： 1,2,3
     * @return List<Integer>
     */
    private static final String regex = "^-\\d+(\\.\\d+)?$";
    public static List<Integer> getListFromStr(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        List<String> stringList = Arrays.stream(StringUtils.split(str, ",")).collect(Collectors.toList());
        List<Integer> integerList = stringList.stream().filter(StringUtils::isNotBlank)
                .filter(s -> StringUtils.isNumeric(s) || s.matches(regex)).map(Integer::valueOf).collect(Collectors.toList());
        return stringList.size() == integerList.size() ? integerList : Collections.emptyList();
    }
    public static List<String> getListFromStrContainBlank(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        List<String> stringList = Arrays.asList(str.split(",", -1));
        List<String> stringListNew = stringList.stream()
                .filter(s -> (StringUtils.isNotBlank(s) && (StringUtils.isNumeric(s)) || s.matches(regex)) || StringUtils.isBlank(s)).collect(Collectors.toList());
        return stringList.size() == stringListNew.size() ? stringListNew : Collections.emptyList();
    }


    /// <summary>
    /// 油车燃料类型，不展示电动机分类
    /// </summary>
    public static List<Integer> OILFUELTYPELIST =  Arrays.asList( 1, 2, 8, 9, 10, 11,13,14,15);

    public static List<Integer> DynamicDisplayParamItems = Arrays.asList( 7, 8, 9, 12, 34, 61, 65, 66, 67, 68, 69, 77, 78, 89, 95, 98, 99, 107, 109, 113, 117, 118, 119, 122, 125, 124, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 141, 142, 143, 144, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156 );


    /**
     * 检查参数
     */
    public static boolean check(List<Integer> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        List<Integer> collect = list.stream().filter(s -> s != 0).distinct().collect(Collectors.toList());

        return list.size() == collect.size();
    }

    public static boolean unDistinctCheck(List<Integer> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        List<Integer> collect = list.stream().filter(s -> s != 0).collect(Collectors.toList());
        return list.size() == collect.size();
    }

    public static boolean checkSpecParamIsShow(List<Integer> list, SpecBaseService specBaseService) {
        for (Integer specId : list) {
            SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
            if (specBaseInfo == null) {
                continue;
            }
            if (!(specBaseInfo.getSpecState() == 40 || specBaseInfo.getIsSpecParamIsShow() == 1)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static boolean checkSpecParamIsShow(int specId, SpecBaseService specBaseService) {
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        return specBaseInfo != null && specBaseInfo.getIsSpecParamIsShow() == 1;
    }

    public static String getDefaultParamSign(String str) {
        if(StringUtils.isBlank(str))
            return "-";
        String trim = StringUtils.trim(str);
        return trim.length() == 0 || StringUtils.equalsAny(trim, "0", "0.0", "null") ? "-" : str;
    }

    public static String getDefaultParamSignNew(String str) {
        if(StringUtils.isBlank(str)) {
            return "-";
        }
        String trim = StringUtils.trim(str);
        return (trim.length() == 0 || StringUtils.equalsAny(trim, "0", "0.0", "null","0.00万")) ? "-" : str;
    }

    public static KeyValueDto<Boolean, Boolean> getCvType(List<Integer> speiList) {
        List<Boolean> list = new ArrayList<>();
        for (Integer specId : speiList) {
            list.add(Spec.isCvSpec(specId));
        }
        KeyValueDto<Boolean, Boolean> keyValueDto = new KeyValueDto<>();
        keyValueDto.setKey(list.stream().filter(s -> s).count() == speiList.size());
        keyValueDto.setValue(list.stream().filter(s -> !s).count() == speiList.size());
        return keyValueDto;
    }

    public static <T> Map<String, Object> objectToMap(T obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static <T> List<Map<String, Object>> listToMap(List<T> list) {
        List<Map<String, Object>> result = new ArrayList<>(list.size());
        for (T t : list) {
            result.add(objectToMap(t));
        }
        return result;
    }

    public static String ConfigItemvalue(String text){
        if(StringUtils.isBlank(text))
            return "";
        return (text.contains("/") && (text.contains("-") || text.contains("●") || text.contains("○"))) ? text.replace("/", "&nbsp;/&nbsp;") : text;
    }

    static Map<Integer, String> vrDicIdMap = new LinkedHashMap<Integer, String>();
    static Map<Integer, String> vrDicNameMap = new LinkedHashMap<Integer, String>();

    /**
     * 25图点位对应vr点位字典
     * @return
     */
    public static Map<Integer, String> getDicPic25PointToVR()
    {
        if(vrDicIdMap == null || vrDicIdMap.size() == 0){
            Map<Integer, String> dict = new LinkedHashMap<>();
            dict.put(1, "1,2");
            dict.put(2, "4,5");
            dict.put(3, "18,19");
            dict.put(4, "26,27");
            dict.put(33, "13,14");

            dict.put(10, "26,27");
            dict.put(11, "13,14");
            dict.put(12, "13,14");
            dict.put(6, "7,8");
            dict.put(7, "28,29");

            dict.put(25, "9,10");
            dict.put(26, "9,10");
            dict.put(27, "18,19");
            dict.put(28, "11,12");
            dict.put(8, "7,8");

            dict.put(31, "4,5");
            dict.put(34, "18,19");
            dict.put(29, "9,10");
            dict.put(30, "1,2");
            dict.put(32, "4,5");

            dict.put(21, "28,29");
            dict.put(22, "11,12");
            dict.put(23, "1,2");
            dict.put(35, "26,27");
            dict.put(24, "7,8");
            vrDicIdMap = dict;
        }
        return vrDicIdMap;
    }

    public static String getPic25PointLocation(Integer id){
        if(vrDicNameMap == null || vrDicNameMap.size() == 0){
            HashMap<Integer, String> dict = new HashMap<Integer, String>();
            dict.put(1, "头图");
            dict.put(2, "正前");
            dict.put(3, "正后");
            dict.put(4, "正侧");
            dict.put(33, "斜后");

            dict.put(10, "中控全图");
            dict.put(11, "驾驶位");
            dict.put(12, "中控台");
            dict.put(6, "仪表盘");
            dict.put(7, "挡把");

            dict.put(25, "前排空间");
            dict.put(26, "后排空间");
            dict.put(27, "后备厢");
            dict.put(28, "前门板");
            dict.put(8, "门窗控制");

            dict.put(31, "车头特写");
            dict.put(34, "车尾特写");
            dict.put(29, "前灯");
            dict.put(30, "后灯");
            dict.put(32, "外后视镜");

            dict.put(21, "前轮");
            dict.put(22, "备胎");
            dict.put(23, "发动机舱");
            dict.put(35, "后悬架");
            dict.put(24, "钥匙");
            vrDicNameMap = dict;
        }
        return vrDicNameMap.get(id);
    }


    public static String replaceFactName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        if (name.contains("ë")) {
            name = name.replace("ë", "&#235;");
        }
        if (name.contains("Ä")) {
            name = name.replace("Ä", "&#196;");
        }
        return name;
    }

    public static String getPriceDesc(int price){
        String priceDesc = "";
        if(price == 0){
            priceDesc = "免费";
        }else if(price == 1){
            priceDesc = "暂无价格";
        }else {
            priceDesc = price + "元";
        }
        return priceDesc;
    }

    public static final List<Integer> ELECTRIC_SERIESID = Arrays.asList(87,407,3436,3559,3438,3439);

    //级别
    public static final List<Integer> FIND_CAR_LEVEL = Arrays.asList(0,1,2,3,4,5,6,7,8,9,11,12,13,14,16,17,18,19,20,21,22,23,24,101);

    //国家
    public static final List<Integer> FIND_CAR_COUNTRY = Arrays.asList(0,1,2,3,4,5,6,7,8,9,11,201);

    //结构类型
    public static final List<Integer> FIND_CAR_STRUCT = Arrays.asList(0,1,2,3,4,5,6,7,8,9,1000);

    //座位数
    public static final List<Integer> FIND_CAR_SEAT = Arrays.asList(0,2,4,5,6,7,8);

    //进气形式
    public static final List<Integer> FIND_CAR_FLOW_MODE = Arrays.asList(0,1,2,3);

    //能源类型
    public static final List<Integer> FIND_CAR_FUEL_TYPE = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,15,701,801);

    //驱动方式
    public static final List<Integer> FIND_CAR_DRIVE_TYPE = Arrays.asList(0,1,2,3);

    //变速箱
    public static final List<Integer> FIND_CAR_GEAR_BOX = Arrays.asList(0,1,4,5,9,10,101);

    //生产方式
    public static final List<Integer> FIND_CAR_IS_IMPORT = Arrays.asList(0,1,3);

    //代表查询哪个标签下的数据
    public static final List<Integer> FIND_CAR_STATE = Arrays.asList(3,1,2,0);


    public static final String[] FIELDS_ARRAY = {"SeriesId","id","SeriesFctMaxPrice","SeriesFctMinPrice"};


}
