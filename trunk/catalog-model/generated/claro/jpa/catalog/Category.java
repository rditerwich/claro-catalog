package claro.jpa.catalog;

import java.lang.Boolean;
import java.lang.Override;

public class Category extends Item {
    private Boolean containsProducts;

    public Boolean getContainsProducts() {
        return containsProducts;
    }

    public void setContainsProducts(Boolean value) {
        this.containsProducts = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Category) {
            Category otherCategory = (Category) other;
            return super.equals(otherCategory);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}