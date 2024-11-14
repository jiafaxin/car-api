package com.autohome.car.api.data.popauto.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "solr.searchseries")
public class SearchSeriesProperties {
    private String url;
    private String corename;
}
