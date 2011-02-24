package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Override;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Category extends Item implements Serializable {
    private Boolean containsProducts = false;
    private List<PropertyGroupAssignment> propertyGroupAssignments;

    public Boolean getContainsProducts() {
        return containsProducts;
    }

    public void setContainsProducts(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.containsProducts = value;
    }

    public List<PropertyGroupAssignment> getPropertyGroupAssignments() {
        if (propertyGroupAssignments == null) {
            propertyGroupAssignments = new ArrayList<PropertyGroupAssignment>();
        }
        return propertyGroupAssignments;
    }

    public void setPropertyGroupAssignments(List<PropertyGroupAssignment> value) {
        this.propertyGroupAssignments = value;
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