package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class LevelEntity implements Serializable {

    int id;
    String name;

    String dir;

    String description;

    public String getName() {
        if (name.contains("皮卡"))
            return "皮卡";
        return name;
    }
}
