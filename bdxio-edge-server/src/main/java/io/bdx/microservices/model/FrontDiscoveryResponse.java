package io.bdx.microservices.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.bdx.microservices.SearchServiceInstance;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.*;

/**
 * Created by Presentation on 14/10/2015.
 */
public class FrontDiscoveryResponse {
    private Map<String, List<FrontServiceInstance>> stringListMap = new HashMap<>();

    @JsonValue
    public Map<String, List<FrontServiceInstance>> getStringListMap() {
        return stringListMap;
    }

    public void putList(String key, Collection<ServiceInstance<SearchServiceInstance>> serviceInstances) {
        List<FrontServiceInstance> displaybleList = new ArrayList<>();
        for(ServiceInstance<SearchServiceInstance> serviceInstance : serviceInstances) {
            displaybleList.add(new FrontServiceInstance(
                    serviceInstance.getName(),
                    serviceInstance.getAddress(),
                    serviceInstance.getPort(),
                    serviceInstance.getPayload().getStartDate()
            ));
        }
        this.stringListMap.put(key, displaybleList);
    }
}
