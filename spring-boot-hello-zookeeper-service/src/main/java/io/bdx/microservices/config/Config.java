package io.bdx.microservices.config;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.inject.Inject;

import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.SocketUtils;

import io.bdx.microservices.SearchServiceInstance;

@Configuration
public class Config {

    @Inject
    protected Environment env;

    @Value("${service.min-port}")
    private int minPort;

    @Value("${service.max-port}")
    private int maxPort;

    @Bean
    public ServiceInstance<SearchServiceInstance> getServiceInstance() throws Exception {
        SearchServiceInstance payload = new SearchServiceInstance();

        return ServiceInstance.<SearchServiceInstance>builder()
                .name(getAppName())
                .payload(payload)
                .port(8080)
                .address(getIpAddress())
                .build();
    }

    @Bean
    @Inject
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        EmbeddedServletContainerCustomizer container = new EmbeddedServletContainerCustomizer() {
            public void customize(ConfigurableEmbeddedServletContainer container) {
                int availablePort = 0;
                if (maxPort == 0 || minPort == 0) {
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
            }
        };
        return container;
    }

    private String getAppName() {
        return "spring-boot-hello-zookeeper-service";
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
