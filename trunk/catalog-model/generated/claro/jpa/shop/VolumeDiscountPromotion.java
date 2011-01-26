package claro.jpa.shop;

import java.io.Serializable;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import claro.jpa.catalog.Product;

@SuppressWarnings("serial")
public class VolumeDiscountPromotion extends Promotion implements Serializable {
    private Product product;
    private Double price = 0.0;
    private String priceCurrency = "";
    private Integer volumeDiscount = 0;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product value) {
        this.product = value;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double value) {
        if (value == null) {
            value = 0.0;
        }
        this.price = value;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String value) {
        if (value == null) {
            value = "";
        }
        this.priceCurrency = value;
    }

    public Integer getVolumeDiscount() {
        return volumeDiscount;
    }

    public void setVolumeDiscount(Integer value) {
        if (value == null) {
            value = 0;
        }
        this.volumeDiscount = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VolumeDiscountPromotion) {
            VolumeDiscountPromotion otherVolumeDiscountPromotion = (VolumeDiscountPromotion) other;
            return super.equals(otherVolumeDiscountPromotion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}