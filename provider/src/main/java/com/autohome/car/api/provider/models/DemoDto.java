package com.autohome.car.api.provider.models;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DemoDto {

    private LinkedHashMap linkedHashMap;
    private SDto dto;
    private int intField;
    private Integer integerField;
    private String stringField;
    private Boolean aBoolean;
    private boolean aBoolean2;
    private Long aLong;
    private long aLong2;
    private Double aDouble;
    private double aDouble2;
    private float aFloat;
    private Float aFloat2;
    private BigDecimal bigDecimal;
    private List<SDto> stringList;
    private Map<String,Integer> map;
    private SDto[] strings;

    public static class SDto{
        private int anInt;
    }



}
