package io.bdx.microservices;

import java.util.Date;

/**
 * Instance of a search service for service discovery.
 * 
 * @author stephane.lagraulet
 *
 */
public class SearchServiceInstance {

    private String startDate = new Date().toString();

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
