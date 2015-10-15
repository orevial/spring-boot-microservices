package io.bdx.microservices.kpi.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiConfig {

    @Value("${elastic-search.url}")
    private String esUrl;

    @Value("${elastic-search.port}")
    private int esPort;

    @Value("${elastic-search.index}")
    private String esIndex;

    @Bean
    public Client getESClient() {
        return new TransportClient().addTransportAddress(new InetSocketTransportAddress(esUrl, esPort));
    }

    public String getEsIndex() {
        return esIndex;
    }
}
