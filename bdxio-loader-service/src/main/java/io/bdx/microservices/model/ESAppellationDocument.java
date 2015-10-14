package io.bdx.microservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Presentation on 14/10/2015.
 */
public class ESAppellationDocument {
    private String aireGeo;
    private String ida;
    private String productName;

    public ESAppellationDocument(String aireGeo, String ida, String productName) {
        this.aireGeo = aireGeo;
        this.ida = ida;
        this.productName = productName;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
