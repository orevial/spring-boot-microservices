package io.bdx.microservices.model;

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

    public List<String> getAppellations() {
        return appellations;
    }

    public void setAppellations(List<String> appellations) {
        this.appellations = appellations;
    }
}
