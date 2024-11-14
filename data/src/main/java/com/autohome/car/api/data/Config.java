package com.autohome.car.api.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.autohome.car.api.data.**")
public class Config {

}
