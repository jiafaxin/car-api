package com.autohome.car.api.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueDto<T1,T2> {
    T1 key;
    T2 value;
}
