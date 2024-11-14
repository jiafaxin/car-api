package com.autohome.car.api.services.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IsHaveMaintain implements Serializable {
    private int havemtninfo;
}
