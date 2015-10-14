package io.bdx.microservices.kpi.service;

import javax.inject.Inject;

import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.stereotype.Service;

import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import io.bdx.microservices.DiscoveryService;
import io.bdx.microservices.SearchMicroService;
import io.bdx.microservices.SearchServiceInstance;

@Service
public class KpiLoaderService {

	@Inject
	private DiscoveryService discoveryService;
	
	interface Loader {
		@RequestLine("GET /nbIndexedDocuments")
		@Headers("Content-Type: application/json")
		Integer getNbIndexedDocuments();
	}
	
	
	public Integer getNbImport() throws Exception {
		ServiceInstance<SearchServiceInstance> serviceInstance = getServiceInstance();

		Loader target = Feign.builder().decoder(new JacksonDecoder()).target(Loader.class, getBaseURL(serviceInstance));

		return target.getNbIndexedDocuments();
	}

	private ServiceInstance<SearchServiceInstance> getServiceInstance() throws Exception {
		return discoveryService.getInstance(SearchMicroService.BDX_IO_LOADER_SERVICE);
	}

	private String getBaseURL(ServiceInstance<SearchServiceInstance> serviceInstance) {
		return "http://" + serviceInstance.getAddress() + ":" + serviceInstance.getPort();
	}

}
