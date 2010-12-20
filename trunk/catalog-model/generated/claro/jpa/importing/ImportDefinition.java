package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class ImportDefinition implements Serializable {
    private Long id;
    private String name;
    private String importUrl;
    private String importSourceName;
    private String matchProperty;
    private Integer priority;
    private Integer sequenceNr;
    private Collection<ImportCategory> categories;
    private Collection<ImportProperty> properties;
    private Collection<ImportSource> importSources;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getImportUrl() {
        return importUrl;
    }

    public void setImportUrl(String value) {
        this.importUrl = value;
    }

    public String getImportSourceName() {
        return importSourceName;
    }

    public void setImportSourceName(String value) {
        this.importSourceName = value;
    }

    public String getMatchProperty() {
        return matchProperty;
    }

    public void setMatchProperty(String value) {
        this.matchProperty = value;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer value) {
        this.priority = value;
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

    public Collection<ImportSource> getImportSources() {
        if (importSources == null) {
            importSources = new ArrayList<ImportSource>();
        }
        return importSources;
    }

    public void setImportSources(Collection<ImportSource> value) {
        this.importSources = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportDefinition) {
            ImportDefinition otherImportDefinition = (ImportDefinition) other;
            if (this.id == null) {
                if (otherImportDefinition.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportDefinition.id)) {
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