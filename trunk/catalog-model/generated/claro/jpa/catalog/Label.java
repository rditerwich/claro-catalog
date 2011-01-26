package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class Label implements Serializable {
    private Long id = 0l;
    private String language;
    private String label = "";
    private Property property;
    private PropertyGroup propertyGroup;
    private EnumValue enumValue;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String value) {
        this.language = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        if (value == null) {
            value = "";
        }
        this.label = value;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property value) {
        this.property = value;
    }

    public PropertyGroup getPropertyGroup() {
        return propertyGroup;
    }

    public void setPropertyGroup(PropertyGroup value) {
        this.propertyGroup = value;
    }

    public EnumValue getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(EnumValue value) {
        this.enumValue = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Label) {
            Label otherLabel = (Label) other;
            if (this.id == null) {
                if (otherLabel.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherLabel.id)) {
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