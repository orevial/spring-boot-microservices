package io.bdx.microservices.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommuneAire {
    private String ci;
    private String departement;
    private String commune;
    private String art;
    @JsonProperty("aire_geo")
    private String aireGeo;
    private String ida;

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getAireGeo() {
        return aireGeo;
    }

    public void setAireGeo(String aireGeo) {
        this.aireGeo = aireGeo;
    }

    public String getIda() {
        return ida;
    }

    public void setIda(String ida) {
        this.ida = ida;
    }
}
