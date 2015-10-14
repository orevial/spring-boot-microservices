package io.bdx.microservices;

/**
 * @author stephane.lagraulet
 *
 */
public enum SearchMicroService {
    SPRING_BOOT_HELLO_ZOOKEEPER_SERVICE("spring-boot-hello-zookeeper-service"),
    BDXIO_EDGE_SERVER("bdxio-edge-server"),
    BDX_IO_SEARCHER_SERVICE("bdxio-searcher-service"),
    BDX_IO_LOADER_SERVICE("bdxio-loader-service");

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
