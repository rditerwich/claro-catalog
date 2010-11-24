package claro.jpa.catalog;

import java.lang.Boolean;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;

public class Property {
    private Long id;
    private Collection<Label> labels;
    private Boolean categoryProperty;
    private Item item;
    private Collection<PropertyGroup> propertyGroups;
    private PropertyType type;
    private Boolean isMany;
    private Collection<EnumValue> enumValues;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Collection<Label> getLabels() {
        if (labels == null) {
            labels = new ArrayList<Label>();
        }
        return labels;
    }

    public void setLabels(Collection<Label> value) {
        this.labels = value;
    }

    public Boolean getCategoryProperty() {
        return categoryProperty;
    }

    public void setCategoryProperty(Boolean value) {
        this.categoryProperty = value;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item value) {
        this.item = value;
    }

    public Collection<PropertyGroup> getPropertyGroups() {
        if (propertyGroups == null) {
            propertyGroups = new ArrayList<PropertyGroup>();
        }
        return propertyGroups;
    }

    public void setPropertyGroups(Collection<PropertyGroup> value) {
        this.propertyGroups = value;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType value) {
        this.type = value;
    }

    public Boolean getIsMany() {
        return isMany;
    }

    public void setIsMany(Boolean value) {
        this.isMany = value;
    }

    public Collection<EnumValue> getEnumValues() {
        if (enumValues == null) {
            enumValues = new ArrayList<EnumValue>();
        }
        return enumValues;
    }

    public void setEnumValues(Collection<EnumValue> value) {
        this.enumValues = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Property) {
            Property otherProperty = (Property) other;
            if (this.id == null) {
                if (otherProperty.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherProperty.id)) {
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