package io.bdx.microservices.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoAireProduit {
    private String ida;
    @JsonProperty("aire_geo")
    private String aireGeo;
    @JsonProperty("signe_fr")
    private String signeFr;
    @JsonProperty("signe_ue")
    private String signeUe;
    private String produit;
    private String reference;

    public String getIda() {
        return ida;
    }

    public void setIda(String ida) {
        this.ida = ida;
    }

    public String getAireGeo() {
        return aireGeo;
    }

    public void setAireGeo(String aireGeo) {
        this.aireGeo = aireGeo;
    }

    public String getSigneFr() {
        return signeFr;
    }

    public void setSigneFr(String signeFr) {
        this.signeFr = signeFr;
    }

    public String getSigneUe() {
        return signeUe;
    }

    public void setSigneUe(String signeUe) {
        this.signeUe = signeUe;
    }

    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
