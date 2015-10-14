package io.bdx.microservices.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.stereotype.Service;

import io.bdx.microservices.DiscoveryService;
import io.bdx.microservices.SearchMicroService;
import io.bdx.microservices.SearchServiceInstance;

@Service
public class DiscoveryProxyService {
    @Inject
    private DiscoveryService discoveryService;

    public Map<String, Collection<ServiceInstance<SearchServiceInstance>>> listAllServicesInstances()
            throws Exception {
        Map<String, Collection<ServiceInstance<SearchServiceInstance>>> map = new HashMap<>();

        for (SearchMicroService microService : SearchMicroService.values()) {
            map.put(microService.getAppName(), discoveryService.getAllInstancesForService(microService));
        }

        return map;
    }

    public Collection<ServiceInstance<SearchServiceInstance>> getAllServiceInstances(
            SearchMicroService microService)
            throws Exception {
        return discoveryService.getAllInstancesForService(microService);
    }
}
