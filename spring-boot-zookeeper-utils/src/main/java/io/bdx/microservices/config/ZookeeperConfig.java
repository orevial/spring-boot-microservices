package io.bdx.microservices.config;

import io.bdx.microservices.SearchServiceInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class ZookeeperConfig {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);
    public static final String SERVICES_DISCOVERY_BASE_PATH = "/services/discovery/";

    private AtomicBoolean built = new AtomicBoolean(false);

    private AtomicReference<ServiceInstance<SearchServiceInstance>> serviceInstance = new AtomicReference<>();
    private AtomicReference<ServiceDiscovery<SearchServiceInstance>> serviceDiscovery = new AtomicReference<>();

    @Value("${zookeeper.url}")
    private String zookeeperUrl;

    @Value("${zookeeper.port}")
    private String zookeeperPort;

    private CuratorFramework zkClient;

    @Bean
    public CuratorFramework getZkClient() {
        String zkURL = zookeeperUrl + ":" + zookeeperPort;

        // Initialize zookeeper client and make sure it is connected before doing anything else
        zkClient = CuratorFrameworkFactory.newClient(zkURL, new ExponentialBackoffRetry(1000, 10));
        if (zkClient != null && zkClient.getState() != CuratorFrameworkState.STARTED) {
            zkClient.start();
            try {
                zkClient.getZookeeperClient().blockUntilConnectedOrTimedOut();
            } catch (InterruptedException e) {
                logger.error("[GRAVE] - Unable to instantiate a zookeeper client. Please check your zk configuration.");
            }
        }
        try {
            configureServiceDiscovery(this.serviceInstance.get());
            this.serviceDiscovery.get().start();
        } catch (Exception e) {
            logger.error("Service registration failed !", e);
            throw new RuntimeException(e);
        }
        return zkClient;
    }

    public void prepareServiceDiscovery(ServiceInstance<SearchServiceInstance> serviceInstance) {
        if (built.compareAndSet(false, true)) {
            try {
                configureServiceInstance(serviceInstance);
            } catch (Exception e) {
                logger.error("Service registration failed !", e);
                throw new RuntimeException(e);
            }
        }
    }

    protected void configureServiceInstance(ServiceInstance<SearchServiceInstance> serviceInstance) throws Exception {
        this.serviceInstance.set(serviceInstance);
    }

    protected void configureServiceDiscovery(ServiceInstance<SearchServiceInstance> serviceInstance) {
        serviceDiscovery.set(ServiceDiscoveryBuilder.builder(SearchServiceInstance.class)
                .client(zkClient)
                .basePath(SERVICES_DISCOVERY_BASE_PATH)
                .serializer(instanceSerializer())
                .thisInstance(serviceInstance)
                .build());
    }

    public ServiceDiscovery<SearchServiceInstance> getServiceDiscovery() {
        return serviceDiscovery.get();
    }

    @Bean
    public InstanceSerializer<SearchServiceInstance> instanceSerializer() {
        return new JsonInstanceSerializer<>(SearchServiceInstance.class);
    }
}
