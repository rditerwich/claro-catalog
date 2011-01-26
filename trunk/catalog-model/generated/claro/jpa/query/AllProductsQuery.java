package claro.jpa.query;

import java.io.Serializable;
import java.lang.Override;
import java.lang.String;
import claro.jpa.catalog.Category;
import claro.jpa.shop.Shop;

@SuppressWarnings("serial")
public class AllProductsQuery extends Query implements Serializable {
    private Category category;
    private String stringValue = "";
    private Shop shop;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category value) {
        this.category = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String value) {
        if (value == null) {
            value = "";
        }
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
        if (other instanceof AllProductsQuery) {
            AllProductsQuery otherAllProductsQuery = (AllProductsQuery) other;
            return super.equals(otherAllProductsQuery);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}