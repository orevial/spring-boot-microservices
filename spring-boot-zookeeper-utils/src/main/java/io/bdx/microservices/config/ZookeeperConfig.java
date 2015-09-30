package io.bdx.microservices.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);

    @Bean
    public CuratorFramework getZkClient() {
        String zkURL = "localhost:2181";

        // Initialize zookeeper client and make sure it is connected before doing anything else
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(zkURL, new ExponentialBackoffRetry(1000, 10));
        if (zkClient != null && zkClient.getState() != CuratorFrameworkState.STARTED) {
            zkClient.start();
            try {
                zkClient.getZookeeperClient().blockUntilConnectedOrTimedOut();
            } catch (InterruptedException e) {
                logger.error("[GRAVE] - Unable to instantiate a zookeeper client. Please check your zk configuration.");
            }
        }
        return zkClient;
    }
}
