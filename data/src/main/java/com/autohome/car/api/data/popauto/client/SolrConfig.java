package com.autohome.car.api.data.popauto.client;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import com.autohome.car.api.data.popauto.properties.SearchSeriesProperties;
import org.springframework.data.solr.core.SolrTemplate;

@Configuration
public class SolrConfig {

    @Resource
    SearchSeriesProperties searchSeriesProperties;

    public HttpSolrClient solrClient() {
        return new HttpSolrClient.Builder(searchSeriesProperties.getUrl()).build();
    }

    @Bean
    public SolrTemplate solrTemplate() {
        return new SolrTemplate(solrClient());
    }

}

