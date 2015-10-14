package io.bdx.microservices.controller;

import java.util.List;

import javax.inject.Inject;

import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import io.bdx.microservices.DiscoveryService;
import io.bdx.microservices.SearchMicroService;
import io.bdx.microservices.SearchServiceInstance;
import io.bdx.microservices.model.CityResult;

/**
 * Demonstrates usage of "Feign".
 */
@RestController
@RequestMapping("/search")
public class SearchController {
	@Inject
	private DiscoveryService zkService;

	interface Search {
		@RequestLine("GET /search/city/{city}")
		@Headers("Content-Type: application/json")
		List<CityResult> getCityResult(@Param("city") String city);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/city/{city}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CityResult> searchByCity(@PathVariable String city) throws Exception {
		ServiceInstance<SearchServiceInstance> serviceInstance = getServiceInstance();

		Search target = Feign.builder().decoder(new GsonDecoder()).target(Search.class, getBaseURL(serviceInstance));

		return target.getCityResult(city);
	}

	private ServiceInstance<SearchServiceInstance> getServiceInstance() throws Exception {
		return zkService.getInstance(SearchMicroService.BDX_IO_SEARCHER_SERVICE);
	}

	private String getBaseURL(ServiceInstance<SearchServiceInstance> serviceInstance) {
		return "http://" + serviceInstance.getAddress() + ":" + serviceInstance.getPort();
	}
}
