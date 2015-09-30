/**
 * 
 */
package io.bdx.microservices;

/**
 * @author stephane.lagraulet
 *
 */
public enum SearchMicroService {
    SEARCH_SERVICE("search-service"),
    EDGE_SERVICE("edge-service");

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
