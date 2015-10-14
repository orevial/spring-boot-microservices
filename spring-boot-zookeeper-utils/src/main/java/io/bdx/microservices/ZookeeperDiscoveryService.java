package io.bdx.microservices;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.springframework.stereotype.Service;

/**
 * Zookeeper implementation of {@link DiscoveryService}.
 */
@Service
public class ZookeeperDiscoveryService implements DiscoveryService {

    @Inject
    private CuratorFramework zkClient;

    private final Map<String, ServiceDiscovery<SearchServiceInstance>> serviceDiscoveries = new HashMap<>();

    private final Map<String, Map<SearchMicroService, ServiceProvider<SearchServiceInstance>>> serviceProviders = new HashMap<>();

    private static final String DISCOVERY_BASE_URL = "/services/discovery/";

    private ServiceDiscovery<SearchServiceInstance> getServiceDiscoveryForService(String serviceName) {
        ServiceDiscovery<SearchServiceInstance> serviceDiscovery = serviceDiscoveries.get(serviceName);
        if (serviceDiscovery == null) {
            serviceDiscovery = ServiceDiscoveryBuilder.builder(SearchServiceInstance.class)
                    .client(zkClient)
                    .basePath(DISCOVERY_BASE_URL)
                    .build();
            try {
                serviceDiscovery.start();
            } catch (Exception e) {
                throw new RuntimeException("Cannot start discovery");
            }
        }
        return serviceDiscovery;
    }

    private ServiceProvider<SearchServiceInstance> getServiceProvider(SearchMicroService service) {
        Map<SearchMicroService, ServiceProvider<SearchServiceInstance>> providers;
        String appName = service.getAppName();
        if (serviceProviders.containsKey(appName)) {
            providers = serviceProviders.get(appName);
        } else {
            providers = new HashMap<>();
            serviceProviders.put(appName, providers);
        }
        ServiceProvider<SearchServiceInstance> serviceProvider;
        if (!providers.containsKey(service)) {
            serviceProvider = getServiceDiscoveryForService(appName).serviceProviderBuilder()
                    .serviceName(appName).build();
            try {
                serviceProvider.start();
            } catch (Exception e) {
                throw new RuntimeException("Cannot start service provider for service " + service, e);
            }
            providers.put(service, serviceProvider);
        } else {
            serviceProvider = providers.get(service);
        }
        return serviceProvider;
    }

    public Collection<ServiceInstance<SearchServiceInstance>> getAllInstancesForService(SearchMicroService service)
            throws Exception {
        return getServiceProvider(service).getAllInstances();
    }

    public ServiceInstance<SearchServiceInstance> getInstance(SearchMicroService service)
            throws Exception {
        return getServiceProvider(service).getInstance();
    }
}
