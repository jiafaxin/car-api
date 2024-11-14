package com.autohome.car.api.services;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching(order = 1)
public class ServiceConfig {
}
