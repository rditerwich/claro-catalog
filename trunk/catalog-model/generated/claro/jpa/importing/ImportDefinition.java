package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.Source;

@SuppressWarnings("serial")
public class ImportDefinition extends Source implements Serializable {
    private String name;
    private String importUrlExpression;
    private String languageExpression;
    private String defaultCurrency;
    private String outputChannelExpression;
    private Property matchProperty;
    private Integer sequenceNr;
    private Collection<ImportCategory> categories;
    private Collection<ImportProperty> properties;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getImportUrlExpression() {
        return importUrlExpression;
    }

    public void setImportUrlExpression(String value) {
        this.importUrlExpression = value;
    }

    public String getLanguageExpression() {
        return languageExpression;
    }

    public void setLanguageExpression(String value) {
        this.languageExpression = value;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String value) {
        this.defaultCurrency = value;
    }

    public String getOutputChannelExpression() {
        return outputChannelExpression;
    }

    public void setOutputChannelExpression(String value) {
        this.outputChannelExpression = value;
    }

    public Property getMatchProperty() {
        return matchProperty;
    }

    public void setMatchProperty(Property value) {
        this.matchProperty = value;
    }

    public Integer getSequenceNr() {
        return sequenceNr;
    }

    public void setSequenceNr(Integer value) {
        this.sequenceNr = value;
    }

    public Collection<ImportCategory> getCategories() {
        if (categories == null) {
            categories = new ArrayList<ImportCategory>();
        }
        return categories;
    }

    public void setCategories(Collection<ImportCategory> value) {
        this.categories = value;
    }

    public Collection<ImportProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<ImportProperty>();
        }
        return properties;
    }

    public void setProperties(Collection<ImportProperty> value) {
        this.properties = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportDefinition) {
            ImportDefinition otherImportDefinition = (ImportDefinition) other;
            return super.equals(otherImportDefinition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}