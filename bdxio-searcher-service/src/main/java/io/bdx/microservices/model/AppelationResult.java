package io.bdx.microservices.model;

/**
 * Created by Presentation on 14/10/2015.
 */
public class AppelationResult {
    private String ida;
    private String aireGeo;
    private String productName;

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
