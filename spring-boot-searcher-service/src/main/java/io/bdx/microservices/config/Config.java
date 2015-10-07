package io.bdx.microservices.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class Config {

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

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(mapper);
        return converter;
    }


    public String getEsIndex() {
        return null;
    }
}
