package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Property implements Serializable {
    private Long id = 0l;
    private List<Label> labels;
    private Boolean categoryProperty = false;
    private Item item;
    private PropertyType type;
    private Boolean isMany = false;
    private List<EnumValue> enumValues;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public List<Label> getLabels() {
        if (labels == null) {
            labels = new ArrayList<Label>();
        }
        return labels;
    }

    public void setLabels(List<Label> value) {
        this.labels = value;
    }

    public Boolean getCategoryProperty() {
        return categoryProperty;
    }

    public void setCategoryProperty(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.categoryProperty = value;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item value) {
        this.item = value;
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
        if (value == null) {
            value = false;
        }
        this.isMany = value;
    }

    public List<EnumValue> getEnumValues() {
        if (enumValues == null) {
            enumValues = new ArrayList<EnumValue>();
        }
        return enumValues;
    }

    public void setEnumValues(List<EnumValue> value) {
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