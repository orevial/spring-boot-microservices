package io.bdx.microservices.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bdx.microservices.SearchMicroService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.bdx.microservices.service.DiscoveryService;

import javax.inject.Inject;

@RestController
@RequestMapping("/discovery")
public class DiscoveryController {

    @Inject
    private DiscoveryService discoveryService;

    private ObjectMapper mapper = new ObjectMapper();

    private String convertAnswer(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listService/{serviceName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String listService(@PathVariable String serviceName) throws Exception {
        return convertAnswer(discoveryService.getAllServiceInstances(SearchMicroService.forName(serviceName)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listAllServices", produces = MediaType.APPLICATION_JSON_VALUE)
    public String listAllServices() throws Exception {
        return convertAnswer(discoveryService.listAllServicesInstances());
    }

}