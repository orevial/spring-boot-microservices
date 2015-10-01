package io.bdx.microservices;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.zookeeper.KeeperException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic Operations for zookeeper.
 */
public abstract class ZookeeperService {

    @Inject
    private CuratorFramework zkClient;

    private final Map<String, ServiceDiscovery<SearchServiceInstance>> serviceDiscoveries = new HashMap<>();

    private final Map<String, Map<SearchMicroService, ServiceProvider<SearchServiceInstance>>> serviceProviders = new HashMap<>();

    private static final String DISCOVERY_BASE_URL = "/services/discovery/";

    public void pushPathContent(String path, byte[] bytes) throws Exception {
        ensurePath(path);
        // Actually set the data to the ZNode
        zkClient.setData().forPath(path, bytes);
    }

    private void ensurePath(String path) throws Exception {
        // Make sure the path is created
        EnsurePath ensurePath = new EnsurePath(path);
        ensurePath.ensure(zkClient.getZookeeperClient());
    }

    public byte[] getPathContent(String path) throws Exception {
        return zkClient.getData().forPath(path);
    }

    public byte[] getPathContent(String path, boolean createIfNotExists)
            throws Exception {
        try {
            return getPathContent(path);
        } catch (KeeperException.NoNodeException nne) {
            if (createIfNotExists) {
                ensurePath(path);
                return getPathContent(path);
            } else {
                throw nne;
            }
        }
    }

    public void removeNode(String path) throws Exception {
        zkClient.delete().forPath(path);
    }

    public boolean hasNode(String path) throws Exception {
        return zkClient.checkExists().forPath(path) != null;
    }

    public List<String> getChildrenNodesNames(String rootPath) throws Exception {
        ensurePath(rootPath);
        return zkClient.getChildren().forPath(rootPath);
    }

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
//
//    public List<String> getAllTenants() throws Exception {
//        return getChildrenNodesNames(ZookeeperDiscoveryPath.getTenantsRootPath());
//    }
//
//    public List<String> getAllTenants(String tenantPrefix) throws Exception {
//        List<String> filteredTenants = new ArrayList<String>();
//        for (String tenant : getAllTenants()) {
//            if (tenant.startsWith(tenantPrefix)) {
//                filteredTenants.add(tenant);
//            }
//        }
//        return filteredTenants;
//    }
//
//    public Collection<Collection<ServiceInstance<SearchServiceInstance>>> getInstancesForAllTenants(
//            SearchMicroService service)
//            throws Exception {
//        return getInstancesMapForAllTenants(service).values();
//    }
//
//    public Map<String, Collection<ServiceInstance<SearchServiceInstance>>> getInstancesMapForAllTenants(
//            SearchMicroService service)
//            throws Exception {
//        Map<String, Collection<ServiceInstance<SearchServiceInstance>>> serviceInstancesMap = new HashMap<>();
//
//        for (String tenantNodeName : getAllTenants()) {
//            Collection<ServiceInstance<SearchServiceInstance>> instancesForTenant = getInstancesForTenant(service,
//                    tenantNodeName);
//            if (instancesForTenant != null && !instancesForTenant.isEmpty()) {
//                serviceInstancesMap.put(tenantNodeName, instancesForTenant);
//            }
//        }
//        return serviceInstancesMap;
//    }
//
//    public Map<String, Collection<ServiceInstance<SearchServiceInstance>>> getInstancesMapForTenantPrefix(
//            SearchMicroService service, String tenantPrefix) throws Exception {
//        Map<String, Collection<ServiceInstance<SearchServiceInstance>>> serviceInstancesMap = new HashMap<>();
//        for (String tenant : getAllTenants(tenantPrefix)) {
//            Collection<ServiceInstance<SearchServiceInstance>> instancesForTenant = getInstancesForTenant(service,
//                    tenant);
//            if (instancesForTenant != null && !instancesForTenant.isEmpty()) {
//                serviceInstancesMap.put(tenant, instancesForTenant);
//            }
//        }
//        return serviceInstancesMap;
//    }
//
//    private Collection<ServiceInstance<SearchServiceInstance>> getInstancesForTenant(
//            SearchMicroService service, String tenant)
//            throws Exception {
//        return getServiceProvider(tenant, service).getAllInstances();
//    }
//
//    public ServiceInstance<SearchServiceInstance> getServiceInstanceForTenantPrefix(
//            SearchMicroService service, String tenantPrefix) throws Exception {
//        ServiceInstance<SearchServiceInstance> serviceInstance;
//        for (String tenant : getAllTenants(tenantPrefix)) {
//            serviceInstance = getServiceInstanceForTenant(service, tenant);
//            if (serviceInstance != null) {
//                return serviceInstance;
//            }
//        }
//        throw new DiscoveryException("No service found for service " + service + " with tenantPrefix " + tenantPrefix);
//    }
//
//    public ServiceInstance<SearchServiceInstance> getServiceInstanceForTenant(SearchMicroService service, String tenant)
//            throws Exception {
//
//        return getServiceProvider(tenant, service).getInstance();
//    }
}
