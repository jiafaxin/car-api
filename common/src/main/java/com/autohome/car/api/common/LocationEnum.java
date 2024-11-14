package com.autohome.car.api.common;

public enum LocationEnum {


    LOCATION_ENUM1(1,"车系列表的状态和价格"),
    LOCATION_ENUM2(2,"车系综述的状态和价格"),
    LOCATION_ENUM3(3,"车系综述的车型列表价格"),

    LOCATION_ENUM4(4,"参数配置"),

    LOCATION_ENUM5(5,"图片列表"),

    LOCATION_ENUM6(6,"车型综述的状态和价格");

    private int locationId;

    private String locationName;

    LocationEnum(int locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    public static String getLocationName(int locationId) {
        for (LocationEnum locationEnum : values()) {
            if (locationEnum.getLocationId() == locationId) {
                return locationEnum.getLocationName();
            }
        }
        return "";
    }


    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
