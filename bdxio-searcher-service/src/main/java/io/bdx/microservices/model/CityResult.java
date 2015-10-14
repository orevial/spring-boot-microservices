package io.bdx.microservices.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Presentation on 14/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityResult {
    private String department;
    private String commune;
    private List<AppelationResult> appelationResults;
    private List<String> appellations = new ArrayList<>();

    public String getDepartment() {
        return department;
    }

    @JsonProperty("departement")
    public void setDepartment(String department) {
        this.department = StringUtils.capitalize(department.toLowerCase());
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    @JsonIgnore
    public List<AppelationResult> getAppelationResults() {
        return appelationResults;
    }

    @JsonProperty("appellations")
    public void setAppelationResults(List<AppelationResult> appelationResults) {
        this.appelationResults = appelationResults;
    }

    @JsonProperty("appellations")
    public List<String> getAppellations() {
        appellations.clear();
        for(AppelationResult result : appelationResults) {
            appellations.add(result.getIda() + " : " + result.getProductName());
        }

        return appellations;
    }

    @JsonIgnore
    public void setAppellations(List<String> appellations) {
        this.appellations = appellations;
    }
}
