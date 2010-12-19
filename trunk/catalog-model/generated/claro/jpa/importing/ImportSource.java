package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;

@SuppressWarnings("serial")
public class ImportSource implements Serializable {
    private Long id;
    private ImportDefinition importDefinition;

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

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportSource) {
            ImportSource otherImportSource = (ImportSource) other;
            if (this.id == null) {
                if (otherImportSource.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportSource.id)) {
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