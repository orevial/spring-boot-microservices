/**
 * 
 */
package io.bdx.microservices;

/**
 * @author stephane.lagraulet
 *
 */
public enum SearchMicroService {
    SPRING_BOOT_HELLO_ZOOKEEPER_SERVICE("spring-boot-hello-zookeeper-service"),
    SPRING_BOOT_DISCOVERY_SERVICE("spring-boot-discovery-service"),
    SPRING_BOOT_SEARCHER_SERVICE("spring-boot-searcher-service");

    private String appName;

    private SearchMicroService(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public static SearchMicroService forName(String serviceName) {
        for (SearchMicroService service : SearchMicroService.values()) {
            if (service.getAppName().equals(serviceName)) {
                return service;
            }
        }
        throw new IllegalArgumentException(serviceName + " does not exist!");
    }
}
