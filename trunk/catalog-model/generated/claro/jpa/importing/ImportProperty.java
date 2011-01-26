package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import claro.jpa.catalog.Property;

@SuppressWarnings("serial")
public class ImportProperty implements Serializable {
    private Long id = 0l;
    private ImportProducts importProducts;
    private Property property;
    private String valueExpression = "";

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public ImportProducts getImportProducts() {
        return importProducts;
    }

    public void setImportProducts(ImportProducts value) {
        this.importProducts = value;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property value) {
        this.property = value;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String value) {
        if (value == null) {
            value = "";
        }
        this.valueExpression = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportProperty) {
            ImportProperty otherImportProperty = (ImportProperty) other;
            if (this.id == null) {
                if (otherImportProperty.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportProperty.id)) {
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