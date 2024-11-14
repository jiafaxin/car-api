package com.autohome.car.api.services;


import autohome.rpc.car.car_api.v1.app.AutoTagCarListAutoHomeRequest;
import autohome.rpc.car.car_api.v1.app.AutoTagCarListAutoHomeResponse;
import autohome.rpc.car.car_api.v1.app.AutoTagCarListPriceRequest;
import autohome.rpc.car.car_api.v1.app.AutoTagCarListPriceResponse;

public interface TagService {
    AutoTagCarListAutoHomeResponse autoTagCarListAutoHome(AutoTagCarListAutoHomeRequest request);
    AutoTagCarListPriceResponse autoTagCarListPrice(AutoTagCarListPriceRequest request);
}
