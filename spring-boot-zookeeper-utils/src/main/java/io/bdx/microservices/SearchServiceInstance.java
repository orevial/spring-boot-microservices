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

    private String version;

    private String gitCommitId;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getVersion() {
        return version;
    }

    public SearchServiceInstance setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getGitCommitId() {
        return gitCommitId;
    }

    public SearchServiceInstance setGitCommitId(String gitCommitId) {
        this.gitCommitId = gitCommitId;
        return this;
    }
}
