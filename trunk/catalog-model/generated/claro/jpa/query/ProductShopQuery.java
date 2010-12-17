package claro.jpa.query;

import java.io.Serializable;
import java.lang.Override;
import java.lang.String;
import claro.jpa.shop.Shop;

@SuppressWarnings("serial")
public class ProductShopQuery extends Query implements Serializable {
    private String stringValue;
    private Shop shop;

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String value) {
        this.stringValue = value;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop value) {
        this.shop = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ProductShopQuery) {
            ProductShopQuery otherProductShopQuery = (ProductShopQuery) other;
            return super.equals(otherProductShopQuery);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}