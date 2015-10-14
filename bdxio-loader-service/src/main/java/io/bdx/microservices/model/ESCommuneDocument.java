package io.bdx.microservices.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Presentation on 14/10/2015.
 */
public class ESCommuneDocument {
    private String ci;
    private String departement;
    private String commune;
    private String art;

    private List<ESAppellationDocument> appellations = new ArrayList<>();

    public ESCommuneDocument(String art, String ci, String departement, String commune) {
        this.art = art;
        this.ci = ci;
        this.departement = departement;
        this.commune = commune;
    }

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

    public List<ESAppellationDocument> getAppellations() {
        return appellations;
    }

    public void addAppellations(ESAppellationDocument appellation) {
        this.appellations.add(appellation);
    }
}
