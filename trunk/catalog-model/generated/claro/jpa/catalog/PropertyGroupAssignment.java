package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;

@SuppressWarnings("serial")
public class PropertyGroupAssignment implements Serializable {
    private Long id = 0l;
    private PropertyGroup propertyGroup;
    private Property property;
    private Category category;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public PropertyGroup getPropertyGroup() {
        return propertyGroup;
    }

    public void setPropertyGroup(PropertyGroup value) {
        this.propertyGroup = value;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property value) {
        this.property = value;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category value) {
        this.category = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PropertyGroupAssignment) {
            PropertyGroupAssignment otherPropertyGroupAssignment = (PropertyGroupAssignment) other;
            if (this.id == null) {
                if (otherPropertyGroupAssignment.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherPropertyGroupAssignment.id)) {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (id  == null? 0 : id .hashCode());
        return result;
    }

}