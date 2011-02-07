package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import claro.jpa.catalog.Property;

@SuppressWarnings("serial")
public class ImportProducts implements Serializable {
    private Long id;
    private Property matchProperty;
    private String outputChannelExpression = "";
    private List<ImportCategory> categories;
    private List<ImportProperty> properties;
    private ImportRules rules;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Property getMatchProperty() {
        return matchProperty;
    }

    public void setMatchProperty(Property value) {
        this.matchProperty = value;
    }

    public String getOutputChannelExpression() {
        return outputChannelExpression;
    }

    public void setOutputChannelExpression(String value) {
        if (value == null) {
            value = "";
        }
        this.outputChannelExpression = value;
    }

    public List<ImportCategory> getCategories() {
        if (categories == null) {
            categories = new ArrayList<ImportCategory>();
        }
        return categories;
    }

    public void setCategories(List<ImportCategory> value) {
        this.categories = value;
    }

    public List<ImportProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<ImportProperty>();
        }
        return properties;
    }

    public void setProperties(List<ImportProperty> value) {
        this.properties = value;
    }

    public ImportRules getRules() {
        return rules;
    }

    public void setRules(ImportRules value) {
        this.rules = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportProducts) {
            ImportProducts otherImportProducts = (ImportProducts) other;
            if (this.id == null) {
                if (otherImportProducts.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportProducts.id)) {
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