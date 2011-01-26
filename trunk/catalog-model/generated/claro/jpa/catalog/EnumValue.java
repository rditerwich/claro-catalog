package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class EnumValue implements Serializable {
    private Long id = 0l;
    private Integer value = 0;
    private List<Label> labels;
    private Property property;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        if (value == null) {
            value = 0;
        }
        this.value = value;
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

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property value) {
        this.property = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnumValue) {
            EnumValue otherEnumValue = (EnumValue) other;
            if (this.id == null) {
                if (otherEnumValue.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherEnumValue.id)) {
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