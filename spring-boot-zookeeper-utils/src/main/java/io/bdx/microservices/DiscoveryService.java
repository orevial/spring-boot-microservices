package io.bdx.microservices;

import java.util.Collection;

import org.apache.curator.x.discovery.ServiceInstance;

public interface DiscoveryService {

	ServiceInstance<SearchServiceInstance> getInstance(SearchMicroService service) throws Exception;

	Collection<ServiceInstance<SearchServiceInstance>> getAllInstancesForService(SearchMicroService microService) throws Exception;

}
