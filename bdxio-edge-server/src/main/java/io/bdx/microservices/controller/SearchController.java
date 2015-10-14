package io.bdx.microservices.controller;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import io.bdx.microservices.SearchMicroService;
import io.bdx.microservices.SearchServiceInstance;
import io.bdx.microservices.ZookeeperService;
import io.bdx.microservices.model.CityResult;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Created by Presentation on 14/10/2015.
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Inject
    private ZookeeperService zkService;

    interface Search {
        @RequestLine("GET /search/city/{city}")
        @Headers("Content-Type: application/json")
        List<CityResult> getCityResult(@Param("city") String city);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/city/{city}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CityResult> searchByCity(@PathVariable String city) throws Exception {
        ServiceInstance<SearchServiceInstance> serviceInstance = getServiceInstance();

        Search target = Feign.builder()
                .decoder(new GsonDecoder())
                .target(Search.class, getBaseURL(serviceInstance));

        return target.getCityResult(city);
    }

    private ServiceInstance<SearchServiceInstance> getServiceInstance() throws Exception {
        Collection<ServiceInstance<SearchServiceInstance>> allInstancesForService = zkService.getAllInstancesForService(SearchMicroService.BDX_IO_SEARCHER_SERVICE);
        if(allInstancesForService.size() > 0) {
            return allInstancesForService.iterator().next();
        }
        return null;
    }

    private String getBaseURL(ServiceInstance<SearchServiceInstance> serviceInstance) {
        return "http://" + serviceInstance.getAddress() + ":" + serviceInstance.getPort();
    }
}
