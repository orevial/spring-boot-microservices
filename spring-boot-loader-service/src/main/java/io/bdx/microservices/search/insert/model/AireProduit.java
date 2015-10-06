package io.bdx.microservices.search.insert.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AireProduit {
    private String ida;
    @JsonProperty("aire_geo")
    private String aireGeo;
    @JsonProperty("signe_fr")
    private String signeFr;
    @JsonProperty("signe_ue")
    private String signeUe;
    private String produit;
    private String reference;
}
