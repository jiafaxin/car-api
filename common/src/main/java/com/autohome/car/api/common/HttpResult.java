package com.autohome.car.api.common;

import lombok.Data;

@Data
public class HttpResult<T> {
    int statusCode;
    String message;
    T result;
}
