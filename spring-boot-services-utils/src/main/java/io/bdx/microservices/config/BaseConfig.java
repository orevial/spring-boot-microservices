package io.bdx.microservices.config;

import io.bdx.microservices.SearchServiceInstance;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.SocketUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Configuration
public class BaseConfig {

    @Inject
    protected Environment env;

    @Inject
    private ZookeeperConfig zookeeperConfig;

    @Value("${service.port:0}")
    private Integer port;

    @Value("${service.min-port:0}")
    private Integer minPort;

    @Value("${service.max-port:0}")
    private Integer maxPort;

    @Value("${info.app.artifact}")
    private String artifactId;

    @Value("${info.app.name}")
    private String appName;

    @Value("${info.app.description}")
    private String appDescription;

    @Value("${info.app.version}")
    private String appVersion;

    @Bean
    @Inject
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        EmbeddedServletContainerCustomizer container = new EmbeddedServletContainerCustomizer() {
            public void customize(ConfigurableEmbeddedServletContainer container) {
                int availablePort = 0;
                if(port != 0) {
                    availablePort = port;
                } else if (maxPort == 0 || minPort == 0) {
                    if (minPort == 0) {
                        availablePort = SocketUtils.findAvailableTcpPort();
                    } else {
                        availablePort = SocketUtils.findAvailableTcpPort(minPort);
                    }
                } else {
                    // Find an available port within given range
                    availablePort = SocketUtils.findAvailableTcpPort(minPort, maxPort);
                }
                container.setPort(availablePort);
                zookeeperConfig.prepareServiceDiscovery(initializeZookeeperDiscovery(availablePort));
            }
        };
        return container;
    }

    private ServiceInstance<SearchServiceInstance> initializeZookeeperDiscovery(int port) {
        SearchServiceInstance payload = new SearchServiceInstance();

        try {
            return ServiceInstance.<SearchServiceInstance>builder()
                    .name(artifactId)
                    .payload(payload)
                    .port(port)
                    .address(getIpAddress())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("FATAL - Unable to get service instance", e);
        }
    }

    /**
     * Return a non loopback IPv4 address for the machine running this process. If the machine has multiple network
     * interfaces, the IP address for the first interface returned by
     * {@link java.net.NetworkInterface#getNetworkInterfaces} is returned.
     *
     * @return non loopback IPv4 address for the machine running this process
     * @see java.net.NetworkInterface#getNetworkInterfaces
     * @see java.net.NetworkInterface#getInetAddresses
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> enumNic = NetworkInterface.getNetworkInterfaces(); enumNic
                    .hasMoreElements();) {
                NetworkInterface ifc = enumNic.nextElement();
                if (ifc.isUp()) {
                    for (Enumeration<InetAddress> enumAddr = ifc.getInetAddresses(); enumAddr.hasMoreElements();) {
                        InetAddress address = enumAddr.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return "unknown";
    }
}
