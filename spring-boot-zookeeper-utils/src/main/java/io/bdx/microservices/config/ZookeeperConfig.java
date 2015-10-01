package io.bdx.microservices.config;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

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

import io.bdx.microservices.SearchServiceInstance;

@Configuration
public class ZookeeperConfig {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);
    public static final String SERVICES_DISCOVERY_BASE_PATH = "/services/discovery/";

    private AtomicBoolean built = new AtomicBoolean(false);

    private AtomicReference<ServiceInstance<SearchServiceInstance>> serviceInstance = new AtomicReference<>();
    private AtomicReference<ServiceDiscovery<SearchServiceInstance>> serviceDiscovery = new AtomicReference<>();

    @Inject
    private ServiceInstance<SearchServiceInstance> configServiceInstance;

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
            build();
        } catch (Exception e) {
            logger.error("Service registration failed !", e);
            throw new RuntimeException(e);
        }
        return zkClient;
    }

    public void build() throws Exception {
        if (built.compareAndSet(false, true)) {
            // UriSpec uriSpec = new UriSpec(properties.getUriSpec());
            configureServiceInstance();
            configureServiceDiscovery();
            this.serviceDiscovery.get().start();
        }
    }

    protected void configureServiceInstance() throws Exception {
        serviceInstance.set(configServiceInstance);
    }

    protected void configureServiceDiscovery() {
        serviceDiscovery.set(ServiceDiscoveryBuilder.builder(SearchServiceInstance.class)
                .client(zkClient)
                .basePath(SERVICES_DISCOVERY_BASE_PATH)
                .serializer(instanceSerializer())
                .thisInstance(configServiceInstance)
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
