package com.autohome.car.api.services.basic.models;

import lombok.Data;

import java.util.Map;

@Data
public class KbScore {
    int returncode;
    String message;
    Result result;


    @Data
    public static class Result{
        Map<Integer,Double> items;
    }

}
