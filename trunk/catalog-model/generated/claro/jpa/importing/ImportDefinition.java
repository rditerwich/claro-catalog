package claro.jpa.importing;

import java.lang.Boolean;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;

public class ImportDefinition {
    private Long id;
    private String name;
    private String importUrl;
    private String importFileBaseName;
    private String importFilePattern;
    private String importSourceName;
    private Boolean importSourceNameAppendFileName;
    private Collection<ImportCategory> categories;
    private Collection<ImportProperty> properties;

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

    public String getImportFileBaseName() {
        return importFileBaseName;
    }

    public void setImportFileBaseName(String value) {
        this.importFileBaseName = value;
    }

    public String getImportFilePattern() {
        return importFilePattern;
    }

    public void setImportFilePattern(String value) {
        this.importFilePattern = value;
    }

    public String getImportSourceName() {
        return importSourceName;
    }

    public void setImportSourceName(String value) {
        this.importSourceName = value;
    }

    public Boolean getImportSourceNameAppendFileName() {
        return importSourceNameAppendFileName;
    }

    public void setImportSourceNameAppendFileName(Boolean value) {
        this.importSourceNameAppendFileName = value;
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