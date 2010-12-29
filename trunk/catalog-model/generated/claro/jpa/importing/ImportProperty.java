package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import claro.jpa.catalog.Property;

@SuppressWarnings("serial")
public class ImportProperty implements Serializable {
    private Long id;
    private ImportDefinition importDefinition;
    private Property property;
    private String valueExpression;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public ImportDefinition getImportDefinition() {
        return importDefinition;
    }

    public void setImportDefinition(ImportDefinition value) {
        this.importDefinition = value;
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