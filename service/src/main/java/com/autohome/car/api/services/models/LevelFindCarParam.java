package com.autohome.car.api.services.models;

import autohome.rpc.car.car_api.v1.car.LevelFindCarRequest;
import com.autohome.car.api.common.*;

import java.io.Serializable;


public class LevelFindCarParam implements Serializable {
    private String _levelId = "";
    private int levelId = -1;
    private String _price;
    private String price;
    private int PriceMinValue;
    private int PriceMaxValue;
    private int PriceMin;
    private int PriceMax;
    private String _displacement;
    private String Displacement;
    private double DisplacementMin;
    private double DisplacementMax;
    private String _driver;
    private CarDriveEnum Drive;
    private String _gear;
    private CarGearBoxEnum Gear = CarGearBoxEnum.空;
    private String _struct;
    private CarStructEnum Struct = CarStructEnum.空;
    private String _attribute;
    private int Attribute = -1;
    private String _fuel;
    private CarFuelEnum Fuel = CarFuelEnum.空;
    private String _country;
    private int Country = -1;
    private String _seat;
    private CarSeatEnum Seat  = CarSeatEnum.空;
    private String _config = "";
    private String Config;
    private boolean IsNoParas;
    private String Canonical;
    private int _seoParamNum;


    public LevelFindCarParam(LevelFindCarRequest request){
        this._levelId = request.getLevel();
        this._price = request.getPrice();
        this._displacement = request.getDisplacement();
        this._driver = request.getDrive();
        this._gear = request.getGear();
        this._struct = request.getStructure();
        this._attribute = request.getAttribute();
        this._fuel = request.getFuel();
        this._country = request.getCountry();
        this._seat = request.getSeat();
        this.Config = request.getConfig();
    }

    public int getLevelId() {
        if (levelId > -1) {
            return levelId;
        }

        if ("car".equals(_levelId)) {
            levelId = 0;
            return levelId;
        }

        switch (_levelId) {
            case "a00":
                levelId = 1;
                return levelId;
            case "a0":
                levelId = 2;
                return levelId;
            case "a":
                levelId = 3;
                return levelId;
            case "b":
                levelId = 4;
                return levelId;
            case "c":
                levelId = 5;
                return levelId;
            case "d":
                levelId = 6;
                return levelId;
            case "s":
                levelId = 7;
                return levelId;
            case "mpv":
                levelId = 8;
                return levelId;
            case "suv":
                levelId = 9;
                return levelId;
            case "mb":
                levelId = 11;
                return levelId;
            case "qk":
                levelId = 13;
                return levelId;
            case "p":
                levelId = 14;
                return levelId;
            case "suva0":
                levelId = 16;
                return levelId;
            case "suva":
                levelId = 17;
                return levelId;
            case "suvb":
                levelId = 18;
                return levelId;
            case "suvc":
                levelId = 19;
                return levelId;
            case "suvd":
                levelId = 20;
                return levelId;
            case "mpva":
                levelId = 21;
                return levelId;
            case "mpvb":
                levelId = 22;
                return levelId;
            case "mpvc":
                levelId = 23;
                return levelId;
            case "mpvd":
                levelId = 24;
                return levelId;
            default:
                return levelId;
        }
    }

    public String getPrice() {
        if (price != null && !price.isEmpty()) {
            return price;
        }
        if (_price == null || _price.isEmpty() || _price.equals("0")) {
            price = "0_0";
        }else{
            price = _price;
        }

        return price;
    }

    public int getPriceMinValue() {
        int min = Integer.parseInt(getPrice().split("_")[0]);
        if (min == 0) {
            //return BaseData.Price.MIN_VALUE;
            return 0;
        } else if (min == 1) {
            return 2;
        } else {
            return min;
        }
    }

    public int getPriceMaxValue() {
        int max = Integer.parseInt(getPrice().split("_")[1]);
        if (max == 0) {
            //return BaseData.Price.MAX_VALUE;
            return 0;
        } else if (max == 1) {
            return 10000;
        } else {
            return max;
        }
    }

    public int getPriceMin() {
        return getPriceMinValue() * 10000;
    }

    public int getPriceMax() {
        return getPriceMaxValue() * 10000;
    }

    public String getDisplacement() {
        if (Displacement != null && !Displacement.isEmpty()) {
            return Displacement;
        }
        if (_displacement == null || _displacement.isEmpty() || _displacement.equals("0")) {
            Displacement = "0.0_0.0";
        }else{
            Displacement = _displacement;
        }
        return Displacement;
    }

    public double getDisplacementMin() {
        return Double.parseDouble(getDisplacement().split("_")[0]);
    }

    public double getDisplacementMax() {
        return Double.parseDouble(getDisplacement().split("_")[1]);
    }

    public CarDriveEnum getDriver() {
        if (Drive != CarDriveEnum.空 && Drive != null) {
            return Drive;
        }
        int i = 0;
        if (_driver != null && !_driver.isEmpty()) {
            try {
                i = Integer.parseInt(_driver);
            } catch (NumberFormatException e) {
                // 处理无效的数字字符串
            }
        }
        Drive = CarDriveEnum.fromValue(i);
        return Drive;
    }

    public CarGearBoxEnum getGear() {
        if (Gear != CarGearBoxEnum.空 && Gear != null) {
            return Gear;
        }
        int i = 0;
        if (_gear != null && !_gear.isEmpty()) {
            try {
                i = Integer.parseInt(_gear);
            } catch (NumberFormatException e) {
                // 处理无效的数字字符串
            }
        }

        Gear = CarGearBoxEnum.fromValue(i);
        return Gear;
    }

    public CarStructEnum getStruct() {
        if (Struct != CarStructEnum.空 && Struct != null) {
            return Struct;
        }
        int i = 0;
        if (_struct != null && !_struct.isEmpty()) {
            try {
                i = Integer.parseInt(_struct);
            } catch (NumberFormatException e) {
                // 处理无效的数字字符串
            }
        }
        Struct = CarStructEnum.fromValue(i);
        return Struct;
    }

    public int getAttribute() {
        if (Attribute >= 0) {
            return Attribute;
        }
        int i = 0;
        if (_attribute != null && !_attribute.isEmpty()) {
            try {
                i = Integer.parseInt(_attribute);
                i = (i == 2) ? 1 : i;
            } catch (NumberFormatException e) {
                // 处理无效的数字字符串
            }
        }

        Attribute = i;
        return Attribute;
    }

    public CarFuelEnum getFuel() {
        if (Fuel != CarFuelEnum.空 && Fuel != null) {
            return Fuel;
        }
        int i = 0;
        if (_fuel != null && !_fuel.isEmpty()) {
            try {
                i = Integer.parseInt(_fuel);
            } catch (NumberFormatException e) {
                // 处理无效的数字字符串
            }
        }

        Fuel = CarFuelEnum.fromValue(i);
        return Fuel;
    }

    public int getCountry() {
        if (Country >= 0) {
            return Country;
        }
        int i = 0;
        if (_country != null && !_country.isEmpty()) {
            try {
                i = Integer.parseInt(_country);
            } catch (NumberFormatException e) {
                // 处理无效的数字字符串
            }
        }

        Country = i;
        return Country;
    }

    public CarSeatEnum getSeat() {
        if (Seat != CarSeatEnum.空 && Seat != null) {
            return Seat;
        }
        int i = 0;
        if (_seat != null && !_seat.isEmpty()) {
            try {
                i = Integer.parseInt(_seat);
            } catch (NumberFormatException e) {
                // 处理无效的数字字符串
            }
        }

        Seat = CarSeatEnum.fromValue(i);
        return Seat;
    }

    public String getConfig() {
        if (Config != null && !Config.isEmpty()) {
            return "_" + Config + "_";
        }
        if ("__".equals("_" + _config + "_")) {
            Config = "_0_";
        }else{
            Config = _config;
        }
        return Config;
    }

    public boolean isNoParas() {
        if (levelId == 0 && (price.equals("0_0") || price.isEmpty()) && (Displacement.equals("0.0_0.0") || Displacement.isEmpty())
                && Drive == CarDriveEnum.全部 && Gear == CarGearBoxEnum.全部 && Struct == CarStructEnum.全部 && Attribute == 0
                && Fuel == CarFuelEnum.全部 && Country == 0 && Seat == CarSeatEnum.全部 && Config.equals("_0_")) {
            return true;
        }
        return false;
    }
}
