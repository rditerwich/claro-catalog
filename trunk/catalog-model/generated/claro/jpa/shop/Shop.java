package claro.jpa.shop;

import java.io.Serializable;
import java.lang.Double;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import claro.jpa.catalog.OutputChannel;

@SuppressWarnings("serial")
public class Shop extends OutputChannel implements Serializable {
    private String urlPrefix;
    private String languages = "";
    private List<Navigation> navigation;
    private List<Promotion> promotions;
    private Double shippingCosts;
    private Double expressDeliveryCharge;

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String value) {
        this.urlPrefix = value;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String value) {
        if (value == null) {
            value = "";
        }
        this.languages = value;
    }

    public List<Navigation> getNavigation() {
        if (navigation == null) {
            navigation = new ArrayList<Navigation>();
        }
        return navigation;
    }

    public void setNavigation(List<Navigation> value) {
        this.navigation = value;
    }

    public List<Promotion> getPromotions() {
        if (promotions == null) {
            promotions = new ArrayList<Promotion>();
        }
        return promotions;
    }

    public void setPromotions(List<Promotion> value) {
        this.promotions = value;
    }

    public Double getShippingCosts() {
        return shippingCosts;
    }

    public void setShippingCosts(Double value) {
        this.shippingCosts = value;
    }

    public Double getExpressDeliveryCharge() {
        return expressDeliveryCharge;
    }

    public void setExpressDeliveryCharge(Double value) {
        this.expressDeliveryCharge = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Shop) {
            Shop otherShop = (Shop) other;
            return super.equals(otherShop);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}