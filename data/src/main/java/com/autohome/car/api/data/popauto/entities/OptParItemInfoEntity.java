package com.autohome.car.api.data.popauto.entities;


import lombok.Data;

import java.io.Serializable;

@Data
public class OptParItemInfoEntity implements Serializable {

    private String name;
    private String item;
    private int configId;
    private int pordercls;
    private int ordercls;
}
