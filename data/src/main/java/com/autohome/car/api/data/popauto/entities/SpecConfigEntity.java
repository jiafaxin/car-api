package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class SpecConfigEntity implements Serializable {
    int specId;
    int configid;
    String item;
    String name;
    String itemValue;
    private int pordercls;
    private int ordercls;


    public static Map<String, Object> getObjToMap(SpecConfigEntity entity, int specId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("configId", entity.getConfigid());
        map.put("item", entity.getItem());
        map.put("name", entity.getName());
        map.put("pordercls", entity.getPordercls());
        map.put("ordercls", entity.getOrdercls());
        map.put(String.valueOf(specId), entity.getItemValue());
        return map;
    }

    public static List<Map<String, Object>> listToMap(List<SpecConfigEntity> entityList, int specId) {
        List<Map<String, Object>> list = new ArrayList<>(entityList.size());
        for (SpecConfigEntity specConfigEntity : entityList) {
            list.add(getObjToMap(specConfigEntity, specId));
        }
        return list;
    }
}
