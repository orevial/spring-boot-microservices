package io.bdx.microservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Presentation on 14/10/2015.
 */
public class ESAppellationDocument {
    @JsonProperty("aire_geo")
    private String aireGeo;
    private String ida;

    public ESAppellationDocument(String aireGeo, String ida) {
        this.aireGeo = aireGeo;
        this.ida = ida;
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
