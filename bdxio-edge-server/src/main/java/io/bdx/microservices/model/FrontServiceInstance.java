package io.bdx.microservices.model;

/**
 * Created by Presentation on 14/10/2015.
 */
public class FrontServiceInstance {
    private String name;
    private String ipAddress;
    private int port;
    private String startDate;

    public FrontServiceInstance(String name, String ipAddress, int port, String startDate) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
